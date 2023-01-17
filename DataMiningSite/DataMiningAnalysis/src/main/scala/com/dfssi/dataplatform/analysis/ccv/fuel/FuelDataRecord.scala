package com.dfssi.dataplatform.analysis.ccv.fuel


import java.util.UUID

import com.dfssi.dataplatform.analysis.ccv.trip.TripDataRecord
import org.apache.spark.Logging

import scala.collection.mutable

/**
  * Description:
  *     油耗分析中使用到的数据
  * @author LiXiaoCong
  * @version 2018/5/15 10:20 
  */
class FuelDataRecord(val vid: String,
                     val sim: String,
                     val id: String,
                     val lon: Double,
                     val lat: Double,
                     val alt: Double,
                     val isplateau: Int,
                     val iseco: Int,
                     val totalmile: Double,
                     val totalfuel: Double,
                     var isOn: Boolean,
                     var dataId: String,
                     var gpstime: Long,
                     val routeid: String = "112233") extends Comparable[FuelDataRecord]
        with Serializable with Logging{

    //行程id
    var tripid: String = null

    var speed: Double = 0.0

    //驾驶异常告警程度
    var alarms: mutable.HashSet[VehicleAlarmRecord] = null

    //单一批次内 数据聚合
    var timeGap: Long = 0L
    var mileGap: Double = 0.0
    var fuelGap: Double = 0.0

    //三段比较 先比较时间 再比较里程 再比较油耗
    override def compareTo(o: FuelDataRecord): Int = {
        var result = gpstime.compareTo(o.gpstime)
        if (result == 0) {
            result = totalmile.compareTo(o.totalmile)
            if (result == 0) result = totalfuel.compareTo(o.totalfuel)
        }
        result
    }

    def canEqual(other: Any): Boolean = other.isInstanceOf[FuelDataRecord]

    override def equals(other: Any): Boolean = other match {
        case that: FuelDataRecord =>
            var staus: Boolean = false
            if((that canEqual this) && id == that.id){
                staus = true

                //更新集合中的数据到最新状态
                if (that.gpstime > gpstime) {
                    this.gpstime = that.gpstime
                    this.dataId = that.dataId
                    this.isOn = that.isOn
                    this.speed = that.speed
                }

                //更新告警状态
                updateDrivingAlarm(that.alarms)
            }
            staus
        case _ => false
    }

    //更新告警状态
    def updateDrivingAlarm(alarms: mutable.HashSet[VehicleAlarmRecord]): Unit ={
        if (alarms != null) {
            if (this.alarms == null)
                this.alarms = alarms
            else
                this.alarms ++= alarms
        }
    }

    def setGapMsg(timeGap: Long, mileGap: Double, fuelGap: Double): Unit ={
        this.timeGap = timeGap
        this.mileGap = mileGap
        this.fuelGap = fuelGap
    }

    override def hashCode(): Int = {
        val state = Seq(id)
        state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }

    override def toString = s"FuelDataRecord($tripid, $speed, $alarms," +
            s"$timeGap, $mileGap, $fuelGap, $vid, $sim, $dataId, $lon, $lat, $alt, " +
            s"$isplateau, $iseco, $totalmile, $totalfuel, $isOn, $id, $gpstime, $routeid)"


    def toTripDataRecord(): TripDataRecord ={
        val id = UUID.nameUUIDFromBytes(toString.getBytes).toString
        val tripRecord = TripDataRecord(id, vid, sim)

        tripRecord.isover = if(isOn) 0 else 1

        tripRecord.starttotalmile = totalmile
        tripRecord.endtotalmile = totalmile
        tripRecord.starttotalfuel = totalfuel
        tripRecord.endtotalfuel = totalfuel

        tripRecord.startlon = lon
        tripRecord.endlon = lon
        tripRecord.startlat = lat
        tripRecord.endlat = lat

        tripRecord.starttime = gpstime
        tripRecord.endtime = gpstime
        tripRecord.interval = 0L
        tripRecord.iseco = iseco

        tripRecord
    }
}
