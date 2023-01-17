package com.dfssi.dataplatform.analysis.dbha.behavior.together

import com.dfssi.dataplatform.analysis.dbha.bean.BasicEventBean
import com.dfssi.dataplatform.analysis.dbha.behavior.BehaviorEvent
import com.dfssi.dataplatform.analysis.dbha.behavior.terminal0705.DiscreteSignalEventParser
import org.apache.spark.sql.Row

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * 油门控制相关行为：
  * 大油门行驶：速度>0km/h，连续油门开度>80%，持续5s
  * 满油行驶：速度>0km/h，连续油门开度>95%，持续2s以上
  * 停车踩油门：空挡，油门踏板开度>0 （暂时无法识别）
  *
  * @param paramsMap 配置参数
  * @param discreteSignalEventParser  0705离散信号
  */
class ThrottleControlBehaviorParser(paramsMap: Map[String, String], discreteSignalEventParser: DiscreteSignalEventParser)
  extends ParseBehaviorEventTogether(discreteSignalEventParser){

  private val max0705EventDurationMillis = paramsMap
    .getOrElse("max0705EventDuration", "5")
    .toFloat * 1000
  private val max0200EventDurationMillis = paramsMap
    .getOrElse("max0200EventDuration", "5")
    .toFloat * 1000

  private val minSpeedWithHighOpening: Double = paramsMap.getOrElse("minSpeedAtHighThrottleOpen", "20").toDouble * 10
  private val minHighThrottleOpening: Double = paramsMap.getOrElse("minHighThrottleOpening", "80").toDouble
  private val minHighOpeningMillis: Double = paramsMap.getOrElse("minHighOpeningMillis", "4").toDouble * 1000
  private val minFullThrottleOpening: Double =paramsMap.getOrElse("minFullThrottleOpening", "95").toDouble
  private val minFullThrottheMillis = paramsMap.getOrElse("minFullThrottheLastTime", "2").toDouble * 1000

  private val driveAtHighThrottleOpeningEvent = new BehaviorEvent()
  private val driveAtFullThrottleOpeningEvent = new BehaviorEvent()

  private val drivingPeriods = ListBuffer[TimePeriod]()

  private val eventContainer = ArrayBuffer[BasicEventBean]()

  import ThrottleControlBehaviorParser._

  override def parseMultipleRows2(rows1: Array[Row], rows2: Array[Row]): Unit = {

    rows1.foreach(produceDrivingSpeedPeriods)

    rows2.foreach{ row =>
      val signalName = getSignalName(row)
      val signalValue = getSignalValue(row)

      if (signalName.contains("油门踏板")) {
        checkDriveAtHighThrottleOpeningEvent(signalValue, getCaptureTime(row), row)
//        checkDriveAtFullThrottleOpeningEvent(signalValue, getCaptureTime(row), row)
      }

    }
  }

  private def produceDrivingSpeedPeriods(row: Row): Unit = {
    val speed = get0200Speed(row)
    val gpsTime = getGPSTime(row)

    if (speed > 0) {
      if (drivingPeriods.last.startTime == -1)
        drivingPeriods.last.startTime = gpsTime
      else if (gpsTime - drivingPeriods.last.endTime > max0200EventDurationMillis) {
        drivingPeriods.append(new TimePeriod(gpsTime))
      }
      drivingPeriods.last.endTime = gpsTime
    } else {
      if (drivingPeriods.last.startTime != -1)
        drivingPeriods.append(new TimePeriod(-1))
    }
  }

  private def checkDriveAtHighThrottleOpeningEvent(signalValue: Double, receiveTime: Long, row: Row): Unit = {
    if (signalValue >= minHighThrottleOpening) {
      if (driveAtHighThrottleOpeningEvent.startRow == null) {
        driveAtHighThrottleOpeningEvent.startRow = row
        driveAtHighThrottleOpeningEvent.endRow = row
      }
      else if (receiveTime - getCaptureTime(driveAtHighThrottleOpeningEvent.endRow) < max0705EventDurationMillis) {
        driveAtHighThrottleOpeningEvent.endRow = row
      }
      else {
        // TODO add this event
        if (getCaptureTime(driveAtHighThrottleOpeningEvent.endRow) - getCaptureTime(driveAtHighThrottleOpeningEvent.startRow) >= minHighOpeningMillis) {
          println(s"发现大油门行驶，从${driveAtHighThrottleOpeningEvent.startRow} \n 到 ${driveAtHighThrottleOpeningEvent.endRow}")
          addEvent(DriveAtHighThrottleOpeningEventType)
        }
        driveAtHighThrottleOpeningEvent.reset()
      }
    }
    else if (driveAtHighThrottleOpeningEvent.startRow != null) {
      // TODO add this event
      if (getCaptureTime(driveAtHighThrottleOpeningEvent.endRow) - getCaptureTime(driveAtHighThrottleOpeningEvent.startRow) >= minHighOpeningMillis) {
        addEvent(DriveAtHighThrottleOpeningEventType)
        println(s"发现大油门行驶，从${driveAtHighThrottleOpeningEvent.startRow} \n 到 ${driveAtHighThrottleOpeningEvent.endRow}")
      }
      driveAtHighThrottleOpeningEvent.reset()
    }

  }

  private def checkDriveAtFullThrottleOpeningEvent(signalValue: Double, receiveTime: Long, row: Row): Unit = {
    if (signalValue >= minFullThrottleOpening) {
      if (driveAtFullThrottleOpeningEvent.startRow == null) {
        driveAtFullThrottleOpeningEvent.startRow = row
        driveAtFullThrottleOpeningEvent.endRow = row
      }
      else if (receiveTime - getCaptureTime(driveAtFullThrottleOpeningEvent.endRow) < max0705EventDurationMillis) {
        driveAtFullThrottleOpeningEvent.endRow = row
      }
      else {
        // TODO add this event
        if (getCaptureTime(driveAtFullThrottleOpeningEvent.endRow) - getCaptureTime(driveAtFullThrottleOpeningEvent.startRow) >= minFullThrottheMillis) {
          addEvent(DriveAtFullThrottleOpeningEventType)
          println(s"发现满油门行驶，从${driveAtFullThrottleOpeningEvent.startRow} \n 到 ${driveAtFullThrottleOpeningEvent.endRow}")
        }
        driveAtFullThrottleOpeningEvent.reset()
      }
    }
    else if (driveAtFullThrottleOpeningEvent.startRow != null) {
      // TODO add this event
      if (getCaptureTime(driveAtFullThrottleOpeningEvent.endRow) - getCaptureTime(driveAtFullThrottleOpeningEvent.startRow) >= minFullThrottheMillis) {
        addEvent(DriveAtFullThrottleOpeningEventType)
        println(s"发现满油门行驶，从${driveAtFullThrottleOpeningEvent.startRow} \n 到 ${driveAtFullThrottleOpeningEvent.endRow}")
      }
      driveAtFullThrottleOpeningEvent.reset()
    }

  }


  private def addEvent(eventType: Int): Unit = {
    import com.dfssi.dataplatform.analysis.dbha.bean.EventBean._

    eventType match {
      case DriveAtHighThrottleOpeningEventType =>
        val startTime = getCaptureTime(driveAtHighThrottleOpeningEvent.startRow)
        val endTime = getCaptureTime(driveAtHighThrottleOpeningEvent.endRow)
        eventContainer.append(BasicEventBean(EVENT_NAME_DURATION_RUNATBIGACCELERATOR, startTime, endTime))

      case DriveAtFullThrottleOpeningEventType =>
        val startTime = getCaptureTime(driveAtFullThrottleOpeningEvent.startRow)
        val endTime = getCaptureTime(driveAtFullThrottleOpeningEvent.endRow)
        eventContainer.append(BasicEventBean(EVENT_NAME_NUM_RUNATFULLACCELERATOR, startTime, endTime))
    }
  }

  override def outputAllEvents(): Iterator[BasicEventBean] = eventContainer.toIterator

  override def setUp(): Unit = {
    drivingPeriods.append(new TimePeriod(-1))
  }

  override def cleanUp(): Unit = {}
}


object ThrottleControlBehaviorParser {

  val DriveAtHighThrottleOpeningEventType = 1
  val DriveAtFullThrottleOpeningEventType = 2

}