package com.dfssi.dataplatform.analysis.preprocess.process.rateindicator

import java.sql.Timestamp
import java.text.SimpleDateFormat

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.dbha.config.IndicatorsConfig
import com.dfssi.dataplatform.analysis.dbha.rdbutils.{DbManager, DbPropsParser, OutputRdbms}
import com.dfssi.dataplatform.analysis.dbha.score.IndicatorScoreUtils
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.preprocess.process.rateindicator.ProcessIndicatorScore.DailyScoreSummary
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.ml.feature.MinMaxScaler
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SaveMode}

import scala.xml.Elem

class ProcessIndicatorScore extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {

    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
    val paramsMap: Map[String, String] = extractSimpleParams(defEl).toMap

    val inputIds: Array[String] = getInputs(defEl)

    val groupTypeStr = paramsMap.getOrElse("groupType", "NONE")
    val groupType = groupTypeStr match {
      case "NONE" => 0
      case "FLEET" => 1
      case "MODEL" => 2
    }

    // trip_indicators 表
    val optionTripIndicatorsDF = processContext.dataFrameMap.get(inputIds(0))
    require(optionTripIndicatorsDF.nonEmpty)
    val tripIndicatorsDF = optionTripIndicatorsDF.get//.where("indicator_score IS NULL")

    // indicator_distribution 表
    val optionIndicatorDisDF = processContext.dataFrameMap.get(inputIds(2))
    require(optionIndicatorDisDF.nonEmpty)
    val indicatorDisDF = optionIndicatorDisDF.get.where(s"group_type = $groupType")

    // get broadcast configs
    val bcConfig = processContext.broadcastConfig.value
    val bcConfigEl = bcConfig.asInstanceOf[Elem]
    val indicatorIdContrib = IndicatorsConfig.getIndicatorIdContribMap(bcConfigEl)
    val dimIndicatorsMap = IndicatorsConfig.getDimIndicatorsMap(bcConfigEl)

    val (scoredTripIndicators, objectScoreDF) = IndicatorScoreUtils.score(tripIndicatorsDF, indicatorDisDF, indicatorIdContrib, dimIndicatorsMap)
    /** 为dbha_trip_indciators 生成分数更新sql*/
    val indicatorScoreUpdateRdd = scoredTripIndicators.map{ row =>
      val indicatorId = row.getAs[Int]("indicator_id")
      val indicatorScore = row.getAs[Float]("indicator_score")
      val tripId = row.getAs[String]("trip_id")

      s"UPDATE dbha_trip_indicators SET indicator_score=$indicatorScore WHERE trip_id='$tripId' AND indicator_id='$indicatorId'"
    }

    val objectScoreUpdateRdd = objectScoreDF.map{ row =>
      val tripId = row.getAs[String]("trip_id")
      val safetyScore = row.getAs[Double]("safety_score")
      val ecoScore = row.getAs[Double]("economy_score")
      val mainScore = row.getAs[Double]("maintainence_score")
      val civiScore = row.getAs[Double]("civilization_score")
      val ocerallScore = row.getAs[Double]("overall_score")

      s"UPDATE dbha_trip_detail SET score_safety=$safetyScore, score_economy=$ecoScore, score_maintenance=$mainScore, " +
        s"score_civilization=$civiScore, score_overall=$ocerallScore WHERE trip_id='$tripId'"
    }

    val updateSqlRdd = indicatorScoreUpdateRdd.union(objectScoreUpdateRdd)

    //    println("保存打分结果")
//    saveIndicatorSocre(updateSqlRdd, paramsMap)

    val hiveContext = processContext.hiveContext
    import hiveContext.implicits._
    val resultDF = updateSqlRdd.toDF("update_sql")

    processContext.dataFrameMap.put(id, resultDF)

    val tripDriverRdd = getTripDriverRdd(paramsMap, objectScoreDF)
    val tripDriverDF = tripDriverRdd.toDF()

    val validDF = objectScoreDF.join(tripDriverDF, Seq("trip_id"), joinType = "left_outer")
      .where("fleet_id IS NOT NULL")

    // 车队每日汇总得分
    val driverLevelSummaryDF = validDF.groupBy("date", "fleet_id", "driver_id")
      .agg(
//        count("trip_id") as "count",
        mean("safety_score") as "score_safety",
        mean("economy_score") as "score_economy",
        mean("maintainence_score") as "score_maintenance",
        mean("civilization_score") as "score_civilization",
        mean("overall_score") as "score_overall")

    OutputRdbms.saveDailySummaryScore(paramsMap, driverLevelSummaryDF, SaveMode.Overwrite )

    val fleetLevelSummaryDF = validDF.groupBy("date", "fleet_id")
      .agg(
        //        count("trip_id") as "count",
        mean("safety_score") as "score_safety",
        mean("economy_score") as "score_economy",
        mean("maintainence_score") as "score_maintenance",
        mean("civilization_score") as "score_civilization",
        mean("overall_score") as "score_overall")
//      .withColumn("driver_id", lit(""))

    OutputRdbms.saveDailySummaryScore(paramsMap, fleetLevelSummaryDF, SaveMode.Append)
  }


  def getTripDriverRdd(paramsMap: Map[String, String], objectScoreDF: DataFrame): RDD[DailyScoreSummary] = {
    val motorcadeDbProps = DbPropsParser.getMySqlMotocadeDbProps(paramsMap)

    objectScoreDF.map(_.getAs[String]("trip_id")).mapPartitions{ part =>
      val motorcadeDbConn = DbManager.apply.getSingleConn(motorcadeDbProps)

      val tmp = part.toArray.flatMap { tripId =>
        //        val tripId = row.getAs[String]("trip_id")
        //        val safetyScore = row.getAs[Double]("safety_score")
        //        val ecoScore = row.getAs[Double]("economy_score")
        //        val mainScore = row.getAs[Double]("maintainence_score")
        //        val civiScore = row.getAs[Double]("civilization_score")
        //        val overallScore = row.getAs[Double]("overall_score")
        val res = OutputRdbms.getTripDriverInfo(motorcadeDbConn, tripId)
        if (res != null && res._3 != null)
          Some(DailyScoreSummary(tripId, ProcessIndicatorScore.getDateString(res._3), res._2, res._1))
        else None
      }

      motorcadeDbConn.close()
      tmp.toIterator
    }
  }

  def saveIndicatorSocre(updateSqlRdd: RDD[String], paramsMap: Map[String, String]): Unit = {
    val motorcadeDbProps = DbPropsParser.getMySqlMotocadeDbProps(paramsMap)

    updateSqlRdd.foreachPartition{ part =>
      val conn = DbManager.apply.getSingleConn(motorcadeDbProps)
      part.toList.foreach{ slqStr =>
        OutputRdbms.executeSqlStatement(conn, slqStr)
      }
      conn.close()
    }
  }


  @DeveloperApi
  def scoreIndicatorByMaxMinNormalize(driverIndicators: DataFrame) = {
    val scaler = new MinMaxScaler()
      .setInputCol("features")  // 包含一个指标在各个车辆上的值
      .setOutputCol("scaledFeatures") // 归一化后的指标值

    // Compute summary statistics and generate MinMaxScalerModel
    val scalerModel = scaler.fit(driverIndicators)

    // rescale each feature to range [min, max].
    val scaledData = scalerModel.transform(driverIndicators)
    println(s"Features scaled to range: [${scaler.getMin}, ${scaler.getMax}]")
    scaledData.select("features", "scaledFeatures")
  }

  // 依据正态分布进行打分
  @DeveloperApi
  def scoreIndicatorByNormalDis(diverIndicators: DataFrame) = {
    // E[X^2] - (E[X])^2
    // E[X^2] = sum_square_value / count  E[X] = sum_value / count
  }

  @DeveloperApi
  def computeScoreByMaxMinNormalize(indicatorValue: Double,
                                    minValue: Double,
                                    maxValue: Double,
                                    isPositive: Boolean,
                                    minScore: Double = 60,
                                    maxScore: Double = 98) = {

    val compressionFactor = (100 - maxScore) / 100
    val expandedMaxValue = maxValue * (1 + compressionFactor)
    val expandedMinValue = minValue * (1 - compressionFactor)

    val scaledValue = if(isPositive) {
      (indicatorValue - expandedMinValue) / (expandedMaxValue - expandedMinValue)
    } else {
      1 - (indicatorValue - expandedMinValue) / (expandedMaxValue - expandedMinValue)
    }

    (maxScore - minScore) * scaledValue + minScore
  }
}

object ProcessIndicatorScore {

  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_INDICATOR_SCORE

  case class DailyScoreSummary(trip_id: String, date: String, driver_id: String, fleet_id: String)

  val sdf = new SimpleDateFormat("yyyy-MM-dd")

  def getDateString(ts: Timestamp): String = {
    sdf.format(ts)
  }
}
