/**
 * Copyright (c) 2016, jechedo All Rights Reserved.
 *
 */
package com.dfssi.spark.common

import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.utils.ZKGroupTopicDirs
import org.I0Itec.zkclient.ZkClient
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka.HasOffsetRanges

import scala.reflect.ClassTag

/**
 * Description:
 *
 *  Date    2016-7-25 下午1:38:21
 *
 * @author  LiXiaoCong
 * @version 1.0
 * @since   JDK 1.7
 */
class KafkaOffsetOperator(val topic  : String,
						  val groupId: String, 
						  val zkServers: String) extends Logging {
  
  // 获取 zookeeper 中的路径，这里会变成 /consumers/${groupId}/offsets/${topic}
  val zkTopicPath = s"${new ZKGroupTopicDirs(groupId, topic).consumerOffsetDir}"
  
  //zookeeper 的host 和 ip，创建一个 client
  val zkClient = new ZkClient(zkServers)  
  
  val messageHandler = (mmd : MessageAndMetadata[String, String]) => (mmd.key, mmd.message()) 
  
  def readFromOffsets(): Map[TopicAndPartition, Long] = {
    
    //查询该路径下是否字节点（默认有字节点为我们自己保存不同 partition 时生成的）
    val children = zkClient.countChildren(zkTopicPath) 
    
     var fromOffsets: Map[TopicAndPartition, Long] = Map()  
 
    //如果保存过 offset，这里更好的做法，还应该和  kafka 上最小的 offset 做对比，不然会报 OutOfRange 的错误
    if (children > 0) {  
        for (i <- 0 until children) {
          val partitionOffset = zkClient.readData[String](s"${zkTopicPath}/${i}")
          val tp = TopicAndPartition(topic, i)
          //将不同 partition 对应的 offset 增加到 fromOffsets 中
          fromOffsets += (tp -> partitionOffset.toLong)  
          logInfo("***read topic[" + topic + "] partition[" + i + "] offset[" + partitionOffset + "] ***")
        }
    }
    fromOffsets
  }

  def storeOffsets[T: ClassTag](rdd :RDD[T]){
    val  offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges 
    for (o <- offsetRanges) {
        
     val zkPath = s"${zkTopicPath}/${o.partition}"
    
     //将该 partition 的 offset 保存到 zookeeper
     //ZkUtils.updatePersistentPath(zkClient, zkPath, o.fromOffset.toString)
     logInfo(s"***store topic  ${o.topic}  partition ${o.partition}  fromoffset ${o.fromOffset}  untiloffset ${o.untilOffset} ***")
   }
  }
}

object KafkaOffsetOperator{
  
 def apply(topic: String, groupId: String, zkServers: String) = new KafkaOffsetOperator(topic, groupId, zkServers)
  
 
}