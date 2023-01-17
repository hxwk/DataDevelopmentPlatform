package com.dfssi.dataplatform.analysis.preprocess.input

import java.util.Properties

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}

import scala.collection.mutable
import scala.xml.Elem

class InputGreenplumTable extends AbstractProcess {

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {

    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
    val paramsMap: mutable.Map[String, String] = extractSimpleParams(defEl)

    val jdbcHostname = getParamValue(paramsMap,"jdbcHostname")
    val jdbcPort = getParamValue(paramsMap,"jdbcPort")
    val jdbcDatabase = getParamValue(paramsMap,"jdbcDatabase")
    val dbtable = getParamValue(paramsMap, "dbtable")

    val user = getParamValue(paramsMap, "user")
    val password = getParamValue(paramsMap, "password")

    val connectionProperties = new Properties()
    connectionProperties.put("driver", InputGreenplumTable.driver)
    connectionProperties.put("user", user)
    connectionProperties.put("password", password)

    val jdbc_url = s"jdbc:postgresql://$jdbcHostname:$jdbcPort/$jdbcDatabase" //jdbc:pivotal:greenplum

    paramsMap.get("fetchsize") match {
      case Some(x) => connectionProperties.put("fetchsize", x)
      case None =>
    }

    var inputDF = processContext.hiveContext.read
      .jdbc(jdbc_url, s"$dbtable", connectionProperties)

    inputDF = paramsMap.get("condition") match {
        case Some(x) => inputDF.where(x)
        case None => inputDF
      }

    processContext.dataFrameMap.put(id, inputDF)
  }

}

object InputGreenplumTable {

  val driver = "org.postgresql.Driver"
//    "com.pivotal.jdbc.GreenplumDriver" // jdbc:postgresql

  val processType: String = ProcessFactory.PROCESS_NAME_INPUT_GREENPLUM_TABLE
}