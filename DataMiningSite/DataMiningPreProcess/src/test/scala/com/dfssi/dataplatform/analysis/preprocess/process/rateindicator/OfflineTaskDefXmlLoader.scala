package com.dfssi.dataplatform.analysis.preprocess.process.rateindicator

import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}

import scala.collection.mutable
import scala.xml.{Elem, XML}

object OfflineTaskDefXmlLoader {

  val SPARK_OFFLINE_ANALYSIS_DEF_FILE_NAME = "/OfflineTaskDef.xml"

  def loadParamConfig: Map[String, String] = {
    val is = this.getClass.getResourceAsStream(SPARK_OFFLINE_ANALYSIS_DEF_FILE_NAME)
    val sparkDefEl = XML.load(is)

    val preprocessEl = XmlUtils.getSingleSubXmlEl(sparkDefEl, SparkDefTag.SPARK_DEF_ELEMENT_TAG_PREPROCESS)

    val processNode = preprocessEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PROCESS
    var processEl = processNode.head.asInstanceOf[Elem]
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(processEl)

    paramsMap.toMap
  }

  private def extractSimpleParams(processEl: Elem): mutable.Map[String, String] = {
    val paramsNodes = processEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAMS
    var paramsEl: Elem = null
    if (paramsNodes.nonEmpty) {
      paramsEl = paramsNodes.head.asInstanceOf[Elem]
    }
    extractParams(paramsEl)
  }

  private def extractParams(paramsEl: Elem): mutable.Map[String, String] = {
    val paramsMap: mutable.Map[String, String] = mutable.Map[String, String]()
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

  def main(args: Array[String]): Unit = {
    println(loadParamConfig)
  }

}
