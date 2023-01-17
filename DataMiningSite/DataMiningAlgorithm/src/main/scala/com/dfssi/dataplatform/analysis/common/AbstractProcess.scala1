package com.dfssi.dataplatform.analysis.common

import java.sql.{Connection, DriverManager}

import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.hadoop.fs.Path
import org.apache.spark.{Logging, SparkContext}

import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.mutable
import scala.xml.Elem

trait AbstractProcess extends Logging with Serializable {

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

  def extractSimpleParams(processEl: Elem): mutable.Map[String, String] = {
    val paramsNodes = processEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAMS
    var paramsEl: Elem = null
    if (paramsNodes.nonEmpty) {
      paramsEl = paramsNodes.head.asInstanceOf[Elem]
    }
    extractParams(paramsEl)
  }

  def getSchemaName(paramsMap: mutable.Map[String, String]): String = {
    getParamValue(paramsMap, "schema")
  }

  def getTableName(paramsMap: mutable.Map[String, String]): String = {
    getParamValue(paramsMap, "tableName")
  }

  def getParamValue(paramsMap: mutable.Map[String, String], paramName: String): String = {
    paramsMap(paramName)
  }

  def getInputs(el: Elem): Array[String] = {
    XmlUtils.getAttrValue(el, SparkDefTag.SPARK_DEF_ATTR_TAG_INPUTIDS).split(",")
  }

  def deleteHdfsDir(sparkContext: SparkContext, path: String): Unit = {
    val hadoopConf = sparkContext.hadoopConfiguration
    val fs = org.apache.hadoop.fs.FileSystem.get(hadoopConf)
    fs.delete(new Path(path),true)
    fs.close()
  }

  def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem)

  def close(processContext: ProcessContext): Unit ={}

}
