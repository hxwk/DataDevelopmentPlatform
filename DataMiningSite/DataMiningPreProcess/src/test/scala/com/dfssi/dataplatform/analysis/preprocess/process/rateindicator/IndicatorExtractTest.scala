package com.dfssi.dataplatform.analysis.preprocess.process.rateindicator

import com.dfssi.dataplatform.analysis.common.ProcessContext
import com.dfssi.dataplatform.analysis.preprocess.offline.OfflineAnalysisService.{getSparkAppName, getSparkTaskDef}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.mutable
import scala.xml.Elem

object IndicatorExtractTest {

  val SPARK_OFFLINE_ANALYSIS_DEF_FILE_NAME = "DriveEventDef.xml"

  val terminal_0200_tableName = "terminal_0200"
  val terminal_0705_tableName = "terminal_0705"

  def main(args: Array[String]): Unit = {
    val nameNode = "file:///"
    val appPath = "D:\\SSI\\SSIDataPlatform\\05-Implement\\SourceCode\\trunk\\SSIDataPlatform\\DataDevelopmentPlatform\\DataMiningSite\\DataMiningPreProcess\\src\\main\\resources"
    val sparkTaskDefEl = getSparkTaskDef(nameNode, appPath, SPARK_OFFLINE_ANALYSIS_DEF_FILE_NAME)

    val config = new SparkConf().setAppName(getSparkAppName(sparkTaskDefEl).getOrElse("NULL"))
    config.setMaster("local[4]")  //
    val sc = new SparkContext(config)
    sc.setLogLevel("WARN")

    val processContext: ProcessContext = new ProcessContext()
    processContext.nameNode = nameNode
    processContext.appPath = appPath
    processContext.dataFrameMap = mutable.Map[String, DataFrame]()
    processContext.hiveContext = new HiveContext(sc)
    processContext.sparkContext = sc
    processContext.processType = ProcessContext.PROCESS_TYPE_OFFLINE


    val preprocessEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_PREPROCESS)



    val hiveContext = processContext.hiveContext

    hiveContext.sql("use prod_analysis")

//    hiveContext.sql("select * from terminal_0200 where gps_time is not null")
//        .describe("vehicle_status")
//      .show(5)
//    hiveContext.sql("select * from terminal_0705")
//      .show(5)

    // 从Hive表中读取每段行程的数据进行处理
        val terminal_0200_trajectoryRdd = hiveContext
          .sql(s"SELECT * FROM $terminal_0200_tableName where gps_time is not null and gps_time > 1517385600000 ORDER BY gps_time limit 1000")
          .rdd.persist(StorageLevel.MEMORY_AND_DISK)

        val terminal_0705_canRdd = hiveContext
          .sql(s"SELECT receive_time, signal_name, value FROM $terminal_0705_tableName where receive_time is not null and receive_time > 1517385600000 ORDER BY receive_time limit 1000")
          .rdd.distinct().persist(StorageLevel.MEMORY_AND_DISK)

    for (processNode <- preprocessEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS) {
      val processEl = processNode.asInstanceOf[Elem]
      val processType = XmlUtils.getAttrValue(processEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE)

      val id = XmlUtils.getAttrValue(processEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
      val paramsMap: mutable.Map[String, String] = extractSimpleParams(processEl)

      println(paramsMap)

//      OutputRdbms.init(sc)


    }
  }


  def extractSimpleParams(processEl: Elem): mutable.Map[String, String] = {
    val paramsNodes = processEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAMS
    var paramsEl: Elem = null
    if (paramsNodes.nonEmpty) {
      paramsEl = paramsNodes.head.asInstanceOf[Elem]
    }
    extractParams(paramsEl)
  }

  def extractParams(paramsEl: Elem): mutable.Map[String, String] = {
    val paramsMap: mutable.Map[String, String] = new java.util.LinkedHashMap[String, String]()
    if (paramsEl != null) {
      val paramNodes = paramsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAM
      for (paramNode <- paramNodes) {
        val paramEl = paramNode.asInstanceOf[Elem]
        paramsMap.put(XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_NAME),
          XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_VALUE))
      }
    }

    paramsMap
  }
}

// 0200 distinct vids are:
