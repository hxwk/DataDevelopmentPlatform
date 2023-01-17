package com.dfssi.dataplatform.streaming.store

import java.net.URI

import com.dfssi.dataplatform.streaming.store.config.StreamingStoreConfig
import org.apache.commons.io.IOUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

trait SparkProcessTrait {
  val CONFIG_FILE_NAME = "StoreConfig.xml"

  def loadConfigFromPackage(): StreamingStoreConfig = {
    val conf = new StreamingStoreConfig()
    conf.loadConfigFromPackage(
      "/com/dfssi/dataplatform/streaming/store/config/StoreConfig.xml")

    conf
  }

  def loadConfigFromHdfs(nameNode: String,
                         appPath: String): StreamingStoreConfig = {
    val uri = new URI(nameNode)
    val conf = new Configuration()
    val fs = FileSystem.get(uri, conf)
    val is = fs.open(new Path(appPath + "/" + CONFIG_FILE_NAME))
    val configStr = IOUtils.toString(is)
    is.close()
    fs.close()

    val config = new StreamingStoreConfig()
    config.loadConfig(configStr)

    config
  }
}
