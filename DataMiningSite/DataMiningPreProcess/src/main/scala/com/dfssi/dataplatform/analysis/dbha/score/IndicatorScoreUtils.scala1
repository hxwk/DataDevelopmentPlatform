package com.dfssi.dataplatform.analysis.dbha.score

import com.dfssi.dataplatform.analysis.dbha.bean.IndicatorBean
import com.dfssi.dataplatform.analysis.dbha.config.IndicatorsConfig
import org.apache.spark.mllib.linalg
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Row, UserDefinedFunction}



object IndicatorScoreUtils extends Serializable {

  val columnMultiply2: UserDefinedFunction = udf { (col1: Any, col2: Any) =>
    col1.asInstanceOf[Double] * col2.asInstanceOf[Double]
  }

  // 指标差异系数与指标评分之积
  def indicatorScoreProduct(indicatorDiffMap: Map[Int, Double]): UserDefinedFunction = udf { (indicatorId: Int, indicatorScore: Double) =>
    indicatorDiffMap.getOrElse(indicatorId, 0.0) * indicatorScore
  }

  /**
    * 指标评分函数
    * @param indicatorDisDF     指标统计分布数据
    * @param tripIndicatorsDF   行程指标数据
    * @param indicatorIdContrib 指标贡献数据（-1 负贡献，1 正贡献）
    * @param dimIndicatorsMap   一二级指标数据
    * @param objectType          评分对象类型，trip代表行程，driver代表驾驶员
    * @return : RDD[String]
    */
  def score(tripIndicatorsDF: DataFrame,
            indicatorDisDF: DataFrame,
            indicatorIdContrib: Map[Int, Int],
            dimIndicatorsMap: Map[Int, Array[Int]],
            objectType: String = "trip") = {

    /** 先对二级指标进行打分 */
    val indicatorDisMap = indicatorDisDF.map{ row =>
      val indicatorId = row.getAs[Int]("indicator_id")
      indicatorId -> (row.getAs[Double]("min_value"),
                      row.getAs[Double]("max_value"),
                      indicatorIdContrib(indicatorId))
    }.collectAsMap()

    val getIndicatorScore = udf { (indicatorId: Int, indicatorValue: Double) =>
      val (minValue, maxValue, contrib) = indicatorDisMap(indicatorId)
      computeScoreByMaxMinScale(indicatorValue, minValue, maxValue, contrib == 1)
    }

    val scoredTripIndicators = tripIndicatorsDF
      .withColumn("indicator_score", getIndicatorScore(col("indicator_id"), col("indicator_value")))

//    println("行程指标分数为")
//    scoredTripIndicators.select("trip_id", "indicator_id","indicator_name","indicator_score").sort("indicator_id").show(50)

    /** 接下来行程或者车辆分组，熵权法根据评分计算指标权重 */
    val objectColName = objectType match {
      case "trip" => "trip_id"
      case "vehicle" => "vehicle_id"
    }

    val scoreMatrix = scoredTripIndicators.groupBy(objectColName, "indicator_id")
      .agg(mean("indicator_score") as "indicator_score")

    // 转换成指标向量，然后进行L1正则化
//    val normalizer = new Normalizer(1) //.setInputCol("features").setOutputCol("normFeatures")

    // 指标不多，指标差异系数collect成Map进行操作
    val indicatorDiffCoeffMap: collection.Map[Int, Double] = scoreMatrix.map{ case Row(_, indicatorId: Int, score: Double) =>
      indicatorId -> score
    }.groupByKey()
      .mapValues{ iter =>
        val sumIter = iter.map(math.abs).sum
        val scalar = -1.0/iter.size //-1/math.log(iter.size)
        val Ej = iter.map{ v =>
          // 先进行L1正则化
          val l1value = math.abs(v)/sumIter
          // 根据熵权法计算指标信息熵
          l1value * math.log(l1value)
        }.sum * scalar

        if(1 - Ej < 0) println("差异系数为负数")

        1 - Ej // 差异系数
//        val normVec: linalg.Vector = normalizer.transform(Vectors.dense(iter.toArray))
//        val m = normVec.size
//        normEntropy(normVec, m)
      }
      .collectAsMap()

    val objectScoreDF = computeScore(scoreMatrix, indicatorDiffCoeffMap, dimIndicatorsMap, objectColName)
//      .join(indicatorDisDF.select(col("indicator_id"), col("indicator_name")), usingColumns=Seq("indicator_id"), joinType="left_outer")

    (scoredTripIndicators, objectScoreDF)
  }


  def safetyIndicatorDiffCoeffMap(indicatorDiffCoeffMap: collection.Map[Int, Double],
                                  dimIndicatorsMap: Map[Int, Array[Int]]): Map[Int, Double] = {
    val indicatorIds = dimIndicatorsMap(IndicatorsConfig.DBHA_DIMENSION_ID_SAFETY)
//      IndicatorTable.DEFAULT_INDICATORS_FOR_SAFETY.map(IndicatorBean.INDICATORS_ID_MAP)
    indicatorIds.map(k => k -> indicatorDiffCoeffMap.getOrElse(k, 0.0)).toMap
  }

  def ecoIndicatorDiffCoeffMap(indicatorDiffCoeffMap: collection.Map[Int, Double],
                               dimIndicatorsMap: Map[Int, Array[Int]]): Map[Int, Double] = {
    val indicatorIds = dimIndicatorsMap(IndicatorsConfig.DBHA_DIMENSION_ID_ECONOMY)
//      IndicatorTable.DEFAULT_INDICATORS_FOR_ECONOMY.map(IndicatorBean.INDICATORS_ID_MAP)
    indicatorIds.map(k => k -> indicatorDiffCoeffMap.getOrElse(k, 0.0)).toMap
  }

  def mainIndicatorDiffCoeffMap(indicatorDiffCoeffMap: collection.Map[Int, Double],
                                dimIndicatorsMap: Map[Int, Array[Int]]): Map[Int, Double] = {
    val indicatorIds = dimIndicatorsMap(IndicatorsConfig.DBHA_DIMENSION_ID_MAINTENANCE)
//      IndicatorTable.DEFAULT_INDICATORS_FOR_MAINTENANCE.map(IndicatorBean.INDICATORS_ID_MAP)
    indicatorIds.map(k => k -> indicatorDiffCoeffMap.getOrElse(k, 0.0)).toMap
  }

  def civiIndicatorDiffCoeffMap(indicatorDiffCoeffMap: collection.Map[Int, Double],
                                dimIndicatorsMap: Map[Int, Array[Int]]): Map[Int, Double] = {
    val indicatorIds = dimIndicatorsMap(IndicatorsConfig.DBHA_DIMENSION_ID_CIVILIZATION)
//      IndicatorTable.DEFAULT_INDICATORS_FOR_CIVILIZATION.map(IndicatorBean.INDICATORS_ID_MAP)
    indicatorIds.map(k => k -> indicatorDiffCoeffMap.getOrElse(k, 0.0)).toMap
  }

  /**
    * 计算objectType的综合得分
    * @param scoreMatrix              评分矩阵，<object, indicatorId, indicatorScore>三元组
    * @param indicatorDiffCoeffMap   指标差异系数
    * @param dimIndicatorsMap         评分维度及其包含指标
    * @param objectColName            对象类型
    * @return
    */
  def computeScore( scoreMatrix: DataFrame,
                    indicatorDiffCoeffMap: collection.Map[Int, Double],
                    dimIndicatorsMap: Map[Int, Array[Int]],
                    objectColName: String): DataFrame = {

    val safety = safetyIndicatorDiffCoeffMap(indicatorDiffCoeffMap, dimIndicatorsMap)
    val eco = ecoIndicatorDiffCoeffMap(indicatorDiffCoeffMap, dimIndicatorsMap)
    val main = mainIndicatorDiffCoeffMap(indicatorDiffCoeffMap, dimIndicatorsMap)
    val civi = civiIndicatorDiffCoeffMap(indicatorDiffCoeffMap, dimIndicatorsMap)

    val sumDiffCoeff = indicatorDiffCoeffMap.values.sum
    val sumSafetyDiffCoeff = safety.values.sum
    val sumEcoDiffCoeff = eco.values.sum
    val sumMainDiffCoeff = main.values.sum
    val sumCiviDiffCoeff = civi.values.sum

    val tmp = scoreMatrix
      .withColumn("overall_score", indicatorScoreProduct(indicatorDiffCoeffMap.toMap)(col("indicator_id"), col("indicator_score")))
      .withColumn("safety_score", indicatorScoreProduct(safety)(col("indicator_id"), col("indicator_score")))
      .withColumn("economy_score", indicatorScoreProduct(eco)(col("indicator_id"), col("indicator_score")))
      .withColumn("maintainence_score", indicatorScoreProduct(main)(col("indicator_id"), col("indicator_score")))
      .withColumn("civilization_score", indicatorScoreProduct(civi)(col("indicator_id"), col("indicator_score")))

    val overallScoreDF = tmp.groupBy(objectColName)
      .agg(sum("overall_score")/sumDiffCoeff as "overall_score",
        sum("safety_score")/sumSafetyDiffCoeff as "safety_score",
        sum("economy_score")/sumEcoDiffCoeff as "economy_score",
        sum("maintainence_score")/sumMainDiffCoeff as "maintainence_score",
        sum("civilization_score")/sumCiviDiffCoeff as "civilization_score")

    overallScoreDF
  }



  private def getDimScore(scoreMatrix: DataFrame,
                          indicatorDiffCoeffMap: Map[Int, Double],
                  validIndicatorNames: Array[String],
                  objectColName: String,
                  newDimColName: String): DataFrame = {

    val safetyIndicatorIds = validIndicatorNames.map(IndicatorBean.INDICATORS_ID_MAP)

    val isInSafetyDim= udf { indicatorId: Int =>
      safetyIndicatorIds.contains(indicatorId)
    }

//    val safetyIndicatorDiffCoeffDF = indicatorDiffCoeffDF.filter(isInSafetyDim(col("indicator_id")))

    val sumDiffCoeff = safetyIndicatorIds.map(indicatorDiffCoeffMap.getOrElse(_, 0.0)).sum
//      safetyIndicatorDiffCoeffDF.map(_.getAs[Double]("diffCoefficient")).sum

    val objectDimScore = scoreMatrix.filter(isInSafetyDim(col("indicator_id")))
//      .join(safetyIndicatorDiffCoeffDF, usingColumns=Seq("indicator_id"), joinType="left_outer")
        .withColumn("product", indicatorScoreProduct(indicatorDiffCoeffMap)(col("indicator_id"), col("indicator_score")))
      .select(objectColName, "product")
      .groupBy(objectColName).agg(sum("product")/sumDiffCoeff as newDimColName)

    objectDimScore
  }

  def safetyScore(scoreMatrix: DataFrame,
                  indicatorDiffCoeffDF: DataFrame,
                  objectColName: String) = {

//    getDimScore(scoreMatrix,
//      indicatorDiffCoeffDF,
//      IndicatorTable.DEFAULT_INDICATORS_FOR_SAFETY,
//      objectColName, "safety_score")
  }

  def getEconomyScore(scoreMatrix: DataFrame,
                      indicatorDiffCoeffDF: DataFrame,
                      objectColName: String) = {
//    getDimScore(scoreMatrix,
//      indicatorDiffCoeffDF,
//      IndicatorTable.DEFAULT_INDICATORS_FOR_ECONOMY,
//      objectColName, "economy_score")
  }

  def getMaintainenceScore(scoreMatrix: DataFrame,
                      indicatorDiffCoeffDF: DataFrame,
                      objectColName: String) = {
//    getDimScore(scoreMatrix,
//      indicatorDiffCoeffDF,
//      IndicatorTable.DEFAULT_INDICATORS_FOR_MAINTENANCE,
//      objectColName, "maintainence_score")
  }

  def getCivilizationScore(scoreMatrix: DataFrame,
                      indicatorDiffCoeffDF: DataFrame,
                      objectColName: String) = {
//    getDimScore(scoreMatrix,
//      indicatorDiffCoeffDF,
//      IndicatorTable.DEFAULT_INDICATORS_FOR_CIVILIZATION,
//      objectColName, "civilization_score")
  }


  def normEntropy(vector: linalg.Vector, m: Int): Double = {
    val scaler = -1/math.log(m)
    vector.toArray.map(e => e * math.log(e)).sum * scaler
  }


  def computeScoreByMaxMinScale(indicatorValue: Double,
                                    minValue: Double,
                                    maxValue: Double,
                                    isPositive: Boolean,
                                    minScore: Float = 60,
                                    maxScore: Float = 98): Float = {
    val delta = 1e-3
    val maxMinDiff = maxValue - minValue

    val scaledValue = if(isPositive) {
      if(maxMinDiff <= delta) 0
      else if (indicatorValue > maxValue) 1
      else (indicatorValue + delta - minValue) / maxMinDiff
    } else {
      if(maxMinDiff <= delta) 1
      else if (indicatorValue > maxValue) 0
      else (maxValue + delta - indicatorValue) / maxMinDiff
    }

    val finalScore = (maxScore - minScore) * scaledValue + minScore

    finalScore.toFloat
  }

}
