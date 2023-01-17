package com.dfssi.dataplatform.analysis.es

import com.dfssi.dataplatform.analysis.config.XmlReader
import com.dfssi.dataplatform.analysis.es.indexs.{DynamicIndexCreator, TypeEntity}
import org.apache.spark.Logging
import org.apache.spark.rdd.RDD
import org.elasticsearch.spark.rdd.EsSpark

import scala.collection.mutable
import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/8 14:17
  */
class EsOutput(val idField: String,
               val params: Map[String, String]) extends Serializable with Logging{

  @transient
  val dynamicIndexCreator: DynamicIndexCreator = initDynamicIndexCreator(params)

  private def initDynamicIndexCreator(params: Map[String, String]): DynamicIndexCreator ={
    val clusterName = params.getOrElse("es.clustername", null)
    require(clusterName != null, "es.clustername配置不能为空")

    val nodes = params.getOrElse("es.nodes", null)
    require(nodes != null, "es.nodes配置不能为空")

    val port = params.getOrElse("es.port", "9200").toInt

    new DynamicIndexCreator(clusterName, nodes, port)
  }

  def executeOutput(rdd: RDD[java.util.Map[String, Object]]): Unit ={
    EsSpark.saveToEs(rdd,
      s"{${Constants.INDEX_FIELD}}/{${Constants.TABLE_FIELD}}",
      params)
  }

  def createIndex(typeEntity: TypeEntity, indexName: String): Unit ={
    this.dynamicIndexCreator.checkOrCreate(typeEntity, indexName)
  }
}

object EsOutput extends Logging{
  def buildFromXmlElem(outputElem: Elem, esConfig: Map[String, String] = Map.empty[String, String]): EsOutput ={
    val paramElem = XmlReader.getNextSingleSubElem(outputElem, "params")

    val esParams = new mutable.HashMap[String, String]()
    (paramElem \ "param").foreach(node =>{
      val elem = node.asInstanceOf[Elem]
      esParams.put(XmlReader.getAttr(elem, "name"), XmlReader.getAttr(elem, "value"))
    })

    //优先使用环境配置
    esParams ++= esConfig
    val params = esParams.toMap

    val idfield = params.getOrElse("es.mapping.id", null)
    require(idfield != null, s"es.mapping.id不能为空。")

    logInfo(s"输出到es的配置为：\n\t ${params}")

    new EsOutput(idfield, params)
  }
}
