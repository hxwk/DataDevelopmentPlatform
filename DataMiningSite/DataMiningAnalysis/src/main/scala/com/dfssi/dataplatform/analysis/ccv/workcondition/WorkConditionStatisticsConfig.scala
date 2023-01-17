package com.dfssi.dataplatform.analysis.ccv.workcondition

import java.sql.Connection
import java.util.Properties

import com.dfssi.dataplatform.analysis.config.{HdfsXmlConfig, XmlReader}
import org.apache.spark.Logging

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/7/3 9:08 
  */
class WorkConditionStatisticsConfig (xmlConfig: HdfsXmlConfig) extends Serializable with Logging{
    private val elem = xmlConfig.getConfigNode("condition")

    val  (hiveDB, terminal0200, terminal0705) = loadHiveConfig()
    val (databaseId, conditionfuel, area) = loadDatabseConfig()

    private def loadHiveConfig(): (String, String, String) ={
        val hiveEl = XmlReader.getNextSingleSubElem(elem, "condition-hive")

        val hiveDB = XmlReader.getAttrWithDefault(hiveEl, "database", "prod_analysis")
        val terminal0200 = XmlReader.getAttrWithDefault(hiveEl, "terminal0200", "terminal_0200")
        val terminal0705 = XmlReader.getAttrWithDefault(hiveEl, "terminal0705", "terminal_0705")

        logInfo(s"hive配置为： (hiveDB, terminal0200, terminal0705) = (${hiveDB}, ${terminal0200}, ${terminal0705})")

        (hiveDB, terminal0200, terminal0705)
    }

    private def loadDatabseConfig(): (String, String, String) ={
        val dbEl = XmlReader.getNextSingleSubElem(elem, "condition-database")

        val databaseId = XmlReader.getAttr(dbEl, "id")
        require(databaseId != null, s"${dbEl}中的数据库id不能为空。")

        val conditionfuel = XmlReader.getAttrWithDefault(dbEl, "conditionfuel", "vehicle_workcondition_fuel")
        val area = XmlReader.getAttrWithDefault(dbEl, "area", "vehicle_area_fuel")
        logInfo(s"数据库配置为：(databaseId, conditionfuel, area) = (${databaseId}, ${conditionfuel}, ${area})")

        (databaseId, conditionfuel, area)
    }

    def getConnectionProperties(): (String, Properties) ={

        val connection = xmlConfig.dataBaseConnectionManager.getConnection(databaseId)

        val connectionProperties = new Properties()
        connectionProperties.put("driver", connection.getDriver)
        connectionProperties.put("user", connection.getUser)
        connectionProperties.put("password", connection.getPwd)

        (connection.getUrl, connectionProperties)
    }

    def getConnection(): Connection =
        xmlConfig.dataBaseConnectionManager.getConnection(databaseId).getConnection

    def closeConnection(): Unit =
        xmlConfig.dataBaseConnectionManager.closeConnection(databaseId)
}
