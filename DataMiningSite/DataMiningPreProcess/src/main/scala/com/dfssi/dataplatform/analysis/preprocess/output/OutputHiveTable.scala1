package com.dfssi.dataplatform.analysis.preprocess.output

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}

import scala.collection.mutable
import scala.xml.Elem


class OutputHiveTable extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl);
    var inputIds: Array[String] = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_INPUTIDS).split(",");
    val outputDF = processContext.dataFrameMap.get(inputIds(0));
    if (!outputDF.isEmpty) {
      outputDF.get.insertInto(getSchemaName(paramsMap) + "." + getTableName(paramsMap));
    }
  }
}

object OutputHiveTable {
  val processType: String = ProcessFactory.PROCESS_NAME_OUTPUT_HIVE_TABLE
}
