package com.dfssi.dataplatform.streaming.store.merge

import com.dfssi.dataplatform.streaming.store.config.StreamingStoreConfig
import com.dfssi.dataplatform.streaming.store.utils.HdfsUtils
import com.dfssi.dataplatform.streaming.store.{
  ProcessContext,
  SparkProcessTrait
}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.{DataFrame, SaveMode}
import org.apache.spark.{SparkConf, SparkContext}

class MergeTask extends SparkProcessTrait {

  def execute(nameNode: String,
              configPath: String,
              tableName: String,
              firstLevelPartitionValue: String,
              secondLevelPartitionValue: String): Unit = {

    val config = loadConfigFromHdfs(nameNode, configPath)
    val processContext = new ProcessContext()

    val sparkConf = new SparkConf().setAppName("Kafka-To-Hive")
    sparkConf.setMaster("yarn-cluster");

    processContext.sparkContext = new SparkContext(sparkConf);
    val broadcastConfig: Broadcast[StreamingStoreConfig] =
      processContext.sparkContext.broadcast(config)

    processContext.hiveContext = new HiveContext(processContext.sparkContext)

    mergeSmallFileOfTable(processContext,
                          broadcastConfig,
                          tableName,
                          firstLevelPartitionValue,
                          secondLevelPartitionValue)

    processContext.sparkContext.stop()
  }

  def mergeSmallFileOfTable(processContext: ProcessContext,
                            broadcastConfig: Broadcast[StreamingStoreConfig],
                            tableName: String,
                            firstLevelPartitionValue: String,
                            secondLevelPartitionValue: String): Unit = {
    val config = broadcastConfig.value
    var partitionDF = loadTableData(processContext,
                                    broadcastConfig,
                                    tableName,
                                    firstLevelPartitionValue,
                                    secondLevelPartitionValue)
    partitionDF = partitionDF.coalesce(config.getMergePartitionNum())
    val mergePath = buildMergePath(config,
                                   tableName,
                                   firstLevelPartitionValue,
                                   secondLevelPartitionValue,
                                   "_merge")
    HdfsUtils.deleteDir(config.getNameNode(),
                        mergePath,
                        config.getHadooConfUser())

    if (config.isMergeEnableCompress()) {
      processContext.hiveContext
        .setConf("spark.sql.parquet.compression.codec",
                 config.getMergeCompressMethod())
      partitionDF.write
        .option("compression", config.getMergeCompressMethod())
        .mode(SaveMode.Append)
        .parquet(mergePath)
    } else {
      processContext.hiveContext
        .setConf("spark.sql.parquet.compression.codec", "uncompressed")
      partitionDF.write
        .option("compression", "none")
        .mode(SaveMode.Append)
        .parquet(mergePath)
    }

    /* backup old path */
    val partitionPath = buildMergePath(config,
                                       tableName,
                                       firstLevelPartitionValue,
                                       secondLevelPartitionValue,
                                       "")
    val bkPath = buildMergePath(config,
                                tableName,
                                firstLevelPartitionValue,
                                secondLevelPartitionValue,
                                "_bk")
    HdfsUtils.deleteDir(config.getNameNode(),
                        partitionPath,
                        config.getHadooConfUser())
//    HdfsUtils.rename(config.getNameNode(),
//                     partitionPath,
//                     bkPath,
//                     config.getHadooConfUser())
    HdfsUtils.rename(config.getNameNode(),
                     mergePath,
                     partitionPath,
                     config.getHadooConfUser())
  }

  def loadTableData(processContext: ProcessContext,
                    broadcastConfig: Broadcast[StreamingStoreConfig],
                    tableName: String,
                    firstLevelPartitionValue: String,
                    secondLevelPartitionValue: String): DataFrame = {
    val config = broadcastConfig.value
    val hiveTableDef = config.getHiveTableDef(tableName)
    val loadDataSql = hiveTableDef.buildMergeSql(config,
                                                 firstLevelPartitionValue,
                                                 secondLevelPartitionValue)

    processContext.hiveContext.sql(loadDataSql)
  }

  def buildMergePath(config: StreamingStoreConfig,
                     tableName: String,
                     firstLevelPartitionValue: String,
                     secondLevelPartitionValue: String,
                     tag: String): String = {
    config
      .getHiveExternalTableDataRootPath() + tableName + "/" + firstLevelPartitionValue + "/" + secondLevelPartitionValue + tag
  }

}
