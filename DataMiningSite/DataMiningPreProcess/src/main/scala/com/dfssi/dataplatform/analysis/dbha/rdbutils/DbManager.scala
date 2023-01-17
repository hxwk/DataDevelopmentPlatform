package com.dfssi.dataplatform.analysis.dbha.rdbutils

import java.sql._
import java.util.Properties

import org.apache.commons.dbcp.{BasicDataSource, BasicDataSourceFactory}
import org.apache.spark.Logging

class DbManager {

  /**
    * 创建单个的数据库连接，适用于分布式数据集中每个分区创建一个连接的情况
    * @return
    */
  def getSingleConn(dbProps: Properties): Connection = {

    if(dbProps.getProperty("driverClassName") == null) Class.forName(dbProps.getProperty("driver"))

    Class.forName(dbProps.getProperty("driverClassName"))

    DriverManager.getConnection(dbProps.getProperty("url"), dbProps.getProperty("username"), dbProps.getProperty("password"))
  }

}


object DbManager extends Serializable with Logging {

  def apply: DbManager = new DbManager()

  /**************************************************************************/
  // 以下参数和方法用于创建连接池，不适用于RDD分布环境中需要切换不同数据源的场景

  private var bs: BasicDataSource = _

  /**
    * 根据数据库配置参数初始化一个数据源
    * @param dbProps 数据库参数
    * @return
    */
  def initDataSource(dbProps: Properties): BasicDataSource = {
    if(bs != null && !bs.isClosed)
      bs.close()
    bs = BasicDataSourceFactory.createDataSource(dbProps).asInstanceOf[BasicDataSource]
    bs
  }

  /**
    * 创建数据源
    *
    * @return
    */
  def getDataSource(dbProps: Properties): BasicDataSource = {
    if (bs == null || bs.isClosed) {
      bs = BasicDataSourceFactory.createDataSource(dbProps).asInstanceOf[BasicDataSource]
    }
    bs
  }

  /**
    * 释放数据源
    */
  def shutDownDataSource() {
    if (bs != null) {
      bs.close()
    }
    bs = null
  }

  /**
    * 获取数据库连接
    *
    * @return
    */
  def getConnection(dbProps: Properties): Connection = {
    var conn: Connection = null
    try {
      if (bs != null && !bs.isClosed) {
        conn = bs.getConnection()
      } else {
        conn = getDataSource(dbProps).getConnection()
      }
    } catch {
      case e: Exception => logError("---------Get connection " + e.getMessage + "\n-----------------------\n" + e.printStackTrace())
    }
    conn
  }

  /**
    * 关闭连接
    */
  def closeCon(rs: ResultSet, ps: PreparedStatement, conn: Connection) = {
    if (rs != null) {
      try {
        rs.close()
      } catch {
        case e: Exception => logError("---------Close ResultSet " + e.getMessage + "\n-----------------------\n" + e.printStackTrace())
      }
    }
    if (ps != null) {
      try {
        ps.close()
      } catch {
        case e: Exception => logError("---------Close PreparedStatement " + e.getMessage + "\n-----------------------\n" + e.printStackTrace())
      }
    }
    if (conn != null) {
      try {
        conn.close()
      } catch {
        case e: Exception => logError("---------Close Connection " + e.getMessage + "\n-----------------------\n" + e.printStackTrace())
      }
    }
  }


  def closeConn(rs: ResultSet, ps: Statement, conn: Connection) = {
    if (rs != null) {
      try {
        rs.close()
      } catch {
        case e: Exception =>
          logError("---------Close ResultSet " + e.getMessage + "\n-----------------------\n" + e.printStackTrace())
      }
    }
    if (ps != null) {
      try {
        ps.close()
      } catch {
        case e: Exception =>
          logError("---------Close PreparedStatement " + e.getMessage + "\n-----------------------\n" + e.printStackTrace())
      }
    }
    if (conn != null) {
      try {
        conn.close()
      } catch {
        case e: Exception =>
          logError("---------Close Connection " + e.getMessage + "\n-----------------------\n" + e.printStackTrace())
      }
    }
  }

}
