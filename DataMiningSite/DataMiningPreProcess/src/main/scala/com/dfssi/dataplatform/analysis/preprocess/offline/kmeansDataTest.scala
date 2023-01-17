package com.dfssi.dataplatform.analysis.preprocess.offline

import java.util

import com.dfssi.dataplatform.analysis.common.ProcessContext
import com.dfssi.dataplatform.analysis.preprocess.{AnalysisService, ProcessFactory}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.hive.HiveContext

import scala.collection.JavaConversions.mapAsScalaMap
import scala.xml.Elem

object kmeansDataTest extends AnalysisService {
    val SPARK_OFFLINE_ANALYSIS_DEF_FILE_NAME = "kmeansAlgTest.xml"

    def main(args:Array[String]):Unit = {
        Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
        Logger.getLogger("com.dfssi").setLevel(Level.ERROR)
        Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

        val nodeName = "file:///"
        val appPath = "F:\\last_ssi_project\\05-Implement\\SourceCode\\trunk\\" +
                "SSIDataPlatform\\DataDevelopmentPlatform\\DataMiningSite\\" +
                "DataMiningPreProcess\\src\\main\\resources"
        val sparkTaskDefEl = getSparkTaskDef(nodeName,appPath,SPARK_OFFLINE_ANALYSIS_DEF_FILE_NAME)
        val conf = new SparkConf()
                .setAppName(getSparkAppName(sparkTaskDefEl).getOrElse("KmeansXMLTest"))
                .setMaster("local")
        val sc = new SparkContext(conf)


        val processContext:ProcessContext = new ProcessContext()
        processContext.dataFrameMap = new util.LinkedHashMap[String,DataFrame]()
        processContext.processType = ProcessContext.PROCESS_TYPE_OFFLINE
        processContext.nameNode = "hdfs://"
        processContext.appPath = "/user/chenz"
        processContext.hiveContext = new HiveContext(sc)
        processContext.sparkContext = sc

        val inputsEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl,SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUTS)
        val preprocessEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl,SparkDefTag.SPARK_DEF_ELEMENT_TAG_PREPROCESS)
        val algorithmEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl,SparkDefTag.SPARK_DEF_ELEMENT_TAG_ALGORITHMS)

        processInputs(processContext,inputsEl,sparkTaskDefEl)
        processConvert(processContext,preprocessEl,sparkTaskDefEl)
        processAlgorithm(processContext,algorithmEl,sparkTaskDefEl)


        sc.stop()
    }

    def getSparkAppName(sparkDefEl:Elem):Option[String] = {
        val name = XmlUtils.getAttrValue(sparkDefEl,"name")
        Option(name)
    }

    def processInputs(processContext: ProcessContext, inputsEl: Elem, sparkTaskDefEl: Elem){
        if (inputsEl == null || (inputsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUT).isEmpty) {
            return
        }

        for (inputNode <- inputsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUT) {
            var inputEl = inputNode.asInstanceOf[Elem]
            var inputType = XmlUtils.getAttrValue(inputEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE)
            ProcessFactory.getProcess(inputType).execute(processContext, inputEl, sparkTaskDefEl)
        }
    }

    def processConvert(processContext: ProcessContext,preprocessEl:Elem,sparkTaskDefEl:Elem):Unit = {
        if (preprocessEl == null || (preprocessEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS).isEmpty) {
            return
        }

        for(processNode<-preprocessEl\SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS){
            var processEl = processNode.asInstanceOf[Elem]
            var processType = XmlUtils.getAttrValue(processEl,SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE)
            ProcessFactory.getProcess(processType).execute(processContext,processEl,sparkTaskDefEl)
        }
    }

    def processAlgorithm(processContext:ProcessContext,algorithmsEl:Elem,sparkTaskDefEl:Elem):Unit = {
        if(algorithmsEl==null||(algorithmsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_ALGORITHM).isEmpty){
            return
        }

        for(algorithmNode<-algorithmsEl\SparkDefTag.SPARK_DEF_ELEMENT_TAG_ALGORITHM){
            var algorithmEl = algorithmNode.asInstanceOf[Elem]
            var algorithmType = XmlUtils.getAttrValue(algorithmEl,SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE)
            ProcessFactory.getProcess(algorithmType).execute(processContext,algorithmEl,sparkTaskDefEl)
        }
    }

    def processOutputs(processContext: ProcessContext, outputsEl: Elem, sparkTaskDefEl: Elem) {
        if (outputsEl == null || (outputsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUT).isEmpty) {
            return;
        }

        for (outputNode <- outputsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_OUTPUT) {
            var outputEl = outputNode.asInstanceOf[Elem];
            val outputType = XmlUtils.getAttrValue(outputEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE)

            ProcessFactory.getProcess(outputType).execute(processContext, outputEl, sparkTaskDefEl)
        }
    }
}
