package com.dfssi.dataplatform.analysis.dbha.driverbehavior.clutch

import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

class DriveBehaviorStartupStatus {

  import com.dfssi.dataplatform.analysis.dbha.DriveBehaviorUtils._
  import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.DriveBehaviorDescriptor._

  var priorTimeOfReceive: Long = 0
  /* The last receive time */
  var currentTimeOfReceive: Long = 0
  var startTimeOfStartup: Long = 0
  var startStartup: Boolean = false;

  /* 速度 */
  var receiveTimeOfVehicleSpeed: Long = 0
  var vehicleSpeedStatus: Boolean = false

  def clear(): Unit = {
    priorTimeOfReceive = 0
    currentTimeOfReceive = 0
    startTimeOfStartup = 0
    startStartup = false;

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

    if (signalName.contains(KEYWORD_VECHICLE_SPEED)) {
      status = checkVehicleSpeed(value, statusParam)
      signalType = SIGNAL_TYPE_VECHICLE_SPEED
    } else { return None }

    refreshCheckStatus(receiveTime, signalType, status, statusParam)
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
      setValueToTrue(receiveTime, signalType, true)

      checkStartupNullPos()
    } else {
      /* exceed 2s */
      if (receiveTime > priorTimeOfReceive + statusParam.durationOfNullPos * 1000) {
        if (startStartup) {
          /* stop idle event */
          result = stopStartupEvent(receiveTime)
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
  private def stopStartupEvent(receiveTime: Long): Option[(Long, Long)] = {
    val result = (getMaxReceiveTime(), currentTimeOfReceive)

    clear()

    new Some(result)
  }

  private def getMaxReceiveTime(): Long = {
    //    Seq(priorTimeOfReceive + (currentTimeOfReceive - priorTimeOfReceive) / 2,
    //        currentTimeOfReceive).min
    priorTimeOfReceive
  }

  private def checkStartupNullPos(): Unit = {
    startStartup = (vehicleSpeedStatus == true)
  }

  private def setValueToTrue(receiveTime: Long,
                             signalType: String,
                             status: Boolean): Unit = {
    if (SIGNAL_TYPE_VECHICLE_SPEED.equalsIgnoreCase(signalType) && vehicleSpeedStatus == false) {
      receiveTimeOfVehicleSpeed = receiveTime
      vehicleSpeedStatus = status
    }
  }

  private def updateLastReceiveTime(newLastReceiveTime: Long): Unit = {
    if (newLastReceiveTime > currentTimeOfReceive) {
      priorTimeOfReceive = currentTimeOfReceive
      currentTimeOfReceive = newLastReceiveTime
      if (priorTimeOfReceive == 0)
        priorTimeOfReceive = currentTimeOfReceive
    }
  }
}
