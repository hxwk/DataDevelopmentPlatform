package com.dfssi.dataplatform.analysis.dbha

import com.dfssi.dataplatform.analysis.dbha.driverbehavior.turnlight.ImproperTurnEventCounter
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row

import scala.collection.mutable


class DriveEvent(id: Int) {
  var startTime: Long = _
  var lastingTime: Int = _

  //  def this(startTime: Long, lastingTime: Int) = {
  //    this.startTime = startTime
  //    this.lastingTime = lastingTime
  //  }
}


object DriveBehaviorUtils {

  // 辅助模板类
  case class Timeseries(timestamp: Long, value: Double)

  case class CollectedDvrItem(timestamp: Long, speed: Long, dir: Long, mile: Long, accState: Int, turnLightSignals: Set[String])

  case class EventsBy0200Only(totalDrivingDuration: Double,
                              improperSpeedAndTurnEventCountArr: Array[Int],
                              cumulateDriveAtNightStatArr: Array[Double],
                              coastingWithEngineOffCount: Int,
                              cumulateEcoDriveStatArr: Array[Double],
                              stopEventCount: Int,
                              speedStabilityCount: Int,
                              zeroSpeedRanges: Array[(Long, Long)],
                              improperTurnEventCounter: ImproperTurnEventCounter,
                              cumulateHighSpeedDriveStatArr: Array[Double])


  // 识别相关事件的默认的阈值参数
  val DefaultNightHourSet = Set(18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4, 5, 6)
  def isNightHour(h: Int, nightHourSet: Set[Int]): Boolean = nightHourSet.contains(h)

  val DefaultIdleRotationRange: (Int, Int) = (50, 800) //默认怠速的发动机转速范围

  val DefaultEcoSpeedRange: (Double, Double) = (400.0, 1000.0) //10km/h
  def isEcoSpeed(v: Long, ecoDriveSpeedRange: (Double, Double)): Boolean = {
    ecoDriveSpeedRange._1 <= v && v <= ecoDriveSpeedRange._2
  }

  def isInRange(r: (Int, Int), e: Double): Boolean = e >= r._1 && e <= r._2

  // 0200表常量字段
  val Field_VehicleStatus = "vehicle_status"
  val Field_SignalStates = "signal_states"
  val Field_Alarms = "alarms"
  val Field_CumulativeMile = "mile"
  val Field_DirectionAngle = "dir"
  val Field_VDR_Speed = "speed1" //行车记录仪速度
  val Field_GPS_Speed = "speed"  // GPS速度
  val Field_GPS_Time = "gps_time"
  // 0705表常量字段
  val Field_SignalName = "signal_name"
  val Field_Value = "value"
  val Field_CAN_Time = "receive_time"

  // 一些关键词常量
  val malfunctionAlarms = Set("电源", "盗", "油量") // 车身故障报警
  val fatigueDriving = "疲劳驾驶"
  val overSpeed = "超速"
  val routeBias = "路线偏离"

  def updateAlramCount(alarmCountArr: Array[Int],
                       alarms: mutable.WrappedArray[String]): Unit =
    for (alarm <- alarms) {
      if (malfunctionAlarms.exists(alarm.contains(_))) {
        alarmCountArr(0) = 1
      }
      else if (alarm.contains(fatigueDriving)) {
        alarmCountArr(1) = 1
      }
      else if (alarm.contains(overSpeed)) {
        alarmCountArr(2) = 1
      }
      else if (alarm.contains(routeBias)) {
        alarmCountArr(3) = 1
      }
    }


  def get0200Speed(row: Row): Long = if (row.getAs[Long](Field_VDR_Speed) > 0) row.getAs[Long](Field_VDR_Speed) else row.getAs[Long](Field_GPS_Speed)

  /**
    * 从0200中收集相关数据
    * @param terminal_0200_dvrRdd 行车记录仪数据
    * @return  Array[CollectedDvrItem]
    */
  def collectValidDvrData(terminal_0200_dvrRdd: Iterator[Row]): Array[CollectedDvrItem] = {

    val startRunTime = System.currentTimeMillis()
    println(s"开始执行collectValidDvrData，时刻为$startRunTime")

    // 合并同一时刻的转向灯信号
    val turnLightTs = terminal_0200_dvrRdd
      .flatMap(r =>
        r.getAs[mutable.WrappedArray[String]](Field_SignalStates).filter(_.contains("转向灯"))
          .map(f => r.getAs[Long](Field_GPS_Time) -> f))
      .toArray
      .groupBy(_._1)
      .mapValues(_.map(_._2).toSet)

    // 假设同一时刻只有一个ACC信号
    val accStateTs = terminal_0200_dvrRdd.map{ row =>
      val timestamp = row.getAs[Long](Field_GPS_Time)
      val vehicleStatus = row.getAs[mutable.WrappedArray[String]](Field_VehicleStatus)
      val accState = if(vehicleStatus.contains("ACC 关")) 0
      else if(vehicleStatus.contains("ACC 开")) 1
      else -1
      timestamp -> accState
    }.toMap

    val res = terminal_0200_dvrRdd.map{ row =>
      val speed = get0200Speed(row)
      val timestamp = row.getAs[Long](Field_GPS_Time)
      val dir = row.getAs[Long](Field_DirectionAngle)
      val mile = row.getAs[Long](Field_CumulativeMile)
      timestamp -> (speed, dir, mile)
    }.toArray
        .groupBy(_._1)
        .map{ case(key, arr)  =>
          var avgSpeed = 0L
          var avgDir = 0L
          var avgMile = 0L
          arr.foreach{ case(_, tuple) =>
            avgSpeed += tuple._1
            avgDir += tuple._2
            avgMile += tuple._3
          }
          CollectedDvrItem(key, avgSpeed/arr.length, avgDir/arr.length, avgMile/arr.length, accStateTs(key), turnLightTs(key))
        }
        .toArray
      .sortBy(_.timestamp)

    val durationMinutes = (System.currentTimeMillis()- startRunTime)/1000/60.0
    println(s"执行collectValidDvrData结束，耗时 $durationMinutes 分钟")

    res
  }

  /**
    * 从0200中收集相关数据
    * @param terminal_0200_dvrRdd 行车记录仪数据
    * @return  Array[CollectedDvrItem]
    */
  def collectValidDvrData(terminal_0200_dvrRdd: RDD[Row]): Array[CollectedDvrItem] = {

    val startRunTime = System.currentTimeMillis()
    println(s"开始执行collectValidDvrData，时刻为$startRunTime")

    // 合并同一时刻的转向灯信号
    val turnLightRdd = terminal_0200_dvrRdd
      .flatMap(r =>
        r.getAs[mutable.WrappedArray[String]](Field_SignalStates).filter(_.contains("转向灯"))
          .map(f => r.getAs[Long](Field_GPS_Time) -> f))
      .aggregateByKey(mutable.Set[String]())((U,v)=>{U.add(v);U}, (U1, U2)=> U1.++:(U2))

    // 假设同一时刻只有一个ACC信号
    val stringRdd = terminal_0200_dvrRdd.map{ row =>
      val timestamp = row.getAs[Long](Field_GPS_Time)
      val vehicleStatus = row.getAs[mutable.WrappedArray[String]](Field_VehicleStatus)
      val accState = if(vehicleStatus.contains("ACC 关")) 0
      else if(vehicleStatus.contains("ACC 开")) 1
      else -1
      timestamp -> accState
    }.distinct()
      .join(turnLightRdd)
      .map{ case(key, (left, right)) =>
        key -> (left, right.toSet)
      }

    def seqOp(U: Array[Long], v: (Long, Long, Long)) = {
      U(0) += v._1
      U(1) += v._2
      U(2) += v._3
      U(3) += 1
      U
    }

    def combOp(U1: Array[Long], U2: Array[Long]) = {
      U1(0) += U2(0)
      U1(1) +=U2(1)
      U1(2) +=U2(2)
      U1(3) +=U2(3)
      U1
    }

    val res = terminal_0200_dvrRdd.map{ row =>
      val speed = get0200Speed(row)
      val timestamp = row.getAs[Long](Field_GPS_Time)
      val dir = row.getAs[Long](Field_DirectionAngle)
      val mile = row.getAs[Long](Field_CumulativeMile)
      timestamp -> (speed, dir, mile)
    }.aggregateByKey(Array(0L, 0L, 0L, 0L))(seqOp, combOp)
      .mapValues { arr =>
        val size = arr.last
        (arr(0)/size, arr(1)/size, arr(2)/size)
      }
      .join(stringRdd)
      .map{ case(key, (left, right)) =>
        CollectedDvrItem(key, left._1, left._2, left._3, right._1, right._2)
      }
      .collect()
      .sortBy(_.timestamp)

    val durationMinutes = (System.currentTimeMillis()- startRunTime)/1000/60.0
    println(s"执行collectValidDvrData结束，耗时 $durationMinutes 分钟")

    res
  }

}