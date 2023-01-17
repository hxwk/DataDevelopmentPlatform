package com.dfssi.dataplatform.analysis.es

import java.util
import java.util.{Date, UUID}

import com.alibaba.fastjson.TypeReference
import com.dfssi.common.{DateSuffixAppender, SysEnvs}
import com.dfssi.resources.ConfigUtils
import com.dfssi.spark.SparkConfFactory
import kafka.serializer.StringDecoder
import org.apache.commons.cli.{HelpFormatter, Options, ParseException, PosixParser}
import org.apache.spark.Logging
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaManager
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.elasticsearch.spark.rdd.EsSpark

import scala.collection.JavaConversions
import scala.collection.mutable.ArrayBuffer

/**
  * Description:
  *   只解析0705的数据
  *
  * @author LiXiaoCong
  * @version 2018/2/6 8:37
  */
//spark-submit --class com.dfssi.dataplatform.analysis.es.Terminal0705ToEsFromKafka --master yarn --deploy-mode client --num-executors 2 --driver-memory 1g --executor-memory 3g --executor-cores 2 --jars $(echo /tmp/lixc/es/jars/*.jar | tr ' ' ',') /tmp/lixc/es/DataMiningAnalysis-0.1-SNAPSHOT.jar --topic CANINFORMATION_0705_TOPIC --brokerList 172.16.1.121:9092,172.16.1.122:9092,172.16.1.121:9092 --esNodes 172.16.1.221,172.16.1.222,172.16.1.223 --esClusterName elk --interval 60
class Terminal0705ToEsFromKafka extends Serializable with Logging{

  val TABLE_NAME = "terminal_0705"
  val TABLE_FIELD = "table"
  val INDEX_FIELD = "index"
  val ID_FIELD = "id"
  var TYPE_MAP: Map[String, String] = null

  def start(appName:String,
            interval:Int,
            topic0705:Set[String],
            brokerList:String,
            offset:String,
            consumeGroup:String,
            esNodes:String,
            esClusterName:String,
            partition:Int): Unit ={

    val indexCreator = new EsDateIndexCreator(esNodes, 9200, esClusterName)
    TYPE_MAP = JavaConversions.mapAsScalaMap(indexCreator.getMappingFieldType(TABLE_NAME)).toMap
    logInfo(s"${TABLE_NAME}的字段类型列表为：\n\t ${TYPE_MAP}")

    val ssc = newStreamingContext(appName, interval)
    val kafkaManager = newKafkaManager(brokerList, offset, consumeGroup)

    val orginDStream =  kafkaManager.createDirectStreamWithOffsetCheck[String, String, StringDecoder, StringDecoder](ssc, topic0705)
    store2es(orginDStream, indexCreator, esNodes, esClusterName, partition)

    orginDStream.foreachRDD(rdd => {
      kafkaManager.updateZKOffsets(rdd)
    })

    ssc.start
    ssc.awaitTermination()
  }


  private def newStreamingContext(appName:String, interval:Int): StreamingContext ={

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
    sparkConf.set("spark.streaming.concurrentJobs", "1");
    sparkConf.set("spark.driver.allowMultipleContexts", "true")
    new StreamingContext(sparkConf, Seconds(interval))
  }

  private def newKafkaManager(brokerList:String,
                              offset:String,
                              consumeGroup:String): KafkaManager ={

    val kafkaParam=Map[String,String](
      "metadata.broker.list" -> brokerList,
      "auto.offset.reset" -> offset,
      "group.id" -> consumeGroup,
      "refresh.leader.backoff.ms" -> "2000",
      "num.consumer.fetchers" -> "1")

    new KafkaManager(kafkaParam, false)
  }



  def store2es(orginDStream: DStream[(String, String)],
               indexCreator: EsDateIndexCreator,
               esNodes:String,
               esClusterName:String,
               partition:Int) = {

    val dealDStream = orginDStream.map(pair => converter(pair._2)).filter(_._1 != null)
    dealDStream.foreachRDD(rdd =>{
      rdd.persist()

      //检查并创建不存在的索引
      val indexs = rdd.map(_._1).distinct(1).collect()
      indexs.foreach(index =>{
        indexCreator.createMapping(index, TABLE_NAME)
      })

      //类型校验
      val dataRDD = rdd.flatMap(_._2).map(record =>{
        fieldValueCheck(record)
        record
      })

      //存储到es
      EsSpark.saveToEs(dataRDD, s"{${INDEX_FIELD}}/{${TABLE_FIELD}}", Map("es.mapping.id" -> ID_FIELD,
        "es.nodes" -> esNodes,
        "es.clustername" -> esClusterName,
        //"es.user"-> esUser,
        "es.batch.size.entries"->"5000"))
      rdd.unpersist()
    })

  }

  private def converter(line: String): (String, ArrayBuffer[util.Map[String, Object]]) ={
    var records: ArrayBuffer[util.Map[String, Object]] = new ArrayBuffer[util.Map[String, Object]]()
    try {
      val record = com.alibaba.fastjson.JSON.parseObject(line,
        new TypeReference[java.util.Map[String, Object]]() {})

      //确定记录所属的索引
      val gpsTime = ConfigUtils.getAsLongWithDefault(record, "receiveTime", System.currentTimeMillis())
      val index = DateSuffixAppender.DAY.append(TABLE_NAME, new Date(gpsTime))

      //删除无用字段
      val beanlist = record.remove("messageBeanList")
      record.remove("itemNum")
      record.remove("id")

      //增加索引信息
      record.put(INDEX_FIELD, index)
      record.put(TABLE_FIELD, TABLE_NAME)

      //解析数据
      if(beanlist != null){
        val beans = beanlist.asInstanceOf[util.List[util.Map[String, Object]]]
        val n = beans.size()
        for(i <- 0 until n){
          val map = beans.get(i)

          val id = ConfigUtils.getAsStringWithDefault(map, ID_FIELD, UUID.randomUUID().toString)
          map.put(ID_FIELD, id)

          map.putAll(record)

          records += map
        }
      }
      (index, records)
    } catch {
      case e: Exception => {
        logError(s"解析${TABLE_NAME}的记录 ${line} 失败。", e)
        (null, null)
      }
    }
  }



  //字段对应的值类型校验
  private def fieldValueCheck(record: java.util.Map[String, Object]): Unit ={

    TYPE_MAP.foreach(kv =>{

      val value = record.get(kv._1)
      if(value != null) {
        try {
          kv._2.toLowerCase match {
            case "long" =>
              if (!value.isInstanceOf[java.lang.Long]) {
                record.put(kv._1, value.toString.toLong.asInstanceOf[java.lang.Object])
              }
            case "double" =>
              if (!value.isInstanceOf[java.lang.Double]) {
                record.put(kv._1, value.toString.toDouble.asInstanceOf[java.lang.Object])
              }
            case "integer" =>
              if (!value.isInstanceOf[java.lang.Integer]) {
                record.put(kv._1, value.toString.toInt.asInstanceOf[java.lang.Object])
              }
            case "float" =>
              if (!value.isInstanceOf[java.lang.Float]) {
                record.put(kv._1, value.toString.toFloat.asInstanceOf[java.lang.Object])
              }
            case "text" =>
              if (!value.isInstanceOf[java.lang.String]
                && !value.getClass.isArray) {
                record.put(kv._1, value.toString.asInstanceOf[java.lang.Object])
              }
            case "string" =>
              if (!value.isInstanceOf[java.lang.String]
                && !value.getClass.isArray && !SysEnvs.isCollection(value)) {
                record.put(kv._1, value.toString.asInstanceOf[java.lang.Object])
              }
            case _ =>
              ""
          }
        } catch {
          case e =>
            logError(s"类型转换失败：${kv._1}:${value}:${kv._2} ,\n\t ${record}", e)
            record.remove(kv._1)
        }
      }else{
        record.remove(kv._1)
      }

    })

  }
}

object Terminal0705ToEsFromKafka extends Logging {

  def main(args: Array[String]): Unit ={

    val lines = parseArgs(args)
    val appName: String = lines.getOptionValue("appName", "Terminal0705ToEsFromKafka")
    val topicSet: Set[String] = lines.getOptionValue("topic").split(",").toSet
    val offset: String = lines.getOptionValue("offset", "smallest")
    val interval: Int = lines.getOptionValue("interval", "300").toInt
    val brokerList: String = lines.getOptionValue("brokerList")
    val consumeGroup = lines.getOptionValue("consumeGroup", "Terminal0705ToEsFromKafka")
    val esNodes = lines.getOptionValue("esNodes")
    val esClusterName = lines.getOptionValue("esClusterName")
    val partition = lines.getOptionValue("partition", "8").toInt

    logInfo(" 任务启动配置如下 ： ")
    logInfo(s" 		appName     ：  $appName ")
    logInfo(s" 		topic       ：  $topicSet ")
    logInfo(s" 		offset      ：  $offset ")
    logInfo(s" 		interval    ：  $interval ")
    logInfo(s" 		brokerList  ：  $brokerList ")
    logInfo(s" 	 consumeGroup  ：  $consumeGroup ")
    logInfo(s" 	 esNodes  ：  $esNodes ")
    logInfo(s" 	 esClusterName  ：  $esClusterName ")
    logInfo(s" 	 partition  ：  $partition ")

    new Terminal0705ToEsFromKafka().start(appName, interval, topicSet, brokerList, offset,
      consumeGroup, esNodes, esClusterName, partition)

  }

  @throws[ParseException]
  private def parseArgs(args: Array[String]) = {

    val options = new Options

    options.addOption("help", false, "帮助 打印参数详情")
    options.addOption("appName", true, "application名称, 默认为：Terminal0705ToEsFromKafka")
    options.addOption("topic", true, "kafka 的 0705数据的topic名称")
    options.addOption("offset", true, "kafka读取配置 auto.offset.reset，值为 largest 或 smallest，默认为smallest ")
    options.addOption("brokerList", true, "kafka的 broker列表，格式为 ip:port,ip:port")
    options.addOption("interval", true, "spark streaming 的批次间隔时间 单位为 秒， 默认 300")
    options.addOption("consumeGroup", true, "kafka消费者组")
    options.addOption("esNodes", true, "es集群节点")
    options.addOption("esClusterName", true, "es集群名称")

    val parser = new PosixParser
    val lines = parser.parse(options, args)
    if (lines.hasOption("help")
      || args.length == 0 || !lines.hasOption("topic") || !lines.hasOption("brokerList")
      || !lines.hasOption("esNodes") || !lines.hasOption("esClusterName"))
    {
      val formatter = new HelpFormatter
      formatter.setOptPrefix(HelpFormatter.DEFAULT_LONG_OPT_PREFIX)
      formatter.printHelp("Terminal0705ToEsFromKafka", options)
      System.exit(0)
    }
    lines
  }
}




