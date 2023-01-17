package com.dfssi.dataplatform.analysis.config

import java.io.File

import com.dfssi.common.databases.DBType
import com.dfssi.dataplatform.analysis.KafkaStream
import com.dfssi.dataplatform.analysis.utils.DataBaseConnectionManager
import org.apache.commons.io.IOUtils
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.{Logging, SparkContext, SparkFiles}

import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/4/10 10:05
  */
class HdfsXmlConfig(val configDir: String,
                    val configFileName: String,
                    val config: Elem,
                    val environmentConfig: EnvironmentConfig) extends Serializable with Logging{

    require(config != null, "未读取到配置，参数如下：\n\t " +
            s"configPath = ${configDir}, configFileName = ${configFileName}")

    lazy val dataBaseConnectionManager:DataBaseConnectionManager = loadDatabase()

    lazy val kafkaStream: KafkaStream = loadKafkaStream()

    def getConfigNode(key: String): Elem = XmlReader.getNextSingleSubElem(config, key)

    def getAttr(key: String): String = XmlReader.getAttr(config, key)

    def getAttrWithDefault(key: String, defaultValue: String): String =
        XmlReader.getAttrWithDefault(config, key, defaultValue)

    private def loadDatabase(): DataBaseConnectionManager ={

        val connectionManager = new DataBaseConnectionManager()

        val databasesEl = getConfigNode("databases")
        if(databasesEl != null) {
            (databasesEl \ "database").foreach(node => {
                val databaseEl = node.asInstanceOf[Elem]
                val id = XmlReader.getAttr(databaseEl, "id")
                require(id != null, s"${databaseEl}中的id属性不能为空")
                val map = environmentConfig.getEnvironment(id)

                val typeName = map.getOrElse("type", XmlReader.getAttr(databaseEl, "type"))
                val bType = DBType.newDBType(typeName)

                val url =  map.getOrElse("url", XmlReader.getAttr(databaseEl, "url"))
                require(url != null, s"${databaseEl}中的url属性不能为空")

                val user = map.getOrElse("user", XmlReader.getAttr(databaseEl, "user"))
                require(user != null, s"${databaseEl}中的user属性不能为空")

                val password = map.getOrElse("password", XmlReader.getAttr(databaseEl, "password"))
                require(password != null, s"${databaseEl}中的password属性不能为空")

                val driver = map.getOrElse("driver", XmlReader.getAttr(databaseEl, "driver"))
                require(driver != null, s"${databaseEl}中的driver属性不能为空")

                val connection = new DataBaseConnectionManager.DataBaseConnection(bType, url, driver, user, password)
                connectionManager.addConnection(id, connection)
            })
        }
        connectionManager
    }

    private def loadKafkaStream(): KafkaStream ={
        val elem = getConfigNode("kafka-input")
        if(elem != null){
            KafkaStream.buildFromXmlElem(elem, environmentConfig.getEnvironment("kafka"))
        }else{
            null
        }
    }
}

object HdfsXmlConfig extends Logging {

    def apply(fs: FileSystem,
              configDir: String,
              configFileName: String): HdfsXmlConfig ={
        val config = EnvironmentConfig.buildFromHdfsOrLocal(fs)
        val elem = XmlReader.loadXmlConfig(fs, configDir, configFileName)
        new HdfsXmlConfig(configDir, configFileName, elem, config)
    }

    def apply(fs: FileSystem,
              configDir: String,
              configFileName: String,
              environmentConfig: EnvironmentConfig): HdfsXmlConfig ={
        val elem = XmlReader.loadXmlConfig(fs, configDir, configFileName)
        new HdfsXmlConfig(configDir, configFileName, elem, environmentConfig)
    }

    def apply(sc: SparkContext,
              configDir: String,
              configFileName: String): HdfsXmlConfig ={

        val elems = sc.parallelize(0 to 0, 1).map { i =>

            val dir = SparkFiles.getRootDirectory()
            try {
                var file = new File(dir, configFileName)
                var config: Elem = null
                if(file.exists()){
                    val stream = file.toURI.toURL.openStream()
                    config = XmlReader.loadFromInputStream(stream)
                    IOUtils.closeQuietly(stream)
                }

                file = new File(EnvironmentConfig.CONFIG_FILE)
                var environmentConfig: Elem = null
                if(file.exists()){
                    val stream = file.toURI.toURL.openStream()
                    environmentConfig = XmlReader.loadFromInputStream(stream)
                    IOUtils.closeQuietly(stream)
                }
                (config, environmentConfig)
            } catch {
                case e:Throwable =>
                    (null, null)
            }
        }.first()

        //获取hdfs
        val fs = FileSystem.get(sc.hadoopConfiguration)

        var environmentConfig: EnvironmentConfig = null
        if(elems._2 == null){
            environmentConfig = EnvironmentConfig.buildFromHdfsOrLocal(fs)
        }else{
            environmentConfig = EnvironmentConfig.buildFromXml(elems._2)
        }

        logInfo(s"从节点中读取到配置文件内容：\n ${elems}")
        var config: HdfsXmlConfig = null

        if(elems._1 == null){
            config = HdfsXmlConfig(fs, configDir, configFileName, environmentConfig)
        }else{
            config = new HdfsXmlConfig(configDir, configFileName, elems._1, environmentConfig)
        }
        config
    }
}