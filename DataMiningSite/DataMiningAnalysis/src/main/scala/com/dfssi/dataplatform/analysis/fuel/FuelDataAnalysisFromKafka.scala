package com.dfssi.dataplatform.analysis.fuel

import com.dfssi.spark.SparkContextFactory
import kafka.serializer.StringDecoder
import org.apache.commons.cli.{HelpFormatter, Options, ParseException, PosixParser}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.Logging
import org.apache.spark.streaming.kafka.KafkaManager

/**
  * Description:
  *    从kafka中实时读取数据， 处理后写入greenplum
  *    待解决的性能问题：
  *        在Dstream中直接使用jdbc的方式操作greenplum，会造成连接过多的情况！
  *
  * @author LiXiaoCong
  * @version 2017/12/29 10:21
  */
//spark-submit --class com.dfssi.dataplatform.analysis.fuel.FuelDataAnalysisFromKafka  --master yarn --deploy-mode client --num-executors 2 --driver-memory 1g --executor-memory 3g --executor-cores 2 --jars $(echo /tmp/lixc/jars/*.jar | tr ' ' ',') /tmp/lixc/DataMiningAnalysis-0.1-SNAPSHOT.jar --topics test808 --brokerList 172.16.1.121:9092,172.16.1.122:9092,172.16.1.121:9092 --interval 60
class FuelDataAnalysisFromKafka(fuelConf: FuelConfig)
  extends FuelAndTripWriter with Logging{

  /*def start(appName:String, groupId:String, broker:String, batchInterval : Int = 300 ,
            topicSet:Set[String], offset:String = "smallest", partitions:Int = 1): Unit ={

    //激活checkpoint
    val ssc = StreamingContext.getOrCreate("/tmp/trip/checkpoint",  () => {
      function(appName, groupId, broker, batchInterval, topicSet, offset, partitions)
    })

    SparkContextFactory.startSparkStreaming(ssc)
  }*/

   def function(appName:String, groupId:String, broker:String, batchInterval : Int = 300 ,
                topicSet:Set[String], offset:String = "smallest", partitions:Int = 1){

    val ssc = SparkContextFactory.newSparkStreamingContext(appName, batchInterval)

     val bcConf = ssc.sparkContext.broadcast(fuelConf)

    val manager: KafkaManager = KafkaManager(broker, groupId, 1, offset, false)
    val dstream = manager.createDirectStreamWithOffsetCheck[String, String, StringDecoder, StringDecoder](ssc, topicSet)

     val data = dstream.map(_._2)
     fuelAndTripWrite(data, bcConf, partitions, batchInterval, groupId)

    //记录offset
    dstream.foreachRDD(rdd => {
      manager.updateZKOffsets(rdd)
    })

     SparkContextFactory.startSparkStreaming(ssc)
     //ssc
  }



}

object FuelDataAnalysisFromKafka extends Logging{

  //默认参数
  private val _NAME = "realtime-fuels"
  private val _GROUPID = "realtime-fuels-group"
  private val _OFFSET = "smallest"
  private val _PARTITIONS = "3"
  private val _INTERVAL = "300"
  private val _MINTIME = "0"

  def main(args: Array[String]) {

    Logger.getLogger("org.apache.spark").setLevel(Level.INFO)
    Logger.getLogger("com.dfssi").setLevel(Level.INFO)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val lines = parseArgs(args)
    val appName:String = lines.getOptionValue("appName", _NAME)
    val groupId:String = lines.getOptionValue("groupId", _GROUPID)
    val offset:String = lines.getOptionValue("offset", _OFFSET)
    val interval:Int = lines.getOptionValue("interval", _INTERVAL).toInt
    val topicSet:Set[String] = lines.getOptionValue("topics").split(",").toSet
    val brokerList:String = lines.getOptionValue("brokerList")

    val partitions = lines.getOptionValue("partitions", _PARTITIONS).toInt

    val env = lines.getOptionValue("env", "NONE")


    logInfo(" 任务启动配置如下 ： ")
    logInfo(s" 		appName     ：  $appName ")
    logInfo(s" 		groupId     ：  $groupId ")
    logInfo(s" 		topic       ：  $topicSet ")
    logInfo(s" 		offset      ：  $offset ")
    logInfo(s" 		interval    ：  $interval ")
    logInfo(s" 		brokerList  ：  $brokerList ")
    logInfo(s" 	  partitions  ：  $partitions ")
    logInfo(s" 	  env         ：  $env ")

    val fuelConf = new FuelConfig(env, true)
    val app = new FuelDataAnalysisFromKafka(fuelConf)
    app.function(appName, groupId, brokerList, interval, topicSet, offset, partitions)
  }

  @throws[ParseException]
  private def parseArgs(args: Array[String]) = {

    val options = new Options

    options.addOption("help", false, "帮助 打印参数详情")
    options.addOption("appName", true, s"application名称, 默认为：${_NAME}")
    options.addOption("interval", true, s"spark streaming 的批次间隔时间 单位为 秒， 默认 ${_INTERVAL}")
    options.addOption("partitions", true, s"默认分区数 核数的整数倍 默认为 ${_PARTITIONS}")
    options.addOption("mintime", true, s"最小数据时间，小于该时间的数据将会被过滤。单位为秒, 默认为${_MINTIME}")

    options.addOption("groupId", true, s"任务消费者组名称, 默认为：${_GROUPID}")
    options.addOption("offset", true, s"kafka读取配置 auto.offset.reset，值为 largest 或 smallest，默认为${_OFFSET} ")
    options.addOption("topics", true, "kafka 的 topic名称")
    options.addOption("brokerList", true, "kafka的 broker列表，格式为 ip:port,ip:port")

    options.addOption("env", true, "运行环境: PRO, DEV, TEST, NONE, 默认为NONE")


    val formatter = new HelpFormatter
    formatter.setOptPrefix(HelpFormatter.DEFAULT_LONG_OPT_PREFIX)

    val parser = new PosixParser
    val lines = parser.parse(options, args)

    if(args.length == 0 || lines.hasOption("help")){
      formatter.printHelp(_NAME, options)
      System.exit(0)
    }


    if(lines.getOptionValue("topics") == null){
      logError("数据接入的topics参数不能为空。")
      formatter.printHelp(_NAME, options)
      System.exit(0)
    }

    if(lines.getOptionValue("brokerList") == null){
      logError("数据接入的brokerList参数不能为空。")
      formatter.printHelp(_NAME, options)
      System.exit(0)
    }

    lines
  }

}
