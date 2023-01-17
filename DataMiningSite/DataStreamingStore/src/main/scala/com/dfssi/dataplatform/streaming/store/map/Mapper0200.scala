package com.dfssi.dataplatform.streaming.store.map

import com.dfssi.dataplatform.streaming.store.config.{HiveTableDef, MessageDef}
import com.google.gson.JsonObject
import org.apache.spark.sql.Row

import scala.collection.mutable.ArrayBuffer

object Mapper0200 extends AbstractMapper {
  val MAPPER_TYPE = "0200"

  def map(msgJsonObject: JsonObject,
          messageDef: MessageDef,
          hiveTableDef: HiveTableDef): ArrayBuffer[Row] = {
    val Rows: ArrayBuffer[Row] = new ArrayBuffer[Row]()
    val seq: ArrayBuffer[Any] = new ArrayBuffer[Any]()
    for (colDef <- hiveTableDef.tableColumnDefs.values) {
      seq += getValue(msgJsonObject, messageDef, hiveTableDef, colDef)
    }

    Rows += Row.fromSeq(seq)
    Rows
  }
}
