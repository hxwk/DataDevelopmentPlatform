package com.dfssi.dataplatform.analysis.oozie

import com.dfssi.dataplatform.analysis.es.Terminal0200ToEsFromKafka

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/1 15:11 
  */
object Terminal0200ToEsFromKafkaForOozie extends OozieAppTrait {

  val rootTag = "streaming-task-def"

  def main(args: Array[String]): Unit = {

    val nameNode = args(0)
    val configPath = args(1)

    val config = loadConfigFromHdfs(nameNode, configPath, rootTag)

    //读取输入配置
    val inputConfig = config.getInputsConfig().get(0)

    val topicStr = inputConfig.getConfigItemValue("topics")
    require(topicStr != null, "topics的配置不能为空。")
    val topicSet = topicStr.split(",").toSet

    val brokerList = inputConfig.getConfigItemValue("brokerList")
    require(brokerList != null, "brokerList的配置不能为空。")

    val consumeGroup = inputConfig.getConfigItemValue("consumeGroup", "ESstreaming-11")
    val offset = inputConfig.getConfigItemValue("offset", "smallest")

    //读取处理配置
    val processConfig = config.getProcesssConfig.get(0)
    val appName = processConfig.getConfigItemValue("appName", "Terminal0200ToEsFromKafka")

    val interval = processConfig.getConfigItemValue("interval", "300").toInt
    val partition = processConfig.getConfigItemValue("partition", "8").toInt

    val esNodes = inputConfig.getConfigItemValue("esNodes")
    require(esNodes != null, "esNodes的配置不能为空。")

    val esClusterName = inputConfig.getConfigItemValue("esClusterName")
    require(esClusterName != null, "esClusterName的配置不能为空。")


    logInfo(" 任务启动配置如下 ： ")
    logInfo(s" 		appName     ：  $appName ")
    logInfo(s" 		topics       ：  $topicSet ")
    logInfo(s" 		offset      ：  $offset ")
    logInfo(s" 		interval    ：  $interval ")
    logInfo(s" 		brokerList  ：  $brokerList ")
    logInfo(s" 	  consumeGroup  ：  $consumeGroup ")
    logInfo(s" 	  esNodes  ：  $esNodes ")
    logInfo(s" 	  esClusterName  ：  $esClusterName ")
    logInfo(s" 	  partition  ：  $partition ")

    new Terminal0200ToEsFromKafka().start(appName, interval, topicSet, brokerList, offset,
      consumeGroup, esNodes, esClusterName, partition)


  }

}
