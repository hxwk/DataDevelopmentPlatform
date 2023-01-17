package com.dfssi.dataplatform.analysis.oozie

import java.net.URI

import com.dfssi.spark.config.xml.{XmlConfig, XmlConfigReader}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.Logging

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/1 15:21 
  */
trait OozieAppTrait extends Logging{
  val CONFIG_FILE_NAME = "StoreConfig.xml"

  def loadConfigFromHdfs(nameNode: String,
                         appPath: String,
                         rootTag: String):XmlConfig = {
    val uri = new URI(nameNode)
    val conf = new Configuration()
    val fs = FileSystem.get(uri, conf)
    val is = fs.open(new Path(s"${appPath}/${CONFIG_FILE_NAME}"))

    val config = XmlConfigReader.readXml(is, rootTag)

    is.close()
    fs.close()

    config
  }

}
