package com.dfssi.dataplatform.analysis.preprocess.process.fuel

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.fuel.{FuelConfig, FuelDataAnalysisFromKafka}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}

import scala.collection.{JavaConversions, mutable}
import scala.xml.Elem

/**
  * Description:
  *     实时油耗
  * @author LiXiaoCong
  * @version 2018/3/1 19:05 
  */
class ProcessKafkaToFuel extends AbstractProcess {

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

    val consumeGroup = inputMap.getOrElse("consumeGroup", "realtime-fuels-group")
    val offset = inputMap.getOrElse("offset", "smallest")

    //读取处理配置
    val appName = processMap.getOrElse("appName", "realtime-fuels")

    val interval = processMap.getOrElse("interval", "300").toInt
    val partition = processMap.getOrElse("partition", "8").toInt

    logInfo(" 任务启动配置如下 ： ")
    logInfo(s" 		appName     ：  $appName ")
    logInfo(s" 		groupId     ：  $consumeGroup ")
    logInfo(s" 		topic       ：  $topicSet ")
    logInfo(s" 		offset      ：  $offset ")
    logInfo(s" 		interval    ：  $interval ")
    logInfo(s" 		brokerList  ：  $brokerList ")
    logInfo(s" 	  partitions  ：  $partition ")

    val config = JavaConversions.mapAsJavaMap(processMap.filter(_._1.startsWith("fuel.")))

    val fuelConf = new FuelConfig(config, true)
    val app = new FuelDataAnalysisFromKafka(fuelConf)
    app.function(appName, consumeGroup, brokerList, interval, topicSet, offset, partition)
  }
}

object ProcessKafkaToFuel {
  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_KAFKATOFUEL
}


