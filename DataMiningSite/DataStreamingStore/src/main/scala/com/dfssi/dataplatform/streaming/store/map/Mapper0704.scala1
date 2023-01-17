package com.dfssi.dataplatform.streaming.store.map

import com.dfssi.dataplatform.streaming.store.config.{
  HiveColumnDef,
  HiveTableDef,
  MessageDef
}
import com.dfssi.dataplatform.streaming.store.utils.ExceptionUtils
import com.google.gson.JsonObject
import org.apache.spark.sql.Row

import scala.collection.mutable.ArrayBuffer

object Mapper0704 extends AbstractMapper {
  val MAPPER_TYPE = "0704"

  def map(msgJsonObject: JsonObject,
          messageDef: MessageDef,
          hiveTableDef: HiveTableDef): ArrayBuffer[Row] = {
    val Rows: ArrayBuffer[Row] = new ArrayBuffer[Row]()
    val gpsVoJsonObj: JsonObject = msgJsonObject.getAsJsonObject("gpsVo")
    val seq: ArrayBuffer[Any] = new ArrayBuffer[Any]()
    for (colDef <- hiveTableDef.tableColumnDefs.values) {
      seq += getValueFromTwoJsonObj(msgJsonObject,
                                    messageDef,
                                    hiveTableDef,
                                    colDef,
                                    gpsVoJsonObj)
    }
    Rows += Row.fromSeq(seq)
    Rows
  }

  def getValueFromTwoJsonObj(msgJsonObject: JsonObject,
                             messageDef: MessageDef,
                             hiveTableDef: HiveTableDef,
                             columnDef: HiveColumnDef,
                             secondJsonObj: JsonObject): Any = {
    var value = getValue(msgJsonObject, messageDef, hiveTableDef, columnDef)
    if (value == null) {
      value = getValue(secondJsonObj, messageDef, hiveTableDef, columnDef)
    }

    value
  }
}
