//package com.dfssi.dataplatform.service
//
//import java.sql.{Connection, DriverManager, ResultSet, Statement}
//
//import com.dfssi.dataplatform.config.JdbcManageConfig
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Service
//
///**
//  * Description
//  *
//  * @author bin.Y
//  * @version 2018/4/2 20:19
//  */
//object JdbcManage {
//  var connection: Connection = null
//  private final var driver = ""
//  private final var url = ""
//  private final var username = ""
//  private final var password = ""
//
//  def setParam(driver: String, url: String, username: String, password: String): Unit = {
//    this.driver = driver
//    this.url = url
//    this.username = username
//    this.password = password
//  }
//
//  def getConnection(): Unit = {
//    //    var properties = ConformanceCommon.loadProperties("mysqlDb.properties")
//    //
//    //    val driver = properties.getProperty("driverClassName")
//    //    val url = properties.getProperty("url")
//    //    val username = properties.getProperty("username")
//    //    val password = properties.getProperty("password")
//    try {
//      Class.forName(driver)
//      this.connection = DriverManager.getConnection(url, username, password)
//    } catch {
//      case e => e.printStackTrace
//    }
//  }
//
//  def executeQuery(sql: String): List[Map[String, String]] = {
//    var resultSet: ResultSet = null
//    var result = List[Map[String, String]]()
//    try {
//      val statement = getStatement()
//      resultSet = statement.executeQuery(sql)
//      while (resultSet.next()) {
//        var hm = Map[String, String]()
//        val rsmd = resultSet.getMetaData();
//        val count = rsmd.getColumnCount();
//        for (i <- 1 to count) {
//          val key = rsmd.getColumnLabel(i).toLowerCase();
//          val value = resultSet.getString(i);
//          hm += (key -> value)
//        }
//        result = result :+ hm
//      }
//    } catch {
//      case e => e.printStackTrace
//    } finally {
//      closeConn()
//    }
//    result
//  }
//
//  def executeUpdate(sql: String): Unit = {
//    try {
//      val statement = getStatement()
//      statement.executeUpdate(sql)
//    } finally {
//      closeConn()
//    }
//  }
//
//  def getStatement(): Statement = {
//    var statement: Statement = null
//    try {
//      getConnection()
//      statement = connection.createStatement()
//    } catch {
//      case e => e.printStackTrace
//    }
//    statement
//  }
//
//  def closeConn(): Unit = {
//    if (connection != null)
//      connection.close()
//  }
//
//
//}
