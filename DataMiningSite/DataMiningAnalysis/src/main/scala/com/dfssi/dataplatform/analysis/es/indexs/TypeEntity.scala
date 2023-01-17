package com.dfssi.dataplatform.analysis.es.indexs

import java.text.SimpleDateFormat

import com.dfssi.common.json.Jsons
import com.dfssi.dataplatform.analysis.config.XmlReader
import com.dfssi.dataplatform.analysis.es.Constants
import org.apache.spark.Logging

import scala.collection.JavaConversions
import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/7 21:04 
  */
class TypeEntity(val name: String,
                 val typeHead: TypeHead) extends Serializable {

  private val columns = new java.util.HashMap[String, java.util.HashMap[String, Any]]
  addDefaultColumn()

  private var indexEntity: IndexEntity = null

  private def addDefaultColumn(): Unit ={
    addColumn(Constants.INDEX_FIELD, "keyword", null, false, false)
    addColumn(Constants.TABLE_FIELD, "keyword", null, false, false)
  }

  def addColumn(name: String,
                typeName: String,
                analyzer: String,
                index: Boolean = true,
                store: Boolean = false): Unit ={
     val map = TypeEntity.createColumnMap(typeName, analyzer, index, store)
     this.columns.put(name, map)
  }

    def addColumnMap(name: String,
                     map: java.util.HashMap[String, Any]): Unit ={
        this.columns.put(name, map)
    }

  def setIndexEntity(indexEntity: IndexEntity): Unit ={
    this.indexEntity = indexEntity
  }

  def getIndexEntity(): IndexEntity = this.indexEntity

  def getColumns() =  JavaConversions.asScalaSet(this.columns.keySet())

  def getColumnType(column: String): String ={
    val map = this.columns.get(column)
    if(map != null){
      return map.get("type").toString
    }
    null
  }

  def getJsonSettings(): String ={
    val settings = new java.util.HashMap[String, Any]
    settings.put("properties", this.columns)
    settings.put("_all", JavaConversions.mapAsJavaMap(Map("enabled" -> false)))

    val mapping = new java.util.HashMap[String, Any]
    mapping.put(name, settings)

    Jsons.obj2JsonString(mapping)
  }

  override def toString = s"TypeEntity($getJsonSettings)"

}

case class TypeHead(val identificationFiled: String,
                    val identificationValues: Set[String],
                    val convertorID: String,
                    val convertorClass: String,
                    val timeField: String,
                    val timePattern: String,
                    val timeMin: Long,
                    val timeMax: Long) extends Serializable with Logging{

  def formatTimeToLong(timeValue: String): Long ={
      if("long".equalsIgnoreCase(timePattern)){
        return timeValue.toLong
      }else{
        try {
          val date = new SimpleDateFormat(timePattern).parse(timeValue)
          return date.getTime
        } catch {
          case e: Exception =>
            logError(s"时间转化失败：timeField = ${timeField}, timePattern = ${timePattern}, timeValue = ${timeValue}", e)
            return System.currentTimeMillis()
        }
      }
  }

}

object TypeEntity{

  def buildFromXmlElem(typeElem: Elem): TypeEntity ={
    val typeName = XmlReader.getAttr(typeElem, "name")
    require(typeName != null && typeName.length > 0,
      "es-type 的 name 属性不能为空！")

    val identificationElem = XmlReader.getNextSingleSubElem(typeElem, "identification")
    val identificationField = XmlReader.getAttr(identificationElem, "field")
    val identificationValue = XmlReader.getAttr(identificationElem, "value")
    require(identificationField != null && identificationValue != null,
      "identification的field和value不能为空！")

    val convertorElem =  XmlReader.getNextSingleSubElem(typeElem, "convertor")
    val convertorID = XmlReader.getAttr(convertorElem, "id")
    val convertorClass = XmlReader.getAttr(convertorElem, "class")
    require(convertorID != null || convertorClass != null,
      "convertor的id和class不能同时为空！")

    val timeElem =  XmlReader.getNextSingleSubElem(typeElem, "timefield")
    val timeField = XmlReader.getAttr(timeElem, "field")
    val timePattern = XmlReader.getAttrWithDefault(timeElem, "pattern", "long")
    val timeMin = XmlReader.getAttrAsLong(timeElem, "min", -1L)
    val timeMax = XmlReader.getAttrAsLong(timeElem, "max", Long.MaxValue)

    val head = TypeHead(
      identificationField, identificationValue.split(",").toSet,
      convertorID, convertorClass,
      timeField, timePattern, timeMin, timeMax
    )
    val typeEntity = new TypeEntity(typeName, head)

    val columns = typeElem \ "columns"
    for(column <- columns \ "column"){

        val columnElem = column.asInstanceOf[Elem]
        val typeName = XmlReader.getAttr(columnElem, "type")
        val name = XmlReader.getAttr(columnElem, "name")
        require(name != null, "column 名称不能为空")

        if(!"object".equalsIgnoreCase(typeName)) {
            typeEntity.addColumn(name,
                typeName,
                XmlReader.getAttr(columnElem, "analyzer"),
                XmlReader.getAttrAsBoolean(columnElem, "index", true),
                XmlReader.getAttrAsBoolean(columnElem, "store", false))
        }else{
            val map = createObjectColumn(columnElem)
            typeEntity.addColumnMap(name, map)
        }
    }
    typeEntity
  }

    private def createObjectColumn(columnElem: Elem): java.util.HashMap[String, Any] ={
        val properties = new java.util.HashMap[String, Any]
        for(property <- columnElem \ "property"){
            val pElem = property.asInstanceOf[Elem]
            val propertyName = XmlReader.getAttr(pElem, "name")
            require(propertyName != null, "column 名称不能为空")
            properties.put(propertyName,
                createColumnMap(XmlReader.getAttr(pElem, "type"),
                                XmlReader.getAttr(pElem, "analyzer"),
                                XmlReader.getAttrAsBoolean(pElem, "index", true),
                                XmlReader.getAttrAsBoolean(pElem, "store", false)))
        }

        val map = new java.util.HashMap[String, Any]
        map.put("properties", properties)
        map
    }

    def createColumnMap(typeName: String,
                        analyzer: String,
                        index: Boolean = true,
                        store: Boolean = false): java.util.HashMap[String, Any] ={

        require(typeName != null, "type 名称不能为空")

        val map = new java.util.HashMap[String, Any]
        map.put("type", typeName)
        if(analyzer != null && analyzer.length > 0)
            map.put("analyzer", analyzer)
        if(!index)
            map.put("index", false)
        if(store)
            map.put("store", true)
        map
    }
}
