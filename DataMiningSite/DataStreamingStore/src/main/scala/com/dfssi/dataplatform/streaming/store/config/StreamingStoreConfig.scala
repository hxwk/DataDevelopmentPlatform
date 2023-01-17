package com.dfssi.dataplatform.streaming.store.config

import java.util

import com.dfssi.dataplatform.streaming.store.utils.{JacksonUtils, XmlUtils}
import com.google.gson.JsonObject
import org.apache.commons.io.IOUtils

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.xml.{Elem, XML}

class StreamingStoreConfig extends Serializable {
  import StreamingStoreConfig._

  val params: mutable.Map[String, String] =
    new util.LinkedHashMap[String, String]().asScala
  val hiveTableDefs: mutable.Map[String, HiveTableDef] =
    new util.LinkedHashMap[String, HiveTableDef]().asScala
  val messageDefs: mutable.Map[String, MessageDef] =
    new util.LinkedHashMap[String, MessageDef]().asScala

  def loadConfigFromPackage(path: String): Unit = {
    val is = getClass.getResourceAsStream(path)
    loadConfig(IOUtils.toString(is))
  }

  def loadConfig(confXml: String): Unit = {
    val configEl = XML.loadString(confXml)
    val contextEl =
      XmlUtils.getSingleSubXmlEl(configEl, CONFIG_ELEMENT_TAG_CONTEXT)
    val hiveTableDefsEl =
      XmlUtils.getSingleSubXmlEl(configEl, CONFIG_ELEMENT_TAG_HIVE_TABLE_DEFS)
    val messsageDefsEl =
      XmlUtils.getSingleSubXmlEl(configEl, CONFIG_ELEMENT_TAG_MESSSAGE_DEFS)

    loadContext(contextEl)
    loadHiveTableDefs(hiveTableDefsEl)
    loadMessageDefs(messsageDefsEl)
  }

  def loadContext(contextEl: Elem): Unit = {
    if (contextEl == null) {
      return
    }

    val paramsNodes =
      XmlUtils.getSubNodeSeq(contextEl, SPARK_DEF_ELEMENT_TAG_PARAMS)
    var paramsEl: Elem = null
    if (paramsNodes.size > 0) {
      paramsEl = paramsNodes.head.asInstanceOf[Elem]
    }
    loadParams(paramsEl)
  }

  def loadParams(paramsEl: Elem): Unit = {
    if (paramsEl != null) {
      val paramNodes =
        XmlUtils.getSubNodeSeq(paramsEl, SPARK_DEF_ELEMENT_TAG_PARAM)
      for (paramNode <- paramNodes) {
        val paramEl = paramNode.asInstanceOf[Elem]
        val name = XmlUtils.getAttrValue(paramEl, CONFIG_ATTR_TAG_NAME)
        val value = XmlUtils.getAttrValue(paramEl, CONFIG_ATTR_TAG_VALUE)

        params.put(name, value)
      }
    }
  }

  def loadHiveTableDefs(hiveTableDefsEl: Elem): Unit = {
    if (hiveTableDefsEl == null) {
      return
    }

    val hiveTableNodes =
      XmlUtils.getSubNodeSeq(hiveTableDefsEl, CONFIG_ELEMENT_TAG_HIVE_TABLE)

    for (hiveTableNode <- hiveTableNodes) {
      val hiveTableEl = hiveTableNode.asInstanceOf[Elem]
      val hiveTableDef: HiveTableDef = new HiveTableDef(hiveTableEl)

      hiveTableDefs.put(hiveTableDef.tableName, hiveTableDef)
    }
  }

  def loadMessageDefs(messageDefsEl: Elem): Unit = {
    if (messageDefsEl == null) {
      return
    }

    val messageDefNodes =
      XmlUtils.getSubNodeSeq(messageDefsEl, CONFIG_ELEMENT_TAG_MESSAGE)
    for (messageDefNode <- messageDefNodes) {
      val messageDefEl = messageDefNode.asInstanceOf[Elem]
      val messageDef: MessageDef = new MessageDef(messageDefEl)
      messageDef.loadDefFromEl(this, messageDefEl)
      messageDefs.put(messageDef.name, messageDef)
    }
  }

  def getHiveTableDef(tableName: String): HiveTableDef = {
    if (hiveTableDefs.get(tableName).isDefined) {
      return hiveTableDefs(tableName)
    }
    null
  }

  def getMessageDef(name: String): MessageDef = {
    if (messageDefs.get(name).isDefined) {
      return messageDefs(name)
    }
    null
  }

  def getParamValue(paramName: String): String = {
    if (params.get(paramName).isDefined) {
      return params(paramName)
    }
    null
  }

  def getDataPartitionNum(): Int = {
    val numStr = getParamValue(CONTEXT_PARAM_NAME_DATAPARTITIONNUM)
    numStr.toInt
  }

  def getHiveExternalTableDataRootPath(): String = {
    getParamValue(CONTEXT_PARAM_NAME_HIVEEXTERNALTABLEDATAROOTPATH)
  }

  def getCurrentHiveSchema(): String = {
    getParamValue(CONTEXT_PARAM_NAME_HIVESCHEMA)
  }

  def getMsgId(msgJsonObject: JsonObject): String = {
    JacksonUtils.getAsString(msgJsonObject,
                             getParamValue(CONTEXT_PARAM_NAME_MSGIDATTRNAME))
  }

  def getErrorMsgTableName(): String = {
    getParamValue(CONTEXT_PARAM_NAME_ERRORMSGTABLENAME)
  }

  def checkTableExist(): Boolean = {
    "true".equalsIgnoreCase(getParamValue(CONTEXT_PARAM_NAME_CHECKTABLEEXIST))
  }

  def getStreamingDuration(): Long = {
    getParamValue(CONTEXT_PARAM_NAME_STREAMINGDURATION).toLong
  }

  def getNameNode(): String = {
    getParamValue(CONTEXT_PARAM_NAME_NAMENODE)
  }

  def getKafkaBrokers(): String = {
    getParamValue(CONTEXT_PARAM_NAME_KAFKABROKERS)
  }

  def getKafkaGroupName(): String = {
    getParamValue(CONTEXT_PARAM_NAME_KAFKAGROUPNAME)
  }

  def getKafkaTopics(): String = {
    getParamValue(CONTEXT_PARAM_NAME_KAFKATOPICS)
  }

  def getHadooConfUser(): String = {
    getParamValue(CONTEXT_PARAM_NAME_HADOOPCONFUSER)
  }

  def isMergeEnableCompress(): Boolean = {
    "true".equalsIgnoreCase(
      getParamValue(CONTEXT_PARAM_NAME_MERGEENABLECOMPRESS))
  }

  def getMergeCompressMethod(): String = {
    getParamValue(CONTEXT_PARAM_NAME_MERGECOMPRESSMETHOD)
  }

  def getMergePartitionNum(): Int = {
    getParamValue(CONTEXT_PARAM_NAME_MERGEPARTITIONNUM).toInt
  }
}

object StreamingStoreConfig extends Serializable {

  val CONFIG_ELEMENT_TAG_CONTEXT = "context"
  val CONFIG_ELEMENT_TAG_HIVE_TABLE_DEFS = "hive-table-defs"
  val CONFIG_ELEMENT_TAG_HIVE_TABLE = "hive-table"
  val CONFIG_ELEMENT_TAG_COLUMNS = "columns"
  val CONFIG_ELEMENT_TAG_COLUMN = "column"
  val CONFIG_ELEMENT_TAG_PARTITIONS = "partitions"
  val CONFIG_ELEMENT_TAG_MESSSAGE_DEFS = "messsage-defs"
  val CONFIG_ELEMENT_TAG_MESSAGE = "message"
  val CONFIG_ELEMENT_TAG_ATTRS = "attrs"
  val CONFIG_ELEMENT_TAG_ATTR = "attr"
  val SPARK_DEF_ELEMENT_TAG_PARAMS = "params"
  val SPARK_DEF_ELEMENT_TAG_PARAM = "param"

  val CONFIG_ATTR_TAG_SCHEMANAME = "schemaName"
  val CONFIG_ATTR_TAG_NAME = "name"
  val CONFIG_ATTR_TAG_REFMESSAGENAME = "refMessageName"
  val CONFIG_ATTR_TAG_VALUE = "value"
  val CONFIG_ATTR_TAG_TYPE = "type"
  val CONFIG_ATTR_TAG_TOTABLENAME = "toTableName"
  val CONFIG_ATTR_TAG_PARTITIONRULEPATTERN = "partitionRulePattern"
  val CONFIG_ATTR_TAG_PARTITIONCOLNAMES = "partitionColNames"
  val CONFIG_ATTR_TAG_TOFIELDNAME = "toFieldName"
  val CONFIG_ATTR_TAG_LENGTH = "length"
  val CONFIG_ATTR_TAG_PRECISION = "precision"
  val CONFIG_ATTR_TAG_PARAMIZEDCLASSNAME = "paramizedClassName"
  val CONFIG_ATTR_TAG_DATEATTRNAME = "dateAttrName"

  val CONTEXT_PARAM_NAME_NAMENODE = "nameNode"
  val CONTEXT_PARAM_NAME_KAFKABROKERS = "kafkaBrokers"
  val CONTEXT_PARAM_NAME_KAFKAGROUPNAME = "kafkaGroupName"
  val CONTEXT_PARAM_NAME_KAFKATOPICS = "kafkaTopics"
  val CONTEXT_PARAM_NAME_HIVEEXTERNALTABLEDATAROOTPATH =
    "hiveExternalTableDataRootPath"
  val CONTEXT_PARAM_NAME_HIVESCHEMA = "hiveSchema"
  val CONTEXT_PARAM_NAME_DATAPARTITIONNUM = "dataPartitionNum"
  val CONTEXT_PARAM_NAME_HADOOPCONFUSER = "hadoopConfUser"
  val CONTEXT_PARAM_NAME_MSGIDATTRNAME = "msgIdAttrName"
  val CONTEXT_PARAM_NAME_ERRORMSGTABLENAME = "errorMsgTableName"
  val CONTEXT_PARAM_NAME_CHECKTABLEEXIST = "checkTableExist"
  val CONTEXT_PARAM_NAME_STREAMINGDURATION = "streamingDuration"
  val CONTEXT_PARAM_NAME_MERGEENABLECOMPRESS = "mergeEnableCompress"
  val CONTEXT_PARAM_NAME_MERGECOMPRESSMETHOD = "mergeCompressMethod"
  val CONTEXT_PARAM_NAME_MERGEPARTITIONNUM = "mergePartitionNum"

  val HIVE_DATA_TYPE_STRING = "STRING"
  val HIVE_DATA_TYPE_TINYINT = "TINYINT"
  val HIVE_DATA_TYPE_SMALLINT = "SMALLINT"
  val HIVE_DATA_TYPE_INT = "INT"
  val HIVE_DATA_TYPE_BIGINT = "BIGINT"
  val HIVE_DATA_TYPE_FLOAT = "FLOAT"
  val HIVE_DATA_TYPE_DOUBLE = "DOUBLE"
  val HIVE_DATA_TYPE_DECIMAL = "DECIMAL"
  val HIVE_DATA_TYPE_ARRAY = "ARRAY"
  val HIVE_DATA_TYPE_TIMESTAMP = "TIMESTAMP"
  val HIVE_DATA_TYPE_BINARY = "BINARY"

}
