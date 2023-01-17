package com.dfssi.dataplatform.analysis.preprocess.input.streaming

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.preprocess.streaming.StreamingMsgDecoder
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import com.dfssi.dataplatform.streaming.message.StreamingMsg.Message
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.sql.Row
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.xml.Elem


class InputKafkaReceiver extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID);
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl);
    val colNames = getParamValue(paramsMap, "colNames").split(",");

    val kafkaProperties: Map[String, String] =
      Map(
        "zookeeper.connect" -> getParamValue(paramsMap, "zkConnect"),
        "consumer.id" -> getParamValue(paramsMap, "consumerId"),
        "group.id" -> getParamValue(paramsMap, "groupId"),
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "true"
      )

    val topicMap: Map[String, Int] = getParamValue(paramsMap, "topics").split(",").map((_, getParamValue(paramsMap,
      "numThreads").toInt)).toMap

    val inputDStream = KafkaUtils.createStream[String, Message, StringDecoder,
      StreamingMsgDecoder](processContext.streamingContext, kafkaProperties, topicMap,
      StorageLevel.MEMORY_ONLY).map(_._2)

    dstreamToDataFrame(processContext, id, inputDStream, colNames, sparkTaskDefEl)
  }

  def dstreamToDataFrame(processContext: ProcessContext, id: String, inputDStream: DStream[Message],
                         colNames: Array[String], sparkTaskDefEl: Elem): Unit = {
    var schema = new StructType()
    for (colName <- colNames) {
      schema = schema.add(StructField(colName, StringType, true))
    }

    inputDStream.foreachRDD(rdd => {
      if (!rdd.isEmpty()) {
        def findValByKey(keyName: String, msg: Message): String = {
          for (keyVal <- msg.getDataList) {
            if (keyName.equalsIgnoreCase(keyVal.getKey)) {
              return keyVal.getValue
            }
          }
          null
        }

        if (processContext.hiveContext == null)
          processContext.hiveContext = new HiveContext(rdd.sparkContext);

        val seqRdd = rdd.map(msg => {
          var seq: ArrayBuffer[String] = new ArrayBuffer[String]();
          for (i <- 0 to colNames.size - 1) {
            seq += findValByKey(colNames(i), msg)
          }
          Row.fromSeq(seq)
        })
        val newDF = processContext.hiveContext.createDataFrame(seqRdd, schema)
        processContext.dataFrameMap.put(id, newDF)

        val preprocessEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_PREPROCESS);
        val algorithmsEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_ALGORITHMS);
        val outputsEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUTS);
        processConvert(processContext, preprocessEl, sparkTaskDefEl);
        processAlgorithms(processContext, algorithmsEl, sparkTaskDefEl);
        processOutputs(processContext, outputsEl, sparkTaskDefEl);
      }
    }
    )
  }

  def processConvert(processContext: ProcessContext, preprocessEl: Elem, sparkTaskDefEl: Elem) {
    if (preprocessEl == null || (preprocessEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS).size == 0) {
      return;
    }

    for (processNode <- (preprocessEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS)) {
      var processEl = processNode.asInstanceOf[Elem];
      var processType = XmlUtils.getAttrValue(processEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE);
      ProcessFactory.getProcess(processType).execute(processContext, processEl, sparkTaskDefEl);
    }
  }

  def processAlgorithms(processContext: ProcessContext, algorithmsEl: Elem, sparkTaskDefEl: Elem) {
    if (algorithmsEl == null || (algorithmsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_ALGORITHM).size == 0) {
      return;
    }
  }

  def processOutputs(processContext: ProcessContext, outputsEl: Elem, sparkTaskDefEl: Elem) {
    if (outputsEl == null || (outputsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUT).size == 0) {
      return;
    }

    for (outputNode <- (outputsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUT)) {
      var outputEl = outputNode.asInstanceOf[Elem];
      val outputType = XmlUtils.getAttrValue(outputEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE);

      ProcessFactory.getProcess(outputType).execute(processContext, outputEl, sparkTaskDefEl);
    }
  }


}

object InputKafkaReceiver {
  val processType: String = ProcessFactory.PROCESS_NAME_INPUT_KAFKA_RECEIVER

}
