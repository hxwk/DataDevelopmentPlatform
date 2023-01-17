package com.dfssi.dataplatform.analysis.dbha.behavior

import org.apache.spark.sql.Row

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


object DriveBehaviorUtils {

  // 辅助模板类
  case class Timeseries(timestamp: Long, value: Double)

  // 识别相关事件的默认的阈值参数
  val DefaultNightHourSet = Set(18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4, 5, 6)
  def isNightHour(h: Int, nightHourSet: Set[Int]): Boolean = nightHourSet.contains(h)

  val DefaultIdleRotationRange: (Int, Int) = (50, 800) //默认怠速的发动机转速范围

  val DefaultEcoSpeedRange: (Double, Double) = (400.0, 1000.0) //10km/h
  def isEcoSpeed(v: Long, ecoDriveSpeedRange: (Double, Double)): Boolean = {
    ecoDriveSpeedRange._1 <= v && v <= ecoDriveSpeedRange._2
  }

  def isInRange(r: (Int, Int), e: Double): Boolean = e >= r._1 && e <= r._2

//  // 0200表常量字段
//  val Field_VehicleStatus = "vehicle_status"
//  val Field_SignalStates = "signal_states"
//  val Field_Alarms = "alarms"
//  val Field_CumulativeMile = "mile"
//  val Field_DirectionAngle = "dir"
//  val Field_VDR_Speed = "speed1" //行车记录仪速度
//  val Field_GPS_Speed = "speed"  // GPS速度
//  val Field_GPS_Time = "gps_time"
//  // 0705表常量字段
//  val Field_SignalName = "signal_name"
//  val Field_Value = "value"
//  val Field_CAN_Time = "receive_time"

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


//  def get0200Speed(row: Row): Long = if (row.getAs[Long]("speed1") > 0)
//    row.getAs[Long]("speed1")
//  else row.getAs[Long]("speed")

}