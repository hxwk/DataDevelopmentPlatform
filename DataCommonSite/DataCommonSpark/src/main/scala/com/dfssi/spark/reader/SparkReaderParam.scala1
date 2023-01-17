package com.dfssi.spark.reader

import org.apache.spark.SparkContext


/**
  * Description:
  *     spark读取数据的实体类
  * @author LiXiaoCong
  * @version 1.0
  */
sealed abstract class SparkReaderParam(@transient val sc:SparkContext){

  def id: String

  def isHdfsReader: Boolean = isInstanceOf[HdfsReaderParam]

  override def toString: String = id
  override def hashCode: Int = id.hashCode
  override def equals(other: Any): Boolean = other match {
    case o: SparkReaderParam => getClass == o.getClass && id.equals(o.id)
    case _ => false
  }

}

case class HdfsReaderParam(@transient override val sc:SparkContext,
                           val dataPath:java.util.List[String]) extends SparkReaderParam(sc){
  override def id: String = s"hdfs:${sc.hashCode()}:$dataPath"
}
