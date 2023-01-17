package com.dfssi.dataplatform.analysis.es

import com.dfssi.dataplatform.analysis.ApplicationFromKafka
import com.dfssi.dataplatform.analysis.config.HdfsXmlConfig
import org.apache.log4j.{Level, Logger}
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream

/**
  * Description:
  *   只解析0705的数据
  *
  * @author LiXiaoCong
  * @version 2018/2/6 8:37
  */
//spark-submit --class com.dfssi.dataplatform.analysis.es.TerminalDataToEsFromKafka --master yarn --deploy-mode client --num-executors 2 --driver-memory 1g --executor-memory 3g --executor-cores 2 --jars $(echo /tmp/lixc/es/jars/*.jar | tr ' ' ',') /tmp/lixc/es/DataMiningAnalysis-0.1-SNAPSHOT.jar --topic CANINFORMATION_0705_TOPIC --brokerList 172.16.1.121:9092,172.16.1.122:9092,172.16.1.121:9092 --esNodes 172.16.1.221,172.16.1.222,172.16.1.223 --esClusterName elk --interval 60
class TerminalDataToEsFromKafka extends ApplicationFromKafka{

  /**
    * xml配置文件
    *
    * @return
    */
  override def configName: String = "es-store-config.xml"

  override def appName: String = "KAFKA_DATA_TO_ES"

  override def start(ssc: StreamingContext,
                     config: HdfsXmlConfig,
                     orginDStream: InputDStream[(String, String)]): Unit = {

    val kafkaToEsDataProcess = createKafkaToEsProcess(config)
    val esOutput = createEsOutput(config)
    val idField = esOutput.idField

    val dstream = parseJsonToJMap(orginDStream.map(_._2))
    val typeEntityMap = kafkaToEsDataProcess.getTypeEntityMap

    dstream.foreachRDD(rdd =>{

      //数据转换处理
      val resRDD = kafkaToEsDataProcess.executeProcess(rdd, idField)

      //全部数据
      val allDataRDD = resRDD.map(_.record)
      //获取并拼接最新一条的数据
      val esRDD = allDataRDD.union(getLatestData(resRDD, idField))

      //检查和创建索引
      val indexs = esRDD.map(record =>s"${record.get(Constants.INDEX_FIELD)},${record.get(Constants.TABLE_FIELD)}")
              .distinct(1).collect()
      indexs.foreach(index => {
        val indexTypes = index.split(",")
        val typeEntity = typeEntityMap(indexTypes(1))
        esOutput.createIndex(typeEntity, indexTypes(0))
      })

      //写入es
      esOutput.executeOutput(esRDD)
    })

  }

  private def getLatestData(resRDD: RDD[EsRecord],
                            idField: String): RDD[java.util.Map[String, Object]] ={

    val latestDataRDD = resRDD.filter(_.subject != null)
            .map(r => (r.subject, (r.time, r.record)))
            .reduceByKey((r1, r2) =>{if(r1._1 >= r2._1) r1 else r2 })
            .map(r =>{
              val record = r._2._2
              val table = record.get(Constants.TABLE_FIELD)
              record.put(idField, r._1)
              record.put(Constants.INDEX_FIELD, s"${table}_latest")
              record
            })

    latestDataRDD
  }

  private def createKafkaToEsProcess(config: HdfsXmlConfig): KafkaToEsDataProcess ={
    val elem = config.getConfigNode( "es-index-defs")
    KafkaToEsDataProcess.buildFromXmlElem(elem)
  }

  private def createEsOutput(config: HdfsXmlConfig): EsOutput ={
    val elem = config.getConfigNode("es-output")
    EsOutput.buildFromXmlElem(elem, config.environmentConfig.getEnvironment("elasticsearch"))
  }

}

object TerminalDataToEsFromKafka extends Logging {

  def main(args: Array[String]): Unit ={

    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("com.dfssi").setLevel(Level.INFO)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val lines = ApplicationFromKafka.parseArgs("TerminalDataToEsFromKafka", args)
    val configPath = lines.getOptionValue("configPath")
    val batchDuration = lines.getOptionValue("batchDuration", "30").toLong

    logInfo(" 任务启动配置如下 ： ")
    logInfo(s" 		batchDuration  ：  $batchDuration ")
    logInfo(s" 		configPath   ：  $configPath ")

    new TerminalDataToEsFromKafka().execute(configPath, batchDuration)
  }

}





