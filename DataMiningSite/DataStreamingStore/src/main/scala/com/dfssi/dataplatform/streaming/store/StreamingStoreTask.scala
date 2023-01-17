package com.dfssi.dataplatform.streaming.store

import java.util.{Date, UUID}

import com.dfssi.dataplatform.streaming.store.config.{
  HiveTableDef,
  MessageDef,
  StreamingStoreConfig
}
import com.dfssi.dataplatform.streaming.store.map.{
  Mapper0200,
  Mapper0702,
  Mapper0704,
  Mapper0705
}
import com.dfssi.dataplatform.streaming.store.utils.{
  ExceptionUtils,
  HdfsUtils,
  StreamingLog
}
import com.google.gson.{JsonObject, JsonParser}
import kafka.serializer.StringDecoder
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.{DataFrame, Row, SaveMode}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaManagerHive
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

/**
  * Streaming Process
  *
  */
class StreamingStoreTask extends StreamingStoreTrait {

  def execute(nameNode: String, configPath: String): Unit = {
    StreamingLog.setStreamingLogLevels()

    val config = loadConfigFromHdfs(nameNode, configPath)
    val processContext: ProcessContext = new ProcessContext()

    val sparkConf = new SparkConf().setAppName("Kafka-To-Hive")
    val streamingContext: StreamingContext =
      new StreamingContext(sparkConf, Seconds(config.getStreamingDuration()))

    processContext.streamingContext = streamingContext

    /** val broadcastConfig: Broadcast[StreamingStoreConfig] = streamingContext.sparkContext.broadcast(config) */
    startupStreamingProcessDirect(processContext, config)

    streamingContext.start()
    streamingContext.awaitTermination()
  }

  /**
    *
    * @param processContext
    * @param config
    */
  def startupStreamingProcessDirect(processContext: ProcessContext,
                                    config: StreamingStoreConfig): Unit = {
    val kafkaProperties: Map[String, String] = KafkaManagerHive.createKafkaParam(
      config.getKafkaBrokers(),
      config.getKafkaGroupName())
    val topicSet: Set[String] = Set(config.getKafkaTopics() split (","): _*)
    val kafkaManager: KafkaManagerHive = new KafkaManagerHive(kafkaProperties)
    val inputDStream = kafkaManager
      .createDirectStream[String, String, StringDecoder, StringDecoder](
        processContext.streamingContext,
        topicSet)

    val broadcastConfig: Broadcast[StreamingStoreConfig] =
      processContext.streamingContext.sparkContext.broadcast(config)

    divideTableAndPartition(processContext,
                            broadcastConfig,
                            inputDStream,
                            kafkaManager)
  }

  /**
    * Divide table and partition by msgId and received time.
    *
    * @param processContext
    * @param broadcastConfig
    * @param inputDStream
    * @param kafkaManager
    */
  def divideTableAndPartition(processContext: ProcessContext,
                              broadcastConfig: Broadcast[StreamingStoreConfig],
                              inputDStream: DStream[(String, String)],
                              kafkaManager: KafkaManagerHive): Unit = {
    val config = broadcastConfig.value
    val dataPartitionNum = config.getDataPartitionNum()

    inputDStream.foreachRDD(rdd => {
      if (!rdd.isEmpty()) {
        if (processContext.hiveContext == null) {
          import org.apache.spark.sql.functions._
          processContext.hiveContext = new HiveContext(rdd.sparkContext)
          processContext.udfGenerateUUID = udf(() => UUID.randomUUID().toString)
        }
        val hiveContext = processContext.hiveContext
        import hiveContext.implicits._

        val dividedDf = rdd
          .map(_._2)
          .map(jsonStr => tagTableAndPartition(broadcastConfig, jsonStr))
          .filter(_ != null)
          .toDF(StreamingStoreTask.TAB_FIELD_NAME_TABLE_PARTITION,
                StreamingStoreTask.TAG_FIELD_NAME_MSG_CONTENT,
                StreamingStoreTask.TAG_FIELD_NAME_ERROR_MSG)
          .repartition(dataPartitionNum)
        val persist = dividedDf.persist(StorageLevel.MEMORY_AND_DISK_SER)

        /** save error data */
        val errorDataDf = persist.filter(
          StreamingStoreTask.TAG_FIELD_NAME_ERROR_MSG + " is not null")
        storeErrorData(processContext, broadcastConfig, errorDataDf)

        /** save right data */
        val rightDataDf = persist.filter(
          StreamingStoreTask.TAG_FIELD_NAME_ERROR_MSG + " is null")
        storeRightData(processContext, broadcastConfig, rightDataDf)

        persist.unpersist()
      }

      if (kafkaManager != null) {
        kafkaManager.updateOffsetsToZookeeper(rdd)
      }
    })
  }

  /**
    * Tag table and partition info.
    *
    * @param broadcastConfig
    * @param jsonStr
    * @return
    */
  def tagTableAndPartition(broadcastConfig: Broadcast[StreamingStoreConfig],
                           jsonStr: String): (String, String, String) = {
    try {
      val config = broadcastConfig.value
      var errMsg: String = null
      val msgJsonObject = new JsonParser().parse(jsonStr).getAsJsonObject
      val msgId = config.getMsgId(msgJsonObject)
      val messageDef: MessageDef = config.getMessageDef(msgId)
      val hiveTableDef: HiveTableDef =
        config.getHiveTableDef(messageDef.toTableName)
      if (messageDef == null || hiveTableDef == null) {
        errMsg = "msgId:" + msgId + " does not exist MessageDef or HiveTableDef."
      }

      val msgDate = messageDef.getMessageCreatedDate(msgJsonObject)

      if (errMsg == null) {
        val data = validMessageTableData(broadcastConfig,
                                         msgJsonObject,
                                         messageDef,
                                         hiveTableDef)
        if (StreamingStoreTask.ERROR_VALUE.equals(data(0))) {
          errMsg = data(1)
        }
      }
      if (errMsg == null) {
        val partitionValueInfo = hiveTableDef.getPartitionValueInfo(msgDate)
        val tableName = hiveTableDef.tableName
        (s"${msgId}" + HiveTableDef.PARTITION_SPLIT_CHAR + s"${tableName}" + HiveTableDef.PARTITION_SPLIT_CHAR + s"${partitionValueInfo}",
         jsonStr,
         errMsg)
      } else {
        ("Error Msg", jsonStr, errMsg)
      }
    } catch {
      case t: Throwable => {
        ("Error Msg",
         jsonStr,
         "Bad message format.\n" + ExceptionUtils.getStackTraceAsString(t))
      }
    }
  }

  private def storeErrorData(processContext: ProcessContext,
                             broadcastConfig: Broadcast[StreamingStoreConfig],
                             errorDataDf: DataFrame): Unit = {
    val tableName = broadcastConfig.value.getErrorMsgTableName()
    val tableDef = broadcastConfig.value.getHiveTableDef(tableName)
    val partitionValueInfo = tableDef.getPartitionValueInfo(new Date())
    val partitionValues =
      partitionValueInfo.split(HiveTableDef.PARTITION_SPLIT_CHAR)
    val firstLevelPartitionValue = partitionValues(0)
    val secondLevelPartitionValue = partitionValues(1)

    val errorMsgTableDf = errorDataDf
      .withColumn(StreamingStoreTask.TAG_FIELD_NAME_ID,
                  processContext.udfGenerateUUID())
      .select(tableDef.getColumnNames(): _*)

    storeDataToHive(processContext,
                    broadcastConfig,
                    errorMsgTableDf,
                    tableName,
                    firstLevelPartitionValue,
                    secondLevelPartitionValue)
  }

  private def storeRightData(processContext: ProcessContext,
                             broadcastConfig: Broadcast[StreamingStoreConfig],
                             rightDataDf: DataFrame): Unit = {

    /**
      * java.lang.NullPointerException	at org.apache.spark.sql.hive.client.ClientWrapper.conf(ClientWrapper.scala:205)
      * dataDF.select("TABLE").distinct().collect().par.foreach(tableRow => {
      * so will not use par, must be single thread
      */
    rightDataDf
      .select(StreamingStoreTask.TAB_FIELD_NAME_TABLE_PARTITION)
      .distinct()
      .collect()
      .foreach(tableRow => {
        val tablePartitionKey = tableRow.getAs[String](
          StreamingStoreTask.TAB_FIELD_NAME_TABLE_PARTITION)
        val tableAndPartitionInfo =
          tablePartitionKey.split(HiveTableDef.PARTITION_SPLIT_CHAR)

        val msgId = tableAndPartitionInfo(0)
        val tableName = tableAndPartitionInfo(1)
        val firstLevelPartitionValue = tableAndPartitionInfo(2)
        val secondLevelPartitionValue = tableAndPartitionInfo(3)

        logInfo(
          s"Save data to table ${tableName} partition first level:${firstLevelPartitionValue} second level:${secondLevelPartitionValue}")

        val dfForCurrentRowKey = rightDataDf.filter(
          StreamingStoreTask.TAB_FIELD_NAME_TABLE_PARTITION + " = " + s"'${tablePartitionKey}'")
        val partitionDataRdd = dfForCurrentRowKey.flatMap(row =>
          transferJsonStrToDataRdd(broadcastConfig, row))
        val partitionDf = processContext.hiveContext.createDataFrame(
          partitionDataRdd,
          broadcastConfig.value.getHiveTableDef(tableName).getTableColSchema())

        storeDataToHive(processContext,
                        broadcastConfig,
                        partitionDf,
                        tableName,
                        firstLevelPartitionValue,
                        secondLevelPartitionValue)
      })
  }

  def transferJsonStrToDataRdd(broadcastConfig: Broadcast[StreamingStoreConfig],
                               row: Row): ArrayBuffer[Row] = {
    val msgContent =
      row.getAs[String](StreamingStoreTask.TAG_FIELD_NAME_MSG_CONTENT)
    val msgJsonObject = new JsonParser().parse(msgContent).getAsJsonObject

    mapJsonToTable(broadcastConfig, msgJsonObject)
  }

  private def mapJsonToTable(broadcastConfig: Broadcast[StreamingStoreConfig],
                             msgJsonObject: JsonObject): ArrayBuffer[Row] = {
    val config = broadcastConfig.value
    val msgId = config.getMsgId(msgJsonObject)
    val messageDef: MessageDef = config.getMessageDef(msgId)
    val hiveTableDef: HiveTableDef =
      config.getHiveTableDef(messageDef.toTableName)

    if (Mapper0200.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper0200.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper0704.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper0704.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper0705.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper0705.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper0702.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper0702.map(msgJsonObject, messageDef, hiveTableDef)
    } else {
      logError(
        "-----------------------Unknowable Mapper---------------" + "msgId=" + msgId)
      throw new Exception("Mapper does not exist. msgId=" + msgId)
    }
  }

  private def validMessageTableData(
      broadcastConfig: Broadcast[StreamingStoreConfig],
      msgJsonObject: JsonObject,
      messageDef: MessageDef,
      hiveTableDef: HiveTableDef): Array[String] = {
    val result: ArrayBuffer[String] = new ArrayBuffer[String]()
    var errorMsg: String = null

    try {
      mapJsonToTable(broadcastConfig, msgJsonObject)
    } catch {
      case t: Throwable => {
        logError("Data is invalid.", t)
        errorMsg = "Data is invalid." + ExceptionUtils.getStackTraceAsString(t)
      }
    }
    if (errorMsg != null) {
      Array(StreamingStoreTask.ERROR_VALUE, errorMsg)
    } else {
      Array(StreamingStoreTask.RIGHT_VALUE, "")
    }
  }

  private def checkAndCreateTable(
      processContext: ProcessContext,
      broadcastConfig: Broadcast[StreamingStoreConfig],
      tableName: String): Unit = {
    val config = broadcastConfig.value
    if (!config.checkTableExist()) {
      return
    }

    val currentHiveSchema =
      config.getParamValue(StreamingStoreConfig.CONTEXT_PARAM_NAME_HIVESCHEMA)
    val tableNames: Array[String] =
      processContext.hiveContext.tableNames(currentHiveSchema)

    if (tableNames.contains(tableName)) {

      /** exist table */
      return
    }

    /** create table */
    processContext.hiveContext.sql(
      config.getHiveTableDef(tableName).toDDL(config))
  }

  private def storeDataToHive(processContext: ProcessContext,
                              broadcastConfig: Broadcast[StreamingStoreConfig],
                              partitionDf: DataFrame,
                              tableName: String,
                              firstLevelPartitionValue: String,
                              secondLevelPartitionValue: String): Unit = {
    if (partitionDf.rdd == null || partitionDf.rdd.isEmpty()) {
      return
    }
    val config = broadcastConfig.value
    checkAndCreateTable(processContext, broadcastConfig, tableName)

    val path = config
      .getHiveExternalTableDataRootPath() + tableName + "/" + firstLevelPartitionValue + "/" + secondLevelPartitionValue
    val exists =
      HdfsUtils.existPath(config.getNameNode(), path, config.getHadooConfUser())

    partitionDf.write.mode(SaveMode.Append).parquet(path)

    val schemaAndTableName = config.getCurrentHiveSchema() + "." + tableName
    val tableDef: HiveTableDef = config.getHiveTableDef(tableName)

    if (!exists) {
      val partitionCondition = tableDef.getPartitionCondition(
        firstLevelPartitionValue,
        secondLevelPartitionValue)
      processContext.hiveContext.sql(
        s"ALTER TABLE ${schemaAndTableName} ADD IF NOT EXISTS PARTITION " + partitionCondition + s" LOCATION '${path}'")
    }
  }
}

object StreamingStoreTask extends Serializable {
  val ERROR_VALUE = "-1"
  val RIGHT_VALUE = "0"

  /** to error_message field name */
  val TAB_FIELD_NAME_TABLE_PARTITION = "table_partition"
  val TAG_FIELD_NAME_MSG_CONTENT = "msg_content"
  val TAG_FIELD_NAME_ERROR_MSG = "error_msg"
  val TAG_FIELD_NAME_ID = "id"
}
