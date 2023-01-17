package com.dfssi.dataplatform.analysis.algorithm.linearregression

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext, DataVisualization}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.feature.VectorAssembler
import org.apache.spark.localMl.regression.{LinearRegression, LinearRegressionModel}
import org.apache.spark.localMl.util.Identifiable
import org.apache.spark.localMllib.linalg.Vectors

import scala.xml.Elem


class ProcessLinearRegression extends AbstractProcess with DataVisualization {

  import ProcessLinearRegression._

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)

    val inputIds = getInputs(defEl)
    val optionDF = processContext.dataFrameMap.get(inputIds.head)

    require(optionDF.nonEmpty)

    val paramsMap = extractSimpleParams(defEl)
    // necessary params
    val action = getParamValue(paramsMap, "action") // action: training or predicting
    val saveTo = getParamValue(paramsMap, "saveTo")

    action match {
      // 使用回归算法进行训练
      case "AUTO_TRAINING" =>
        val labelColName = getParamValue(paramsMap,"labelCol")
        val featureCols = paramsMap.get("featureCols") match {
          case Some(v) => v.split(",")
          case None =>
            paramsMap.get("nonFeatureCols") match {
              case Some(v) => optionDF.get.columns.diff(v.split(","))
              case None =>
                throw new NoSuchElementException("key not found, neither `featureCols` nor `nonFeatureCols`")
            }
        }
        // merge muliple feature cols into one column
        val (featureColName, parsedDF) = if (featureCols.length > 1) {
          (defaultFeatureColName, new VectorAssembler().setInputCols(featureCols)
            .setOutputCol(defaultFeatureColName)
            .transform(optionDF.get))
        }
        else {
          (featureCols.head, optionDF.get)
        }

        val linearRegression = new LinearRegression()
                .setFeaturesCol(featureColName)
                .setLabelCol(labelColName)

        // optional params
        paramsMap.get("maxIter") match {
          case Some(maxIter) => linearRegression.setMaxIter(maxIter.toInt)
          case None =>
        }
        paramsMap.get("regParam") match {
          case Some(regularFactor) => linearRegression.setRegParam(regularFactor.toDouble)
          case None =>
        }
        paramsMap.get("elasticNetParam") match {
          case Some(elasticNetParam) => linearRegression.setElasticNetParam(elasticNetParam.toDouble)
          case None =>
        }

        // Fit the model
        val lrModel = linearRegression.fit(parsedDF)

        // Print the coefficients and intercept for linear regression
        println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

        // Summarize the model over the training set and print out some metrics
        val trainingSummary = lrModel.summary
        println(s"numIterations: ${trainingSummary.totalIterations}")
        println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
        trainingSummary.residuals.show()
        println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
        println(s"r2: ${trainingSummary.r2}")

        deleteHdfsDir(processContext.getSparkContext, processContext.appPath + "/" + saveTo)
        lrModel.save(processContext.appPath + "/" + saveTo)

      // 人工设置权重生成回归模型
      case "MANU_TRAINING" =>
        println("mannually train")
        val intercept = getParamValue(paramsMap, "intercept").toDouble
        val coefficients = getParamValue(paramsMap, "coefficients").split(",").map(_.toDouble)

        val manuModel = new LinearRegressionModel(Identifiable.randomUID("linReg"), Vectors.dense(coefficients), intercept)

        deleteHdfsDir(processContext.getSparkContext, processContext.appPath + "/" + saveTo)
        manuModel.save(processContext.appPath + "/" + saveTo)

      // 使用预训练的模型进行预测
      case "PREDICTING" =>
        println("lr predicting")
        val featureCols = paramsMap.get("featureCols") match {
          case Some(v) => v.split(",")
          case None =>
            paramsMap.get("nonFeatureCols") match {
              case Some(v) => optionDF.get.columns.diff(v.split(","))
              case None =>
                throw new NoSuchElementException("key not found, neither `featureCols` nor `nonFeatureCols`")
            }
        }
        // merge muliple feature cols into one column
        val (featureColName, parsedDF) = if (featureCols.length > 1) {
          (defaultFeatureColName, new VectorAssembler().setInputCols(featureCols)
            .setOutputCol(defaultFeatureColName)
            .transform(optionDF.get))
        }
        else {
          (featureCols.head, optionDF.get)
        }

        val modelPath = processContext.appPath+"/"+saveTo
        val model = LinearRegressionModel.load(modelPath)

        val resultDF = model
          .setFeaturesCol(featureColName)
          .setPredictionCol(defaultPredictColName)
          .transform(parsedDF)

        resultDF.show()

        val visualDF = visualizate(resultDF,2,defaultFeatureColName,"visualCol")

        visualDF.show()

        processContext.dataFrameMap.put(id, visualDF)

      case _ =>
        throw new UnsupportedOperationException("ProcessLinearRegression: Unsupported action param!")
    }
  }
}

object ProcessLinearRegression {

  val processType = "AlgorithmLinearRegression"

  val defaultFeatureColName = "LinearReg_Feature"

  val defaultPredictColName = "Prediction"

  val defaultProbColName = "LinearReg_Probability"
}