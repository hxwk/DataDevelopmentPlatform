package com.dfssi.dataplatform.analysis.ccv.fuel

import com.dfssi.dataplatform.analysis.ApplicationFromKafka
import com.dfssi.dataplatform.analysis.config.HdfsXmlConfig
import com.dfssi.dataplatform.analysis.fuel.FuelTables
import com.dfssi.spark.common.Functions
import org.apache.commons.cli.CommandLine
import org.apache.log4j.{Level, Logger}
import org.apache.spark.Logging
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream

/**
  * Description:
  *     油耗数据分析
  *
  * @author LiXiaoCong
  * @version 2018/5/15 9:49 
  */
class FuelDataAnalysisFromKafka extends FuelDataAnalysis with ApplicationFromKafka{


    override def configName: String = "ccv-fuel.xml"

    override def appName: String = "CVV_FUELANALYSIS"


    override def hdfsConfigPath: String = "/user/hdfs/config/prod/cvv"

    override def start(ssc: StreamingContext,
                       config: HdfsXmlConfig,
                       orginDStream: InputDStream[(String, String)]): Unit = {

        val fuelConfig = new VehicleFuelConfig(config)
        val bcConf = ssc.sparkContext.broadcast(fuelConfig)

        val connection = fuelConfig.getConnection()
        //检查表是否存在
        FuelTables.checkAndCreateTripTable(fuelConfig.fueltripTable, connection)
        FuelTables.checkAndCreateTotalFuelTable(fuelConfig.totalfuelTable, connection)
        FuelTables.checkAndCreateFuelTable(fuelConfig.fuelTable, connection)
        FuelTables.checkAndCreateAbnormalDrivingTable(fuelConfig.abnormaldriving, connection)
        connection.close()


        val partitions = Functions.getPartitionByAppParam(ssc.sparkContext)

        val data = orginDStream.map(_._2)
        executeAnalysis(data, bcConf, partitions)

    }

    /*def function(appName:String,
                 groupId:String,
                 broker:String,
                 batchInterval : Int = 300 ,
                 topicSet:Set[String],
                 offset:String = "smallest",
                 partitions:Int = 1){

        //val cancellable = startSyncTask()

        val ssc = SparkContextFactory.newSparkStreamingContext(appName, batchInterval)

        val bcConf = ssc.sparkContext.broadcast(fuelConf)

        val manager: KafkaManager = KafkaManager(broker, groupId, 1, offset, false)
        val dstream = manager.createDirectStreamWithOffsetCheck[String, String, StringDecoder, StringDecoder](ssc, topicSet)

        val data = dstream.map(_._2)
        executeAnalysis(data, bcConf, partitions, batchInterval, groupId)

        //记录offset
        dstream.foreachRDD(rdd => {
            manager.updateZKOffsets(rdd)
        })

        SparkContextFactory.startSparkStreaming(ssc)
       // cancellable.cancel()
    }*/


   /* def startSyncTask(): Cancellable ={
        val cancellable = FuelDataSyncFromRedis.startSync(fuelConf)
        logInfo(s"启动数据同步任务成功。")
        cancellable
    }*/

}

object FuelDataAnalysisFromKafka extends Logging{

    def main(args: Array[String]): Unit ={

        Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
        Logger.getLogger("com.dfssi").setLevel(Level.INFO)
        Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

        //val lines = ApplicationFromKafka.parseArgs("FuelDataAnalysisFromKafka", args)
        val lines: CommandLine = ApplicationFromKafka.parseArgs("FuelDataAnalysisFromKafka",args)
        val configPath = lines.getOptionValue("configPath")
        val batchDuration = lines.getOptionValue("batchDuration", "60").toLong

        logInfo(" 任务启动配置如下 ： ")
        logInfo(s" 		configPath     ：  $configPath ")
        logInfo(s" 		batchDuration  ：  $batchDuration ")

        val evsDataDetectStatsFromKafka = new FuelDataAnalysisFromKafka()
        evsDataDetectStatsFromKafka.execute(configPath, batchDuration)

    }


}