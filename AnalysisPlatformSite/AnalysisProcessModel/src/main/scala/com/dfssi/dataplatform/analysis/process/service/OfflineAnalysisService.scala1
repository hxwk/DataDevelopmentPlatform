package com.dfssi.dataplatform.analysis.process.service

import java.util

import com.dfssi.dataplatform.analysis.process.{ProcessContext, ProcessFactory}
import com.dfssi.dataplatform.analysis.process.utils.XmlUtils
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.hive.HiveContext

import scala.collection.JavaConversions.mapAsScalaMap
import scala.xml.Elem

/**
  * Description:
  *
  * @author PengWuKai
  * @version 2018/9/25 10:18
  */
object OfflineAnalysisService extends AnalysisService {

  def main(args: Array[String]) {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("com.dfssi").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val nameNode = args(0)
    val appPath = args(1)
    val sparkTaskDefEl = getSparkTaskDef(nameNode, appPath, SPARK_ANALYSIS_DEF_FILE_NAME)

    val processContext: ProcessContext = new ProcessContext()
    processContext.dataFrameMap = new util.LinkedHashMap[String, DataFrame]()
    processContext.processType = ProcessContext.PROCESS_TYPE_OFFLINE
    processContext.nameNode = nameNode
    processContext.appPath = appPath

    val appName = getSparkAppName(sparkTaskDefEl).getOrElse("NULL")
    val config = new SparkConf().setAppName(appName)
    val sc = new SparkContext(config)
    processContext.sparkContext = sc
    processContext.hiveContext = new HiveContext(sc)

    val seq = (sparkTaskDefEl \ "outputs").head.asInstanceOf[Elem] \ "output"
    for (se <- seq) {
      executor(processContext, se.asInstanceOf[Elem], sparkTaskDefEl)
    }
  }

  def getSparkAppName(sparkDefEl: Elem): Option[String] = {
    val name = XmlUtils.getAttrValue(sparkDefEl, "name")
    if (name == null) {
      return None
    }
    Some(name)
  }

  def init(processContext: ProcessContext, inputsEl: Elem, sparkTaskDefEl: Elem) {
    if (inputsEl == null || (inputsEl \ "input").nonEmpty) {
      return
    }
    for (inputNode <- inputsEl \ "input") {
      val inputEl = inputNode.asInstanceOf[Elem]
      val className = XmlUtils.getAttrValue(inputEl, "className")
      ProcessFactory.getProcess(className).execute(processContext, inputEl, sparkTaskDefEl)
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
