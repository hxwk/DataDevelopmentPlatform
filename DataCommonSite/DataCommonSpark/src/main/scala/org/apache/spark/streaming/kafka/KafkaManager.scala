package org.apache.spark.streaming.kafka

import com.alibaba.fastjson.TypeReference
import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.serializer.{Decoder, StringDecoder}
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.{DStream, InputDStream}

import scala.reflect.ClassTag


class KafkaManager(val kafkaParams: Map[String, String],
                   val autoUpdateoffset:Boolean = true) extends Serializable with Logging{

  @transient
  private var cluster = new KafkaCluster(kafkaParams)

  def kc(): KafkaCluster ={
    if(cluster == null){
      cluster =  new KafkaCluster(kafkaParams);
    }
    cluster
  }

  def createDirectStream[K: ClassTag, V: ClassTag,
  KD <: Decoder[K]: ClassTag,
  VD <: Decoder[V]: ClassTag](ssc: StreamingContext , topics: Set[String]): InputDStream[(K, V)] =  {

    createDirectStreamWithCheck(ssc, topics, false)
  }

  def createDirectStreamWithOffsetCheck[K: ClassTag, V: ClassTag,
  KD <: Decoder[K]: ClassTag,
  VD <: Decoder[V]: ClassTag](ssc: StreamingContext , topics: Set[String]): InputDStream[(K, V)] =  {

    createDirectStreamWithCheck(ssc, topics, true)
  }

  private def createDirectStreamWithCheck[K: ClassTag, V: ClassTag, KD <: Decoder[K]: ClassTag, VD <: Decoder[V]: ClassTag](
            ssc: StreamingContext , topics: Set[String], offsetCheck:Boolean): InputDStream[(K, V)] =  {

    val groupId = kafkaParams.get("group.id").getOrElse("default")

    // 在zookeeper上读取offsets前先根据实际情况更新offsets
    setOrUpdateOffsets(topics, groupId)

    //从zookeeper上读取offset开始消费message
    val messages = {
      //获取分区
      val partitionsE = kc.getPartitions(topics)
      require(partitionsE.isRight,s"获取 kafka topic ${topics}`s partition 失败。" )
      val partitions = partitionsE.right.get

      //获取分区的offset
      val consumerOffsetsE = kc.getConsumerOffsets(groupId, partitions)
      require(consumerOffsetsE.isRight,s"获取 kafka topic ${topics}`s consumer offsets 失败。" )
      val consumerOffsets = consumerOffsetsE.right.get

      //读取数据
      if(offsetCheck){

        val cleanedHandler = ssc.sc.clean((mmd: MessageAndMetadata[K, V]) => (mmd.key, mmd.message))
        new DirectKafkaInputDStream2[K, V, KD, VD, (K, V)](
          ssc, kafkaParams, consumerOffsets, cleanedHandler)

      } else{

        KafkaUtils.createDirectStream[K, V, KD, VD, (K, V)](
          ssc, kafkaParams, consumerOffsets, (mmd: MessageAndMetadata[K, V]) => (mmd.key, mmd.message))
      }
    }

    if(autoUpdateoffset){
      //更新offset
      messages.foreachRDD(rdd => {
          updateZKOffsets(rdd)
      })
    }
    messages
  }

  /**
    * 创建数据流前，根据实际消费情况更新消费offsets
    * @param topics
    * @param groupId
    */
  private def setOrUpdateOffsets(topics: Set[String], groupId: String): Unit = {

    topics.foreach(topic => {

      val partitionsE = kc.getPartitions(Set(topic))
      require(partitionsE.isRight, s"获取 kafka topic ${topic}`s partition 失败。")
      val partitions = partitionsE.right.get

      val consumerOffsetsE = kc.getConsumerOffsets(groupId, partitions)
      val earliestLeader = kc.getEarliestLeaderOffsets(partitions)

      if (consumerOffsetsE.isRight) {
        /**
          * 如果zk上保存的offsets已经过时了，即kafka的定时清理策略已经将包含该offsets的文件删除。
          * 针对这种情况，只要判断一下zk上的consumerOffsets和earliestLeaderOffsets的大小，
          * 如果consumerOffsets比earliestLeaderOffsets还小的话，说明consumerOffsets已过时,
          * 这时把consumerOffsets更新为earliestLeaderOffsets
          */
          if(earliestLeader.isRight) {

            val earliestLeaderOffsets = earliestLeader.right.get
            val consumerOffsets = consumerOffsetsE.right.get

            // 可能只是存在部分分区consumerOffsets过时，所以只更新过时分区的consumerOffsets为earliestLeaderOffsets
            var offsets: Map[TopicAndPartition, Long] = Map()
            consumerOffsets.foreach({ case (tp, n) =>
              val earliestLeaderOffset = earliestLeaderOffsets(tp).offset
              if (n < earliestLeaderOffset) {
                logWarning("consumer group:" + groupId + ",topic:" + tp.topic + ",partition:" + tp.partition +
                        " offsets已经过时，更新为" + earliestLeaderOffset)
                offsets += (tp -> earliestLeaderOffset)
              }
            })

            //设置offsets
            setOffsets(groupId, offsets)
          }
      } else {

        // 没有消费过
        if(earliestLeader.isLeft)
             logError(s"${topic} hasConsumed but earliestLeaderOffsets is null。")

        val reset = kafkaParams.get("auto.offset.reset").map(_.toLowerCase).getOrElse("smallest")
        var leaderOffsets: Map[TopicAndPartition, Long] = Map.empty
        if (reset.equals("smallest")) {
           //分为 存在 和 不存在 最早的消费记录 两种情况
          if(earliestLeader.isRight){
            leaderOffsets = earliestLeader.right.get.map {
              case (tp, offset) => (tp, offset.offset)
            }
          }
        } else {
          //直接获取最新的offset
          leaderOffsets = kc.getLatestLeaderOffsets(partitions).right.get.map {
            case (tp, offset) => (tp, offset.offset)
          }
        }

        //设置offsets
        setOffsets(groupId, leaderOffsets)
      }
    })
  }

  private def setOffsets(groupId: String, offsets: Map[TopicAndPartition, Long]): Unit ={
    if(offsets.nonEmpty){
      //更新offset
      val o = kc.setConsumerOffsets(groupId, offsets)
      logInfo(s"更新zookeeper中消费组为：${groupId} 的 topic offset信息为： ${offsets}")
      if (o.isLeft) {
        logError(s"Error updating the offset to Kafka cluster: ${o.left.get}")
      }
    }
  }

  /**
    * 更新zookeeper上的消费offsets
    * @param rdd
    */
  def updateZKOffsets[K: ClassTag, V: ClassTag](rdd: RDD[(K, V)]) : Unit = {

    val groupId = kafkaParams.get("group.id").getOrElse("default")
    val offsetsList = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

    for (offsets <- offsetsList) {
      val topicAndPartition = TopicAndPartition(offsets.topic, offsets.partition)
      setOffsets(groupId, Map((topicAndPartition, offsets.untilOffset)))
    }

  }

  def createDirectStreamWithOffset[T:ClassTag](ssc:StreamingContext ,
                                   topicsSet:Set[String], converter:String => T): DStream[T] = {

    createDirectStream[String, String, StringDecoder, StringDecoder](ssc, topicsSet)
      .map(pair =>converter(pair._2))
  }


  def createJsonToJMapDirectStreamWithOffset(ssc:StreamingContext ,
                                             topicsSet:Set[String]): DStream[java.util.Map[String,String]] = {

    val converter = {json:String =>
        var res : java.util.Map[String,String] = null
        try {
          res = com.alibaba.fastjson.JSON.parseObject(json,
            new TypeReference[java.util.Map[String, String]]() {})
        } catch {
          case e: Exception => logError(s"解析topic ${topicsSet}, 的记录 ${json} 失败。", e)
        }
      res
    }
    createDirectStreamWithOffset(ssc, topicsSet, converter).filter(_ != null)
  }


}

object KafkaManager extends Logging{

  def apply(broker:String, groupId:String = "default",
            numFetcher:Int = 1, offset:String = "smallest",
            autoUpdateoffset:Boolean = true): KafkaManager ={
    new KafkaManager(
      createKafkaParam(broker, groupId, numFetcher, offset),
      autoUpdateoffset)
  }

  def createKafkaParam(broker:String, groupId:String = "default",
                       numFetcher:Int = 1, offset:String = "smallest"): Map[String, String] ={

    //创建 stream 时使用的 topic 名字集合
    Map[String, String](
      "metadata.broker.list" -> broker,
      "auto.offset.reset" -> offset,
      "group.id" -> groupId,
      "num.consumer.fetchers" -> numFetcher.toString)
  }

}