package com.dfssi.dataplatform.analysis.algorithm.randomforest

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext, DataVisualization}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.localMl.feature.VectorAssembler

import scala.collection.mutable
import scala.xml.Elem

class ProcessRF extends AbstractProcess with DataVisualization {

  import ProcessRF._

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)

    val inputIds = getInputs(defEl)
    val optionDF = processContext.dataFrameMap.get(inputIds.head)
    require(optionDF.nonEmpty)

    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)
    val action = getParamValue(paramsMap, "action")
    val saveTo: String = getParamValue(paramsMap, "saveTo")

    action match {
      case "AUTO_TRAINING" =>
        val labelColName = getParamValue(paramsMap, "labelCol")
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
        val (featureColName, parsedDF) = if(featureCols.length > 1) {
          (defaultFeatureColName, new VectorAssembler()
              .setInputCols(featureCols)
              .setOutputCol(defaultFeatureColName)
              .transform(optionDF.get))
        }
        else {
          	(featureCols.head, optionDF.get)
        }

        val classifier = new RandomForestClassifier()
            .setLabelCol(labelColName)
            .setFeaturesCol(featureColName)
            .setPredictionCol(defaultPredictColName)

        // optional params
        paramsMap.get("impurity") match {
          	case Some(v) => classifier.setImpurity(v)
          	case None =>
        }
        paramsMap.get("maxDepth") match {
		  	case Some(v) => classifier.setMaxDepth(v.toInt)
		  	case None =>
        }
        paramsMap.get("maxBins") match {
          	case Some(v) => classifier.setMaxBins(v.toInt)
          	case None =>
        }

        paramsMap.get("minInstancesPerNode") match {
            case Some(v) => classifier.setMinInstancesPerNode(v.toInt)
            case None =>
        }

        paramsMap.get("seed") match {
            case Some(v) => classifier.setSeed(v.toLong)
            case None =>
        }

	  	paramsMap.get("subsamplingRate") match {
			case Some(v) => classifier.setSubsamplingRate(v.toDouble)
			case None =>
		}

	  	paramsMap.get("numTrees") match {
		  	case Some(v) => classifier.setNumTrees(v.toInt)
		  	case None =>
	  	}

        // fit a model
        val model = classifier.fit(parsedDF)
	  	println("---------featureImportances----------")
        println(model.featureImportances)

        deleteHdfsDir(processContext.getSparkContext, processContext.appPath + "/" + saveTo)
        model.save(processContext.appPath + "/" + saveTo)


//      case "MANU_TRAINING" =>


      case "PREDICTING" =>
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
        val (featureColName, parsedDF) = if(featureCols.length > 1) {
          (defaultFeatureColName,
            new VectorAssembler().setInputCols(featureCols)
              .setOutputCol(defaultFeatureColName)
              .transform(optionDF.get))
        }
        else {
          (featureCols.head, optionDF.get)
        }

	  	val modelPath = processContext.appPath+"/"+saveTo

        val resultDF = RandomForestClassificationModel.load(modelPath)
          .setFeaturesCol(featureColName)
          .setProbabilityCol(defaultProbColName)
          .setPredictionCol(defaultPredictColName)
          .transform(parsedDF)

        resultDF.show()

        val visualDF = visualizate(resultDF,2,defaultFeatureColName,"visualCol")

        visualDF.show()

        processContext.dataFrameMap.put(id, visualDF)
    }
  }
}

object ProcessRF {
  val processType = "AlgorithmRF"

  val defaultFeatureColName = "RandomForest_Feature"

  val defaultPredictColName = "Prediction"

  val defaultProbColName = "RandomForest_Probability"
}