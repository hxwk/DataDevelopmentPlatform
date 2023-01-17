package com.dfssi.dataplatform.analysis.ccv.fuel.counts

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/16 9:10 
  */
class AlarmRecord(val id: String,
                  val vid: String,
                  val alarmtype: Int,
                  val alarm: String,
                  val alarmlabel: Int) extends Serializable {
    var lat = 0.0
    var lon = 0.0
    var speed = 0.0
    var degree = 0
    var code = 0
    var gpsTime = 0L
    var count = 1

    var changed = false

    override def toString = s"DrivingAlarmRecord($lat, $lon, $speed, $degree, " +
            s"$gpsTime, $count, $vid, $alarm, $alarmlabel)"
}

