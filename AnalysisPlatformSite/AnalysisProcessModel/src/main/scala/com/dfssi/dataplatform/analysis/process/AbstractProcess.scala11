package com.dfssi.dataplatform.analysis.process

import com.dfssi.dataplatform.analysis.process.utils.{SparkDefTag, XmlUtils}
import org.apache.hadoop.fs.Path
import org.apache.spark.{Logging, SparkContext}

import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.mutable
import scala.xml.Elem

trait AbstractProcess extends Logging with Serializable {

  def extractParams(processEl: Elem): mutable.Map[String, String] = {
    val paramsMap: mutable.Map[String, String] = new java.util.LinkedHashMap[String, String]()
    if (processEl != null) {
      val paramNodes = processEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAM
      for (paramNode <- paramNodes) {
        val paramEl = paramNode.asInstanceOf[Elem]
        paramsMap.put(XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_NAME),
          XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_VALUE))
      }
    }

    paramsMap
  }

  def extractSimpleParams(processEl: Elem): mutable.Map[String, String] = {
    extractParams(processEl)
  }

  def getSchemaName(paramsMap: mutable.Map[String, String]): String = {
    getParamValue(paramsMap, "databaseName")
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
  }

  def getImplement:String

  def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem)

  def close(processContext: ProcessContext): Unit ={}
}
