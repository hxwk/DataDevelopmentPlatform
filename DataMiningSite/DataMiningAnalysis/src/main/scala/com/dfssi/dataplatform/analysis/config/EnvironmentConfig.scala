package com.dfssi.dataplatform.analysis.config

import org.apache.hadoop.fs.FileSystem
import org.apache.spark.Logging

import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/6/21 10:15 
  */
class EnvironmentConfig(config: Map[String, Map[String, String]]) extends Serializable with Logging{

    require(config != null, "环境配置为null。")
    logInfo(s"环境配置细节如下：\n\t ${toString}")

    def getEnvironment(id: String): Map[String, String] = config.getOrElse(id, Map.empty[String, String])


    override def toString = s"EnvironmentConfig(${config})"
}

object EnvironmentConfig{

    val CONFIG_DIR = "/user/hdfs/config/env"
    val CONFIG_FILE = "environment.xml"

    def buildFromXml(el: Elem): EnvironmentConfig ={
        val configMap = (el \ "environment").map(node =>{
            val environment = node.asInstanceOf[Elem]
            val id = XmlReader.getAttr(environment, "id")
            require(id != null, s"标签${environment}中不存在id属性。")

            var paramMap: Map[String, String] = null
            val params = XmlReader.getNextSingleSubElem(environment, "params")
            if(params != null){
               paramMap = (params \ "param").map(n =>{
                    val param = n.asInstanceOf[Elem]
                    val name = XmlReader.getAttr(param, "name")
                    val value = XmlReader.getAttr(param, "value")
                    (name, value)
                }).toMap
            }

            (id, paramMap)
        }).toList.filter(_._2 != null).toMap

        new EnvironmentConfig(configMap)
    }

    def buildFromHdfsOrLocal(fs: FileSystem): EnvironmentConfig ={
        val elem = XmlReader.loadXmlConfig(fs, CONFIG_DIR, CONFIG_FILE)
        buildFromXml(elem)
    }

    def main(args: Array[String]): Unit = {
        val config = buildFromHdfsOrLocal(null)
        println(config)
    }

}