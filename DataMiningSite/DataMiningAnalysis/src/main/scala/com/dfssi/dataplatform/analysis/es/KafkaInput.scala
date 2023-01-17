package com.dfssi.dataplatform.analysis.es

import java.util.UUID

import com.alibaba.fastjson.TypeReference
import com.dfssi.dataplatform.analysis.config.XmlReader
import kafka.serializer.StringDecoder
import org.apache.spark.Logging
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka.KafkaManager

import scala.collection.mutable
import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/8 13:21 
  */
class KafkaInput(topicSet: Set[String],
                 kafkaParams: Map[String, String]) extends Serializable with Logging{
  val kafkaManager = new KafkaManager(kafkaParams, false)

  def createDirectStreamWithOffsetCheck(ssc: StreamingContext): InputDStream[(String, String)] = {
    kafkaManager.createDirectStreamWithOffsetCheck[String, String, StringDecoder, StringDecoder](ssc, topicSet)
  }

  def executeInput(idField: String,
                   inputDStream: InputDStream[(String, String)]): DStream[java.util.Map[String, Object]] ={
    inputDStream.map(kv =>{
      var record: java.util.Map[String, Object] = null
      try {
       record = com.alibaba.fastjson.JSON.parseObject(kv._2,
                 new TypeReference[java.util.Map[String, Object]]() {})

        if(!record.containsKey(idField)){
          record.put(idField, UUID.randomUUID().toString)
        }
      }catch {
        case e: Exception => {
          logError(s"解析topic: ${topicSet}中的记录 ${kv._2} 失败。", e)
        }
      }
      record
    }).filter(_ != null)
  }
}

object KafkaInput extends Logging{

  def buildFromXmlElem(inputElem: Elem): KafkaInput ={
    val topics = XmlReader.getAttr(inputElem, "topics")
    require(topics != null, "topics不能为空")

    val params = XmlReader.getNextSingleSubElem(inputElem, "params")
    val kafkaParams = new mutable.HashMap[String, String]()
    for(param <- params \ "param"){
      val elem = param.asInstanceOf[Elem]
      kafkaParams.put(XmlReader.getAttr(elem, "name"),
        XmlReader.getAttr(elem, "value"))
    }
    logInfo(s"kafka的topics为：${topics}, 连接参数为：\n\t ${kafkaParams}")
    new KafkaInput(topics.split(",").toSet, kafkaParams.toMap)
  }
}
