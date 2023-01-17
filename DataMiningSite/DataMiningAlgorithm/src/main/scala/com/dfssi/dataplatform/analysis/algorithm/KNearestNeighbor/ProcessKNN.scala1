package com.dfssi.dataplatform.analysis.algorithm.KNearestNeighbor

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.localMl.clustering.{KMeans, KMeansModel}
import org.apache.spark.localMl.feature.VectorAssembler

import scala.collection.mutable
import scala.xml.Elem

class ProcessKNN extends AbstractProcess {
    import ProcessKNN._

    override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
        val id = XmlUtils.getAttrValue(defEl,SparkDefTag.SPARK_DEF_ATTR_TAG_ID)

        val inputIds = getInputs(defEl)
        val optionDF = processContext.dataFrameMap.get(inputIds(0))
        require(optionDF.nonEmpty)



    }
}

object ProcessKNN {
    val processType = "AlgorithmKNN"

    val defaultFeatureColName = "KNN_Feature"

    val defaultPredictColName = "Prediction"
}

