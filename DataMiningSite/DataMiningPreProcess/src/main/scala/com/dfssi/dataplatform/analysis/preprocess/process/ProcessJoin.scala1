package com.dfssi.dataplatform.analysis.preprocess.process

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}

import scala.collection.mutable
import scala.xml.Elem

class ProcessJoin extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID);
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl);
    // joinType One of: `inner`, `outer`, `left_outer`, `right_outer`, `leftsemi`.
    val joinType = getParamValue(paramsMap, "joinType");
    val firstId = getParamValue(paramsMap, "left");
    val secondId = getParamValue(paramsMap, "right");
    val firstDF = processContext.dataFrameMap(firstId);
    val secondDF = processContext.dataFrameMap(secondId);

    val leftKeyColName = getParamValue(paramsMap, "leftColName")
    val rightKeyColName = getParamValue(paramsMap, "rightColName")

    val resultDF = if(leftKeyColName == rightKeyColName) {
//      val renamedLeftDF = firstDF.withColumnRenamed(leftKeyColName, leftKeyColName+"_left")
      val renamedRightDF = secondDF.withColumnRenamed(rightKeyColName, rightKeyColName+"_right")

      firstDF
        .join(renamedRightDF, firstDF(leftKeyColName) === renamedRightDF(rightKeyColName+"_right"))
        .drop(rightKeyColName+"_right")
    } else {
      firstDF.join(secondDF, firstDF(leftKeyColName)
        === secondDF(rightKeyColName), joinType)
    }

    resultDF.show(5)

    processContext.dataFrameMap.put(id, resultDF);
  }
}

object ProcessJoin {
  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_JOIN;
}
