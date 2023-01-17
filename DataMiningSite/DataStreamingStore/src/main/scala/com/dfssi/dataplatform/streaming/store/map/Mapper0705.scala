package com.dfssi.dataplatform.streaming.store.map

import com.dfssi.dataplatform.streaming.store.config.{
  HiveColumnDef,
  HiveTableDef,
  MessageDef
}
import com.google.gson.{JsonArray, JsonElement, JsonObject}
import org.apache.spark.sql.Row

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

object Mapper0705 extends AbstractMapper {
  val MAPPER_TYPE = "0705"

  def map(msgJsonObject: JsonObject,
          messageDef: MessageDef,
          hiveTableDef: HiveTableDef): ArrayBuffer[Row] = {
    val Rows: ArrayBuffer[Row] = new ArrayBuffer[Row]()
    val messageBeanList: JsonArray =
      msgJsonObject.getAsJsonArray("messageBeanList")
    for (messageBean: JsonElement <- messageBeanList) {
      val messageBeanJsonObj = messageBean.getAsJsonObject
      val seq: ArrayBuffer[Any] = new ArrayBuffer[Any]()
      for (colDef <- hiveTableDef.tableColumnDefs.values) {
        seq += getValueFromTwoJsonObj(msgJsonObject,
                                      messageDef,
                                      hiveTableDef,
                                      colDef,
                                      messageBeanJsonObj)
      }
      Rows += Row.fromSeq(seq)
    }

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
