package com.dfssi.dataplatform.analysis.preprocess.output

import java.sql.{Connection, PreparedStatement, ResultSet, Statement}
import java.util.Properties

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.commons.dbcp.{BasicDataSource, BasicDataSourceFactory}
import org.apache.spark.Logging
import org.apache.spark.sql.{DataFrame, Row, SaveMode}

import scala.collection.mutable
import scala.xml.Elem

class OutputJDBC extends AbstractProcess {

  override def execute(processContext: ProcessContext,
                       defEl: Elem,
                       sparkTaskDefEl: Elem): Unit = {
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl);
    var inputIds: Array[String] = XmlUtils
      .getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_INPUTIDS)
      .split(",");
    val outputDF = processContext.dataFrameMap.get(inputIds(0));
    if (OutputJDBC.OPERATOR_TYPE_OVERWRITE.equalsIgnoreCase(
          getOutputMode(paramsMap)) || OutputJDBC.OPERATOR_TYPE_APPEND
          .equalsIgnoreCase(getOutputMode(paramsMap))) {
      saveOverride(processContext, paramsMap, outputDF.get)
    } else {
      saveSql(processContext, paramsMap, outputDF.get)
    }
  }

  def getOutputMode(paramsMap: mutable.Map[String, String]): String = {
    getParamValue(paramsMap, "outputMode")
  }

  def saveOverride(processContext: ProcessContext,
                   paramsMap: mutable.Map[String, String],
                   outputDF: DataFrame): Unit = {
    val tableName = getParamValue(paramsMap, "tableName")
    val url = getParamValue(paramsMap, "url")
    outputDF.write
      .mode(SaveMode.valueOf(getOutputMode(paramsMap)))
      .jdbc(url, tableName, getSparkProperties(paramsMap))
  }
  def getSparkProperties(paramsMap: mutable.Map[String, String]): Properties = {
    val properties = new Properties()
    properties.setProperty("url", getParamValue(paramsMap, "url"))
    properties.setProperty("user", getParamValue(paramsMap, "username"))
    properties.setProperty("password", getParamValue(paramsMap, "password"))
    properties.setProperty("driver",
                           getParamValue(paramsMap, "driverClassName"))

    properties
  }

  def getProperties(paramsMap: mutable.Map[String, String]): Properties = {
    val properties = new Properties()
    for ((k, v) <- paramsMap) {
      properties.setProperty(k, v)
    }
    properties
  }

  def saveSql(processContext: ProcessContext,
              paramsMap: mutable.Map[String, String],
              outputDF: DataFrame): Unit = {
    if (!outputDF.rdd.isEmpty()) {
      val jDBCManager = new JDBCManager()
      outputDF.foreachPartition(p => {
        val dataArr: Array[Row] = p.toArray
        dataArr.foreach(r => { execSql(jDBCManager, r, paramsMap) })
      })
    }
  }

  def execSql(jDBCManager: JDBCManager,
              row: Row,
              paramsMap: mutable.Map[String, String]): Unit = {
    var conn: Connection = null
    var stat: Statement = null
    try {
      conn = jDBCManager.getConnection(getProperties(paramsMap))
      stat = conn.createStatement()
      val sql = row.getAs[String](0)
      stat.execute(sql)
    } catch {
      case exception: Exception =>
        logError(
          "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
            .printStackTrace())
    } finally {
      jDBCManager.closeConn(null, stat, conn)
    }
  }
}

object OutputJDBC {
  val processType: String = ProcessFactory.PROCESS_NAME_OUTPUT_JDBC
  val OPERATOR_TYPE_OVERWRITE: String = "Overwrite"
  val OPERATOR_TYPE_APPEND: String = "Append"
  val OPERATOR_TYPE_SQL: String = "Sql"

}

class JDBCManager extends Serializable with Logging {
  private var bs: BasicDataSource = _

  /**
    * 根据数据库配置参数初始化一个数据源
    *
    * @param dbProps 数据库参数
    * @return
    */
  def initDataSource(dbProps: Properties): BasicDataSource = {
    if (bs != null && !bs.isClosed) bs.close()
    bs = BasicDataSourceFactory
      .createDataSource(dbProps)
      .asInstanceOf[BasicDataSource]
    bs
  }

  /**
    * 创建数据源
    *
    * @return
    */
  def getDataSource(dbProps: Properties): BasicDataSource = {
    if (bs == null || bs.isClosed) {
      bs = BasicDataSourceFactory
        .createDataSource(dbProps)
        .asInstanceOf[BasicDataSource]
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
      case e: Exception =>
        logError(
          "---------Get connection " + e.getMessage + "\n-----------------------\n" + e
            .printStackTrace())
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
        case e: Exception =>
          logError(
            "---------Close ResultSet " + e.getMessage + "\n-----------------------\n" + e
              .printStackTrace())
      }
    }
    if (ps != null) {
      try {
        ps.close()
      } catch {
        case e: Exception =>
          logError(
            "---------Close PreparedStatement " + e.getMessage + "\n-----------------------\n" + e
              .printStackTrace())
      }
    }
    if (conn != null) {
      try {
        conn.close()
      } catch {
        case e: Exception =>
          logError(
            "---------Close Connection " + e.getMessage + "\n-----------------------\n" + e
              .printStackTrace())
      }
    }
  }

  def closeConn(rs: ResultSet, ps: Statement, conn: Connection) = {
    if (rs != null) {
      try {
        rs.close()
      } catch {
        case e: Exception =>
          logError(
            "---------Close ResultSet " + e.getMessage + "\n-----------------------\n" + e
              .printStackTrace())
      }
    }
    if (ps != null) {
      try {
        ps.close()
      } catch {
        case e: Exception =>
          logError(
            "---------Close PreparedStatement " + e.getMessage + "\n-----------------------\n" + e
              .printStackTrace())
      }
    }
    if (conn != null) {
      try {
        conn.close()
      } catch {
        case e: Exception =>
          logError(
            "---------Close Connection " + e.getMessage + "\n-----------------------\n" + e
              .printStackTrace())
      }
    }
  }

}
