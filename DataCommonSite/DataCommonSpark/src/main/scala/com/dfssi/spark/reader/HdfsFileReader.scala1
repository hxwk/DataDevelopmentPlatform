package com.dfssi.spark.reader

import com.alibaba.fastjson.TypeReference
import com.dfssi.spark.SparkConfFactory
import org.apache.spark.rdd.RDD
import org.apache.spark.{Logging, SparkContext}

import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.reflect.ClassTag

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2017/2/7 11:10    
  */
object HdfsFileReader extends Serializable with Logging{

  def JsonMapConvertor(record:String): java.util.Map[String, String] = {
    var res : java.util.Map[String,String] = null
    try {
      res = com.alibaba.fastjson.JSON.parseObject(record,
        new TypeReference[java.util.Map[String, String]]() {})
    } catch {
      case e: Exception => logError(s"解析记录 ${record} 失败。", e)
    }
    res
  }

  def read[T:ClassTag](paths:Array[String],
                       convertor:String => T,
                       appName:String = "reader"):(SparkContext, RDD[T]) ={

    val sparkConf = SparkConfFactory.newSparkBatchConf(appName)
    sparkConf.registerKryoClasses(Array(classOf[ArrayBuffer[String]], classOf[ListBuffer[String]]))
    //sparkConf.set("spark.serializer", "org.apache.spark.serializer.JavaSerializer")
    val sc = new SparkContext(sparkConf)
    sc.hadoopConfiguration.set("mapreduce.input.fileinputformat.input.dir.recursive","true")

    var rdd:RDD[T] = null
    if(paths.size == 1){
      rdd = sc.textFile(paths(0)).map(convertor)
    }else{
      val rdds = (0 until paths.size) map { i =>
        sc.textFile(paths(i))
      }
      rdd = sc.union(rdds).map(convertor)

    }
    val data = rdd.filter(item => (item != null))
    (sc, data)
  }

  def read(paths:Array[String],
           appName:String = "reader"):(SparkContext, RDD[String]) ={

    val sparkConf = SparkConfFactory.newSparkBatchConf(appName)
    sparkConf.registerKryoClasses(Array(classOf[ArrayBuffer[String]], classOf[ListBuffer[String]]))
    //sparkConf.set("spark.serializer", "org.apache.spark.serializer.JavaSerializer")
    val sc = new SparkContext(sparkConf)
    sc.hadoopConfiguration.set("mapreduce.input.fileinputformat.input.dir.recursive","true")

    var rdd:RDD[String] = null
    if(paths.size == 1){
      rdd = sc.textFile(paths(0))
    }else{
      val rdds = (0 until paths.size) map { i =>
        sc.textFile(paths(i))
      }
      rdd = sc.union(rdds)

    }
    (sc, rdd)
  }

  def readJsonFile2Map(paths:Array[String], appName:String = "reader")={
    read(paths, JsonMapConvertor, appName)
  }

}
