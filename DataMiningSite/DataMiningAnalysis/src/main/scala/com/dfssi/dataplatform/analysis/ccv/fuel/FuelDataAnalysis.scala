package com.dfssi.dataplatform.analysis.ccv.fuel

import java.io.Serializable

import com.dfssi.common.UUIDs
import com.dfssi.common.json.Jsons
import com.dfssi.resources.ConfigUtils
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.{HashPartitioner, Logging}

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Description:
  *    油耗分析
  * @author LiXiaoCong
  * @version 2018/5/15 9:52 
  */
class FuelDataAnalysis extends Logging with Serializable{

    def executeAnalysis(fuelDs: DStream[String],
                        bcConf: Broadcast[VehicleFuelConfig],
                        partitions: Int): Unit ={

        //将kafka中读取json数据转换成 FuelRecord
        val fuelDstream = fuelDs.map(jsonLine => {
            val conf = bcConf.value
            convert(jsonLine, conf)
        }).filter(_ != null)

        val createCombiner = (v: FuelDataRecord) => mutable.HashSet[FuelDataRecord](v)
        val mergeValue = (c: mutable.HashSet[FuelDataRecord], v: FuelDataRecord) => (c += v)
        val mergeCombiner = (c1: mutable.HashSet[FuelDataRecord],
                             c2: mutable.HashSet[FuelDataRecord]) => (c1 ++= c2)

        val fuelGroupedDs = fuelDstream.map(fuel => (fuel.vid, fuel))
                .combineByKey(createCombiner, mergeValue, mergeCombiner, new HashPartitioner(partitions))
                .map(kv =>{
                    (kv._1, kv._2.toList.sorted)
                }).persist(StorageLevel.MEMORY_AND_DISK)

        val tripAggregator = new TripFuelAggregator()
        val fuelDataDs = tripAggregator.executeAggregate(fuelGroupedDs, bcConf)

        /*fuelDataDs.foreachRDD(rdd =>{
            rdd.count()
        })*/

        val fuelAggregator = new VehicleFuelAggregator()
        fuelAggregator.fuelAggregateAndWrite(fuelDataDs, bcConf)

        fuelGroupedDs.foreachRDD(_.unpersist())

    }

    /*
       将json串转换成map

    */
    private def convert(line: String, conf: VehicleFuelConfig): FuelDataRecord ={
        if(line != null){
            try {
                val map: java.util.Map[String, Object] = Jsons.toMap(line)

                val (vid, sim, isOn, dir, uploadTime) = getStatus(map)
                val (lon, lat) = getLocation(map)
                val (totalMile, totalFuel) = getTotalMsg(map)

                //获取数据id
                var id = ConfigUtils.getAsStringByKeys(map, "id", "ID")
                if(id == null)id = UUIDs.uuidFromBytesWithNoSeparator(line.getBytes())

                //数据信息范围检测
                val accept = conf.filters.map(filter =>{
                    !filter.isAccept(map)
                }).filter(b => b)

                if(accept.nonEmpty){
                    logError(s"记录未通过数据校验: \n id = ${id}, vid = ${vid},  gpsTime = ${uploadTime}")
                    return null
                }


                //获取数据去重的id
                val fuelId = UUIDs.uuidFromBytesWithNoSeparator(
                    s"${lon},${lat},${totalMile},${totalFuel}".getBytes())

                //生成油耗分析数据
                val record = new FuelDataRecord(vid, sim, fuelId, lon, lat, 0.0, 0,
                    0, totalMile, totalFuel, isOn, id, uploadTime)

                //读取速度
                var speed = ConfigUtils.getAsDoubleWithDefault(map, "speed1", 0.0)
                if(speed == 0.0)speed = ConfigUtils.getAsDoubleWithDefault(map, "speed", 0.0)
                record.speed = speed

                //异常驾驶行为 以及车辆 告警数据解析
                val  alarms = getAlarms(map, conf)
                if(alarms.nonEmpty){
                    val hashSet = new mutable.HashSet[VehicleAlarmRecord]()
                    hashSet ++= alarms
                    record.updateDrivingAlarm(hashSet)
                }
                return  record

            } catch {
                case e: Throwable => logError(s"数据有误，解析失败：\n\t ${line}", e)
            }
        }
        return null
    }

    private def getStatus(map: java.util.Map[String, Object]): (String, String, Boolean, Int, Long) ={

        val sim = ConfigUtils.getAsStringByKeys(map, "sim", "SIM")
        val vid = ConfigUtils.getAsStringByKeys(map, "vid", "VID")
        require(vid != null, "vid = null")

        var uploadTime = ConfigUtils.getAsLongByKeys(map, "gpsTime", "GPSTIME")
        if(uploadTime == null){
            uploadTime = System.currentTimeMillis()
            map.put("gpsTime", uploadTime)
        }

        var dir = ConfigUtils.getAsIntegerByKeys(map, "dir", "DIR")
        if(dir == null) {
            dir = 0
            map.put("dir", dir)
        }

        val status = String.valueOf(map.get("vehicleStatus"))
        val isOn = !status.contains("ACC 关")

        (vid, sim, isOn, dir, uploadTime)
    }

    //经纬度
    private def getLocation(map: java.util.Map[String, Object]): (Double, Double) ={
        var lon = ConfigUtils.getAsDoubleByKeys(map, "lon", "LON")
        require(lon != null, "lon = null")
        lon = lon/Math.pow(10, 6)
        map.put("lon", lon)

        var lat = ConfigUtils.getAsDoubleByKeys(map, "lat", "LAT")
        require(lat != null, "lat = null")
        lat = lat/Math.pow(10, 6)
        map.put("lat", lat)

        (lon, lat)
    }

    //获取累计信息， 累计里程 + 累计油耗
    private def getTotalMsg(map: java.util.Map[String, Object]): (Double, Double) ={

        var totalMile = ConfigUtils.getAsDoubleByKeys(map, "mile", "MILE")
        require(totalMile != null, "mile = null")
        totalMile = totalMile/10.0
        map.put("mile", totalMile)

        //累计油耗
        var totalFuel = ConfigUtils.getAsDoubleByKeys(map, "cumulativeOilConsumption", "CUMULATIVEOILCONSUMPTION")
        //var totalFuel = ConfigUtils.getAsDoubleByKeys(map, "fuel", "fuel")
        require(totalFuel != null, "cumulativeOilConsumption = null")
        totalFuel = totalFuel/Math.pow(10, 5)
        map.put("cumulativeOilConsumption", totalFuel)

        (totalMile, totalFuel)
    }


    private def getAlarms(map: java.util.Map[String, Object], conf: VehicleFuelConfig): mutable.Buffer[VehicleAlarmRecord] ={
        val records = getDrivingAlarm(map, conf)
        records ++= getVehicleAlarm(map, conf)
    }

    //获取驾驶告警信息
    private def getDrivingAlarm(map: java.util.Map[String, Object], conf: VehicleFuelConfig): mutable.Buffer[VehicleAlarmRecord] ={
        val alarmObj = map.get("abnormalDrivingBehaviorAlarmType")
        if(alarmObj != null){
            try {
                val drivingAlarms = alarmObj.asInstanceOf[java.util.List[String]]
                val drivingAlrmDegree = ConfigUtils.getAsIntegerWithDefault(map, "abnormalDrivingBehaviorAlarmDegree", -1)
                val alarms = drivingAlarms.asScala.map(alarm =>{
                    new VehicleAlarmRecord(alarm, 0, conf.getAlarmLabel(alarm), drivingAlrmDegree, 0)
                })

                return alarms
            }catch {
                case e: Exception =>
                    logError(s"驾驶行为异常报警数据${ConfigUtils.getAsStringByKeys(map, "id", "ID")}" +
                            s"不是一个list, ${alarmObj}", e)
            }
        }
        mutable.Buffer.empty[VehicleAlarmRecord]
    }

    //获取车辆告警信息
    private def getVehicleAlarm(map: java.util.Map[String, Object], conf: VehicleFuelConfig): mutable.Buffer[VehicleAlarmRecord] ={
        val alarmObj = map.get("alarms")
        if(alarmObj != null){
            try {
                val alarms = alarmObj.asInstanceOf[java.util.List[String]]
                val code = ConfigUtils.getAsIntegerWithDefault(map, "alarm", 0)
                val list = alarms.asScala.map(alarm =>{
                    new VehicleAlarmRecord(alarm, 1, conf.getAlarmLabel(alarm), 0, code)
                })

                return list
            }catch {
                case e: Exception =>
                    logError(s"车辆异常报警数据${ConfigUtils.getAsStringByKeys(map, "id", "ID")}" +
                            s"不是一个list, ${alarmObj}", e)
            }
        }
        mutable.Buffer.empty[VehicleAlarmRecord]
    }

}
