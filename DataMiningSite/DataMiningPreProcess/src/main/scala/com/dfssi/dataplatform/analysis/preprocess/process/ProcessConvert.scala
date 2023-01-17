package com.dfssi.dataplatform.analysis.preprocess.process

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.sql.Column

import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.mutable
import org.apache.spark.sql.functions._

import scala.xml.Elem

/**
  * The supported types are: string, boolean, byte, short, int, long, float, double, decimal, date, timestamp.
  */
class ProcessConvert extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem) {
    val convertParamsMap: mutable.Map[String, String] = extractConvertParams(defEl);
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID);
    var inputIds: Array[String] = getInputs(defEl);
    val outputDF = processContext.dataFrameMap.get(inputIds(0));
    if (outputDF.isEmpty) {
      return
    }

    val selectedCols = outputDF.get.columns.map(colName => {
      var convertedCol = getConvertedCol(colName, convertParamsMap)
      if (convertedCol != null) {
        convertedCol
      } else {
        col(colName)
      }
    });
    val resultDF = outputDF.get.select(selectedCols: _*);

    resultDF.cache()
    processContext.dataFrameMap.put(id, resultDF);
  }

  def getConvertedCol(colName: String, convertParamsMap: mutable.Map[String, String]): Column = {
    for ((tempColName, toType) <- convertParamsMap) {
      if (tempColName.equalsIgnoreCase(colName)) {
        // better use the following method for time convert
        if("timestamp" == toType) {
          return unix_timestamp(col(colName), "yyyy/MM/dd HH:mm:ss").cast("timestamp").as(colName) //,
        }
        return col(colName).cast(toType).as(colName);
      }
    }

    null
  }

  def extractConvertParams(defEl: Elem): mutable.Map[String, String] = {
    val paramsNodes = defEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAMS
    var paramsEl: Elem = null;
    if (paramsNodes.size > 0) {
      paramsEl = paramsNodes.head.asInstanceOf[Elem];
    }
    val paramsMap: mutable.Map[String, String] = new java.util.LinkedHashMap[String, String]();
    if (paramsEl != null) {
      val paramNodes = paramsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAM;
      for (paramNode <- paramNodes) {
        var paramEl = paramNode.asInstanceOf[Elem];
        paramsMap.put(XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_COLNAME),
          XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_TOTYPE))
      }
    }

    return paramsMap;
  }
}

object ProcessConvert {
  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_CONVERT
}
