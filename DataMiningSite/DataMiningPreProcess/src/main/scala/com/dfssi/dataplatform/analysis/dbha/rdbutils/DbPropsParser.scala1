package com.dfssi.dataplatform.analysis.dbha.rdbutils

import java.util.Properties

object DbPropsParser extends Serializable {

  def getGreenplumAnalysisDbProps(paramsMap: Map[String, String]): Properties = {
    val properties = new Properties()

    val dbName = paramsMap("AnalysisDB_jdbcDatabase")
    val jdbcHostname = paramsMap("AnalysisDB_jdbcHostname")
    val jdbcPort = paramsMap("AnalysisDB_jdbcPort")
    val url = s"jdbc:postgresql://$jdbcHostname:$jdbcPort/$dbName"
    properties.put("url", url)

    val driverClassName = paramsMap("AnalysisDB_driver")
    properties.put("driverClassName", driverClassName)

    val username = paramsMap("AnalysisDB_user")
    properties.put("username", username)

    val password = paramsMap("AnalysisDB_password")
    properties.put("password", password)

    // other properties ...

    properties
  }

  def getMySqlMotocadeDbProps(paramsMap: Map[String, String]): Properties = {
    val properties = new Properties()

    val dbName = paramsMap("MotorcadeDB_jdbcDatabase")
    val jdbcHostname = paramsMap("MotorcadeDB_jdbcHostname")
    val jdbcPort = paramsMap("MotorcadeDB_jdbcPort")
    val url = s"jdbc:mysql://$jdbcHostname:$jdbcPort/$dbName?useSSL=false"
    properties.put("url", url)

    val driverClassName = paramsMap("MotorcadeDB_driver")
    properties.put("driverClassName", driverClassName)

    val username = paramsMap("MotorcadeDB_user")
    properties.put("username", username)

    val password = paramsMap("MotorcadeDB_password")
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

  @Deprecated
  def getMySqlServiceDbProps(paramsMap: Map[String, String]): Properties = {
    val properties = new Properties()

    val dbName = paramsMap("ServiceDB_jdbcDatabase")
    val jdbcHostname = paramsMap("ServiceDB_jdbcHostname")
    val jdbcPort = paramsMap("ServiceDB_jdbcPort")
    val url = s"jdbc:mysql://$jdbcHostname:$jdbcPort/$dbName?useSSL=false"
    properties.put("url", url)

    val driverClassName = paramsMap("ServiceDB_driver")
    properties.put("driverClassName", driverClassName)

    val username = paramsMap("ServiceDB_user")
    properties.put("username", username)

    val password = paramsMap("ServiceDB_password")
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
}
