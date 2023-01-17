package com.dfssi.dataplatform.analysis.preprocess.process

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import com.dfssi.dataplatform.streaming.store.StreamingStoreTask

import scala.collection.mutable
import scala.xml.Elem

class ProcessKafkaToHive extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID);
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl);
    val configPath = getParamValue(paramsMap, "configPath");
    val nameNode = processContext.nameNode

    val streamingStoreTask: StreamingStoreTask = new StreamingStoreTask()

    streamingStoreTask.execute(nameNode, configPath)
  }
}

object ProcessKafkaToHive {
  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_KAFKATOHIVE;
}


