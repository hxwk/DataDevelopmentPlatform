package com.dfssi.dataplatform.analysis.process.service

import java.lang.reflect.Method

import com.dfssi.dataplatform.analysis.process.utils.{SparkDefTag, XmlUtils}
import org.apache.log4j.{Level, Logger}

import scala.collection.mutable.ArrayBuffer
import scala.xml.Elem

/**
  * Description:
  *
  * @author PengWuKai
  * @version 2018/9/25 16:18
  */
object ExternalAnalysisService extends AnalysisService {

  def main(args: Array[String]) {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("com.dfssi").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val nameNode = args(0)
    val appPath = args(1)
    val sparkTaskDefEl = getSparkTaskDef(nameNode, appPath, SPARK_ANALYSIS_DEF_FILE_NAME)

    val preprocessEl = XmlUtils.getSingleSubXmlEl(sparkTaskDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_PREPROCESS)
    val processEl = XmlUtils.getSingleSubXmlEl(preprocessEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS)
    val params = extractSimpleParams(processEl)

    val className = XmlUtils.getAttrValue(processEl, "className")
    val cls: Class[_] = Class.forName(className.replace(".main", ""))
    val setMethod: Method = cls.getDeclaredMethod("main", classOf[Array[String]])

    setMethod.invoke(cls.newInstance(), params)
  }

  def extractSimpleParams(processEl: Elem): Array[String] = {
    val paramsEl = XmlUtils.getSingleSubXmlEl(processEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAMS)
    val args = ArrayBuffer[String]()

    if (null != paramsEl) {
      val paramNodes = paramsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAM
      for (paramNode <- paramNodes) {
        val paramEl = paramNode.asInstanceOf[Elem]
        val value = XmlUtils.getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_VALUE)
        if (value != null) args += value
      }
    }

    args.toArray
  }
}
