package com.dfssi.dataplatform.analysis

import com.dfssi.dataplatform.analysis.config.XmlReader
import kafka.serializer.StringDecoder
import org.apache.spark.Logging
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.KafkaManager

import scala.collection.mutable
import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/27 20:47 
  */
class KafkaStream (topicSet: Set[String],
                   kafkaParams: Map[String, String]) extends Serializable with Logging{
  val kafkaManager = new KafkaManager(kafkaParams, false)

  def createDirectStreamWithOffsetCheck(ssc: StreamingContext): InputDStream[(String, String)] = {
    kafkaManager.createDirectStreamWithOffsetCheck[String, String, StringDecoder, StringDecoder](ssc, topicSet)
  }
}

object KafkaStream extends Logging{

  def buildFromXmlElem(inputElem: Elem, kafkaConfig: Map[String, String] = Map.empty[String, String]): KafkaStream ={
    val topics = XmlReader.getAttr(inputElem, "topics")
    require(topics != null, "topics不能为空")

    val kafkaParams = new mutable.HashMap[String, String]()

    val params = XmlReader.getNextSingleSubElem(inputElem, "params")
    for(param <- params \ "param"){
      val elem = param.asInstanceOf[Elem]
      kafkaParams.put(XmlReader.getAttr(elem, "name"),
        XmlReader.getAttr(elem, "value"))
    }

    //优先使用环境配置
    kafkaParams ++= kafkaConfig

    logInfo(s"kafka的topics为：${topics}, 连接参数为：\n\t ${kafkaParams}")
    new KafkaStream(topics.split(",").toSet, kafkaParams.toMap)
  }
}