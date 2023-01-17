package com.dfssi.dataplatform.analysis.es.convertor
import java.util

import com.dfssi.dataplatform.analysis.es.EsRecord
import com.dfssi.dataplatform.analysis.util.MathUtil
import com.dfssi.resources.ConfigUtils

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/3/7 17:05 
  */
class Terminal0200Convertor extends TerminalDataConvertorTrait{


  override def preConvert(record: util.Map[String, Object],
                          idField: String,
                          identificationFiled: String): util.Map[String, Object] = {

    val identificationValue = ConfigUtils.getAsString(record, identificationFiled)
    var res: java.util.Map[String, Object] = null

    identificationValue match {
      case "0200" => res = record
      case "0704" =>{
        val data = record.remove("gpsVo")
        if(data != null){

          record.putAll(data.asInstanceOf[util.Map[String, Object]])

          val id = record.remove("idx")
          record.put(idField, id)

          record.put("msgId", "0200_0704")

          res = record
        }
      }
    }
    res
  }

  override def convert(record: java.util.Map[String, Object],
                       idField: String,
                       identificationFiled: String,
                       time: Long): Array[EsRecord] ={
    try {

        val vid = ConfigUtils.getAsString(record, "vid")
        require(vid != null, "vid = null")

        //修正累计里程
        var totalMile = ConfigUtils.getAsDouble(record, "mile")
        require(totalMile != null, "mile = null")
        totalMile = MathUtil.rounding(totalMile / 10.0, 1)
        record.put("mile", totalMile)

        reviseLocation(record)

        reviseFuel(record)

        reviseSpeed(record)

        Array(EsRecord(vid, time, record))
    } catch {
      case e: Exception => {
        logError(s"解析0200|2704的记录 ${record} 失败。", e)
        Array.empty[EsRecord]
      }
    }
  }

  //修正位置
  private def reviseLocation(record: java.util.Map[String, Object]): Unit ={
    //经度
    var lon = ConfigUtils.getAsDouble(record, "lon")
    require(lon != null, "lon = null")
    lon = MathUtil.rounding(lon/Math.pow(10, 6), 6)
    record.put("lon", lon)

    //纬度
    var lat = ConfigUtils.getAsDouble(record, "lat")
    require(lat != null, "lat = null")
    lat = MathUtil.rounding(lat/Math.pow(10, 6), 6)
    record.put("lat", lat)

    if(lon >= -180 && lon <= 180 && lat >= -90 && lat <= 90)
      record.put("location", Array(lon, lat))
  }

  //修正油耗
  private def reviseFuel(record: java.util.Map[String, Object]): Unit ={

    //累计油耗
    var totalFuel = ConfigUtils.getAsDouble(record, "cumulativeOilConsumption")
    //var totalFuel = ConfigUtils.getAsDoubleByKeys(map, "fuel", "fuel")
    require(totalFuel != null, "cumulativeOilConsumption = null")
    totalFuel = MathUtil.rounding(totalFuel/Math.pow(10, 5), 5)
    record.put("cumulativeOilConsumption", totalFuel)

    //总计油耗
    totalFuel = ConfigUtils.getAsDoubleWithDefault(record, "totalFuelConsumption", 0.0)
    totalFuel = MathUtil.rounding(totalFuel/Math.pow(10, 5), 5)
    record.put("totalFuelConsumption", totalFuel)

    //瞬时油耗
    totalFuel = ConfigUtils.getAsDoubleWithDefault(record, "fuel", 0.0)
    totalFuel = MathUtil.rounding(totalFuel * 0.1, 1)
    record.put("fuel", totalFuel)

  }

  //修正速度
  private def reviseSpeed(record: java.util.Map[String, Object]): Unit ={
    val speed = ConfigUtils.getAsDoubleWithDefault(record, "speed", 0.0) * 0.1
    record.put("speed", MathUtil.rounding(speed, 1).asInstanceOf[Object])

    val speed1 = ConfigUtils.getAsDoubleWithDefault(record, "speed1", 0.0) * 0.1
    record.put("speed1", MathUtil.rounding(speed1, 1).asInstanceOf[Object])
  }

}

object Terminal0200Convertor{
  val ID = "0200"
  @volatile
  private var convertor: TerminalDataConvertorTrait = null

  def getOrNewInstance(): TerminalDataConvertorTrait ={
    if(convertor == null){
      this.synchronized{
        if(convertor == null)
          convertor = new Terminal0200Convertor
      }
    }
    convertor
  }

}
