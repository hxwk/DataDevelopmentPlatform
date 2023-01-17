package com.dfssi.dataplatform.analysis.algorithm.dbscan

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.feature.VectorAssembler
import org.apache.spark.localMllib.clustering.dbscan._

import scala.collection.mutable
import scala.xml.Elem

class ProcessDBSCAN extends AbstractProcess{
    import ProcessDBSCAN._

    override def execute(processContext:ProcessContext,defEl:Elem,sparkTaskDefEl:Elem):Unit = {
        val id = XmlUtils.getAttrValue(defEl,SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
        val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)
        val action: String = getParamValue(paramsMap, "action")

        val inputIds:Array[String] = getInputs(defEl)
        val optionDF = processContext.dataFrameMap.get(inputIds(0))

        if(optionDF.isEmpty){
            return
        }

        val saveTo: String = getParamValue(paramsMap, "saveTo")

        action match{
            case "AUTO_TRAINING"=>
                println("training DBSCAN model..")
                val eps:Double = getParamValue(paramsMap, "eps").toDouble
                val minPts:Int = getParamValue(paramsMap, "minPts").toInt
                val maxPtsPerPart:Int = getParamValue(paramsMap, "maxPtsPerPart").toInt

                val featureCols = paramsMap.get("featureCols") match{
                    case Some(v) => v.split(",")
                    case None=>
                        paramsMap.get("nonFeatureCols") match{
                            case Some(v) => optionDF.get.columns.diff(v.split(","))
                            case None=>
                                throw new NoSuchElementException("key not found, neither `featureCols` nor `nonFeatureCols`")
                        }
                }

                val (featureColName, parseDF) = if(featureCols.length>1){
                    (ProcessDBSCAN.defaultFeatureColName,new VectorAssembler().setInputCols(featureCols)
                    .setOutputCol(ProcessDBSCAN.defaultFeatureColName)
                    .transform(optionDF.get))
                }else{
                    (featureCols.head,optionDF.get)
                }

            case _=>
                throw new UnsupportedOperationException("ProcessKMeans: Unsupported action param!")

        }


    }
}

object ProcessDBSCAN {
    val processType = "AlgorithmDBSCAN"

    val defaultFeatureColName = "DBSCAN_Feature"

    val defaultPredictColName = "Prediction"
}


