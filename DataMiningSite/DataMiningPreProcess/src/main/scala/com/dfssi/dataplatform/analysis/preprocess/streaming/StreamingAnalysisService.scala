package com.dfssi.dataplatform.analysis.preprocess.streaming

import java.util

import com.dfssi.dataplatform.analysis.common.ProcessContext
import com.dfssi.dataplatform.analysis.preprocess.{AnalysisService, ProcessFactory}
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, StreamingLog, XmlUtils}
import org.apache.spark.sql.DataFrame
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{Logging, SparkConf}

import scala.collection.JavaConversions.mapAsScalaMap
import scala.xml.Elem

/**
  * Streaming Process
  *
  */
object StreamingAnalysisService extends AnalysisService {

  val SPARK_STREAMING_ANALYSIS_DEF_FILE_NAME = "StreamingTaskDef.xml"

  def main(args: Array[String]) {
    val nameNode = args(0);
    val appPath = args(1);
    val sparkTaskDefEl = getSparkTaskDef(nameNode, appPath, SPARK_STREAMING_ANALYSIS_DEF_FILE_NAME);

    StreamingLog.setStreamingLogLevels()

    val sparkConf = new SparkConf().setAppName("StreamingProcess")
    var streamingContext: StreamingContext = new StreamingContext(sparkConf, Seconds(2))

    val processContext: ProcessContext = new ProcessContext();
    processContext.dataFrameMap = new util.LinkedHashMap[String, DataFrame]();
    processContext.streamingContext = streamingContext;

    val inputsEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_INPUTS);
    processInputs(processContext, inputsEl, sparkTaskDefEl);

    streamingContext.start()
    streamingContext.awaitTermination()
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

}


