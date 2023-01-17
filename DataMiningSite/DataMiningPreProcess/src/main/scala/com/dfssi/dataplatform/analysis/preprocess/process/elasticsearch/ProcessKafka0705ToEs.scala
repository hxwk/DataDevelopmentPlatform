package com.dfssi.dataplatform.analysis.preprocess.process.elasticsearch

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.es.Terminal0705ToEsFromKafka
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}

import scala.collection.mutable
import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/1 19:25 
  */
class ProcessKafka0705ToEs extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {

    //读取数据输入相关参数
    val inputEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUTS)
    val inputs = (inputEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUT).toArray
    val inputMap: mutable.Map[String, String] = extractSimpleParams(inputs(0).asInstanceOf[Elem])

    //读取数据处理相关的参数
    val processMap: mutable.Map[String, String] = extractSimpleParams(defEl)

    val topicStr = inputMap("topics")
    require(topicStr != null, "topics的配置不能为空。")
    val topicSet = topicStr.split(",").toSet

    val brokerList = inputMap("brokerList")
    require(brokerList != null, "brokerList的配置不能为空。")

    val consumeGroup = inputMap.getOrElse("consumeGroup", "Terminal0705ToEsFromKafka")
    val offset = inputMap.getOrElse("offset", "smallest")

    //读取处理配置
    val appName = processMap.getOrElse("appName", "Terminal0705ToEsFromKafka")

    val interval = processMap.getOrElse("interval", "300").toInt
    val partition = processMap.getOrElse("partition", "8").toInt

    val esNodes = processMap("esNodes")
    require(esNodes != null, "esNodes的配置不能为空。")

    val esClusterName = processMap("esClusterName")
    require(esClusterName != null, "esClusterName的配置不能为空。")

    logInfo(" 任务启动配置如下 ： ")
    logInfo(s" 		appName     ：  $appName ")
    logInfo(s" 		topics      ：  $topicSet ")
    logInfo(s" 		offset      ：  $offset ")
    logInfo(s" 		interval    ：  $interval ")
    logInfo(s" 		brokerList  ：  $brokerList ")
    logInfo(s" 	  consumeGroup  ：  $consumeGroup ")
    logInfo(s" 	  esNodes       ：  $esNodes ")
    logInfo(s" 	  esClusterName ：  $esClusterName ")
    logInfo(s" 	  partition     ：  $partition ")

    val terminal0705ToEsFromKafka = new Terminal0705ToEsFromKafka()
    terminal0705ToEsFromKafka.start(appName, interval, topicSet, brokerList, offset,
      consumeGroup, esNodes, esClusterName, partition)
  }
}

object ProcessKafka0705ToEs{
  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_KAFKA0705TOES
}
