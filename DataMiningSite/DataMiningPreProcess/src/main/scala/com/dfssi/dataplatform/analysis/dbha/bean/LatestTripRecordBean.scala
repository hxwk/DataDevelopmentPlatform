package com.dfssi.dataplatform.analysis.dbha.bean

import java.sql.Timestamp

class LatestTripRecordBean {
  var driverId: String = _
  var id: String = _
  var fleetId: String = _
  var driverName: String = _
  var tripEndTime: Timestamp = _
  var updateTime: Timestamp = _

  override def toString: String = {
    s"$id,$fleetId,$driverId,$driverName,$tripEndTime,$updateTime"
  }
}

object LatestTripDetailBean {

  val latestTripDetailTableName = "dbha_latest_trip_record"

  val FIELD_NAME_ID = "id"
  val FIELD_NAME_DRIVER_ID = "driver_id"
  val FIELD_NAME_DRIVER_NAME = "driver_name"
  val FIELD_NAME_FLEET_ID = "fleet_id"
  val FIELD_NAME_TRIP_END_TIME = "trip_end_time"

  val INSERT_FIELDS = s"$FIELD_NAME_ID,$FIELD_NAME_FLEET_ID,$FIELD_NAME_DRIVER_ID,$FIELD_NAME_DRIVER_NAME,$FIELD_NAME_TRIP_END_TIME"
}