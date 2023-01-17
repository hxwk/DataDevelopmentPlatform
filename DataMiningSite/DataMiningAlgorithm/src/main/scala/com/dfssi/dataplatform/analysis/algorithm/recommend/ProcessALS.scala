package com.dfssi.dataplatform.analysis.algorithm.recommend

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, DataVisualization, ProcessContext}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.clustering.{KMeans, KMeansModel}
import org.apache.spark.localMl.feature.VectorAssembler
import org.apache.spark.localMl.recommendation.{ALS, ALSModel}
import org.apache.spark.sql.DataFrame

import scala.collection.mutable
import scala.xml.Elem

class ProcessALS extends AbstractProcess {
	import ProcessALS._


	override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem):Unit = {
		val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
		val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)
		val action: String = getParamValue(paramsMap, "action")

		val inputIds: Array[String] = getInputs(defEl)
		val optionDF = processContext.dataFrameMap.get(inputIds(0))

		if (optionDF.isEmpty) {
			return
		}

		optionDF.get.show(100)
		val parsedDF = optionDF.get

		// necessary common params
		val saveTo: String = getParamValue(paramsMap, "saveTo")
		val userCol:String = getParamValue(paramsMap, "userCol")
		val itemCol:String = getParamValue(paramsMap, "itemCol")

		action match {
			case "AUTO_TRAINING" =>
				println("training ALS model")

				val prefixPath = processContext.appPath
				println("---------ProcessContext.appPath---------",processContext.appPath)
				val checkPointDir = prefixPath +"alsCheckPoint_"+id

				deleteHdfsDir(processContext.getSparkContext, saveTo)
				deleteHdfsDir(processContext.getSparkContext, checkPointDir)

				val ratingCol:String = getParamValue(paramsMap, "ratingCol")
				processContext.sparkContext.setCheckpointDir(checkPointDir)

				val als = new ALS()
						.setUserCol(userCol)
						.setItemCol(itemCol)
						.setRatingCol(ratingCol)

				paramsMap.get("alpha") match {
					case Some(alpha) => als.setAlpha(alpha.toDouble)
					case None =>
				}

				paramsMap.get("implicitPrefs") match {
					case Some(implicitPrefs) => als.setImplicitPrefs(implicitPrefs.toBoolean)
					case None =>
				}

				paramsMap.get("rank") match {
					case Some(rank) => als.setRank(rank.toInt)
					case None =>
				}

				paramsMap.get("regParam") match {
					case Some(regParam) => als.setRegParam(regParam.toDouble)
					case None =>
				}

				paramsMap.get("nonnegative") match {
					case Some(nonnegative) => als.setNonnegative(nonnegative.toBoolean)
					case None =>
				}

				paramsMap.get("seed") match {
					case Some(seed) => als.setSeed(seed.toLong)
					case None =>
				}

				paramsMap.get("maxIter") match {
					case Some(maxIter) => als.setMaxIter(maxIter.toInt)
					case None =>
				}


				// fit a model
				val model = als.fit(parsedDF)

				model.save(saveTo)
				println(" Save to "+saveTo)
				processContext.dataFrameMap.put(id,parsedDF)


			case "PREDICTING" =>
				// load model
				val modelPath = saveTo

				val model = ALSModel.load(modelPath)

				val resultDF = model
						.setUserCol(userCol)
        				.setItemCol(itemCol)
						.setPredictionCol(ProcessALS.defaultPredictColName)
						.transform(parsedDF)

				resultDF.show()

				processContext.dataFrameMap.put(id, resultDF)

			case _ =>
				throw new UnsupportedOperationException("ProcessALS: Unsupported action param!")
		}


	}
}

object ProcessALS {
	val processType: String = "AlgorithmALS"

	val defaultFeatureColName = "ALS_Feature"

	val defaultPredictColName = "Prediction"
}
