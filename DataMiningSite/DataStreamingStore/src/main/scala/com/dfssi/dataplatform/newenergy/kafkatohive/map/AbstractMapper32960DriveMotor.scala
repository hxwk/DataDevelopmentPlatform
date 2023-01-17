package com.dfssi.dataplatform.newenergy.kafkatohive.map

import java.util.UUID

import com.dfssi.dataplatform.streaming.store.config.{HiveColumnDef, HiveTableDef, MessageDef}
import com.dfssi.dataplatform.streaming.store.utils.JacksonUtils
import com.google.gson.JsonObject
import org.apache.spark.Logging
import org.apache.spark.sql.Row

import scala.collection.mutable.ArrayBuffer

trait AbstractMapper32960DriveMotor extends Logging with Serializable {

  val ATTR_NAME_NEDRIVERMOTOR = "neDriverMotor"

  val ATTR_NAME_PRIMARY_KEY = "id"
  val ATTR_NAME_FOREIGN_RELATION = "terminal_32960_id"
  val ATTR_NAME_COLLETCT_TIME = "collectTime"

  val FIELD_NAME_PRIMARY_KEY = "id"
  val FIELD_NAME_FOREIGN_RELATION = "terminal_32960_id"
  val FIELD_NAME_COLLETCT_TIME = "collect_time"

  def map(msgJsonObject: JsonObject,
          messageDef: MessageDef,
          hiveTableDef: HiveTableDef): ArrayBuffer[Row] = {
    val Rows: ArrayBuffer[Row] = new ArrayBuffer[Row]()
    val seq: ArrayBuffer[Any] = new ArrayBuffer[Any]()
    for (colDef <- hiveTableDef.tableColumnDefs.values) {
      var value: Any = null
      if (FIELD_NAME_PRIMARY_KEY.equalsIgnoreCase(colDef.colName)) {
        value = UUID.randomUUID().toString
      } else if (FIELD_NAME_FOREIGN_RELATION.equalsIgnoreCase(colDef.colName)) {
        value = JacksonUtils.getAsString(msgJsonObject, FIELD_NAME_PRIMARY_KEY)
      } else if (FIELD_NAME_COLLETCT_TIME.equalsIgnoreCase(colDef.colName)) {
        value = JacksonUtils.getAsLong(msgJsonObject, ATTR_NAME_COLLETCT_TIME)
      } else {
        val neDriveMotorJsonObj: JsonObject =
          msgJsonObject.getAsJsonObject(ATTR_NAME_NEDRIVERMOTOR)
        value = getValue(neDriveMotorJsonObj, messageDef, hiveTableDef, colDef)
      }

      seq += value
    }

    Rows += Row.fromSeq(seq)
    Rows
  }

  def getValue(msgJsonObject: JsonObject,
               messageDef: MessageDef,
               hiveTableDef: HiveTableDef,
               columnDef: HiveColumnDef): Any = {
    if (msgJsonObject == null || messageDef == null || hiveTableDef == null) {
      return null
    }
    JacksonUtils.getValue(
      msgJsonObject,
      messageDef.getAttrDefBFieldName(columnDef.colName).name,
      columnDef.colType,
      columnDef.paramizedClassName)
  }

}
