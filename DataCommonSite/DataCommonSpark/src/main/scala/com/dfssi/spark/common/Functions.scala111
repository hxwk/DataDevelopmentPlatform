package com.dfssi.spark.common

import com.alibaba.fastjson.TypeReference
import org.apache.spark.SparkContext

/**
  * Description:
  *    spark 相关的 一些通用方法
  * @author LiXiaoCong
  * @version 2018/3/16 14:24 
  */
object Functions {

  def getPartitionByAppParam(sc: SparkContext): Int ={
    val conf = sc.getConf
    val executors = conf.getInt("spark.executor.instances", 1)
    val executorCores = conf.getInt("spark.executor.cores", 1)

    executors * executorCores * 2
  }

  @throws[Exception]
  def jsonParse(json: String): java.util.Map[String, Object] ={
    com.alibaba.fastjson.JSON.parseObject(json,
      new TypeReference[java.util.Map[String, Object]]() {})
  }


}
