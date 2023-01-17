package com.dfssi.dataplatform.analysis.algorithm.kmeans

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, DataVisualization, ProcessContext}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.clustering.{KMeans, KMeansModel}
import org.apache.spark.localMl.feature.VectorAssembler

import scala.collection.mutable
import scala.xml.Elem

class ProcessKMeans extends AbstractProcess with DataVisualization{
  import ProcessKMeans._

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)
    val action: String = getParamValue(paramsMap, "action")

    val inputIds: Array[String] = getInputs(defEl)
    val optionDF = processContext.dataFrameMap.get(inputIds(0))

    if (optionDF.isEmpty) {
      return
    }

    optionDF.get.show(100)

    // necessary common params
    val saveTo: String = getParamValue(paramsMap, "saveTo")

    action match {
      case "AUTO_TRAINING" =>
        println("training kmeans model")

        val k: Int = getParamValue(paramsMap, "k").toInt
        // get feature-columns names or non feature-column names to exclude
        val featureCols = paramsMap.get("featureCols") match {
          case Some(v) => v.split(",")
          case None =>
            paramsMap.get("nonFeatureCols") match {
              case Some(v) => optionDF.get.columns.diff(v.split(","))
              case None =>
                throw new NoSuchElementException("key not found, neither `featureCols` nor `nonFeatureCols`")
            }
        }

        // merge muliple feature cols into one vector column
        val (featureColName, parsedDF) = if (featureCols.length > 1) {
          (ProcessKMeans.defaultFeatureColName, new VectorAssembler().setInputCols(featureCols)
            .setOutputCol(ProcessKMeans.defaultFeatureColName)
            .transform(optionDF.get))
        } else {
          (featureCols.head, optionDF.get)
        }

        val kMeans = new KMeans()
          .setK(k)
          .setFeaturesCol(featureColName)

        // optional params
        paramsMap.get("maxIter") match {
          case Some(numIterations) => kMeans.setMaxIter(numIterations.toInt)
          case None =>
        }

        paramsMap.get("seed") match {
            case Some(seed) => kMeans.setSeed(seed.toLong)
            case None =>
        }

        paramsMap.get("tol") match {
            case Some(tol) => kMeans.setTol(tol.toDouble)
            case None =>
        }

        // fit a model
        val model = kMeans.fit(parsedDF)

        //        model.clusterCenters.foreach(println)
        deleteHdfsDir(processContext.getSparkContext, processContext.appPath + "/" + saveTo)
        model.save(processContext.appPath + "/" + saveTo)
        println(" Save to "+processContext.appPath+"/"+saveTo)

      case "PREDICTING" =>
        val featureCols = getParamValue(paramsMap, "featureCols").split(",")
        // load model
        val modelPath = processContext.appPath+"/"+saveTo

        val model = KMeansModel.load(modelPath)
        println("-------"+model.getFeaturesCol+"---------")
        val (featureColName, parsedDF) = if (featureCols.length > 1) {
          (ProcessKMeans.defaultFeatureColName, new VectorAssembler()
            .setInputCols(featureCols)
            .setOutputCol(ProcessKMeans.defaultFeatureColName)
            .transform(optionDF.get))
        } else {
          (featureCols.head, optionDF.get)
        }

        val resultDF = model
          .setFeaturesCol(featureColName)
          .setPredictionCol(ProcessKMeans.defaultPredictColName)
          .transform(parsedDF)

        resultDF.show()

        val visualDF = visualizate(resultDF,2,defaultFeatureColName,"visualCol")

        visualDF.show()


        processContext.dataFrameMap.put(id, visualDF)

      case _ =>
        throw new UnsupportedOperationException("ProcessKMeans: Unsupported action param!")
    }
  }
}


object ProcessKMeans {

  val processType: String = "AlgorithmKMeans"

  val defaultFeatureColName = "KMeans_Feature"

  val defaultPredictColName = "Prediction"

}
