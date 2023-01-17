package com.dfssi.dataplatform.analysis.es.indexs

import java.nio.charset.Charset

import com.dfssi.common.LRUCache
import com.dfssi.dataplatform.analysis.config.XmlReader
import com.dfssi.dataplatform.analysis.es.SimpleRestClient
import org.apache.spark.Logging
import org.elasticsearch.hadoop.cfg.PropertiesSettings

import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/7 20:51 
  */
class DynamicIndexCreator private extends Logging{

  private var restClient: SimpleRestClient = null

  private val existCache: LRUCache[String, Boolean] =
    new LRUCache[String, Boolean](100, 30 * 24 * 60 * 60 * 1000L)
  private val charset = Charset.forName("utf-8")

  def this(esClusterName: String, esNodes: String, esPort: Int){
    this()
    val propertiesSettings = new PropertiesSettings()
    propertiesSettings.setProperty("es.nodes", esNodes)
    propertiesSettings.setProperty("es.clustername", esClusterName)
    propertiesSettings.setProperty("es.port", String.valueOf(esPort))
    this.restClient = new SimpleRestClient(propertiesSettings)
  }

  def this(propertiesSettings: PropertiesSettings){
    this()
    this.restClient = new SimpleRestClient(propertiesSettings)
  }

  def checkOrCreate(typeEntity: TypeEntity, indexName: String): Unit ={
    if(typeEntity != null){
      val key = s"${indexName}/${typeEntity.name}"
      if(this.existCache.get(key) == null) {
        try {
          val indexSettings = typeEntity.getIndexEntity().getJsonSettings().getBytes(charset)
          val mappingSettings = typeEntity.getJsonSettings().getBytes(charset)

          restClient.putMapping(indexName, indexSettings, typeEntity.name, mappingSettings)
          existCache.put(key, true)
          logInfo(s"创建索引${key}成功。")
        } catch {
          case e: Exception =>
            logError(s"创建索引${key}失败。", e)
        }
      }else{
        logDebug(s"索引${key}已存在")
      }
    }else{
      logWarning(s"TypeEntity 为空。")
    }
  }

}

object DynamicIndexCreator{

  def buildFromXmlElem(esOutElem: Elem): DynamicIndexCreator ={

    val params = XmlReader.getNextSingleSubElem(esOutElem, "params")

    val propertiesSettings = new PropertiesSettings()
    for(param <- params \ "param"){
      val elem = param.asInstanceOf[Elem]
      propertiesSettings.setProperty(XmlReader.getAttr(elem, "name"),
        XmlReader.getAttr(elem, "value"))
    }
    new DynamicIndexCreator(propertiesSettings)
  }

  def main(args: Array[String]): Unit = {
    val xml = XmlReader.loadFromPackage("/es-store-config.xml")

    val outputElem = XmlReader.getNextSingleSubElem(xml, "outputs")
    outputElem

    val dynamicIndexCreator = new DynamicIndexCreator("elk", "172.16.1.221,172.16.1.222,172.16.1.223", 9200)

    val indexElem = XmlReader.getNextSingleSubElem(xml , "es-index-defs")
    for(indexNode <- indexElem \ "es-index"){
      val indexElem = indexNode.asInstanceOf[Elem]
      val indexEntity = IndexEntity.buildFromXmlElem(indexElem)
      indexEntity.getTypes().foreach(kv =>{
        dynamicIndexCreator.checkOrCreate(kv._2, "20180308_demo")
      })
    }

  }
}
