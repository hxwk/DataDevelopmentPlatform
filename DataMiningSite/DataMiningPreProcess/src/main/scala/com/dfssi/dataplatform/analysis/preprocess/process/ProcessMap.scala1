package com.dfssi.dataplatform.analysis.preprocess.process

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

import scala.collection.mutable
import scala.xml.Elem

class ProcessMap extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID);
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl);
    var inputIds: Array[String] = getInputs(defEl);
    val inputDF: DataFrame = processContext.dataFrameMap(inputIds(0));

    val srcColName = getParamValue(paramsMap, "sourceColName");
    val destColName = getParamValue(paramsMap, "destColName");
    val initialColumnNames = for (elem <- inputDF.columns) yield elem;
    val renamedColumns = initialColumnNames.map(name => {
      if (srcColName.equals(name)) {
        col(name).as(destColName)
      } else {
        col(name)
      }
    });

    val resultDF = inputDF.select(renamedColumns: _*)

    processContext.dataFrameMap.put(id, resultDF);
  }
}

object ProcessMap {
  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_MAP
}

