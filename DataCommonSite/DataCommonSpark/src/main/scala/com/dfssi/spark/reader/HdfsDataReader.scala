package com.dfssi.spark.reader

import java.util

import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapred.TextInputFormat
import org.apache.spark.{Logging, SparkContext}
import org.apache.spark.rdd.RDD

/**
  * Description:
  *   hdfs数据读取器
  * @author LiXiaoCong
  * @version 1.0
  */
class HdfsDataReader extends Logging{

  def read(param: SparkReaderParam): RDD[(Long, String)] = {
    var rdd : RDD[(Long, String)] = null
    if(param.isHdfsReader) {
      val hdfsReaderParam = param.asInstanceOf[HdfsReaderParam]
      rdd = read(hdfsReaderParam.sc, hdfsReaderParam.dataPath)
    }else{
      logError(s"传入的读取数据的参数实体为 $param , 不是 HdfsReaderParam。")
    }
    rdd
  }

  def read(sc:SparkContext, paths: util.List[String]): RDD[(Long, String)] = {

      val rdds = (0 until paths.size()).map(i =>{
              sc.hadoopFile(paths.get(i), classOf[TextInputFormat],
                classOf[LongWritable], classOf[Text], sc.defaultParallelism)
                .map(f => (f._1.get(), f._2.toString))
            })
    sc.union(rdds)
  }


}
