package com.dfssi.dataplatform.streaming.store.utils

import scala.xml.{Elem, NodeSeq}

object XmlUtils {

  def getSingleSubXmlEl(srcEl: Elem, elName: String): Elem = {
    if (srcEl == null) return null;

    try {
      val inputsEl = (srcEl \ elName)
      if (inputsEl.size > 0) {
        return inputsEl.head.asInstanceOf[Elem]
      }
    } catch {
      case _: Throwable => return null
    }

    null
  }

  def getAttrValue(srcEl: Elem, attrName: String): String = {
    if (srcEl == null) return null

    val attrMeta = srcEl.attributes(attrName)
    if (attrMeta != null) return attrMeta.text

    null
  }

  def getSubNodeSeq(parentEl: Elem, subTagName: String): NodeSeq = {
    parentEl \ subTagName
  }

}
