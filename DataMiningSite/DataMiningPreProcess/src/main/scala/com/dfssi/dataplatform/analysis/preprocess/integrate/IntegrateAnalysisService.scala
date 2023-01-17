package com.dfssi.dataplatform.analysis.preprocess.integrate

import java.util

import com.dfssi.dataplatform.analysis.common.ProcessContext
import com.dfssi.dataplatform.analysis.preprocess.{AnalysisService, ProcessFactory}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.sql.DataFrame

import scala.collection.JavaConversions.mapAsScalaMap
import scala.xml.Elem

/**
  * Spark Process
  * spark-submit --master yarn-cluster --num-executors 1 --driver-memory 1g --executor-memory 1g --executor-cores 1 --files /etc/hive/conf/hive-site.xml  --class com.dfssi.dataplatform.analysis.preprocess.offline.OfflineAnalysisService DataMiningPreProcess-1.0-SNAPSHOT.jar   hdfs://devmaster:8020 /user/hdfs/config/prod/driveBehavior
  */
object IntegrateAnalysisService extends AnalysisService {

  val SPARK_INTEGRATE_ANALYSIS_DEF_FILE_NAME = "IntegrateTaskDef.xml"

  def main(args: Array[String]) {

    val nameNode = args(0)
    val appPath = args(1)
    val sparkTaskDefEl = getSparkTaskDef(nameNode, appPath, SPARK_INTEGRATE_ANALYSIS_DEF_FILE_NAME)

    val processContext: ProcessContext = new ProcessContext()
    processContext.processType = ProcessContext.PROCESS_TYPE_INTEGRATE
    processContext.nameNode = nameNode
    processContext.appPath = appPath
    processContext.dataFrameMap = new util.LinkedHashMap[String, DataFrame]()

    // preprocess
    val preprocessEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl,
      SparkDefTag.SPARK_DEF_ELEMENT_TAG_PREPROCESS)

    processConvert(processContext, preprocessEl, sparkTaskDefEl)
  }

  def getSparkAppName(sparkDefEl: Elem): Option[String] = {
    var name = XmlUtils.getAttrValue(sparkDefEl, "name");
    if (name == null) {
      return None;
    }

    return Some(name);
  }

  def processConvert(processContext: ProcessContext,
                     preprocessEl: Elem,
                     sparkTaskDefEl: Elem) {
    if (preprocessEl == null || (preprocessEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS).isEmpty) {
      return;
    }

    for (processNode <-   (preprocessEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS)) {
      var processEl = processNode.asInstanceOf[Elem];
      var processType =
        XmlUtils.getAttrValue(processEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TYPE);
      ProcessFactory
        .getProcess(processType)
        .execute(processContext, processEl, sparkTaskDefEl);
    }
  }

}
