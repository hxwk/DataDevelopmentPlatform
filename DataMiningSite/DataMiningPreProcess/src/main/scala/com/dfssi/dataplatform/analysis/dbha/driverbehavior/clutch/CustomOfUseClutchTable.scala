package com.dfssi.dataplatform.analysis.dbha.driverbehavior.clutch

import java.sql.Timestamp

object CustomOfUseClutchTable {
  val TABLE_NAME = "dbha_analysis_custom_use_clutch"

  val FIELD_TRIP_ID = "trip_id"
  val FIELD_TRIP_ENDTIME = "trip_end_time"
  val FIELD_NUM_RUN_NULL_POS = "num_run_null_pos"
  val FIELD_NUM_NORMAL = "num_normal"
  val FIELD_NUM_AT_IDLE = "num_at_Idle"
  val FIELD_NUM_STARTUP = "num_startup"
}

case class CustomOfUseClutch(tripId: String,
                             tripEndTime: Timestamp,
                             numRunAtNullPos: Long = 0,
                             numRunAtIdle: Long = 0,
                             numStartup: Long = 0,
                             numAtNormal: Long = 0) {

  override def toString: String =
    s"'$tripId','$tripEndTime', $numRunAtNullPos, $numAtNormal, $numRunAtIdle, $numStartup"

}
