package com.dfssi.dataplatform.streaming.store.config

import java.util
import java.util.Date

import com.dfssi.dataplatform.streaming.store.utils.{JacksonUtils, XmlUtils}
import com.google.gson.JsonObject
import org.apache.commons.lang3.StringUtils

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.xml.Elem
import StreamingStoreConfig._

class MessageDef extends Serializable {
  var name: String = _
  var toTableName: String = _
  var dateAttrName: String = _
  val attrToFieldDefs: mutable.Map[String, MessageAttrDef] =
    new util.LinkedHashMap[String, MessageAttrDef]().asScala
  val fieldToAttrDefs: mutable.Map[String, MessageAttrDef] =
    new util.LinkedHashMap[String, MessageAttrDef]().asScala

  def this(messageEl: Elem) {
    this()
  }

  def loadDefFromEl(config: StreamingStoreConfig, messageDefEl: Elem): Unit = {
    if (messageDefEl == null) {
      return
    }

    name = XmlUtils.getAttrValue(messageDefEl, CONFIG_ATTR_TAG_NAME)
    val refMessageName =
      XmlUtils.getAttrValue(messageDefEl, CONFIG_ATTR_TAG_REFMESSAGENAME)
    if (StringUtils.isNotBlank(refMessageName)) {
      val messageDef: MessageDef = config.getMessageDef(refMessageName)
      toTableName = messageDef.toTableName
      dateAttrName = messageDef.dateAttrName
      for ((k, v) <- messageDef.attrToFieldDefs) {
        attrToFieldDefs.put(k, v)
      }

      for ((k, v) <- messageDef.fieldToAttrDefs) {
        fieldToAttrDefs.put(k, v)
      }

      return
    }

    toTableName =
      XmlUtils.getAttrValue(messageDefEl, CONFIG_ATTR_TAG_TOTABLENAME)
    dateAttrName =
      XmlUtils.getAttrValue(messageDefEl, CONFIG_ATTR_TAG_DATEATTRNAME)

    val attrsNodes =
      XmlUtils.getSubNodeSeq(messageDefEl, CONFIG_ELEMENT_TAG_ATTRS)
    var attrsEl: Elem = null
    if (attrsNodes.size > 0) {
      attrsEl = attrsNodes.head.asInstanceOf[Elem]
    }

    if (attrsEl != null) {
      val attrNodes = XmlUtils.getSubNodeSeq(attrsEl, CONFIG_ELEMENT_TAG_ATTR)
      for (attrNode <- attrNodes) {
        var attrEl = attrNode.asInstanceOf[Elem]
        val messageAttrDef: MessageAttrDef = new MessageAttrDef(attrEl)
        attrToFieldDefs.put(messageAttrDef.name, messageAttrDef)
        fieldToAttrDefs.put(messageAttrDef.toFieldName, messageAttrDef)
      }
    }
  }

  def getMessageCreatedDate(msgJsonObject: JsonObject): Date = {
    new Date(JacksonUtils.getAsLong(msgJsonObject, dateAttrName))
  }

  def getAttrDefByAttrName(attrName: String): MessageAttrDef = {
    if (attrToFieldDefs.get(attrName).isDefined) {
      attrToFieldDefs(attrName)
    } else {
      null
    }
  }

  def getAttrDefBFieldName(fieldName: String): MessageAttrDef = {
    if (fieldToAttrDefs.get(fieldName).isDefined) {
      return fieldToAttrDefs(fieldName)
    } else {
      null
    }
  }
}
