package com.dfssi.dataplatform.newenergy.kafkatohive

import java.util.{Date, UUID}

import com.dfssi.dataplatform.newenergy.kafkatohive.map._
import com.dfssi.dataplatform.streaming.store.config.{HiveTableDef, MessageDef, StreamingStoreConfig}
import com.dfssi.dataplatform.streaming.store.utils.{ExceptionUtils, HdfsUtils, StreamingLog}
import com.dfssi.dataplatform.streaming.store.{ProcessContext, StreamingStoreTrait}
import com.google.gson.{JsonObject, JsonParser}
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.{DataFrame, Row, SaveMode}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaManagerHive
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ArrayBuffer

/**
  * Streaming Process
  *
  */
class NewEnergyKafkaToHiveTask extends StreamingStoreTrait {
  import NewEnergyKafkaToHiveTask._
  def execute(nameNode: String, configPath: String): Unit = {
    StreamingLog.setStreamingLogLevels()

    val config = loadConfigFromHdfs(nameNode, configPath)
    val processContext: ProcessContext = new ProcessContext()

    val sparkConf = new SparkConf().setAppName("NewEnergy-Kafka-To-Hive")
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
    val kafkaProperties: Map[String, String] = KafkaManagerHive
      .createKafkaParam(config.getKafkaBrokers(), config.getKafkaGroupName())
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
          //.filter(_ != null)
          .toDF(
            TAG_FIELD_NAME_TABLE_PARTITION_MAJOR,
            TAG_FIELD_NAME_TABLE_PARTITION_DRIVEMOTOR,
            TAG_FIELD_NAME_MSG_CONTENT,
            TAG_FIELD_NAME_ERROR_MSG
          )
          .repartition(dataPartitionNum).persist(StorageLevel.MEMORY_AND_DISK_SER)

        /** save error data */
        val errorDataDf =
          dividedDf.filter(TAG_FIELD_NAME_ERROR_MSG + " is not null")
        storeErrorData(processContext, broadcastConfig, errorDataDf)

        /** save right data */
        val rightDataDf =
          dividedDf.filter(TAG_FIELD_NAME_ERROR_MSG + " is null")
        //rightDataDf.show()

        /* save major data */
        storeRightDataOf(processContext,
                         broadcastConfig,
                         rightDataDf,
                         TAG_FIELD_NAME_TABLE_PARTITION_MAJOR)

        /* save drive motor data */
        storeRightDataOf(processContext,
                         broadcastConfig,
                         rightDataDf,
                         TAG_FIELD_NAME_TABLE_PARTITION_DRIVEMOTOR)

        dividedDf.unpersist()
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
  def tagTableAndPartition(
      broadcastConfig: Broadcast[StreamingStoreConfig],
      jsonStr: String): (String, String, String, String) = {
    try {
      val config = broadcastConfig.value
      var errMsg: String = null
      val msgJsonObject = new JsonParser().parse(jsonStr).getAsJsonObject

      /* major info */
      val majorMsgId = config.getMsgId(msgJsonObject)
      val majorMessageDef: MessageDef = config.getMessageDef(majorMsgId)
      val majorHiveTableDef: HiveTableDef =
        config.getHiveTableDef(majorMessageDef.toTableName)
      if (majorMessageDef == null || majorHiveTableDef == null) {
        errMsg = "msgId:" + majorMsgId + " does not exist MessageDef or HiveTableDef."
      }

      /* drive motor info */
      val driveMotorMsgId = majorMsgId + "_drivemotor";
      val driveMotorMessageDef: MessageDef =
        config.getMessageDef(driveMotorMsgId)
      val driveMotorHiveTableDef: HiveTableDef =
        config.getHiveTableDef(driveMotorMessageDef.toTableName)
      if (driveMotorMessageDef == null || driveMotorHiveTableDef == null) {
        errMsg = "driveMotorMsgId:" + driveMotorMsgId + " does not exist MessageDef or HiveTableDef."
      }

      val msgDate = majorMessageDef.getMessageCreatedDate(msgJsonObject)

      if (errMsg == null) {
        val data = validMessageTableData(broadcastConfig,
                                         msgJsonObject,
                                         majorMessageDef,
                                         majorHiveTableDef)
        if (ERROR_VALUE.equals(data(0))) {
          errMsg = data(1)
        }
      }
      if (errMsg == null) {
        val majorPartitionValueInfo =
          majorHiveTableDef.getPartitionValueInfo(msgDate)
        val driveMotorPartitionValueInfo =
          driveMotorHiveTableDef.getPartitionValueInfo(msgDate)
        val majorTableName = majorHiveTableDef.tableName
        val driveMotorTableName = driveMotorHiveTableDef.tableName

        (s"${majorMsgId}" + HiveTableDef.PARTITION_SPLIT_CHAR + s"${majorTableName}" + HiveTableDef.PARTITION_SPLIT_CHAR + s"${majorPartitionValueInfo}",
          s"${driveMotorMsgId}" + HiveTableDef.PARTITION_SPLIT_CHAR + s"${driveMotorTableName}" + HiveTableDef.PARTITION_SPLIT_CHAR + s"${driveMotorPartitionValueInfo}",
         jsonStr,
         errMsg)

      } else {
        ("Error Msg Major", "Error Msg Drive Motor", jsonStr, errMsg)
      }
    } catch {
      case t: Throwable => {
        ("Error Msg Major",
         "Error Msg Drive Motor",
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
      .withColumn(TAG_FIELD_NAME_ID, processContext.udfGenerateUUID())
      .select(tableDef.getColumnNames(): _*)

    storeDataToHive(processContext,
                    broadcastConfig,
                    errorMsgTableDf,
                    tableName,
                    firstLevelPartitionValue,
                    secondLevelPartitionValue)
  }

  /**
    * Save right data of 32960_03
    *
    * @param processContext
    * @param broadcastConfig
    * @param rightDataDf
    */
  private def storeRightDataOf(processContext: ProcessContext,
                               broadcastConfig: Broadcast[StreamingStoreConfig],
                               rightDataDf: DataFrame,
                               partitionFieldName: String): Unit = {
    rightDataDf
      .select(partitionFieldName)
      .distinct()
      .collect()
      .foreach(tableRow => {
        val tablePartitionKey =
          tableRow.getAs[String](partitionFieldName)
        val tableAndPartitionInfo =
          tablePartitionKey.split(HiveTableDef.PARTITION_SPLIT_CHAR)

        val msgId = tableAndPartitionInfo(0)
        val tableName = tableAndPartitionInfo(1)
        val firstLevelPartitionValue = tableAndPartitionInfo(2)
        val secondLevelPartitionValue = tableAndPartitionInfo(3)

        logInfo(
          s"Save data to table ${tableName} partition first level:${firstLevelPartitionValue} second level:${secondLevelPartitionValue}")

        val dfForCurrentRowKey = rightDataDf.filter(
          partitionFieldName + " = " + s"'${tablePartitionKey}'")
        val partitionDataRdd = dfForCurrentRowKey.flatMap(row =>
          transferJsonStrToDataRdd(broadcastConfig, msgId, row))
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
                               msgId: String,
                               row: Row): ArrayBuffer[Row] = {
    val msgContent =
      row.getAs[String](TAG_FIELD_NAME_MSG_CONTENT)
    val msgJsonObject = new JsonParser().parse(msgContent).getAsJsonObject

    mapJsonToTable(broadcastConfig, msgId, msgJsonObject)
  }

  private def mapJsonToTable(broadcastConfig: Broadcast[StreamingStoreConfig],
                             msgId: String,
                             msgJsonObject: JsonObject): ArrayBuffer[Row] = {
    val config = broadcastConfig.value
//    val msgId = config.getMsgId(msgJsonObject)
    val messageDef: MessageDef = config.getMessageDef(msgId)
    val hiveTableDef: HiveTableDef =
      config.getHiveTableDef(messageDef.toTableName)

    if (Mapper3296001.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296001.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296001DriveMotor.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296001DriveMotor.map(msgJsonObject, messageDef, hiveTableDef)
    }  else if (Mapper3296002.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296002.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296002DriveMotor.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296002DriveMotor.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296003.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296003.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296003DriveMotor.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296003DriveMotor.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296004.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296004.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296004DriveMotor.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296004DriveMotor.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296005.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296005.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296005DriveMotor.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296005DriveMotor.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296006.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296006.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296006DriveMotor.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296006DriveMotor.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296007.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296007.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296007DriveMotor.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296007DriveMotor.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296008.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296008.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296008DriveMotor.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296008DriveMotor.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296009.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296009.map(msgJsonObject, messageDef, hiveTableDef)
    } else if (Mapper3296009DriveMotor.MAPPER_TYPE.equalsIgnoreCase(msgId)) {
      Mapper3296009DriveMotor.map(msgJsonObject, messageDef, hiveTableDef)
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

    val config = broadcastConfig.value
    val msgId = config.getMsgId(msgJsonObject)
    try {
      mapJsonToTable(broadcastConfig, msgId, msgJsonObject)
      mapJsonToTable(broadcastConfig, msgId+"_drivemotor", msgJsonObject)

    } catch {
      case t: Throwable => {
        logError("Data is invalid.", t)
        errorMsg = "Data is invalid." + ExceptionUtils.getStackTraceAsString(t)
      }
    }
    if (errorMsg != null) {
      Array(ERROR_VALUE, errorMsg)
    } else {
      Array(RIGHT_VALUE, "")
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
 /*   if (partitionDf.rdd == null || partitionDf.rdd.isEmpty()) {
      return
    }*/
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

object NewEnergyKafkaToHiveTask extends Serializable {
  val ERROR_VALUE = "-1"
  val RIGHT_VALUE = "0"

  /** to error_message field name */
  val TAG_FIELD_NAME_TABLE_PARTITION_MAJOR = "table_partition_major"
  val TAG_FIELD_NAME_TABLE_PARTITION_DRIVEMOTOR = "table_partition_drivemotor"
  val TAG_FIELD_NAME_MSG_CONTENT = "msg_content"
  val TAG_FIELD_NAME_ERROR_MSG = "error_msg"
  val TAG_FIELD_NAME_ID = "id"
}
