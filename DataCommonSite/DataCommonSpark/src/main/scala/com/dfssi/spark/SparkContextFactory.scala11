/**
 * Copyright (c) 2016, jechedo All Rights Reserved.
 *
 */
package com.dfssi.spark

import java.util.concurrent.atomic.AtomicReference

import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Description:
 *
 *  date:   2016-5-17 上午10:28:26
 * @author  LiXiaoCong
 * @version 1.0
 * @since   JDK 1.7
 */
object SparkContextFactory {

    @transient private val instantiatedContext = new AtomicReference[HiveContext]()

  def newSparkBatchContext(appName:String = "SparkBatch") : SparkContext = {
    val sparkConf = SparkConfFactory.newSparkBatchConf(appName)
    new SparkContext(sparkConf)
  }

  def newSparkBatchContext(settings:Map[String, String],
                           appName:String) : SparkContext = {
    val sparkConf = SparkConfFactory.newSparkBatchConf(settings, appName)
    new SparkContext(sparkConf)
  }

  def newSparkStreamingContext(appName:String = "SparkStreaming",
                               batchInterval:Long = 30L) : StreamingContext = {
     val sparkConf = SparkConfFactory.newSparkStreamingConf(appName)
     new StreamingContext(sparkConf, Seconds(batchInterval))
  }
  def newSparkStreamingContext(settings:Map[String, String],
                               appName:String,
                               batchInterval:Long) : StreamingContext = {
     val sparkConf = SparkConfFactory.newSparkStreamingConf(settings, appName)
     new StreamingContext(sparkConf, Seconds(batchInterval))
  }

   def newSparkLocalBatchContext(appName:String = "SparkLocalBatch",
                                 threads : Int = 2) : SparkContext = {
    val sparkConf = SparkConfFactory.newSparkLoalConf(appName, threads)
    new SparkContext(sparkConf)
  }

   def newSparkLocalBatchContext(settings:Map[String, String],
                                 appName:String,
                                 threads : Int) : SparkContext = {
    val sparkConf = SparkConfFactory.newSparkLoalConf(settings, appName, threads)
    new SparkContext(sparkConf)
  }

  def newSparkLocalStreamingContext(appName:String = "SparkLocalStreaming" ,
                                    batchInterval:Long = 30L,
                                    threads : Int = 2) : StreamingContext = {
    val sparkConf = SparkConfFactory.newSparkLoalConf(appName, threads)
     new StreamingContext(sparkConf, Seconds(batchInterval))
  }

  def newSparkLocalStreamingContext(settings:Map[String, String],
                                    appName:String,
                                    batchInterval:Long,
                                    threads : Int) : StreamingContext = {
     val sparkConf = SparkConfFactory.newSparkLoalConf(settings, appName, threads)
     new StreamingContext(sparkConf, Seconds(batchInterval))
  }


    def newSqlContext(appName:String = "SparkSql"): SQLContext ={

        val sc = newSparkBatchContext(appName)
        SQLContext.getOrCreate(sc)
    }

    def newSqlContext(settings:Map[String, String],
                      appName:String): SQLContext ={
        val sc = newSparkBatchContext(settings, appName)
        SQLContext.getOrCreate(sc)
    }


    def newHiveContext(appName:String = "SparkHive"): HiveContext ={

        val sc = newSparkBatchContext(appName)
        getOrCreateHiveContext(sc)
    }

    def newHiveContext(settings:Map[String, String],
                      appName:String): HiveContext ={
        val sc = newSparkBatchContext(settings, appName)
        getOrCreateHiveContext(sc)
    }

    def getOrCreateHiveContext(sparkContext: SparkContext): HiveContext = {

        synchronized {
            var ctx = instantiatedContext.get()
            if (ctx == null || ctx.sparkContext.isStopped) {
                ctx = new HiveContext(sparkContext)
                instantiatedContext.set(ctx)
                ctx
            } else {
                ctx
            }
        }
    }

  def startSparkStreaming(ssc:StreamingContext){

      val id = ssc.sparkContext.applicationId
      val appName = ssc.sparkContext.appName

      //注册系统关闭的钩子
      //Runtime.getRuntime.addShutdownHook(new Thread(new StreamingJobListener(id, appName)))

     ssc.start()
	   ssc.awaitTermination()
	   ssc.stop()
  }
}