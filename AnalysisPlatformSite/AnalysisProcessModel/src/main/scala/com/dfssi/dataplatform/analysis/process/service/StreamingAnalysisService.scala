package com.dfssi.dataplatform.analysis.process.service

import java.util

import com.dfssi.dataplatform.analysis.process.{ProcessContext, ProcessFactory}
import com.dfssi.dataplatform.analysis.process.utils.{SparkDefTag, StreamingLog, XmlUtils}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.JavaConversions.mapAsScalaMap
import scala.xml.Elem

/**
  * Description:
  *
  * @author PengWuKai
  * @version 2018/9/25 14:18 
  */
object StreamingAnalysisService extends AnalysisService {

  def main(args: Array[String]): Unit = {
    val nameNode = args(0)
    val appPath = args(1)
    val processContext: ProcessContext = new ProcessContext()
    processContext.dataFrameMap = new util.LinkedHashMap[String, DataFrame]()
    processContext.processType = ProcessContext.PROCESS_TYPE_STREAMING
    processContext.nameNode = nameNode
    processContext.appPath = appPath

    StreamingLog.setStreamingLogLevels()

    val sparkTaskDefEl = getSparkTaskDef(nameNode, appPath, SPARK_ANALYSIS_DEF_FILE_NAME);
    val appName = XmlUtils.getAttrValueWithDefault(sparkTaskDefEl,
      SparkDefTag.SPARK_DEF_ATTR_TAG_NAME, "StreamingAnalysis")
    val batch = XmlUtils.getAttrValueWithDefault(sparkTaskDefEl,
      SparkDefTag.SPARK_DEF_ATTR_TAG_BATCHDURATIONSECOND, "120").toInt

    val config = new SparkConf().setAppName(appName)
    config.setMaster("yarn-cluster")
    val sc = new SparkContext(config)

    if(applicationExist(appName, sc)){
      logError(s"任务${appName}已在Yarn的运行列表中...")
      sc.stop()
      return
    }
    sc.setLogLevel("ERROR")
    processContext.streamingContext = new StreamingContext(sc, Seconds(batch))
    processContext.hiveContext = new HiveContext(sc)
    processContext.sparkContext = sc

    start(sparkTaskDefEl, processContext)

    if(processContext.streamingContext != null){
      processContext.streamingContext.start()
      processContext.streamingContext.awaitTermination()
      processContext.streamingContext.stop()
    }
  }

  def start(elem: Elem, context: ProcessContext): Unit = {
    val seq = (elem \ "outputs").head.asInstanceOf[Elem] \ "output"
    for (se <- seq) {
      executor(context, se.asInstanceOf[Elem], elem)
    }
  }

  def executor(processContext: ProcessContext, stepEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val map = new util.LinkedHashMap[String, Elem] {}
    val seq = (sparkTaskDefEl \ "inputs").head.asInstanceOf[Elem] \ "input" ++
      (sparkTaskDefEl \ "preprocess").head.asInstanceOf[Elem] \ "process" ++
      (sparkTaskDefEl \ "algorithms").head.asInstanceOf[Elem] \ "algorithm" ++
      (sparkTaskDefEl \ "outputs").head.asInstanceOf[Elem] \ "output"
    for (se <- seq) {
      map.put(XmlUtils.getAttrValue(se.asInstanceOf[Elem], "id"), se.asInstanceOf[Elem])
    }
    val inputIds = XmlUtils.getAttrValue(stepEl, "inputIds")
    if (null != inputIds && inputIds.nonEmpty) {
      for (inputId <- inputIds.split(",")) {
        if (!processContext.dataFrameMap.contains(inputId)) {
          executor(processContext, map.get(inputId.substring(0, 20)), sparkTaskDefEl)
        }
      }
    }
    try {
      val className = XmlUtils.getAttrValue(stepEl, "className")
      ProcessFactory.getProcess(className).execute(processContext, stepEl, sparkTaskDefEl)
    } catch {
      case e:Exception =>
        logError(s"getProcess**********************：$stepEl.")
    }
  }
}
