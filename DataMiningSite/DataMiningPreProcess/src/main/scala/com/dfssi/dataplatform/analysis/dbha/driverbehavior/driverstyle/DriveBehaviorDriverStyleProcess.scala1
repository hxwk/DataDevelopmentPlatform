package com.dfssi.dataplatform.analysis.dbha.driverbehavior.driverstyle

import com.dfssi.dataplatform.analysis.dbha.DriveBehaviorUtils.Field_CAN_Time
import com.dfssi.dataplatform.analysis.dbha.driverbehavior.clutch.{
  DriveBehaviorIdleStatus,
  DriveBehaviorStatusCheckParam
}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row

import scala.collection.mutable.ArrayBuffer

class DriveBehaviorDriverStyleProcess(_0705Rdd: RDD[Row]) {

  /**
    * Return second.
    * @return
    */
  def getIdleTime(): Long = {
    val idleStatusProcess = new DriveBehaviorIdleStatus()
    val statusParam = new DriveBehaviorStatusCheckParam()

    val _0705SortedRdd =
      _0705Rdd.collect().sortBy(row => row.getAs[Long](Field_CAN_Time))

    val timeScopesOfIdle: ArrayBuffer[(Long, Long)] =
      new ArrayBuffer[(Long, Long)]()
    _0705SortedRdd.map { row =>
      val timeScopeOfIdle = idleStatusProcess.checkEvent(row, statusParam)
      if (timeScopeOfIdle.isDefined) {
        timeScopesOfIdle += timeScopeOfIdle.get
      }
    }

    var idleTime: Long = 0
    timeScopesOfIdle.foreach(t => idleTime += (t._2 - t._1))

    idleTime / 1000
  }

}
