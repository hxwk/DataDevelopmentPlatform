/**
 * Copyright (c) 2016, jechedo All Rights Reserved.
 *
 */
package com.dfssi.spark

import com.alibaba.fastjson.{JSON, JSONObject, TypeReference}
import kafka.serializer.StringDecoder
import org.apache.spark.Logging
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.{KafkaManager, KafkaUtils}

import scala.reflect.ClassTag

/**
 * Description:
 *
 *  date:   2016-5-17 下午12:57:59
 *
 * @author  LiXiaoCong
 * @version 1.0
 * @since   JDK 1.7
 */
class StreamFactory extends Serializable with Logging {
  
  def createDirectStream(ssc:StreamingContext , broker:String, topicSet:Set[String],
		  				 groupId:String = "default", numFetcher:Int = 1, offset:String = "smallest") : DStream[String] = {
      
	   val kafkaParams = Map[String, String](
				    				"metadata.broker.list" -> broker,
				    				"auto.offset.reset" -> offset,
				    				"group.id" -> groupId,
				    				"num.consumer.fetchers" -> s"$numFetcher")
				    				
       //读取kafka中的原始数据
	 KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicSet).map(_._2)
  }
  
  def createDirectStream[T: ClassTag](ssc:StreamingContext , broker:String, topicSet:Set[String],  convertor:(String) => T ,
		  				 groupId:String = "default", numFetcher:Int = 1, offset:String = "smallest") : DStream[T] = 
	   createDirectStream(ssc,broker,topicSet,groupId,numFetcher,offset).map(convertor)
  
  def createJsonToJMapDirectStream(ssc:StreamingContext , broker:String, topicSet:Set[String],  
		  				             groupId:String = "default", numFetcher:Int = 1, offset:String = "smallest") : DStream[java.util.Map[String,String]] = {
    
    val convertor = { record:String =>
				var res : java.util.Map[String,String] = null
				try {
					res = com.alibaba.fastjson.JSON.parseObject(record,
						new TypeReference[java.util.Map[String, String]]() {})
				} catch {
					case e: Exception => logError(s"解析topic ${topicSet}, 的记录 ${record} 失败。", e)
				}
				res
    }
    
    createDirectStream(ssc,broker,topicSet,convertor,groupId,numFetcher,offset).filter(_ != null)
  }
  
  def createJsonToJMapDirectStreamWithOffset(ssc:StreamingContext , broker:String, topic:String,  
		  				                     groupId:String = "default", numFetcher:Int = 1, zkServers: String, 
		  				                     offset:String = "smallest"): DStream[java.util.Map[String,String]] = {

		val manager: KafkaManager = KafkaManager(broker,groupId, numFetcher, offset)
		manager.createJsonToJMapDirectStreamWithOffset(ssc, Set(topic))
  }

	def createDirectStreamWithOffset[T:ClassTag](ssc:StreamingContext , broker:String,
																							 topics:Set[String], converter:String => T,
																							 groupId:String = "default", numFetcher:Int = 1,
																							 offset:String = "smallest"): DStream[T] = {
		val manager: KafkaManager = KafkaManager(broker, groupId, numFetcher, offset)
		manager.createDirectStreamWithOffset(ssc, topics, converter)
	}

	def createJObjectDirectStreamWithOffset(ssc:StreamingContext , broker:String,
																							 topics:Set[String], groupId:String = "default",
																							 numFetcher:Int = 1, offset:String = "smallest"): DStream[JSONObject] = {
		val converter = { jsonStr: String =>
			var jo: JSONObject = null
			try {
				jo = JSON.parseObject(jsonStr)
			} catch {
				case e: Exception => logError(s"解析topic ${topics}, 的记录 ${jsonStr} 失败。", e)
			}
			jo
		}
		createDirectStreamWithOffset(ssc, broker, topics, converter, groupId, numFetcher, offset).filter(_ != null)
	}


  
  def createJsonToMapDirectStream(ssc:StreamingContext , broker:String, topicSet:Set[String],  
		  				                    groupId:String = "default", numFetcher:Int = 1,
																	offset:String = "smallest") : DStream[Map[String,String]] = {
    
    val convertor = { record:String =>
      
			val obj = scala.util.parsing.json.JSON.parseFull(record)
			var  res:Map[String, String] = null
			obj match {  
			  case Some(map: Map[String, String]) => res = map
				case None   => logError("Parsing failed.")
			  case other => logError(s"数据结构不合法 ： $other")  
			} 
			res
    }
    
    createDirectStream(ssc,broker,topicSet,convertor,groupId,numFetcher,offset).filter(_ != null)
  }

}

object StreamFactory{
  
  def apply() = new StreamFactory()
  def newInstance = new StreamFactory
}