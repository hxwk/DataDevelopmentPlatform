package com.dfssi.dataplatform.analysis.ccv.fuel

import java.sql.Connection
import java.util.Properties

import com.dfssi.common.JavaOps
import com.dfssi.dataplatform.analysis.config.{HdfsXmlConfig, XmlReader}
import com.dfssi.dataplatform.analysis.es.indexs.{IndexEntity, TypeEntity}
import com.dfssi.dataplatform.analysis.redis.SentinelConnectionPool.SentinelRedisEndpoint
import org.apache.spark.Logging
import redis.clients.jedis.Jedis

import scala.collection.mutable
import scala.xml.Elem

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/23 10:58 
  */
private[fuel]
class VehicleFuelConfig (val config: HdfsXmlConfig) extends Serializable with Logging{


    private val rootElement = config.getConfigNode("analysis")

    //数据过滤相关
    val filters = loadDataFilters()

    //redis 相关
    @transient
    lazy val redisEndpoint = loadRedisEndpoint()
    val redisKeyPartitions = loadRedisKeyPartition()

    //数据库表相关
    val (dataBaseId, totalfuelTable, fueltripTable, fuelTable, abnormaldriving) = loadDataBase()

    //行程切分相关
    val (tv, ta, tr, da) = loadTripConditions()

    //部分必须字段
    val (timeField) = loadFileds()

    //告警标签
    val abnormalLabels = loadAbnormalLabels()

    //告警数据 es相关
    val (typeEntity, esParams, idfield) = loadAlarmEsTypeEntity()

    private def loadDataFilters(): List[DataFilterCondition] ={
        val filtersElement = XmlReader.getNextSingleSubElem(rootElement, "analysis-filters")
        val filters = (filtersElement \ "filter").map(filterNode =>{
            val filterElement = filterNode.asInstanceOf[Elem]
           DataFilterCondition.buildFromXmlElem(filterElement)
        })
        filters.toList.filter(_ != null)
    }

    private def loadRedisEndpoint(): SentinelRedisEndpoint ={
        val redisEl = XmlReader.getNextSingleSubElem(rootElement, "analysis-redis")

        //优先读取环境配置中的参数
        val redisEnv = config.environmentConfig.getEnvironment("redis")

        val master = redisEnv.getOrElse("master", XmlReader.getAttrWithDefault(redisEl, "master", "mymaster"))
        val sentinels = redisEnv.getOrElse("sentinels", XmlReader.getAttr(redisEl, "sentinels"))
        require(sentinels != null, s"${redisEl}中属性sentinels为空")

        val password = redisEnv.getOrElse("password", XmlReader.getAttr(redisEl, "password"))

        val dbNum = XmlReader.getAttrWithDefault(redisEl, "dbNum", "6").toInt
        val timeout = XmlReader.getAttrWithDefault(redisEl, "timeout", "2000").toInt

        SentinelRedisEndpoint(sentinels, master, password, dbNum, timeout)
    }

    private def loadRedisKeyPartition(): Int ={
        val redisEl = XmlReader.getNextSingleSubElem(rootElement, "analysis-redis")
        XmlReader.getAttrWithDefault(redisEl, "partitions", "20").toInt
    }

    private def loadFileds(): (String) ={
        val fieldsEl = XmlReader.getNextSingleSubElem(rootElement, "analysis-fields")
        val timeField = XmlReader.getAttrWithDefault(fieldsEl, "time", "gpsTime")
        (timeField)
    }

    private def loadDataBase(): (String, String, String, String, String) ={
        val dataBaseEl = XmlReader.getNextSingleSubElem(rootElement, "analysis-databse")

        val dataBaseId = XmlReader.getAttr(dataBaseEl, "id")
        require(dataBaseId != null, s"${dataBaseEl}中属性id为空。")

        val totalfuelTable = XmlReader.getAttrWithDefault(dataBaseEl, "totalfuel", "vehicle_total_fuel")
        val fueltripTable = XmlReader.getAttrWithDefault(dataBaseEl, "fueltrip", "vehicle_trip")
        val fuelTable = XmlReader.getAttrWithDefault(dataBaseEl, "fuel", "vehicle_fuel")
        val abnormaldriving = XmlReader.getAttrWithDefault(dataBaseEl, "abnormaldriving", "vehicle_abnormal_driving")

        (dataBaseId, totalfuelTable, fueltripTable, fuelTable, abnormaldriving)
    }

    private def loadAbnormalLabels(): Map[String, Int] ={
        val abnormalLabelsEl = XmlReader.getNextSingleSubElem(rootElement, "analysis-abnormalLabels")
        (abnormalLabelsEl \ "label").map(node =>{
            val labelElem = node.asInstanceOf[Elem]
            (XmlReader.getAttr(labelElem, "name"), XmlReader.getAttr(labelElem, "value").toInt)
        }).toMap

    }

    private def loadAlarmEsTypeEntity(): (TypeEntity, Map[String, String], String) ={

        val esEl = XmlReader.getNextSingleSubElem(rootElement, "analysis-es")
        val index = XmlReader.getAttr(esEl, "index")
        require(index != null, s"${esEl}中属性index为空")

        val createRule = XmlReader.getAttrWithDefault(esEl, "createRule", "day")

        //生成Index实体
        val indexEntity = new IndexEntity(index, createRule)
        //读取index的settings
        val settingsElem = XmlReader.getNextSubElem(esEl, "settings")
        for(setting <- settingsElem \ "setting"){
            val settingElem = setting.asInstanceOf[Elem]
            indexEntity.addSetting(XmlReader.getAttr(settingElem, "name"),
                XmlReader.getAttr(settingElem, "value"))
        }

        //读取入es的配置
        val esParams = new mutable.HashMap[String, String]()
        val paramEl = XmlReader.getNextSingleSubElem(esEl, "params")
        (paramEl \ "param").foreach(node =>{
            val elem = node.asInstanceOf[Elem]
            esParams += ((XmlReader.getAttr(elem, "name"), XmlReader.getAttr(elem, "value")))
        })
        esParams ++= config.environmentConfig.getEnvironment("elasticsearch")
        val params = esParams.toMap

        val idfield = params.getOrElse("es.mapping.id", null)
        require(idfield != null, s"es.mapping.id不能为空。")
        logInfo(s"输出到es的配置为：\n\t ${params}")

        //生成type实体
        val typeName = XmlReader.getAttrWithDefault(esEl, "type", index)
        val typeEntity = new TypeEntity(typeName, null)
        typeEntity.addColumn(idfield, "keyword", null)
        typeEntity.addColumn("vid", "keyword", null)
        typeEntity.addColumn("enterprise", "keyword", null)
        typeEntity.addColumn("hatchback", "keyword", null)
        typeEntity.addColumn("time", "long", null)

        indexEntity.addType(typeName, typeEntity)

        (typeEntity, params, idfield)
    }

    private def loadTripConditions(): (Long, Long, Long, Int) ={
        val tripConditionsElement = XmlReader.getNextSingleSubElem(rootElement, "analysis-trip")

        var elem = XmlReader.getNextSingleSubElem(tripConditionsElement, "time-valid")
        val tv = JavaOps.timeStringAsMs(XmlReader.getAttrWithDefault(elem, "value", "1m"))

        elem = XmlReader.getNextSingleSubElem(tripConditionsElement, "time-acceptable")
        val ta = JavaOps.timeStringAsMs(XmlReader.getAttrWithDefault(elem, "value", "5m"))

        elem = XmlReader.getNextSingleSubElem(tripConditionsElement, "time-refuse")
        val tr = JavaOps.timeStringAsMs(XmlReader.getAttrWithDefault(elem, "value", "15m"))

        elem = XmlReader.getNextSingleSubElem(tripConditionsElement, "mile-acceptable")
        val da = XmlReader.getAttrWithDefault(elem, "value", "1").toInt

        (tv, ta, tr, da)
    }


    def getAlarmLabel(alarm: String): Int ={
        abnormalLabels.getOrElse(alarm, -1)
    }

    def getConnection(): Connection =
        config.dataBaseConnectionManager.getConnection(dataBaseId).getConnection

    def getConnectionProperties(): (String, Properties) ={

        val connection = config.dataBaseConnectionManager.getConnection(dataBaseId)

        val connectionProperties = new Properties()
        connectionProperties.put("driver", connection.getDriver)
        connectionProperties.put("user", connection.getUser)
        connectionProperties.put("password", connection.getPwd)

        (connection.getUrl, connectionProperties)
    }

    def closeConnection(): Unit =
        config.dataBaseConnectionManager.closeConnection(dataBaseId)

    def closeRedisEndPoint: Unit = redisEndpoint.close()

    def getRedisClient(): Jedis = redisEndpoint.connect()

    def getConfigNode(key: String): Elem = XmlReader.getNextSingleSubElem(rootElement, key)


}
