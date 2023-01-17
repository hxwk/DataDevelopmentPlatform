package com.dfssi.dataplatform.analysis.preprocess.offline

import java.util

import com.dfssi.dataplatform.analysis.common.ProcessContext
import com.dfssi.dataplatform.analysis.preprocess.offline.kmeansDataTest.{getSparkAppName,processAlgorithm,processConvert,processInputs}
import com.dfssi.dataplatform.analysis.preprocess.{AnalysisService, ProcessFactory}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.hive.HiveContext

import scala.collection.JavaConversions.mapAsScalaMap
import scala.xml.Elem

object RandomForestDataTest extends AnalysisService {
    def main(args:Array[String]):Unit = {
        val RANDOM_FOREST_XML_FILE_NAME = "RFAlgTest.xml"

        Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
        Logger.getLogger("com.dfssi").setLevel(Level.ERROR)
        Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

        val nodeName = "file:///"
        val appPath = "F:\\last_ssi_project\\05-Implement\\SourceCode\\trunk\\" +
                "SSIDataPlatform\\DataDevelopmentPlatform\\DataMiningSite\\" +
                "DataMiningPreProcess\\src\\main\\resources"
        val sparkTaskDefEl = getSparkTaskDef(nodeName,appPath,RANDOM_FOREST_XML_FILE_NAME)
        val conf = new SparkConf()
                .setAppName(getSparkAppName(sparkTaskDefEl).getOrElse("RFXMLTest"))
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
}
