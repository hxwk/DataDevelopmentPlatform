package com.dfssi.dataplatform.analysis.preprocess.input

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.sql.DataFrame

import scala.collection.mutable
import scala.xml.Elem

class InputHiveTable extends AbstractProcess {
  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID);
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl);
    val inputDF: DataFrame = processContext.hiveContext.sql("select * from " + getSchemaName(paramsMap) + "" +
      "." + getTableName(paramsMap));

    processContext.dataFrameMap.put(id, inputDF);
  }
}

object InputHiveTable {
  val processType: String = ProcessFactory.PROCESS_NAME_INPUT_HIVE_TABLE;
}
