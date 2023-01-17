package com.dfssi.dataplatform.analysis.es.convertor

import java.util
import java.util.UUID

import com.alibaba.fastjson.JSONArray
import com.dfssi.dataplatform.analysis.es.{Constants, EsRecord}
import com.dfssi.dataplatform.analysis.util.MathUtil
import com.dfssi.resources.ConfigUtils

import scala.collection.mutable.ListBuffer

class TerminalD004Convertor extends TerminalDataConvertorTrait {

  override def preConvert(record: util.Map[String, Object],
                          idField: String,
                          identificationFiled: String): util.Map[String, Object] = {

    val identificationValue = ConfigUtils.getAsString(record, identificationFiled)
    var res: java.util.Map[String, Object] = null

    identificationValue match {
      case "D004" =>  {
        res = record
//        val data = record.remove("gpsAndCan")
//        logInfo("record = " + record + ", data = " + data)
//        if(data != null){
//          record.putAll(data.asInstanceOf[util.Map[String, Object]])
//
//          res = record
//        }
      }
    }

    res
  }

  override def convert(record: java.util.Map[String, Object],
                       idField: String,
                       identificationFiled: String,
                       time: Long): Array[EsRecord] ={
    val esRecords = new ListBuffer[EsRecord]

    try {

      val vid = ConfigUtils.getAsString(record, "vid")
      require(vid != null, "vid = null")

      val msgId = ConfigUtils.getAsString(record, "msgId")

      val sim = ConfigUtils.getAsString(record, "sim")

      val receiveTime = ConfigUtils.getAsLong(record, "receiveTime")

      val pkgs = record.get("pkgs").asInstanceOf[JSONArray]

      expandFpkgs(pkgs, vid, sim, msgId, receiveTime, esRecords)

    } catch {
      case e: Exception => {
        logError(s"解析D004的记录 ${record} 失败。", e)
        Array.empty[EsRecord]
      }
    }

    esRecords.toArray
  }

  /**
    * 展开一二级报文
    */
  private def expandFpkgs(fpkgs:JSONArray, vid:String, sim:String,
                          msgId:String, receiveTime:java.lang.Long, esRecords:ListBuffer[EsRecord]) = {
    val fpkgsIter = fpkgs.iterator()
    while (fpkgsIter.hasNext) {
      val fpkg = fpkgsIter.next().asInstanceOf[util.Map[String, Object]]

      val dataTime = ConfigUtils.getAsLong(fpkg, "dataTime")
      val direction = ConfigUtils.getAsInteger(fpkg, "direction")
      val height = ConfigUtils.getAsInteger(fpkg, "height")
      val latitude = ConfigUtils.getAsDouble(fpkg, "latitude")
      val longitute = ConfigUtils.getAsDouble(fpkg, "longitute")
      val secondDataPkgList = fpkg.get("secondDataPkgList").asInstanceOf[JSONArray]
      val secondDataPkgIter = secondDataPkgList.iterator()
      while (secondDataPkgIter.hasNext) {
        val record = secondDataPkgIter.next().asInstanceOf[util.Map[String, Object]]
        record.put("dataTime", dataTime)
        record.put("direction", direction)
        record.put("height", height)
        record.put("latitude", latitude)
        record.put("longitute", longitute)
        record.put("vid", vid)
        record.put("sim", sim)
        record.put("msgId", msgId)
        record.put("receiveTime", receiveTime)
        record.put(Constants.INDEX_FIELD, typeEntity.getIndexEntity().getFullIndexName(dataTime))
        record.put(Constants.TABLE_FIELD, typeEntity.name)

        if (!record.containsKey("id")) {
          record.put("id", UUID.randomUUID().toString)
        }

        reviseLocation(record)

        reviseSpeed(record)

        reviseCanData(record)

//        logInfo(s" expandFpkgs record = ${record}")

        esRecords += EsRecord(vid, dataTime, record)
      }
    }

  }

  //修正位置
  private def reviseLocation(record: java.util.Map[String, Object]): Unit ={
    //经度
    var lon = ConfigUtils.getAsDouble(record, "longitute")
    require(lon != null, "longitute = null")
    lon = MathUtil.rounding(lon/Math.pow(10, 6), 6)
    record.put("longitute", lon)

    //纬度
    var lat = ConfigUtils.getAsDouble(record, "latitude")
    require(lat != null, "latitude = null")
    lat = MathUtil.rounding(lat/Math.pow(10, 6), 6)
    record.put("latitude", lat)

    if(lon >= -180 && lon <= 180 && lat >= -90 && lat <= 90)
      record.put("location", Array(lon, lat))
  }

  //修正速度
  private def reviseSpeed(record: java.util.Map[String, Object]): Unit ={
    val instrumentSpeed = ConfigUtils.getAsDoubleWithDefault(record, "instrumentSpeed", 0.0) * 0.1
    record.put("instrumentSpeed", MathUtil.rounding(instrumentSpeed, 1).asInstanceOf[Object])

    val wheelSpeed = ConfigUtils.getAsDoubleWithDefault(record, "wheelSpeed", 0.0) * 0.1
    record.put("wheelSpeed", MathUtil.rounding(wheelSpeed, 1).asInstanceOf[Object])

    val gpsSpeed = ConfigUtils.getAsDoubleWithDefault(record, "gpsSpeed", 0.0) * 0.1
    record.put("gpsSpeed", MathUtil.rounding(gpsSpeed, 1).asInstanceOf[Object])
  }

  //修正总线数据
  private def reviseCanData(record: java.util.Map[String, Object]): Unit ={
    val rpm = ConfigUtils.getAsDoubleWithDefault(record, "rpm", 0.0) * 0.1
    record.put("rpm", MathUtil.rounding(rpm, 1).asInstanceOf[Object])

    val percentagetorque = ConfigUtils.getAsIntegerWithDefault(record, "percentagetorque", 0) - 125
    record.put("percentagetorque", percentagetorque.asInstanceOf[Object])

    val currentBlock = ConfigUtils.getAsIntegerWithDefault(record, "currentBlock", 0) - 125
    record.put("currentBlock", currentBlock.asInstanceOf[Object])

    val targetGear = ConfigUtils.getAsIntegerWithDefault(record, "targetGear", 0) - 125
    record.put("targetGear", targetGear.asInstanceOf[Object])

    val engineFuelRate = ConfigUtils.getAsDoubleWithDefault(record, "engineFuelRate", 0.0) * 10
    record.put("engineFuelRate", MathUtil.rounding(engineFuelRate, 1).asInstanceOf[Object])

    val grade = ConfigUtils.getAsDoubleWithDefault(record, "grade", 0.0) * 0.1
    record.put("grade", MathUtil.rounding(grade, 1).asInstanceOf[Object])

    val load = ConfigUtils.getAsDoubleWithDefault(record, "load", 0.0) * 0.1
    record.put("load", MathUtil.rounding(load, 1).asInstanceOf[Object])

    val intakeQGpressure = ConfigUtils.getAsIntegerWithDefault(record, "intakeQGpressure", 0) * 2
    record.put("intakeQGpressure", intakeQGpressure.asInstanceOf[Object])

    val relativePress = ConfigUtils.getAsIntegerWithDefault(record, "relativePress", 0) * 2
    record.put("relativePress", relativePress.asInstanceOf[Object])

    val transmissionOutputSpeed = ConfigUtils.getAsDoubleWithDefault(record, "transmissionOutputSpeed", 0.0) * 0.125
    record.put("transmissionOutputSpeed", MathUtil.rounding(transmissionOutputSpeed, 3).asInstanceOf[Object])
  }



}

object TerminalD004Convertor {
  val ID = "D004"
  @volatile
  private var convertor: TerminalDataConvertorTrait = null

  def getOrNewInstance(): TerminalDataConvertorTrait ={
    if(convertor == null){
      this.synchronized{
        if(convertor == null)
          convertor = new TerminalD004Convertor
      }
    }
    convertor
  }
}
