package com.dfssi.dataplatform.analysis.es.indexs

import com.dfssi.common.Dates
import com.dfssi.dataplatform.analysis.config.XmlReader

import scala.collection.mutable
import scala.util.parsing.json.JSONObject
import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/7 21:04 
  */
class IndexEntity(val name: String,
                  val createRule: String) extends Serializable {

  @transient
  private val settings = new mutable.HashMap[String, String]
  @transient
  private val types = new mutable.HashMap[String, TypeEntity]

  def addSetting(key: String, value: String): Unit ={
        if(key != null && value != null)
            this.settings.put(key, value)
    }

  def addSettings(settings:Map[String, String]): Unit ={
     this.settings ++= settings
   }

  def addType(typeName: String, typeEntity: TypeEntity): Unit ={
    typeEntity.setIndexEntity(this)
    this.types.put(typeName, typeEntity)
  }

  def getTypes(): Map[String, TypeEntity] = this.types.toMap

  def getType(typeName: String): TypeEntity = types.getOrElse(typeName, null)

  def existType(typeName: String): Boolean = types.contains(typeName)

  def getSettings(): Map[String, String] = this.settings.toMap

  def getFullIndexName(timeStamp: Long): String ={
     if(createRule != null){
       createRule match {
         case "day" => s"${name}_${Dates.long2Str(timeStamp, "yyyyMMdd")}"
         case "month" => s"${name}_${Dates.long2Str(timeStamp, "yyyyMM")}"
         case "year" => s"${name}_${Dates.long2Str(timeStamp, "yyyy")}"
         case _ => name
       }
     }else{
       name
     }
  }


  def getJsonSettings(): String ={
      JSONObject.apply(this.settings.toMap).toString()
  }

  override def toString = s"IndexEntity($getJsonSettings, ${types})"
}

object IndexEntity{

  def buildFromXmlElem(indexElem: Elem): IndexEntity ={

    val indexName = XmlReader.getAttr(indexElem, "name")
    require(indexName != null && indexName.length > 0,
      "es-index 的 name 属性不能为空！")
    val createRule = XmlReader.getAttr(indexElem, "createRule")

    val indexEntity = new IndexEntity(indexName, createRule)
    //读取index的settings
    val settingsElem = XmlReader.getNextSubElem(indexElem, "settings")
    for(setting <- settingsElem \ "setting"){
      val settingElem = setting.asInstanceOf[Elem]
      indexEntity.addSetting(XmlReader.getAttr(settingElem, "name"),
        XmlReader.getAttr(settingElem, "value"))
    }
    //解析type
    for(typeNode <- indexElem \ "es-type"){
      val typeEntity = TypeEntity.buildFromXmlElem(typeNode.asInstanceOf[Elem])
      indexEntity.addType(typeEntity.name, typeEntity)
    }

    indexEntity
  }

}
