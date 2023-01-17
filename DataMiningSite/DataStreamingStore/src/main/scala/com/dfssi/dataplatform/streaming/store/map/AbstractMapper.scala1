package com.dfssi.dataplatform.streaming.store.map

import com.dfssi.dataplatform.streaming.store.config.{
  HiveColumnDef,
  HiveTableDef,
  MessageDef
}
import com.dfssi.dataplatform.streaming.store.utils.JacksonUtils
import com.google.gson.JsonObject
import org.apache.spark.Logging

trait AbstractMapper extends Logging with Serializable {

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
