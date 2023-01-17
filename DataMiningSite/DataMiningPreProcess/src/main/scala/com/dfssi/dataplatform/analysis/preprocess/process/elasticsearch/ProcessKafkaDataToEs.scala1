package com.dfssi.dataplatform.analysis.preprocess.process.elasticsearch

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.es.TerminalDataToEsFromKafka
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}

import scala.collection.mutable
import scala.xml.Elem

class ProcessKafkaDataToEs extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)
    val configPath = getParamValue(paramsMap, "configPath")
    val nameNode = processContext.nameNode

    new TerminalDataToEsFromKafka().execute(nameNode, configPath)
  }
}

object ProcessKafkaDataToEs {
  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_KAFKADATATOES
}




