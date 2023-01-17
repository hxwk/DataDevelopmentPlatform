package com.dfssi.dataplatform.newenergy.kafkatohive.map

import com.dfssi.dataplatform.streaming.store.config.{HiveColumnDef, HiveTableDef, MessageDef}
import com.dfssi.dataplatform.streaming.store.utils.JacksonUtils
import com.google.gson.JsonObject
import org.apache.spark.Logging
import org.apache.spark.sql.Row

import scala.collection.mutable.ArrayBuffer

trait AbstractMapper32960 extends Logging with Serializable {

  val ATTR_NAME_NEALARMBEAN = "neAlarmBean"
  val ATTR_NAME_NEDRIVERMOTOR = "neDriverMotor"
  val FIELD_NAME_NEEXTREMUMBEAN = "neExtremumBean"
  val FIELD_NAME_NEGPSBEAN = "neGpsBean"
  val FIELD_NAME_NEVEHICLEBEAN = "neVehicleBean"

  def map(msgJsonObject: JsonObject,
          messageDef: MessageDef,
          hiveTableDef: HiveTableDef): ArrayBuffer[Row] = {
    val Rows: ArrayBuffer[Row] = new ArrayBuffer[Row]()
    val seq: ArrayBuffer[Any] = new ArrayBuffer[Any]()
    for (colDef <- hiveTableDef.tableColumnDefs.values) {
      seq += getValueFromMultiJsonObj(msgJsonObject,
        messageDef,
        hiveTableDef,
        colDef)
    }

    Rows += Row.fromSeq(seq)
    Rows
  }

  def getValueFromMultiJsonObj(msgJsonObject: JsonObject,
                               messageDef: MessageDef,
                               hiveTableDef: HiveTableDef,
                               colDef: HiveColumnDef): Any = {
    var value = getValue(msgJsonObject, messageDef, hiveTableDef, colDef)
    if (value == null) {
      val fields: Array[String] = Array(ATTR_NAME_NEALARMBEAN,
        ATTR_NAME_NEDRIVERMOTOR,
        FIELD_NAME_NEEXTREMUMBEAN,
        FIELD_NAME_NEGPSBEAN,
        FIELD_NAME_NEVEHICLEBEAN)
      for (field <- fields) {
        if (value == null) {
          val jsonObj: JsonObject =
            msgJsonObject.getAsJsonObject(field)
          value = getValue(jsonObj, messageDef, hiveTableDef, colDef)
        }
      }
    }

    value
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
