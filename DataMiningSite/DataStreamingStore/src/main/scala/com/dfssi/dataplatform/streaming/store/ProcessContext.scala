package com.dfssi.dataplatform.streaming.store

import org.apache.spark.SparkContext
import org.apache.spark.sql.UserDefinedFunction
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.streaming.StreamingContext

class ProcessContext {

  var sparkContext: SparkContext = _
  var streamingContext: StreamingContext = _
  var hiveContext: HiveContext = _
  var udfGenerateUUID: UserDefinedFunction = _

  def getSparkContext: SparkContext = {
    if (sparkContext != null) {
      return sparkContext
    }
    if (streamingContext != null) {
      return streamingContext.sparkContext
    }

    null
  }

}
