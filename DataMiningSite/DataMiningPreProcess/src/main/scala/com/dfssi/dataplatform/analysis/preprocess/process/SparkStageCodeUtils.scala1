package com.dfssi.dataplatform.analysis.preprocess.process

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructField

object SparkStageCodeUtils extends Serializable {
  def getFieldName(row: Row, structField: StructField): Any = {
    if ("string".equalsIgnoreCase(structField.dataType.simpleString)) {
      return row.getString(row.fieldIndex(structField.name))
    } else if ("boolean".equalsIgnoreCase(structField.dataType.simpleString)) {
      return row.getBoolean(row.fieldIndex(structField.name))
    } else if ("byte".equalsIgnoreCase(structField.dataType.simpleString)) {
      return row.getByte(row.fieldIndex(structField.name))
    } else if ("short".equalsIgnoreCase(structField.dataType.simpleString)) {
      return row.getShort(row.fieldIndex(structField.name))
    } else if ("int".equalsIgnoreCase(structField.dataType.simpleString)) {
      return row.getInt(row.fieldIndex(structField.name))
    } else if ("long".equalsIgnoreCase(structField.dataType.simpleString)) {
      return row.getLong(row.fieldIndex(structField.name))
    } else if ("float".equalsIgnoreCase(structField.dataType.simpleString)) {
      return row.getFloat(row.fieldIndex(structField.name))
    } else if ("double".equalsIgnoreCase(structField.dataType.simpleString)) {
      return row.getDouble(row.fieldIndex(structField.name))
    } else if ("decimal".equalsIgnoreCase(structField.dataType.simpleString)) {
      return row.getDecimal(row.fieldIndex(structField.name))
    } else if ("date".equalsIgnoreCase(structField.dataType.simpleString)) {
      return row.getDate(row.fieldIndex(structField.name))
    } else if ("timestamp".equalsIgnoreCase(structField.dataType.simpleString)) {
      return row.getTimestamp(row.fieldIndex(structField.name))
    }
    null
  }
}
