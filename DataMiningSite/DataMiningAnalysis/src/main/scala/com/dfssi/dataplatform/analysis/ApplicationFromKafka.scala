package com.dfssi.dataplatform.analysis

import com.alibaba.fastjson.TypeReference
import com.dfssi.dataplatform.analysis.config.HdfsXmlConfig
import com.dfssi.spark.SparkConfFactory
import com.dfssi.spark.common.Applications
import org.apache.commons.cli.{HelpFormatter, Options, ParseException, PosixParser}
import org.apache.spark.Logging
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/27 20:25
  */
trait ApplicationFromKafka extends Serializable with Logging{

  /**
    * xml配置文件
    * @return
    */
  def configName: String

  def appName: String

  def checkpointPath: String = null

  def hdfsConfigPath: String = "/user/hdfs/config/prod/streaming"

  def execute(configPath: String,
              batchDuration: Long): Unit = {

    val ssc = getOrCreateStreamingContext(batchDuration)

    val cp = getConfigPath(configPath)
    val config: HdfsXmlConfig = HdfsXmlConfig(ssc.sparkContext, cp, configName)


    val kafkaStream = config.kafkaStream

    //Logger.getRootLogger.setLevel(Level.WARN)

    val orginDStream = kafkaStream.createDirectStreamWithOffsetCheck(ssc)

    start(ssc, config, orginDStream)

    orginDStream.foreachRDD(rdd => {
      kafkaStream.kafkaManager.updateZKOffsets(rdd)
    })

    ssc.start
    ssc.awaitTermination()
  }

  def start(ssc: StreamingContext, config: HdfsXmlConfig, orginDStream: InputDStream[(String, String)])

  private def getConfigPath(configPath: String): String ={

    var cp = configPath
    if(cp == null)cp = hdfsConfigPath
    cp
  }

  protected def getOrCreateStreamingContext(batchDuration: Long): StreamingContext ={
    if(checkpointPath == null)
      createStreamingContext(batchDuration)
    else
    //激活checkpoint
      StreamingContext.getOrCreate(checkpointPath,  () => {createStreamingContext(batchDuration)})
  }

  protected def createStreamingContext(batchDuration: Long): StreamingContext ={

    val sparkConf = SparkConfFactory.newSparkStreamingConf(appName)
    sparkConf.set("es.nodes.discovery", "true")
    sparkConf.set("es.batch.size.bytes", "300000")
    sparkConf.set("es.batch.size.entries", "10000")
    sparkConf.set("es.batch.write.refresh", "false")
    sparkConf.set("es.batch.write.retry.count", "50")
    sparkConf.set("es.batch.write.retry.wait", "500")
    sparkConf.set("es.http.timeout", "5m")
    sparkConf.set("es.http.retries", "50")
    sparkConf.set("es.http.enabled", "true")
    sparkConf.set("es.action.heart.beat.lead", "50")
    //sparkConf.set("spark.streaming.concurrentJobs", "1");
    //sparkConf.set("spark.driver.allowMultipleContexts", "true")

    val ssc = new StreamingContext(sparkConf, Seconds(batchDuration))

    try {
      if(Applications.applicationExist(ssc.sparkContext)){
        logError("任务已运行，毋须重复启动...")
        ssc.stop(true)
        System.exit(-1)
      }
    } catch {
      case e: Exception =>
        logError("检测相同任务失败。", e)
    }

    ssc
  }

  protected def parseJsonToJMap(dStream: DStream[String]):
  DStream[java.util.Map[String,Object]] ={
    dStream.map(json =>{
      var record: java.util.Map[String, Object] = null
      try {
        record = com.alibaba.fastjson.JSON.parseObject(json,
          new TypeReference[java.util.Map[String, Object]]() {})
      }catch {
        case e: Exception => {
          logError(s"解析json串失败：\n\t ${json}", e)
        }
      }
      record
    }).filter(_ != null)
  }

}

object ApplicationFromKafka extends Logging{

  @throws[ParseException]
  def parseArgs(applicationName: String, args: Array[String]) = {
    val options = new Options
    options.addOption("help", false, "帮助 打印参数详情")
    options.addOption("configPath", true, "配置文件路径，hdfs上的路径")
    options.addOption("batchDuration", true, "流任务批次时间，默认60， 单位：秒")

    val parser = new PosixParser
    val lines = parser.parse(options, args)
    if (lines.hasOption("help"))
    {
      val formatter = new HelpFormatter
      formatter.setOptPrefix(HelpFormatter.DEFAULT_LONG_OPT_PREFIX)
      formatter.printHelp(applicationName, options)
      System.exit(0)
    }
    lines
  }
}
