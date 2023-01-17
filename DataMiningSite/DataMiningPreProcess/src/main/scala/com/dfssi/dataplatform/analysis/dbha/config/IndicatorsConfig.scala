package com.dfssi.dataplatform.analysis.dbha.config

import java.net.URI

import com.dfssi.dataplatform.analysis.common.ResourceXmlConfigLoader
import com.dfssi.dataplatform.analysis.utils.XmlUtils
import com.dfssi.dataplatform.analysis.utils.XmlUtils.getAttrValue
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import scala.collection.mutable
import scala.xml.{Elem, XML}



object IndicatorsConfig extends ResourceXmlConfigLoader{

  override val xmlFileName: String = "RateIndicatorConfig.xml"
  override val xmlResourcePath: String = "com/dfssi/dataplatform/analysis/dbha/config"

  val DBHA_DEF_ELEMENT_ID = "id"
  val DBHA_DEF_ELEMENT_NAME = "name"
  val DBHA_DEF_ELEMENT_VALUE = "value"

  val DBHA_DEF_ELEMENT_EVENTS = "events"
  val DBHA_DEF_ELEMENT_EVENT = "event"

  val DBHA_DEF_ELEMENT_INDICATORS = "indicators"
  val DBHA_DEF_ELEMENT_INDICATOR = "indicator"
  val DBHA_DEF_INDICATOR_CONTRIB = "contrib"

  val DBHA_DEF_ELEMENT_DIMENSIONS = "dimensions"
  val DBHA_DEF_ELEMENT_DIMENSION = "dimension"

  override def loadConfig(): Elem = {

    val is = this.getClass.getClassLoader.getResourceAsStream(xmlResourcePath + "/" +xmlFileName)
    val dbhaConfigEl = XML.load(is)

    is.close()

    dbhaConfigEl
  }


  def loadConfig(nameNode: String, filePath: String): Elem = {
    val uri = new URI(nameNode)
    val conf = new Configuration()
    val fs = FileSystem.get(uri, conf)
    val is = fs.open(new Path(filePath + "/" + xmlFileName))
    val dbhaConfigEl = XML.load(is)

    is.close()
    fs.close()

    dbhaConfigEl
  }

   def getEventsEl(srcEl: Elem): Elem = {
     XmlUtils.getSingleSubXmlEl(srcEl, DBHA_DEF_ELEMENT_EVENTS)
   }

  def getEventsIdNameMap(srcEl: Elem): Map[Int, String] = {
    val eventIdNameMap = mutable.Map[Int, String]()

    val eventNodes = XmlUtils.getSingleSubXmlEl(srcEl, DBHA_DEF_ELEMENT_EVENTS) \ DBHA_DEF_ELEMENT_EVENT
    eventNodes.foreach{ node =>
      val e = node.asInstanceOf[Elem]
      eventIdNameMap.put(getAttrValue(e, DBHA_DEF_ELEMENT_ID).toInt, getAttrValue(e, DBHA_DEF_ELEMENT_NAME))
    }

    eventIdNameMap.toMap
  }

  def getEventsIdValueMap(srcEl: Elem): Map[Int, String] = {
    val eventIdValueMap = mutable.Map[Int, String]()

    val eventNodes = XmlUtils.getSingleSubXmlEl(srcEl, DBHA_DEF_ELEMENT_EVENTS) \ DBHA_DEF_ELEMENT_EVENT
    eventNodes.foreach{ node =>
      val e = node.asInstanceOf[Elem]
      eventIdValueMap.put(getAttrValue(e, DBHA_DEF_ELEMENT_ID).toInt, getAttrValue(e, DBHA_DEF_ELEMENT_VALUE))
    }

    eventIdValueMap.toMap
  }

  def getIndicatorsEl(srcEl: Elem): Elem =
    XmlUtils.getSingleSubXmlEl(srcEl, DBHA_DEF_ELEMENT_INDICATORS)

  def getDimEl(srcEl: Elem): Elem = XmlUtils.getSingleSubXmlEl(srcEl, DBHA_DEF_ELEMENT_DIMENSIONS)

  def getIndicatorIdContribMap(bcConfigEl: Elem): Map[Int, Int] = {
    (getIndicatorsEl(bcConfigEl) \ DBHA_DEF_ELEMENT_INDICATOR).map{ node =>
      val indicatorEl = node.asInstanceOf[Elem]
      getAttrValue(indicatorEl, DBHA_DEF_ELEMENT_ID).toInt ->
        getAttrValue(indicatorEl, DBHA_DEF_INDICATOR_CONTRIB).toInt
    }.toMap
  }

  def getDimIndicatorsMap(bcConfigEl: Elem): Map[Int, Array[Int]] = {
    (getDimEl(bcConfigEl) \ DBHA_DEF_ELEMENT_DIMENSION).map{ node =>
      val dimEl = node.asInstanceOf[Elem]
      val indicatorsEl = dimEl \ DBHA_DEF_ELEMENT_INDICATORS
      getAttrValue(dimEl, DBHA_DEF_ELEMENT_ID).toInt -> (indicatorsEl \ DBHA_DEF_ELEMENT_INDICATOR).map{ indicatorNode =>
        val indicatorEl = indicatorNode.asInstanceOf[Elem]
        getAttrValue(indicatorEl, DBHA_DEF_ELEMENT_ID).toInt
      }.toArray
    }.toMap
  }


  //////////////////////////////////////////////////////////////////////////////
  //

  lazy val resourceXmlConfig: Elem = loadConfig()

  val DBHA_RESOURCE_EVENT_ID_NAME: Map[Int, String] = {
    val eventIdNameMap = mutable.Map[Int, String]()

    (getEventsEl(resourceXmlConfig) \ DBHA_DEF_ELEMENT_EVENT).foreach{ node =>
      val e = node.asInstanceOf[Elem]
      eventIdNameMap.put(getAttrValue(e, DBHA_DEF_ELEMENT_ID).toInt, getAttrValue(e, DBHA_DEF_ELEMENT_NAME))
    }

    eventIdNameMap.toMap
  }

  val DBHA_RESOURCE_EVENT_VALUE_ID: Map[String, Int] = {
    val eventIdNameMap = mutable.Map[String, Int]()

    (getEventsEl(resourceXmlConfig) \ DBHA_DEF_ELEMENT_EVENT).foreach{ node =>
      val e = node.asInstanceOf[Elem]
      eventIdNameMap.put(getAttrValue(e, DBHA_DEF_ELEMENT_VALUE), getAttrValue(e, DBHA_DEF_ELEMENT_ID).toInt)
    }

    eventIdNameMap.toMap
  }

  val DBHA_RESOURCE_INDICATOR_ID_NAME: Map[Int, String] = {
    val indicatorIdNameMap = mutable.Map[Int, String]()

    (getIndicatorsEl(resourceXmlConfig) \ DBHA_DEF_ELEMENT_INDICATOR).foreach{ node =>
      val e = node.asInstanceOf[Elem]
      indicatorIdNameMap.put(getAttrValue(e, DBHA_DEF_ELEMENT_ID).toInt, getAttrValue(e, DBHA_DEF_ELEMENT_NAME))
    }

    indicatorIdNameMap.toMap
  }

  val DBHA_RESOURCE_INDICATOR_ID_CONTRIB: Map[Int, Int] = {
    val indicatorIdNameMap = mutable.Map[Int, Int]()

    (getIndicatorsEl(resourceXmlConfig) \ DBHA_DEF_ELEMENT_INDICATOR).foreach{ node =>
      val e = node.asInstanceOf[Elem]
      indicatorIdNameMap.put(getAttrValue(e, DBHA_DEF_ELEMENT_ID).toInt,
        getAttrValue(e, DBHA_DEF_INDICATOR_CONTRIB).toInt)
    }

    indicatorIdNameMap.toMap
  }

  val DBHA_RESOURCE_DIM_INDICATORS: Map[String, Array[String]] = {
    (getDimEl(resourceXmlConfig) \ DBHA_DEF_ELEMENT_DIMENSION).map{ node =>
      val dimEl = node.asInstanceOf[Elem]

      val indicatorNodes = dimEl \ DBHA_DEF_ELEMENT_INDICATORS

      getAttrValue(dimEl, DBHA_DEF_ELEMENT_NAME) -> (indicatorNodes \ DBHA_DEF_ELEMENT_INDICATOR)
        .map{ indicatorNode =>
        val indicatorEl = indicatorNode.asInstanceOf[Elem]
        getAttrValue(indicatorEl, DBHA_DEF_ELEMENT_NAME)
      }.toArray
    }.toMap
  }


  lazy val DBHA_DIMENSION_NAME_SAFETY = "安全维度"
  lazy val DBHA_DIMENSION_ID_SAFETY = 101

  lazy val DBHA_DIMENSION_NAME_ECONOMY= "节能维度"
  lazy val DBHA_DIMENSION_ID_ECONOMY = 102

  lazy val DBHA_DIMENSION_NAME_MAINTENANCE = "护车维度"
  lazy val DBHA_DIMENSION_ID_MAINTENANCE = 103

  lazy val DBHA_DIMENSION_NAME_CIVILIZATION = "文明维度"
  lazy val DBHA_DIMENSION_ID_CIVILIZATION = 104

  // Test
  def main(args: Array[String]): Unit = {
    val namenode ="file:///"
    val filepath = "D:\\SSI\\SSIDataPlatform\\05-Implement\\SourceCode\\trunk\\SSIDataPlatform\\DataDevelopmentPlatform\\DataMiningSite\\DataMiningPreProcess\\src\\main\\resources\\com\\dfssi\\dataplatform\\analysis\\rateindicator"

    val elem = loadConfig() //namenode, filepath

//    println(getEventEl(elem))
//    println(getDimEl(elem))
    println(getEventsIdNameMap(elem))
//    println(getEventEl(elem))
  }

}
