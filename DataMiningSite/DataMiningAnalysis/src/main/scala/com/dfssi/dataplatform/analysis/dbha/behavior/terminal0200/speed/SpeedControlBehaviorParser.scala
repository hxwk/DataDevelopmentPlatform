package com.dfssi.dataplatform.analysis.dbha.behavior.terminal0200.speed

import com.dfssi.dataplatform.analysis.dbha.bean.BasicEventBean
import com.dfssi.dataplatform.analysis.dbha.bean.EventBean._
import com.dfssi.dataplatform.analysis.dbha.behavior.terminal0200.ParseBehaviorEventBy0200
import com.dfssi.dataplatform.analysis.dbha.behavior.{BehaviorEvent, DriveBehaviorUtils}
import org.apache.spark.sql.Row

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  *
  * @param paramsMap
  * 急加速
  * 急减速
  * 连续加减速
  * 速度波动性
  * 经济速度行驶
  * 超高速行驶
  */
final class SpeedControlBehaviorParser(paramsMap: Map[String, String]) extends ParseBehaviorEventBy0200 {

  // 车速波动
  val maxVibrationRate: Double = paramsMap.getOrElse("reasonableVibrationRate", "0.3").toDouble
  val speedVariation: ArrayBuffer[Double] = ArrayBuffer[Double]() // km/h/s

  val minSpeedUpAcc: Float = paramsMap.getOrElse("minAccelerateSpeed", "6").toFloat * 10 // 6 * 10km/h/s
  val maxSpeedDownDec: Float = paramsMap.getOrElse("maxDecelerateSpeed", "-7.2").toFloat * 10 // -7.2 * 10km/h/h

  val minSuperHighSpeed: Float = paramsMap.getOrElse("minSuperHighSpeed", "120").toFloat * 10

  private val maxEventDurationMillis: Int = 5 * 1000 // 5秒

  // init vars
  var eventType: Int = -1 // 0->急加速 1->急减速 2->急转弯 3->连续加速-减速

  private var previousItem: Row = _
  private val harshSpeedBehaviorEvent: BehaviorEvent= new BehaviorEvent()
  private val ecoSpeedDriveEvent: BehaviorEvent= new BehaviorEvent()
  private val superHighSpeedDriveEvent: BehaviorEvent= new BehaviorEvent()
  private val speedVibrationEvent = new SpeedVibrationEvent()

  // event parse classes and container
  private val eventContainer = ListBuffer[BasicEventBean]()

  import DriveBehaviorUtils._

  override def setUp(): Unit = {} // do nothing

  override def parseCurrentRow(item: Row): Unit = {
    if (previousItem != null) {
      val prevV = get0200Speed(previousItem)
      val prevT = getGPSTime(previousItem) //previousItem.getAs[Long](FIELD_GPS_TIME)

      val velocity = get0200Speed(item)
      val ts = getGPSTime(item) //.getAs[Long](FIELD_GPS_TIME)

      if (prevT >= ts) {
        logError(s"当前时间小于或等于前一条记录时间 \n $previousItem \n $item")
      }
      else {
        checkImproperSpeedUpAndDown(item)
      }

      checkSpeedVibration(item)
    }

    previousItem = item
  }


  /**
    * 三急 + "加速-减速"
    * */
  def checkImproperSpeedUpAndDown(item: Row): Unit = {

    val prevV = get0200Speed(previousItem)
    val prevT = getGPSTime(previousItem) //.getAs[Long](FIELD_GPS_TIME)

    val velocity = get0200Speed(item)
    val ts = getGPSTime(item) //.getAs[Long](FIELD_GPS_TIME)

    // 前后时间相同或者时间差大于maxInterval则忽略 prevT != ts &&
    if (ts - prevT <= maxEventDurationMillis) {
      val deltaT = (ts - prevT) / 1000.0  //秒+ 1e-2
      val accSpeed = (velocity - prevV) / deltaT // * 10km/h/s

      if (accSpeed >= minSpeedUpAcc) {
        if (-1 == eventType) {
          eventType = 0 // 0 -> harsh speed up
          harshSpeedBehaviorEvent.startRow = previousItem
        } else if (0 != eventType) {
          harshSpeedBehaviorEvent.endRow = previousItem
          // TODO 保存该事件
          addSpeedControlDriveEvent(eventType)
          println(s"0200数据发现急减速事件$eventType\n ${harshSpeedBehaviorEvent.toString}")

          harshSpeedBehaviorEvent.startRow = previousItem
          eventType = 0 // 0 -> harsh speed up
        }
      }
      else if (accSpeed <= maxSpeedDownDec) {
        if (-1 == eventType) {
          eventType = 1 // 1 -> rapid dece
          harshSpeedBehaviorEvent.startRow = previousItem
        } else if (3 == eventType) {
          // do nothing
        } else if (1 != eventType) { // 之前急加速，现在急减速
          val lastTime = (ts - getGPSTime(harshSpeedBehaviorEvent.startRow)) / 1000.0
          if (lastTime <= maxEventDurationMillis) { //急加速-加减速连续5秒内出现，改变eventtype
            eventType = 3
          } else {
            harshSpeedBehaviorEvent.endRow = previousItem
            // TODO 保存该事件
            addSpeedControlDriveEvent(eventType)
            println(s"0200数据发现事件$eventType\n ${harshSpeedBehaviorEvent.toString}")

            harshSpeedBehaviorEvent.startRow = item
            eventType = 1 // 1 -> rapid dece
          }
        }
      }
    }
    // 判断当前点是否一段变速行为的终点，是则更新
    if (eventType != -1) {
      harshSpeedBehaviorEvent.endRow = item

      addSpeedControlDriveEvent(eventType)
      println(s"0200数据发现事件$eventType\n ${harshSpeedBehaviorEvent.toString}")

      eventType = -1
    }
  }

  /** 速度波动性 */
  def checkSpeedVibration(item: Row): Unit = {
    val prevV = get0200Speed(previousItem)
    val prevT = getGPSTime(previousItem)

    val velocity = get0200Speed(item)
    val ts = getGPSTime(item)

    if( prevV == 0 && velocity == 0) {
      if (speedVibrationEvent.startRow != null) {
        speedVibrationEvent.endRow = previousItem
      }

      if (speedVibrationEvent.endRow != null) {
//        speedVibrationEvent.endRow = previousItem
        // TODO 保存事件
        val be = BasicEventBean(EVENT_NAME_WAVEOFSPEED,
          getGPSTime(speedVibrationEvent.startRow),
          getGPSTime(speedVibrationEvent.endRow))

        // TODO 将-1替换为正确的事件id
        eventContainer.append(be)

        if (speedVibrationEvent.count > 0) {
          be.columnValueMap.put("VARIANCE", speedVibrationEvent.accSpeedVariance.toFloat.toString)
          logError(s"一段行程的速度方差波动为 $be")
          speedVibrationEvent.reset()
        }
      }
    }
    else {
      if (speedVibrationEvent.startRow == null) {
        speedVibrationEvent.startRow = item
      }

      val deltaT = (ts - prevT) / 1000.0 + 1e-4  //秒
      val accSpeed = (velocity - prevV) / deltaT // * 10km/h/s

      speedVibrationEvent.updateAccSpeed(accSpeed)
    }
  }

  def checkIfDriveAtEcoOrHighSpeed(item: Row): Unit = {
    val v = get0200Speed(item)

    if (v >= minSuperHighSpeed) {
      if (superHighSpeedDriveEvent.startRow == null) superHighSpeedDriveEvent.startRow = item
    }
    else {
      if (superHighSpeedDriveEvent.startRow != null) {
        superHighSpeedDriveEvent.endRow = previousItem
        addSpeedControlDriveEvent(SpeedControlBehaviorParser.DriveAtSuperHighSpeedEventType)
      }
      if (isEcoSpeed(v, DefaultEcoSpeedRange)) {
        if (ecoSpeedDriveEvent.startRow == null) ecoSpeedDriveEvent.startRow = item
      }
      else if (ecoSpeedDriveEvent.startRow != null) {
        ecoSpeedDriveEvent.endRow = previousItem
        addSpeedControlDriveEvent(SpeedControlBehaviorParser.DriveAtEcoSpeedEventType)
      }
    }

  }


//  def addSpeedUnAndDownEvent(eventType: Int, startTime: Long, endTime: Long): Unit = {
//    val eventName = eventType match {
//      case 0 => EVENT_NAME_NUM_HARSHSPEEDUP
//      case 1 => EVENT_NAME_NUM_HARSHSPEEDDOWN
//      case 3 => EVENT_NAME_NUM_SPEEDUPTHENBRAKE
//      case _ => ""
//    }
//
//    eventContainer.append(BasicEvent(eventName, startTime, endTime))
//  }


  def addSpeedControlDriveEvent(eventType: Int): Unit = {
    import SpeedControlBehaviorParser._

    val event = eventType match {
      case DriveAtSuperHighSpeedEventType =>
        superHighSpeedDriveEvent

      case DriveAtEcoSpeedEventType =>
        ecoSpeedDriveEvent

      case _ =>
        harshSpeedBehaviorEvent
    }

    val startTime = getGPSTime(event.startRow)
    val endTime = getGPSTime(event.endRow)
    val startMile = getMile(event.startRow)
    val endMile = getMile(event.endRow)

    val be = eventType match {

      case HarshSpeedUpEventType =>
        val be = BasicEventBean(EVENT_NAME_NUM_HARSHSPEEDUP, startTime, endTime)
        harshSpeedBehaviorEvent.reset()
        be

      case HarshSpeedDownEventType =>
        val be = BasicEventBean(EVENT_NAME_NUM_HARSHSPEEDDOWN, startTime, endTime)
        harshSpeedBehaviorEvent.reset()
        be

      case SpeedUpThenBrakeEventType =>
        val be = BasicEventBean(EVENT_NAME_NUM_SPEEDUPTHENBRAKE, startTime, endTime)
        harshSpeedBehaviorEvent.reset()
        be

      case DriveAtSuperHighSpeedEventType =>
        val be = BasicEventBean(EVENT_NAME_MILE_RUNATHIGHSPEED, startTime, endTime)
        be.columnValueMap.put("startMile", startMile)
        be.columnValueMap.put("endMile", endMile)

        superHighSpeedDriveEvent.reset()

        be

      case DriveAtEcoSpeedEventType =>
        val be = BasicEventBean(EVENT_NAME_MILE_RUNATECOSPEED, startTime, endTime)
        be.columnValueMap.put("startMile", startMile)
        be.columnValueMap.put("endMile", endMile)

        ecoSpeedDriveEvent.reset()

        be
    }

    eventContainer.append(be)
  }


  override def cleanUp(): Unit = {
    if (eventType != -1) {
      harshSpeedBehaviorEvent.endRow = previousItem
      println(s"0200数据发现事件$eventType\n ${harshSpeedBehaviorEvent.toString}")
      // TODO 保存该事件
      addSpeedControlDriveEvent(eventType)
      eventType = -1
    }

    if (superHighSpeedDriveEvent.startRow != null){
      superHighSpeedDriveEvent.endRow = previousItem
      addSpeedControlDriveEvent(SpeedControlBehaviorParser.DriveAtSuperHighSpeedEventType)
    }

    if (ecoSpeedDriveEvent.startRow != null) {
      ecoSpeedDriveEvent.endRow = previousItem
      addSpeedControlDriveEvent(SpeedControlBehaviorParser.DriveAtEcoSpeedEventType)
    }
  }

  override def outputAllEvents(): Iterator[BasicEventBean] = {
    eventContainer.toIterator
  }
}

object SpeedControlBehaviorParser {

  val HarshSpeedUpEventType = 0
  val HarshSpeedDownEventType = 1
  val SpeedUpThenBrakeEventType = 3

  val DriveAtSuperHighSpeedEventType = 10
  val DriveAtEcoSpeedEventType = 11

}