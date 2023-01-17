package com.dfssi.dataplatform.analysis.utils

import java.util

import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.mutable
import scala.xml.{Elem, NodeSeq}

object XmlUtils {

  def getSingleSubXmlEl(srcEl: Elem, elName: String): Elem = {
    if (srcEl == null)
      return null;

    try {
      val inputsEl = (srcEl \ elName);
      if (inputsEl.size > 0) {
        return inputsEl.head.asInstanceOf[Elem];
      }
    } catch {
      case ex: Throwable => return null;
    }

    return null;
  }

  def getAttrValue(srcEl: Elem, attrName: String): String = {
    if (srcEl == null)
      return null;

    val attrMeta = srcEl.attributes(attrName);
    if (attrMeta != null)
      return attrMeta.text;

    return null;
  }

  def getAttrValueWithDefault(srcEl: Elem,
                   attrName: String,
                   defaultValue: String): String = {
    if (srcEl == null)
      return defaultValue

    val attrMeta = srcEl.attributes(attrName);
    if (attrMeta != null)
      return attrMeta.text

    return defaultValue
  }

  def extractParams(paramsEl: Elem): mutable.Map[String, String] = {
    val paramsMap: mutable.Map[String, String] = new util.LinkedHashMap[String, String]();
    if (paramsEl != null) {
      val paramNodes = paramsEl \ SparkDefTag.SPARK_DEF_ELEMENT_TAG_PARAM;
      for (paramNode <- paramNodes) {
        var paramEl = paramNode.asInstanceOf[Elem];
        paramsMap.put(getAttrValue(paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_NAME), getAttrValue
        (paramEl, SparkDefTag.SPARK_DEF_ATTR_TAG_VALUE))
      }
    }

    return paramsMap;
  }

  def getSubNodeSeq(parentEl: Elem, subTagName: String): NodeSeq = {
    parentEl \ subTagName
  }
}
