package com.dfssi.dataplatform.analysis.preprocess.process

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.sql.functions.col

import scala.collection.mutable
import scala.xml.Elem

class ProcessSelect extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID);
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl);
    var inputIds = getInputs(defEl);
    val inputDF = processContext.dataFrameMap(inputIds(0));
    val colNames = getParamValue(paramsMap, "colNames").split(",");
    val selectedCols = colNames.map(colName => {
      col(colName)
    });
    val resultDF = inputDF.select(selectedCols: _*);

    processContext.dataFrameMap.put(id, resultDF);
  }
}

object ProcessSelect {
  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_SELECT
}
