package com.dfssi.dataplatform.analysis.dbha.driverbehavior.clutch

import com.dfssi.dataplatform.analysis.dbha.DriveBehaviorUtils
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.DriveBehaviorDescriptor
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

class DriveBehaviorIdleStatus {

  import DriveBehaviorDescriptor._
  import DriveBehaviorUtils._

  var priorLastTimeOfReceive: Long = 0
  /* The last receive time */
  var lastTimeOfReceive: Long = 0
  var startTimeOfIdle: Long = 0
  var startIdle: Boolean = false;

  /* 发动机怠速 */
  var receiveTimeOfEngineRotation: Long = 0
  var engineRotationStatus: Boolean = false
  /* 速度 */
  var receiveTimeOfVehicleSpeed: Long = 0
  var vehicleSpeedStatus: Boolean = false

  def clear(): Unit = {
    priorLastTimeOfReceive = 0
    lastTimeOfReceive = 0
    startTimeOfIdle = 0
    startIdle = false;
    receiveTimeOfEngineRotation = 0
    engineRotationStatus = false
    receiveTimeOfVehicleSpeed = 0
    vehicleSpeedStatus = false
  }

  def checkEvent(
      row: Row,
      statusParam: DriveBehaviorStatusCheckParam): Option[(Long, Long)] = {

    val receiveTime = row.getAs[Long](Field_CAN_Time)
    val signalName = row.getAs[String](Field_SignalName)
    val value = row.getAs[Double](Field_Value)

    var status: Boolean = false
    var signalType: String = null

    if (signalName.contains(Keyword_EngineRotation)) {
      status = checkEngineRotation(value, statusParam)
      signalType = SIGNAL_TYPE_ENGINEROTATION
    } else if (signalName.contains(KEYWORD_VECHICLE_SPEED)) {
      status = checkVehicleSpeed(value, statusParam)
      signalType = SIGNAL_TYPE_VECHICLE_SPEED
    } else {
      return None
    }

    refreshCheckStatus(receiveTime, signalType, status, statusParam)
  }

  private def checkEngineRotation(
      value: Double,
      statusParam: DriveBehaviorStatusCheckParam): Boolean = {
    value > statusParam.motorIdleRotationScope._1 && value <= statusParam.motorIdleRotationScope._2
  }

  private def checkVehicleSpeed(
      value: Double,
      statusParam: DriveBehaviorStatusCheckParam): Boolean = {
    value == 0
  }

  def refreshCheckStatus(
      receiveTime: Long,
      signalType: String,
      status: Boolean,
      statusParam: DriveBehaviorStatusCheckParam): Option[(Long, Long)] = {
    var result: Option[(Long, Long)] = None

    if (StringUtils.isBlank(signalType)) return result

    updateLastReceiveTime(receiveTime)
    if (status) {
      if (startIdle) {
        /* do nothing, continue */
      } else {
        setValueToTrue(receiveTime, signalType, true)
      }
      checkStartupNullPos()
    } else {
      /* exceed 2s */
      if (receiveTime > priorLastTimeOfReceive + statusParam.durationOfNullPos * 1000) {
        if (startIdle) {
          /* stop idle event */
          result = stopIdleEvent(receiveTime)
        } else {
          clear()
        }
      } else {
        /* low 2s */
        /* do nothing, continue */
      }
    }

    result
  }

  /**
    * stop, and generate time scop
    *
    * @param receiveTime
    */
  private def stopIdleEvent(receiveTime: Long): Option[(Long, Long)] = {
    val result =
      (getMaxReceiveTime(),
       Seq(
         getMaxReceiveTime(),
         priorLastTimeOfReceive + (receiveTime - priorLastTimeOfReceive) / 2).max)

    clear()

    new Some(result)
  }

  private def getMaxReceiveTime(): Long = {
    Array(receiveTimeOfEngineRotation, receiveTimeOfVehicleSpeed).max
  }

  private def checkStartupNullPos(): Unit = {
    startIdle = (engineRotationStatus == true && vehicleSpeedStatus == true)
  }

  private def setValueToTrue(receiveTime: Long,
                             signalType: String,
                             status: Boolean): Unit = {
    if (SIGNAL_TYPE_ENGINEROTATION.equalsIgnoreCase(signalType) && engineRotationStatus == false) {
      receiveTimeOfEngineRotation = receiveTime
      engineRotationStatus = status
    } else if (SIGNAL_TYPE_VECHICLE_SPEED.equalsIgnoreCase(signalType) && vehicleSpeedStatus == false) {
      receiveTimeOfVehicleSpeed = receiveTime
      vehicleSpeedStatus = status
    }
  }

  private def updateLastReceiveTime(newLastReceiveTime: Long): Unit = {
    if (newLastReceiveTime > lastTimeOfReceive) {
      priorLastTimeOfReceive = lastTimeOfReceive
      lastTimeOfReceive = newLastReceiveTime
      if (priorLastTimeOfReceive == 0)
        priorLastTimeOfReceive = lastTimeOfReceive
    }
  }
}
