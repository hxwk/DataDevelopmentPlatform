/**
 * Copyright (c) 2016, jechedo All Rights Reserved.
 *
 */
package com.dfssi.spark

import java.util.Properties

import com.dfssi.common.file.Files
import org.apache.commons.io.IOUtils
import org.apache.spark.{Logging, SparkConf}

import scala.collection.JavaConversions

/**
 * Description:
 * 
 * 			sparkConf构造工厂
 *    		在用户自定义配置文件存在的情况只读取用户自定义的配置文件，否在读取默认的配置文件
 * @author  LiXiaoCong
 * @version 1.0
 * @since   JDK 1.7
 */
object SparkConfFactory extends Logging{
  
    private val DEFUALT_BATCH_PATH      = "/spark/spark-batch-config.properties"
    private val DEFUALT_STREAMING_PATH  = "/spark/spark-streaming-config.properties"
    private val DEFUALT_STARTWITHJAVA_PATH  = "/spark/spark-start-config.properties"
      
    private val CUSTOM_BATCH_PATH       = "/usr/conf/spark/spark-batch-config.properties"
    private val CUSTOM_STREAMING_PATH   = "/usr/conf/spark/spark-streaming-config.properties"
    private val CUSTOM_STARTWITHJAVA_PATH   = "/usr/conf/spark/spark-start-config.properties"

   def newSparkBatchConf(appName:String = "SparkBatch") : SparkConf = {
     
      val sparkConf = newSparkConf(appName)
      
      if(Files.exist(CUSTOM_BATCH_PATH))
    	  sparkConf.setAll(readConfigFileAsTraversable(CUSTOM_BATCH_PATH))
      else 
    	  sparkConf.setAll(readConfigFileAsTraversable(DEFUALT_BATCH_PATH))
        
    sparkConf
   }

   def newSparkBatchConf(settings:Map[String, String], appName:String) : SparkConf = {
       newSparkBatchConf(appName).setAll(settings)
   }

   def newSparkStreamingConf(appName:String = "SparkStreaming") : SparkConf = {
     val sparkConf = newSparkBatchConf(appName)
     if(Files.exist(CUSTOM_STREAMING_PATH))
    	 sparkConf.setAll(readConfigFileAsTraversable(CUSTOM_STREAMING_PATH))
     else
    	 sparkConf.setAll(readConfigFileAsTraversable(DEFUALT_STREAMING_PATH))

     sparkConf
   }

   def newSparkStreamingConf(settings:Map[String, String], appName:String) : SparkConf = {

       newSparkStreamingConf(appName).setAll(settings)
   }

   def newSparkLoalConf(appName: String = "SparkLocal", threads: Int = 1):SparkConf = {
       new SparkConf().setMaster(s"local[$threads]").setAppName(appName)
   }

   def newSparkLoalConf(settings:Map[String, String],
                        appName: String,
                        threads: Int):SparkConf = {
       newSparkLoalConf(appName, threads).setAll(settings)
   }

   def newSparkConf(appName:String = "SparkDefualt") : SparkConf = {
      new SparkConf().setAppName(appName)
    }

   def newSparkConf(settings:Map[String, String],
                    appName:String) : SparkConf = {
     newSparkConf(appName).setAll(settings)
    }

  def newSparkBatchConfOnJava(appName:String = "SparkBatch"): SparkConf = {

    val sparkConf = newSparkBatchConf(appName)
    sparkConf.setAll(readConfigFileAsTraversable(DEFUALT_STARTWITHJAVA_PATH))
    if(Files.exist(CUSTOM_STARTWITHJAVA_PATH)){
      sparkConf.setAll(readConfigFileAsTraversable(CUSTOM_STARTWITHJAVA_PATH))
    }
    sparkConf
  }

  def newSparkBatchConfOnJava(settings:Map[String, String],
                              appName:String): SparkConf = {
      newSparkBatchConfOnJava(appName).setAll(settings)
  }

    private def readConfigFileAsTraversable(path:String) : Traversable[(String,String)] = {

        val source = SparkConfFactory.getClass().getResourceAsStream(path)
        require(source != null, s"读取配置文件 ${path} 失败。")
        try{
            val prop = new Properties()
            prop.load(source)
            val values = JavaConversions.collectionAsScalaIterable(prop.entrySet())
            val kvs = values.map(map => {
                (map.getKey().toString().trim(), map.getValue().toString().trim())
            }).filter(!_._2.isEmpty())

            logInfo( s"加载配置文件  $path 成功,具体参数如下：")
            logInfo(kvs.toList.toString)

            kvs
        }finally {
            IOUtils.closeQuietly(source)
        }
    }




    def main(args: Array[String]) {
     
     readConfigFileAsTraversable(DEFUALT_STARTWITHJAVA_PATH).foreach(println)
  }

}