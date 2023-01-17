package com.dfssi.dataplatform.analysis.algorithm.randomforest

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.localMl.feature.VectorAssembler
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}

import scala.collection.mutable
import scala.xml.Elem

class ProcessRFSelector extends AbstractProcess {

    import ProcessRFSelector._

    override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
        val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)

        val inputIds = getInputs(defEl)
        val optionDF = processContext.dataFrameMap.get(inputIds.head)
        require(optionDF.nonEmpty)

        val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)
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
        } else {
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
        println(model.featureImportances.toDense)



        val featureImportance = model.featureImportances.toArray
        val singleRow = Row.fromSeq(featureImportance)
        val tmpRdd = processContext.sparkContext.makeRDD(List(singleRow))
        val fields = featureCols.map(colName=>StructField(colName, DoubleType, nullable=true))
        val schema = StructType(fields)
        val sqlContext = new SQLContext(processContext.sparkContext)
        val parameterDF = sqlContext.createDataFrame(tmpRdd, schema)

        parameterDF.show()

        processContext.dataFrameMap.put(id, parameterDF)



    }
}

object ProcessRFSelector {
    val processType = "FeatureSelectRF"

    val defaultFeatureColName = "RandomForest_Feature"

    val defaultPredictColName = "Prediction"

    val defaultProbColName = "RandomForest_Probability"
}
