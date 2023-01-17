package com.dfssi.dataplatform.streaming.store.config

import java.util
import java.util.Date

import com.dfssi.dataplatform.streaming.store.utils.XmlUtils
import org.apache.commons.lang3.time.DateFormatUtils
import org.apache.spark.sql.Column
import org.apache.spark.sql.types.StructType

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.xml.Elem

class HiveTableDef extends Serializable {
  var tableName: String = _
  val tableColumnDefs: mutable.Map[String, HiveColumnDef] =
    new util.LinkedHashMap[String, HiveColumnDef]().asScala
  val partitionColumnDefs: mutable.Map[String, HiveColumnDef] =
    new util.LinkedHashMap[String, HiveColumnDef]().asScala

  def this(hiveTableEl: Elem) {
    this()
    loadDefFromEl(hiveTableEl)
  }

  def loadDefFromEl(hiveTableEl: Elem): Unit = {
    if (hiveTableEl == null) {
      return
    }

    tableName = XmlUtils.getAttrValue(hiveTableEl,
                                      StreamingStoreConfig.CONFIG_ATTR_TAG_NAME)

    val columnDefNodes = XmlUtils.getSubNodeSeq(
      hiveTableEl,
      StreamingStoreConfig.CONFIG_ELEMENT_TAG_COLUMN)
    for (columnDefNode <- columnDefNodes) {
      val columnDefEl = columnDefNode.asInstanceOf[Elem]
      val hiveColumnDef: HiveColumnDef = new HiveColumnDef(columnDefEl)

      tableColumnDefs.put(hiveColumnDef.colName, hiveColumnDef)
    }

    parseColumns(hiveTableEl,
                 tableColumnDefs,
                 StreamingStoreConfig.CONFIG_ELEMENT_TAG_COLUMNS)
    parseColumns(hiveTableEl,
                 partitionColumnDefs,
                 StreamingStoreConfig.CONFIG_ELEMENT_TAG_PARTITIONS)
  }

  private def parseColumns(hiveTableEl: Elem,
                           columnDefs: mutable.Map[String, HiveColumnDef],
                           containerElName: String): Unit = {
    val containerNodes = XmlUtils.getSubNodeSeq(hiveTableEl, containerElName)
    var containerEl: Elem = null
    if (containerNodes.size > 0) {
      containerEl = containerNodes.head.asInstanceOf[Elem]
    }

    if (containerEl != null) {
      val columnNodes = XmlUtils.getSubNodeSeq(
        containerEl,
        StreamingStoreConfig.CONFIG_ELEMENT_TAG_COLUMN)
      for (columnNode <- columnNodes) {
        var columnEl = columnNode.asInstanceOf[Elem]
        val hiveColumnDef: HiveColumnDef = new HiveColumnDef(columnEl)

        columnDefs.put(hiveColumnDef.colName, hiveColumnDef)
      }
    }
  }

  def toDDL(config: StreamingStoreConfig): String = {
    val sb = new StringBuilder()
    sb.append("CREATE EXTERNAL TABLE IF NOT EXISTS ")
      .append(config.getCurrentHiveSchema())
      .append(".")
      .append(tableName)
    sb.append("\n(\n")
    var needAddComet = false
    for (hiveColumnDef <- tableColumnDefs.values) {
      if (needAddComet) {
        sb.append(", \n")
      }
      sb.append(hiveColumnDef.toDDL())
      needAddComet = true
    }
    sb.append("\n)\n")
    sb.append("partitioned by \n(\n")
    needAddComet = false
    for (partitionColumnDef <- partitionColumnDefs.values) {
      if (needAddComet) {
        sb.append(", \n")
      }
      sb.append(partitionColumnDef.toDDL())
      needAddComet = true
    }
    sb.append("\n)\n")
    sb.append("STORED AS PARQUET")

    sb.toString()
  }

  /**
    * joined by split char.
    *
    * @param date
    * @return
    */
  def getPartitionValueInfo(date: Date): String = {
    val result: ArrayBuffer[String] = new mutable.ArrayBuffer[String]()
    for (colDef <- partitionColumnDefs.values) {
      result += getPartitonPath(date, colDef.partitionRulePattern)
    }
    result.mkString(HiveTableDef.PARTITION_SPLIT_CHAR)
  }

  def getPartitonPath(date: Date, rulePattern: String): String = {
    DateFormatUtils.format(date, rulePattern)
  }

  def getColumnDef(colName: String): HiveColumnDef = {
    if (tableColumnDefs.get(colName).isDefined) {
      return tableColumnDefs(colName)
    }
    null
  }

  def getTableColSchema(): StructType = {
    var schema = new StructType()
    for (colDef: HiveColumnDef <- tableColumnDefs.values) {
      schema = schema.add(colDef.getStructField)
    }

    schema
  }

  def getPartitionFieldNames(): Array[String] = {
    val result: ArrayBuffer[String] = new mutable.ArrayBuffer[String]()
    for (colDef <- partitionColumnDefs.values) {
      result += colDef.colName
    }

    result.toArray
  }

  def getPartitionCondition(partitionValues: String*): String = {
    val sb = new StringBuilder()
    sb.append("(\n")
    var needComet: Boolean = false
    var index: Integer = 0;
    for (colDef <- partitionColumnDefs.values) {
      if (needComet) {
        sb.append(", \n")
      }
      sb.append(colDef.colName).append("=")
      if (colDef.isNumType()) {
        sb.append(partitionValues(index))
      } else {
        sb.append("'").append(partitionValues(index)).append("'")
      }
      index += 1
      needComet = true
    }
    sb.append("\n)\n")

    sb.toString()
  }

  def getColumnNames(): Seq[Column] = {
    import org.apache.spark.sql.functions._
    val result = new ArrayBuffer[Column]()
    for (colDef <- tableColumnDefs.values) {
      result += col(colDef.colName)
    }

    result.toSeq
  }

  def buildMergeSql(config: StreamingStoreConfig,
                    firstLevelPartition: String,
                    secondLevelPartition: String): String = {
    val sb = new StringBuilder()
    sb.append("SELECT ")
    var needAddComet = false
    for (hiveColumnDef <- tableColumnDefs.values) {
      if (needAddComet) {
        sb.append(", ")
      }
      sb.append(hiveColumnDef.colName)
      needAddComet = true
    }

    sb.append("\nFROM ")
      .append(config.getCurrentHiveSchema())
      .append(".")
      .append(tableName)

    sb.append("\nWHERE ")
    sb.append(
      buildMergeSqlPartitionCondition(firstLevelPartition,
                                      secondLevelPartition))
    sb.append("\n")

    sb.toString()
  }

  def buildMergeSqlPartitionCondition(partitionValues: String*): String = {
    val sb = new StringBuilder()
    var needAnd: Boolean = false
    var index: Integer = 0;
    for (colDef <- partitionColumnDefs.values) {
      if (needAnd) {
        sb.append("\nAND ")
      }
      sb.append(colDef.colName).append("=")
      if (colDef.isNumType()) {
        sb.append(partitionValues(index))
      } else {
        sb.append("'").append(partitionValues(index)).append("'")
      }
      index += 1
      needAnd = true
    }

    sb.toString()
  }

}

object HiveTableDef extends Serializable {
  val PARTITION_SPLIT_CHAR = "#"
}
