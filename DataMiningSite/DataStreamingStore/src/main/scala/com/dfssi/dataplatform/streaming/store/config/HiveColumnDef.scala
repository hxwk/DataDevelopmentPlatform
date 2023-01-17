package com.dfssi.dataplatform.streaming.store.config

import com.dfssi.dataplatform.streaming.store.utils.XmlUtils
import org.apache.spark.Logging
import org.apache.spark.sql.types._
import StreamingStoreConfig._
import scala.xml.Elem

class HiveColumnDef extends Serializable with Logging {

  var colName: String = _
  var colType: String = _
  var length: String = _
  var precision: String = _
  var paramizedClassName: String = _
  var partitionRulePattern: String = _

  def this(hiveTableEl: Elem) {
    this()
    loadDefFromEl(hiveTableEl)
  }

  def loadDefFromEl(columnDefEl: Elem): Unit = {
    if (columnDefEl == null) {
      return
    }

    colName = XmlUtils.getAttrValue(columnDefEl, CONFIG_ATTR_TAG_NAME)
    colType = XmlUtils.getAttrValue(columnDefEl, CONFIG_ATTR_TAG_TYPE)
    length = XmlUtils.getAttrValue(columnDefEl, CONFIG_ATTR_TAG_LENGTH)
    precision = XmlUtils.getAttrValue(columnDefEl, CONFIG_ATTR_TAG_PRECISION)
    paramizedClassName =
      XmlUtils.getAttrValue(columnDefEl, CONFIG_ATTR_TAG_PARAMIZEDCLASSNAME)
    partitionRulePattern =
      XmlUtils.getAttrValue(columnDefEl, CONFIG_ATTR_TAG_PARTITIONRULEPATTERN)
  }

  def toDDL(): String = {
    colName + " " + getDDLType()
  }

  private def getDDLType(): String = {
    if (HIVE_DATA_TYPE_DECIMAL.equalsIgnoreCase(colType)) {
      "decimal(" + length + "," + precision + ")"
    } else if (HIVE_DATA_TYPE_ARRAY.equalsIgnoreCase(colType)) {
      "array<" + paramizedClassName + ">"
    } else {
      colType
    }
  }

  def getStructField(): StructField = {
    val dataType = getDataType()
    DataTypes.createStructField(colName, dataType, true)
  }

  private def getDataType(): DataType = {
    val dataType = getSimpleDataType(colType)

    if (dataType != null) {
      dataType
    } else if (HIVE_DATA_TYPE_ARRAY.equalsIgnoreCase(colType)) {
      new ArrayType(getSimpleDataType(paramizedClassName), true)
    } else {
      DataTypes.StringType
    }
  }

  private def getSimpleDataType(typeName: String): DataType = {
    if (HIVE_DATA_TYPE_STRING.equalsIgnoreCase(typeName)) {
      DataTypes.StringType
    } else if (HIVE_DATA_TYPE_INT.equalsIgnoreCase(typeName)) {
      DataTypes.IntegerType
    } else if (HIVE_DATA_TYPE_FLOAT.equalsIgnoreCase(typeName)) {
      DataTypes.FloatType
    } else if (HIVE_DATA_TYPE_DOUBLE.equalsIgnoreCase(typeName)) {
      DataTypes.DoubleType
    } else if (HIVE_DATA_TYPE_TIMESTAMP.equalsIgnoreCase(typeName)) {
      DataTypes.TimestampType
    } else if (HIVE_DATA_TYPE_BINARY.equalsIgnoreCase(typeName)) {
      DataTypes.BinaryType
    } else if (HIVE_DATA_TYPE_BIGINT.equalsIgnoreCase(typeName)) {
      DataTypes.LongType
    } else if (HIVE_DATA_TYPE_DECIMAL.equalsIgnoreCase(typeName)) {
      new DecimalType(length.toInt, precision.toInt)
    } else {
      null
    }
  }

  def isNumType(): Boolean = {
    if (HIVE_DATA_TYPE_INT.equalsIgnoreCase(colType) || HIVE_DATA_TYPE_FLOAT
          .equalsIgnoreCase(colType) || HIVE_DATA_TYPE_DOUBLE
          .equalsIgnoreCase(colType) || HIVE_DATA_TYPE_BIGINT
          .equalsIgnoreCase(colType) || HIVE_DATA_TYPE_DECIMAL
          .equalsIgnoreCase(colType)) {
      true
    } else {
      false
    }
  }

}
