package com.dfssi.dataplatform.analysis.algorithm.logisticregression

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext, DataVisualization}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.localMl.feature.VectorAssembler
import org.apache.spark.localMl.util.Identifiable
import org.apache.spark.localMllib.linalg.Vectors

import scala.xml.Elem

class ProcessLogisticRegression extends AbstractProcess with DataVisualization {

  import ProcessLogisticRegression._

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)

    val inputIds = getInputs(defEl)
    val optionDF = processContext.dataFrameMap.get(inputIds.head)
    require(optionDF.nonEmpty)

    val paramsMap = extractSimpleParams(defEl)
    val action = getParamValue(paramsMap, "action")
    val saveTo = getParamValue(paramsMap, "saveTo")

    action match {
      // 使用回归算法进行训练
      case "AUTO_TRAINING" =>
        val labelCol = getParamValue(paramsMap,"labelCol")
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

        // Fit the model
        val logisticRegression = new LogisticRegression()
                .setFeaturesCol(featureColName)
                .setLabelCol(labelCol)

        // optional params
        paramsMap.get("maxIter") match {
          case Some(numIterations) => logisticRegression.setMaxIter(numIterations.toInt)
          case None =>
        }
        paramsMap.get("elasticNetParam") match {
          case Some(elasticNetParam) => logisticRegression.setRegParam(elasticNetParam.toDouble)
          case None =>
        }

        paramsMap.get("tol") match {
          case Some(tol) => logisticRegression.setTol(tol.toDouble)
          case None =>
        }

        paramsMap.get("threshold") match {
          case Some(threshold) => logisticRegression.setThreshold(threshold.toDouble)
          case None =>
        }

        paramsMap.get("regParam") match {
          case Some(regParam) => logisticRegression.setRegParam(regParam.toDouble)
          case None =>
        }

        // fit a model
        val lrModel = logisticRegression.fit(parsedDF)

        // Print the coefficients and intercept for linear regression
        println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

        // Summarize the model over the training set and print out some metrics
        val trainingSummary = lrModel.summary
        println(s"numIterations: ${trainingSummary.totalIterations}")
        println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")

        deleteHdfsDir(processContext.getSparkContext, processContext.appPath  + saveTo)
        lrModel.save(processContext.appPath  + saveTo)

      // 人工设置权重生成回归模型
      case "MANU_TRAINING" =>
        println("mannually train")
        val intercept = getParamValue(paramsMap, "intercept").toDouble
        val coefficients = getParamValue(paramsMap, "coefficients").split(",").map(_.toDouble)

        val manuModel = new LogisticRegressionModel(Identifiable.randomUID("logReg"),
          Vectors.dense(coefficients), intercept)

        deleteHdfsDir(processContext.getSparkContext, processContext.appPath  + saveTo)
        manuModel.save(processContext.appPath  + saveTo)

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

        val modelPath = processContext.appPath+saveTo
        val model = LogisticRegressionModel.load(modelPath)

        // merge muliple feature cols into one column
        val (featureColName, parsedDF) = if (featureCols.length > 1) {
          (defaultFeatureColName,
            new VectorAssembler().setInputCols(featureCols)
              .setOutputCol(defaultFeatureColName)
              .transform(optionDF.get))
        }
        else {
          (featureCols.head, optionDF.get)
        }

        val resultDF = model
                .setFeaturesCol(featureColName)
                .setProbabilityCol(defaultProbColName)
                .setPredictionCol(defaultPredictColName)
                .transform(parsedDF)

        resultDF.show()

        val visualDF = visualizate(resultDF,2,defaultFeatureColName,"visualCol")

        visualDF.show()

        processContext.dataFrameMap.put(id, visualDF)

      case _ =>
        throw new UnsupportedOperationException("ProcessLogisticRegression: Unsupported action param!")
    }


  }
}

object ProcessLogisticRegression {
  val processType = "AlgorithmLogisticRegression"

  val defaultFeatureColName = "LogisticReg_Feature"

  val defaultPredictColName = "Prediction"

  val defaultProbColName = "LogisticReg_Probability"
}