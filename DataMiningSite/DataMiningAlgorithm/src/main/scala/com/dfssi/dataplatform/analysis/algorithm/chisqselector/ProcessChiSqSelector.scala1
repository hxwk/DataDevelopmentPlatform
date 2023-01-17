package com.dfssi.dataplatform.analysis.algorithm.chisqselector

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.feature.{ChiSqSelector, VectorAssembler}
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.xml.Elem

class ProcessChiSqSelector extends AbstractProcess {

    import ProcessChiSqSelector._

    override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
        val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)

        val inputIds = getInputs(defEl)
        val optionDF = processContext.dataFrameMap.get(inputIds.head)
        require(optionDF.nonEmpty)

        val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)
        val labelColName = getParamValue(paramsMap, "labelCol")
        val numTopFeatures = getParamValue(paramsMap, "numTopFeatures").toInt
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

        val selector = new ChiSqSelector()
                .setFeaturesCol(featureColName)
                .setLabelCol(labelColName)
                .setOutputCol("selectedFeatures")
                .setNumTopFeatures(numTopFeatures)

        val model = selector.fit(parsedDF)
        val resultDF = model.transform(parsedDF)
        val featureSelectedIndice = model.selectedFeatures
        val tmpArrayBuffer = new ArrayBuffer[String]()
        for(index<-featureSelectedIndice){
            tmpArrayBuffer+=featureCols(index)
        }

        val finalDF = resultDF.select(labelColName, tmpArrayBuffer:_*)
//        println(" -----------The Selected Features-----------")
        finalDF.show()
        processContext.dataFrameMap.put(id, finalDF)
    }
}

object ProcessChiSqSelector {
    val processType = "FeatureSelectRF"

    val defaultFeatureColName = "ChiSqSelector_Feature"

    val defaultPredictColName = "Prediction"

}
