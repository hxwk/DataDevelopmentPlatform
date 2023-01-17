package com.dfssi.dataplatform.analysis.common

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.streaming.StreamingContext

import scala.collection.mutable

class ProcessContext {
  var processType: String = ProcessContext.PROCESS_TYPE_OFFLINE

  var nameNode: String = _
  var appPath: String = _
  var dataFrameMap: mutable.Map[String, DataFrame] = _
  var sparkContext: SparkContext = _
  var streamingContext: StreamingContext = _
  var hiveContext: HiveContext = _
  var broadcastConfig: Broadcast[_ <: Serializable] = _

  private val kvs = new mutable.HashMap[String, Any]()

  def isOffline: Boolean =
        ProcessContext.PROCESS_TYPE_OFFLINE.equalsIgnoreCase(processType)

  def getSparkContext: SparkContext = {
    if (sparkContext != null) {
      return sparkContext
    }
    if (streamingContext != null) {
      return streamingContext.sparkContext
    }
    null
  }

  def getHiveContext: HiveContext = {
      if(hiveContext == null){
        val sc = getSparkContext
        require(sc != null, "SparkContext must not be null!")
        hiveContext = new HiveContext(sc)
      }
    hiveContext
  }

  def putKV(key: String, value: Any): Unit = kvs.put(key, value)

  def getValueAs[T](key: String): T =
     kvs.getOrElse(key, null).asInstanceOf[T]

}

object ProcessContext {
  val PROCESS_TYPE_OFFLINE = "offline"
  val PROCESS_TYPE_STREAMING = "streaming"
  val PROCESS_TYPE_INTEGRATE = "integrate"
}
