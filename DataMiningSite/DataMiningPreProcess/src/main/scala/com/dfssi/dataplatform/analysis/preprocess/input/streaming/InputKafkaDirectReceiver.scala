package com.dfssi.dataplatform.analysis.preprocess.input.streaming

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka.KafkaManager

import scala.collection.mutable
import scala.xml.Elem

/**
  * Description:
  *   只负责从kafka中读取数据
  * @author LiXiaoCong
  * @version 2018/3/15 10:12 
  */
class InputKafkaDirectReceiver extends AbstractProcess{

  private var kafkaManager: KafkaManager = null
  private var orginDStream: InputDStream[(String, String)] = null

  override def execute(processContext: ProcessContext,
                       defEl: Elem,
                       sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)
    val topics = paramsMap.remove("topics")
    require(topics.nonEmpty, "参数topics不能为空。")

    kafkaManager = new KafkaManager(paramsMap.toMap, false)
    val topicSet = topics.get.split(",").toSet
    orginDStream = kafkaManager.createDirectStreamWithOffsetCheck[String, String,
      StringDecoder, StringDecoder](processContext.streamingContext, topicSet)

    val dstream = orginDStream.map(_._2)
    processContext.putKV(s"${id}_dstream", dstream)
  }

  override def close(processContext: ProcessContext): Unit = {
    if(orginDStream != null && KafkaManager != null){
      orginDStream.foreachRDD(rdd => {
        kafkaManager.updateZKOffsets(rdd)
      })
    }
  }
}

object InputKafkaDirectReceiver{
  val processType: String = ProcessFactory.PROCESS_NAME_INPUT_KAFKA_DIRECT_RECEIVER
}