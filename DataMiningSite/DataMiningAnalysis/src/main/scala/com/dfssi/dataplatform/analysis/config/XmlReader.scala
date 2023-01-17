package com.dfssi.dataplatform.analysis.config

import java.io.{File, InputStream}

import org.apache.commons.io.IOUtils
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.Logging

import scala.xml.{Elem, NodeSeq, XML}

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/7 19:26
  */
object XmlReader extends Logging{

  def loadXmlConfig(fs: FileSystem,
                    configPath: String,
                    configFileName: String): Elem ={
    var config: Elem = null
    //从hdfs中读
    if(fs != null){
      try {
        config = loadXmlConfigFromHDFS(fs, configPath, configFileName)
        if(config != null)logInfo(s"加载hdfs中的配置文件${configPath}/${configFileName}成功。")
      } catch {
        case e: Exception =>
          logError(s"加载hdfs中的配置文件${configPath}/${configFileName}失败。", e)
      }
    }

    //从本地文件系统读
    if(config == null && configPath != null){
      try {
        config = loadXmlConfigFromFile(configPath, configFileName)
        if(config != null)logInfo(s"加载本地文件系统中的配置文件${configPath}/${configFileName}成功。")
      } catch {
        case e: Exception =>
          logError(s"加载本地文件系统中的配置文件${configPath}/${configFileName}失败。", e)
      }
    }

    //从包中读取
    if(config == null){
      config = loadXmlConfigFromPackage(configFileName)
      if(config != null)logInfo(s"加载jar中的配置文件${configFileName}成功。")
    }

    require(config != null, s"hdfs和本地均未读取到配置文件${configFileName}")

    config
  }

  def loadXmlConfigFromHDFS(fs: FileSystem,
                            configPath: String,
                            configFileName: String): Elem ={
    val is = fs.open(new Path(configPath, configFileName))
    val elem = XmlReader.loadFromInputStream(is)
    is.close()

    elem
  }

  def loadXmlConfigFromPackage(configFileName: String): Elem = XmlReader.loadFromPackage(s"/${configFileName}")

  def loadXmlConfigFromFile(configPath: String, configFileName: String): Elem = {
    val file = new File(configPath, configFileName)
    val stream = file.toURI.toURL.openStream()
    val elem = XmlReader.loadFromInputStream(stream)
    IOUtils.closeQuietly(stream)
    elem
  }

  def loadFromPackage(xmlFile: String): Elem ={
    loadFromInputStream(getClass.getResourceAsStream(xmlFile))
  }

  def loadFromInputStream(is: InputStream): Elem ={
    XML.load(is)
  }

  def loadFromString(xml: String): Elem ={
    XML.loadString(xml)
  }

  def findSubElem(elem: Elem, tag: String): NodeSeq ={
    elem \\ tag
  }

  def findSingleSubElem(elem: Elem, tag: String): Elem ={
    val nodeSeq = (elem \\ tag);
    if (nodeSeq.size > 0) {
      nodeSeq.head.asInstanceOf[Elem]
    }else{
      null
    }
  }

  def getNextSingleSubElem(elem: Elem, tag: String): Elem ={
    val nodeSeq = (elem \ tag)
    if (nodeSeq.size > 0) {
      nodeSeq.head.asInstanceOf[Elem]
    }else{
      null
    }
  }

  def getNextSubElem(elem: Elem, tag: String): NodeSeq ={
    elem \ tag
  }

  def getAttr(elem: Elem, name:String): String ={
    val option = elem.attribute(name)
    if(option.nonEmpty)
      option.get.text
    else
      null
  }

  def getAttrWithDefault(elem: Elem, name:String, defaultVal:String): String ={
    val option = elem.attribute(name)
    if(option.nonEmpty)
      option.get.text
    else
      defaultVal
  }

  def getAttrAsInt(elem: Elem, name:String, defaultVal:Int): Int ={
    val value = getAttr(elem, name)
    if(value != null && value.length > 0)
      value.toInt
    else
      defaultVal
  }

  def getAttrAsLong(elem: Elem, name:String, defaultVal:Long): Long ={
    val value = getAttr(elem, name)
    if(value != null && value.length > 0)
      value.toLong
    else
      defaultVal
  }

  def getAttrAsDouble(elem: Elem, name:String, defaultVal:Double): Double ={
    val value = getAttr(elem, name)
    if(value != null && value.length > 0)
      value.toDouble
    else
      defaultVal
  }

  def getAttrAsBoolean(elem: Elem, name:String, defaultVal:Boolean): Boolean ={
    val value = getAttr(elem, name)
    if(value != null && value.length > 0)
      value.toBoolean
    else
      defaultVal
  }

  def main(args: Array[String]): Unit = {
    val xml = XmlReader.loadFromPackage("/es-store-config.xml")
    println(XmlReader.getAttr(xml, "name"))



    println(XmlReader.findSubElem(xml, "es-index[@name='terminal_0702']"))
  }

}

