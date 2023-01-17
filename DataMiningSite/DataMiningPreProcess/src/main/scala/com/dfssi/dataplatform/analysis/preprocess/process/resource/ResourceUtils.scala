package com.dfssi.dataplatform.analysis.preprocess.process.resource

import com.dfssi.dataplatform.analysis.utils.XmlUtils
import org.apache.spark.Logging

import scala.collection.mutable
import scala.xml.{Elem, XML}


object ResourceUtils extends Serializable with Logging {

  def loadConfig(confXml: String): Map[String, String] = {
    val configEl = XML.loadString(confXml)
    val paramsEl = XmlUtils.getSingleSubXmlEl(configEl, "params")

    loadParams(paramsEl)

  }

  def loadParams(paramsEl: Elem): Map[String, String] = {
    val params: mutable.Map[String, String] = mutable.Map[String, String]()

    if (paramsEl == null) {
      return params.toMap
    }

    if (paramsEl != null) {
      val paramNodes = XmlUtils.getSubNodeSeq(paramsEl, "param")
      for (paramNode <- paramNodes) {
        val paramEl = paramNode.asInstanceOf[Elem]
        val name = XmlUtils.getAttrValue(paramEl, "name")
        val value = XmlUtils.getAttrValue(paramEl, "value")

        params.put(name, value)
      }
    }

    params.toMap
  }

}
