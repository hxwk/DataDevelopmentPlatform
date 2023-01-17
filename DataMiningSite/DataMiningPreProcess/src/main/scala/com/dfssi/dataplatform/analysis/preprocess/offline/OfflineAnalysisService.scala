package com.dfssi.dataplatform.analysis.preprocess.offline

import java.util

import com.dfssi.dataplatform.analysis.common.ProcessContext
import com.dfssi.dataplatform.analysis.preprocess.initialize.AbstractInitProcess
import com.dfssi.dataplatform.analysis.preprocess.{AnalysisService, ProcessFactory}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.hive.HiveContext

import scala.collection.JavaConversions.mapAsScalaMap
import scala.xml.Elem

/**
  * Spark Process
  *
  */
object OfflineAnalysisService extends AnalysisService {

  val SPARK_OFFLINE_ANALYSIS_DEF_FILE_NAME = "OfflineTaskDef.xml"

  def main(args: Array[String]) {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("com.dfssi").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val nameNode = args(0);
    val appPath = args(1);
    println(nameNode,appPath)
    val sparkTaskDefEl = getSparkTaskDef(nameNode, appPath, SPARK_OFFLINE_ANALYSIS_DEF_FILE_NAME);

    val processContext: ProcessContext = new ProcessContext();
    processContext.dataFrameMap = new util.LinkedHashMap[String, DataFrame]();
    processContext.processType = ProcessContext.PROCESS_TYPE_OFFLINE;
    processContext.nameNode = nameNode;
    processContext.appPath = appPath;

    // add a init step by lulin @ 2018/3/5
    var inited:Boolean = true
    val initsEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_INITS)
    if (initsEl == null || (initsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_INIT).isEmpty) {
      val appName = getSparkAppName(sparkTaskDefEl).getOrElse("NULL")
      val config = new SparkConf().setAppName(appName)
      config.setMaster("local"); //yarn-cluster
      val sc = new SparkContext(config);

      if(applicationExist(appName, sc)){
        logError(s"任务${appName}已在Yarn的运行列表中...")
        sc.stop()
        return
      }
      sc.setLogLevel("ERROR")
      processContext.hiveContext = new HiveContext(sc);
      processContext.sparkContext = sc;
    }else{
      inited = processInits(processContext, initsEl, sparkTaskDefEl)
    }

    if(inited){
      val inputsEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUTS);
      val preprocessEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_PREPROCESS);
      val algorithmsEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_ALGORITHMS);
      val outputsEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUTS);

      processInputs(processContext, inputsEl, sparkTaskDefEl);
      processConvert(processContext, preprocessEl, sparkTaskDefEl);
      processAlgorithms(processContext, algorithmsEl, sparkTaskDefEl);
      processOutputs(processContext, outputsEl, sparkTaskDefEl);

      if(processContext.sparkContext != null)
        processContext.sparkContext.stop()

    }else{
      logError("-----任务已经停止，返回")
    }

  }


  def getSparkAppName(sparkDefEl: Elem): Option[String] = {
    var name = XmlUtils.getAttrValue(sparkDefEl, "name");
    Option(name)
  }

  //一个任务只会存在一次初始化的过程
  def processInits(processContext: ProcessContext, initsEl: Elem, sparkTaskDefEl: Elem): Boolean = {

    val processEl = XmlUtils.getSingleSubXmlEl(initsEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_INIT)

    val processType =
      XmlUtils.getAttrValue(processEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE)
    val process = ProcessFactory.getProcess(processType).asInstanceOf[AbstractInitProcess]

    //获取用户自定义的 sparkContext
    val sparkContext = process.getSparkContext(processEl, sparkTaskDefEl)
    if(!process.isOver){
      if(sparkContext != null) {
        processContext.sparkContext = sparkContext
        processContext.hiveContext = new HiveContext(sparkContext)
      }
      //执行初始化操作
      process.execute(processContext, processEl, sparkTaskDefEl)
    }
    !process.isOver
  }

  def processInputs(processContext: ProcessContext, inputsEl: Elem, sparkTaskDefEl: Elem) {
    if (inputsEl == null || (inputsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUT).size == 0) {
      return;
    }

    for (inputNode <- (inputsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUT)) {
      var inputEl = inputNode.asInstanceOf[Elem];
      var inputType = XmlUtils.getAttrValue(inputEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE);
      ProcessFactory.getProcess(inputType).execute(processContext, inputEl, sparkTaskDefEl);
    }
  }

  def processConvert(processContext: ProcessContext, preprocessEl: Elem, sparkTaskDefEl: Elem) {
    if (preprocessEl == null || (preprocessEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS).size == 0) {
      return;
    }

    for (processNode <- (preprocessEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS)) {
      var processEl = processNode.asInstanceOf[Elem];
      var processType = XmlUtils.getAttrValue(processEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE);
      ProcessFactory.getProcess(processType).execute(processContext, processEl, sparkTaskDefEl);
    }
  }

  def processAlgorithms(processContext: ProcessContext, algorithmsEl: Elem, sparkTaskDefEl: Elem) {
    if (algorithmsEl == null || (algorithmsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_ALGORITHM).size == 0) {
      return;
    }

    for (algorithmNode <- (algorithmsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_ALGORITHM)) {
      var algorithmEl = algorithmNode.asInstanceOf[Elem];
      val algorithmType = XmlUtils.getAttrValue(algorithmEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE);
      ProcessFactory.getProcess(algorithmType).execute(processContext, algorithmEl, sparkTaskDefEl);
    }
  }

  def processOutputs(processContext: ProcessContext, outputsEl: Elem, sparkTaskDefEl: Elem) {
    if (outputsEl == null || (outputsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUT).size == 0) {
      return;
    }

    for (outputNode <- (outputsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUT)) {
      var outputEl = outputNode.asInstanceOf[Elem];
      val outputType = XmlUtils.getAttrValue(outputEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE);

      ProcessFactory.getProcess(outputType).execute(processContext, outputEl, sparkTaskDefEl);
    }
  }

}
