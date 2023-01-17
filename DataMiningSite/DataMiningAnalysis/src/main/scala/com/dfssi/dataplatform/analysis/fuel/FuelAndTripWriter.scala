package com.dfssi.dataplatform.analysis.fuel

import java.io.Serializable

import com.dfssi.common.UUIDs
import com.dfssi.common.json.Jsons
import com.dfssi.resources.ConfigUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.{HashPartitioner, Logging}

import scala.collection.mutable

/**
  * Description:
  *   行程切分：
  *      前提： 数据严格按照时间先后顺序入到kafka中
  * @author LiXiaoCong
  * @version 2018/1/19 20:54
  */
class FuelAndTripWriter extends Logging with Serializable{

  def fuelAndTripWrite(fuelDs: DStream[String],
                       bcConf: Broadcast[FuelConfig],
                       partitions: Int,
                       batchInterval: Int,
                       group: String): Unit ={

    //将kafka中读取json数据转换成 FuelRecord
    val fuelDstream = fuelDs.map(jsonLine => {
      val conf = bcConf.value
      converter(jsonLine, conf)
    }).filter(_ != null)

    val createCombiner = (v: FuelRecord) => mutable.HashSet[FuelRecord](v)
    val mergeValue = (c: mutable.HashSet[FuelRecord], v: FuelRecord) => (c += v)
    val mergeCombiner = (c1: mutable.HashSet[FuelRecord],
                         c2: mutable.HashSet[FuelRecord]) => (c1 ++= c2)

    fuelDstream.persist(StorageLevel.MEMORY_AND_DISK_SER)

    val fuelGroupedDs = fuelDstream.map(fuel => (fuel.getVid, fuel))
      .combineByKey(createCombiner, mergeValue, mergeCombiner, new HashPartitioner(partitions))
      .map(kv =>{
        (kv._1, kv._2.toList.sortWith((f, s) =>{
          //先比时间
          var result = f.getUploadtime.compareTo(s.getUploadtime)
          //再比里程
          if(result == 0){
            result = f.getTotalmile.compareTo(s.getTotalmile)
          }
          //再比油耗
          if (result == 0) {
            result = f.getTotalfuel.compareTo(s.getTotalfuel)
          }

          if(result > 0)
            false
          else
            true
        }))
      })

    fuelGroupedDs.persist(StorageLevel.MEMORY_AND_DISK_SER)
    //fuelGroupedDs.map(kv =>(kv._1, kv._2.last)).print()

    val tripAggregator = new TripAggregator()
    val fuelDataDs = tripAggregator.tripAggreate(fuelGroupedDs, batchInterval, bcConf, group)

    val fuelAggregator = new FuelAggregator()
    fuelAggregator.fuelAggregateAndWrite2(fuelDataDs, bcConf)

    fuelDstream.foreachRDD(_.unpersist())

  }

  /*
     将json串转换成map
     问题：识别出线路（往返），
  */
  private def converter(line: String, conf: FuelConfig): FuelRecord ={
    if(line != null){
      try {
        val map: java.util.Map[String, Object] = Jsons.toMap(line)

        val sim = ConfigUtils.getAsStringByKeys(map, "sim", "SIM")

        val vid = ConfigUtils.getAsStringByKeys(map, "vid", "VID")
        require(vid != null, "vid = null")

        var lon = ConfigUtils.getAsDoubleByKeys(map, "lon", "LON")
        require(lon != null, "lon = null")
        lon = lon/Math.pow(10, 6)

        var lat = ConfigUtils.getAsDoubleByKeys(map, "lat", "LAT")
        require(lat != null, "lat = null")
        lat = lat/Math.pow(10, 6)

        //累计里程
        var totalMile = ConfigUtils.getAsDoubleByKeys(map, "mile", "MILE")
        require(totalMile != null, "mile = null")
        totalMile = totalMile/10.0

        //累计油耗
        var totalFuel = ConfigUtils.getAsDoubleByKeys(map, "cumulativeOilConsumption", "CUMULATIVEOILCONSUMPTION")
        //var totalFuel = ConfigUtils.getAsDoubleByKeys(map, "fuel", "fuel")
        require(totalFuel != null, "cumulativeOilConsumption = null")
        totalFuel = totalFuel/Math.pow(10, 5)

        var alt = ConfigUtils.getAsDoubleByKeys(map, "alt", "ALT")
        if(alt == null) alt = 0.0

        var uploadTime = ConfigUtils.getAsLongByKeys(map, "gpsTime", "GPSTIME")
        if(uploadTime == null)uploadTime = System.currentTimeMillis()

        var dir = ConfigUtils.getAsIntegerByKeys(map, "dir", "DIR")
        if(dir == null) dir = 0

        val status = String.valueOf(map.get("vehicleStatus"))
        val isOn = !status.contains("ACC 关")

        require(uploadTime >= conf.getMinTime, s"gpstime = ${uploadTime} < fuel.uploadtime.threshold = ${conf.getMinTime}")
        require(alt < conf.getAltMax, s"alt = ${alt} > fuel.max.altitude.threshold = ${conf.getAltMax}")
        require(dir <= conf.getMaxDirection, s"dir = ${dir} > fuel.direction.threshold = ${conf.getMaxDirection}")
        require(lon >= conf.getLonMin && lon <= conf.getLonMax,  s"lon = ${lon} not in  [${conf.getLonMin}, ${conf.getLonMax}")
        require(lat >= conf.getLatMin && lat <= conf.getLatMax,  s"lat = ${lat} not in  [${conf.getLatMin}, ${conf.getLatMax}")

        val isplateau = if(alt >= conf.getAltMin) 1 else 0

        var id = ConfigUtils.getAsStringByKeys(map, "id", "ID")
        if(id == null)id = UUIDs.uuidFromBytesWithNoSeparator(line.getBytes())

        val fuelId = UUIDs.uuidFromBytesWithNoSeparator(String.format("%s,%s,%s", lon, lat, totalMile).getBytes())

        val record = new FuelRecord(fuelId, id, sim, vid, totalMile, isplateau, 0, totalFuel, lon, lat, alt, uploadTime, isOn, null, "001122")

        //异常驾驶行为告警数据解析
        val alarmObj = map.get("abnormalDrivingBehaviorAlarmType")
        if(alarmObj != null){
           try {
             val alarms = alarmObj.asInstanceOf[java.util.List[String]]
             val alrmDegree = ConfigUtils.getAsIntegerWithDefault(map, "abnormalDrivingBehaviorAlarmDegree", -1)
             var speed = ConfigUtils.getAsDoubleWithDefault(map, "speed1", 0.0)
             if(speed == 0.0)speed = ConfigUtils.getAsDoubleWithDefault(map, "speed", 0.0)
             record.setAlarms(alarms)
             record.setAlarmDegree(alrmDegree)
             record.setSpeed(speed * 0.1)
           }catch {
             case e: Exception =>
               logError(s"驾驶行为异常报警数据${id}/${uploadTime}不是一个list, ${alarmObj}", e)
           }
        }

        return  record

      } catch {
        case e => logDebug(s"数据有误，解析失败：\n\t ${line}", e)
      }
    }
    return null
  }

}



