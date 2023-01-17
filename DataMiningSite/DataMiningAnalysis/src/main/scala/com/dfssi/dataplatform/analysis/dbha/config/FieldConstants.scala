package com.dfssi.dataplatform.analysis.dbha.config

trait FieldConstants {

  final val FIELD_VID = "vid"

}

object Terminal0200FieldConstants extends FieldConstants {

  final val HIVE_TABLE_NAME = "terminal_0200"

  final val FIELD_VDR_SPEED = "speed1"
  final val FIELD_GPS_SPEED = "speed"

  final val FIELD_GPS_TIME = "gps_time"

  final val FIELD_DIR = "dir"
  final val FIELD_VEHICLE_SIGNAL = "signal_states"
  final val FIELD_VEHICLE_STATES = "vehicle_status"
  final val FIELD_MILE = "mile"

}

object Terminal0705FieldConstants extends FieldConstants {

  final val HIVE_TABLE_NAME = "terminal_0705"

  final val FIELD_RECEIVE_TIME = "receive_time"
  final val FIELD_NAME_SIGNAL = "signal_name"
  final val FIELD_VALUE_SIGNAL = "value"

}