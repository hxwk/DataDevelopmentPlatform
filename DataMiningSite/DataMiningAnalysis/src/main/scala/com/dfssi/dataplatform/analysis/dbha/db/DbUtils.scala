package com.dfssi.dataplatform.analysis.dbha.db

import java.sql.{Connection, DriverManager}
import java.util.Properties

object DbUtils extends Serializable {

  //  jdbc:mysql
  def getDbProps(paramPrefix: String,
                 urlPrefix: String,
                 paramsMap: Map[String, String]): Properties = {
    val properties = new Properties()

    val dbName = paramsMap(s"${paramPrefix}jdbcDatabase")
    val jdbcHostname = paramsMap(s"${paramPrefix}jdbcHostname")
    val jdbcPort = paramsMap(s"${paramPrefix}jdbcPort")
    val url = s"$urlPrefix://$jdbcHostname:$jdbcPort/$dbName?useSSL=false"
    properties.put("url", url)

    val driverClassName = paramsMap(s"${paramPrefix}driver")
    properties.put("driverClassName", driverClassName)

    val username = paramsMap(s"${paramPrefix}user")
    properties.put("username", username)

    val password = paramsMap(s"${paramPrefix}password")
    properties.put("password", password)

    // other properties ...
    //    properties.put("initialSize", "1")
    //    properties.put("maxTotal", "5")
    //    properties.put("maxIdle", "3")
    //    properties.put("minIdle", "1")
    //    properties.put("maxWaitMillis", "1000")
    //    properties.put("removeAbandoned", "true")
    //    properties.put("removeAbandonedTimeout", "10")
    //    properties.put("timeBetweenEvictionRunsMillis", "300000")
    //    properties.put("minEvictableIdleTimeMillis", "60000")
    properties.put("validationQuery", "select 1")
    properties.put("testOnBorrow", "true")
    properties.put("useSSL", "false")

    properties
  }

  //  AnalysisDB_
  def getGreenplumDbProps(paramPrefix: String, paramsMap: Map[String, String]): Properties = {
    val properties = new Properties()

    val dbName = paramsMap(s"${paramPrefix}jdbcDatabase")
    val jdbcHostname = paramsMap(s"${paramPrefix}jdbcHostname")
    val jdbcPort = paramsMap(s"${paramPrefix}jdbcPort")
    val url = s"jdbc:postgresql://$jdbcHostname:$jdbcPort/$dbName"
    properties.put("url", url)

    val driverClassName = paramsMap(s"${paramPrefix}driver")
    properties.put("driverClassName", driverClassName)

    val username = paramsMap(s"${paramPrefix}user")
    properties.put("username", username)

    val password = paramsMap(s"${paramPrefix}password")
    properties.put("password", password)

    // other properties ...

    properties
  }

  def getMySqlDbProps(paramPrefix: String, paramsMap: Map[String, String]): Properties = {
    val properties = new Properties()

    val dbName = paramsMap(s"${paramPrefix}jdbcDatabase")
    val jdbcHostname = paramsMap(s"${paramPrefix}jdbcHostname")
    val jdbcPort = paramsMap(s"${paramPrefix}jdbcPort")
    val url = s"jdbc:mysql://$jdbcHostname:$jdbcPort/$dbName?useSSL=false"
    properties.put("url", url)

    val driverClassName = paramsMap(s"${paramPrefix}driver")
    properties.put("driverClassName", driverClassName)

    val username = paramsMap(s"${paramPrefix}user")
    properties.put("username", username)

    val password = paramsMap(s"${paramPrefix}password")
    properties.put("password", password)

    // other properties ...
    //    properties.put("initialSize", "1")
    //    properties.put("maxTotal", "5")
    //    properties.put("maxIdle", "3")
    //    properties.put("minIdle", "1")
    //    properties.put("maxWaitMillis", "1000")
    //    properties.put("removeAbandoned", "true")
    //    properties.put("removeAbandonedTimeout", "10")
    //    properties.put("timeBetweenEvictionRunsMillis", "300000")
    //    properties.put("minEvictableIdleTimeMillis", "60000")
    properties.put("validationQuery", "select 1")
    properties.put("testOnBorrow", "true")
    properties.put("useSSL", "false")

    properties
  }


  /**
    * 创建单个的数据库连接，适用于分布式数据集中每个分区创建一个连接的情况
    *
    * @return
    */
  def getSingleConn(dbProps: Properties): Connection = {

    if (dbProps.getProperty("driverClassName") == null) Class.forName(dbProps.getProperty("driver"))

    Class.forName(dbProps.getProperty("driverClassName"))

    DriverManager.getConnection(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"))
  }
}
