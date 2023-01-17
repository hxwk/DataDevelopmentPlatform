package com.dfssi.dataplatform.analysis.preprocess.streaming

import java.util

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.initialize.AbstractInitProcess
import com.dfssi.dataplatform.analysis.preprocess.{AnalysisService, ProcessFactory}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, StreamingLog, XmlUtils}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConversions.mapAsScalaMap
import scala.xml.Elem

/**
  * Streaming Process
  *
  */
object StreamingAnalysisService2 extends AnalysisService {

  val SPARK_STREAMING_ANALYSIS_DEF_FILE_NAME = "StreamingTaskDef.xml"

  def main(args: Array[String]) {
    val nameNode = args(0)
    val appPath = args(1)
    val processContext: ProcessContext = new ProcessContext()
    processContext.dataFrameMap = new util.LinkedHashMap[String, DataFrame]()
    processContext.processType = ProcessContext.PROCESS_TYPE_STREAMING
    processContext.nameNode = nameNode
    processContext.appPath = appPath

    StreamingLog.setStreamingLogLevels()

    val sparkTaskDefEl = getSparkTaskDef(nameNode, appPath, SPARK_STREAMING_ANALYSIS_DEF_FILE_NAME);
    val appName = XmlUtils.getAttrValueWithDefault(sparkTaskDefEl,
    SparkDefTag.SPARK_DEF_ATTR_TAG_NAME, "StreamingAnalysis")
    val batch = XmlUtils.getAttrValueWithDefault(sparkTaskDefEl,
      SparkDefTag.SPARK_DEF_ATTR_TAG_BATCHDURATIONSECOND, "120").toInt

    var inited:Boolean = true
    val initsEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_INITS)
    if (initsEl == null || (initsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_INIT).isEmpty) {
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
    }else{
      inited = processInits(processContext, initsEl, sparkTaskDefEl)
      if(inited && processContext.sparkContext != null){
        processContext.streamingContext = new StreamingContext(processContext.sparkContext, Seconds(batch))
      }else{
        logWarning("初始化失败，或者sparkContext为null")
      }
    }

    if(inited){
      start(sparkTaskDefEl, processContext)

      if(processContext.streamingContext != null){
        processContext.streamingContext.start()
        processContext.streamingContext.awaitTermination()
        processContext.streamingContext.stop()
      }
    }

  }

 private def start(sparkTaskDefEl: Elem, processContext: ProcessContext): Unit ={

   //执行输入
   val inputs = getProcesss(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUTS,
     SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUT)
   inputs.foreach(kv => kv._1.execute(processContext, kv._2, sparkTaskDefEl))

   //执行转换
   val conveters = getProcesss(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_PREPROCESS,
     SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS)
   conveters.foreach(kv => kv._1.execute(processContext, kv._2, sparkTaskDefEl))

   //执行算法
   val algorithms = getProcesss(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_ALGORITHMS,
     SparkDefTag.SPARK_DEF_ELEMENT_TAG_ALGORITHM)
   algorithms.foreach(kv => kv._1.execute(processContext, kv._2, sparkTaskDefEl))

   //执行输出
   val outs = getProcesss(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUTS,
     SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUT)
   outs.foreach(kv => kv._1.execute(processContext, kv._2, sparkTaskDefEl))

   //关闭操作
   outs.foreach(_._1.close(processContext))
   algorithms.foreach(_._1.close(processContext))
   conveters.foreach(_._1.close(processContext))
   inputs.foreach(_._1.close(processContext))
 }

  //一个任务只会存在一次初始化的过程
  private def processInits(processContext: ProcessContext,
                           initsEl: Elem,
                           sparkTaskDefEl: Elem): Boolean = {

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

  private def getProcesss(sparkTaskDefEl: Elem,
                  parentTag: String,
                  tag: String): Array[(AbstractProcess, Elem)] ={
    val parentEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, parentTag)
    if(parentEl != null){
      (parentEl \ tag).map(node =>{
          val el = node.asInstanceOf[Elem]
          val typeName = XmlUtils.getAttrValue(el, SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE)
          (ProcessFactory.getProcess(typeName), el)
        }).toArray[(AbstractProcess, Elem)]
    }else{
      Array.empty[(AbstractProcess, Elem)]
    }

  }

}


