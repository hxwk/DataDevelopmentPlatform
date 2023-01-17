package com.dfssi.dataplatform.analysis.dbha.behavior.terminal0200.st

import java.util.Calendar

import com.dfssi.dataplatform.analysis.dbha.bean.BasicEventBean
import com.dfssi.dataplatform.analysis.dbha.behavior.{BehaviorEvent, DriveBehaviorUtils}
import com.dfssi.dataplatform.analysis.dbha.behavior.terminal0200.ParseBehaviorEventBy0200
import org.apache.spark.sql.Row

import scala.collection.mutable.ListBuffer


/**
  * 夜间行车时长/里程
  * 总行驶时长/里程
  * 停车次数
  * 停车立即熄火
  * 熄火滑行
  *
  * @param paramsMap 参数
  */
class SpatioTemporalFeatureParser(paramsMap: Map[String, String]) extends ParseBehaviorEventBy0200 {

  // params
  val nightHourSet: Set[Int] = paramsMap.get("nightHours") match {
    case Some(x) => x.split(",").map(_.toInt).toSet
    case _       => DriveBehaviorUtils.DefaultNightHourSet
  }

  private val maxQuickStopToOffGapMillis = paramsMap.getOrElse("maxQuickStopToOffGap", "2").toFloat * 1000F

  // 连续两条记录的最大时差
  private val maxEventDurationMillis: Int = 30 * 1000 // 5秒

  // vars
  private val calendar = Calendar.getInstance()

  private var previousItem: Row = _
  private val vehicleMovingEvent: BehaviorEvent= new BehaviorEvent()
  private val vehicleStopEvent: BehaviorEvent= new BehaviorEvent()
  private val coastingWhileOffEvent = new BehaviorEvent()
//  private var vehicleStateType: Int = -1

  private val driveAtSpecialPeriodEvent: BehaviorEvent= new BehaviorEvent()

  private val eventContainer = ListBuffer[BasicEventBean]()


  import SpatioTemporalFeatureParser._


  // methods
  override def parseCurrentRow(row: Row): Unit = {

    checkCoastingWhileAccOff(row)

    if (previousItem != null) {
      val ts = getGPSTime(row)
      val prevT = getGPSTime(previousItem)
      if (prevT >= ts) {
//        logError(s"当前时间小于或等于前一条记录时间 \n $previousItem \n $row")
        previousItem = row
        return
      }

      val prevV = get0200Speed(previousItem)
      val v = get0200Speed(row)

      checkStopOrQuickOffEvent(prevV, v, prevT, ts, row)
      checkDrivingEvent(prevV, v, prevT, ts, row)
      checkDrivingAtNightEvent(prevV, v, prevT, ts, row)
    }

    previousItem = row
  }

  private def checkStopOrQuickOffEvent(prevV: Long, currentV: Long,
                                       prevT: Long, currentT: Long,
                                       currentRow: Row): Unit = {
    if (prevV == 0 && currentV == 0) {
      if (vehicleStopEvent.startRow == null) {
        vehicleStopEvent.startRow = previousItem
        vehicleStopEvent.endRow = currentRow

        val states = getSignalStates(currentRow)
        val preStates = getSignalStates(previousItem)
        if (preStates.contains("ACC 关")) {
          println(s"发现停车立即熄火事件， $previousItem")
          addEvent(QuickStopToOffEventType)
        }
        else if (currentT - prevT <= maxQuickStopToOffGapMillis && states.contains("ACC 关")) {
          println(s"发现停车立即熄火事件， 从$previousItem 到 $currentRow \n")
          addEvent(QuickStopToOffEventType)
        }

//        vehicleStateType = VehicleStopEventType
      }
      else vehicleStopEvent.endRow = previousItem
    }
    else if (vehicleStopEvent.startRow != null) {
      if (prevV == 0 && currentV > 0) vehicleStopEvent.endRow = previousItem
      addEvent(VehicleStopEventType)
//      vehicleStateType = -1
    }
  }

  private def checkDrivingEvent(prevV: Long, currentV: Long,
                                prevT: Long, currentT: Long,
                                currentRow: Row): Unit = {
    // 当前停车状态
    if (prevV == 0 && currentV == 0) {
        if (vehicleMovingEvent.startRow != null) {
          //val beforePrevT = getGPSTime(vehicleMovingEvent.endRow)
          //if (prevT - beforePrevT < maxEventDurationMillis)
          assert(vehicleMovingEvent.endRow != null)
          addEvent(VehicleMovingEventType)
//          vehicleStateType = -1
        }

      return
    }

    // 当前行驶状态
    if (vehicleMovingEvent.startRow == null){
        if (prevV == 0 && currentV > 0) {
            vehicleMovingEvent.startRow = currentRow
            vehicleMovingEvent.endRow = currentRow
        }
        else if (prevV > 0 && currentV == 0)  {
          vehicleMovingEvent.startRow = previousItem
          vehicleMovingEvent.endRow = previousItem
        }
        else {
          vehicleMovingEvent.startRow = previousItem
          vehicleMovingEvent.endRow = currentRow
        }

//      vehicleStateType = VehicleMovingEventType
    }
    else vehicleMovingEvent.endRow = previousItem

  }

  private def checkDrivingAtNightEvent(prevV: Long, currentV: Long,
                                prevT: Long, currentT: Long,
                                currentRow: Row): Unit = {
    // TODO 短时间（一分钟内的）夜间行驶里程，需要根据连续两点的速度、时间计算大概的里程
    if (vehicleMovingEvent.startRow == null) {
      if (driveAtSpecialPeriodEvent.startRow != null) {
        driveAtSpecialPeriodEvent.endRow = previousItem //vehicleMovingEvent.endRow //
        println(s"存在夜间行车，从${driveAtSpecialPeriodEvent.startRow} 到 ${driveAtSpecialPeriodEvent.endRow} \n")
        addEvent(DriveAtNightEventType)
      }
    }
    else {
      val ts = if (prevV > 0) prevT else currentT

      calendar.setTimeInMillis(ts)
      val hour = calendar.get(Calendar.HOUR_OF_DAY)

      if(DriveBehaviorUtils.isNightHour(hour, nightHourSet)) {
        if (driveAtSpecialPeriodEvent.startRow == null) {
          driveAtSpecialPeriodEvent.startRow = if (prevV > 0) previousItem else currentRow
        }
      }
      else if (driveAtSpecialPeriodEvent.startRow != null) {
        driveAtSpecialPeriodEvent.endRow = previousItem //vehicleMovingEvent.endRow //
        println(s"存在夜间行车，从${driveAtSpecialPeriodEvent.startRow} 到 ${driveAtSpecialPeriodEvent.endRow} \n")
        println(s"行驶中的最后一条为 是 ${vehicleMovingEvent.endRow} \n")
        addEvent(DriveAtNightEventType)
      }
    }
  }

  private def checkCoastingWhileAccOff(currentRow: Row): Unit = {
    val v = get0200Speed(currentRow)
    if (v > 0) {
      val states = getSignalStates(currentRow)
      if (states.contains("ACC 关")) {
        if (coastingWhileOffEvent.startRow == null) {
          coastingWhileOffEvent.startRow = currentRow
        }
      }
      else if (coastingWhileOffEvent.startRow != null) {
        coastingWhileOffEvent.endRow = previousItem
        addEvent(CoastingWhileAccOffEventType)
      }
    }

  }


  override def outputAllEvents(): Iterator[BasicEventBean] = eventContainer.toIterator

  override def setUp(): Unit = {}

  override def cleanUp(): Unit = {
    if (vehicleMovingEvent.startRow != null) {
      logWarning("本段行程结束时的速度不为0")
      vehicleMovingEvent.endRow = previousItem
      addEvent(VehicleMovingEventType)
    }

    if (vehicleStopEvent.startRow != null) {
      logWarning("本段行程结束时的速度不为0")
      vehicleStopEvent.endRow = previousItem
      addEvent(VehicleStopEventType)
    }

    if (driveAtSpecialPeriodEvent.startRow != null) {
      driveAtSpecialPeriodEvent.endRow = previousItem
      addEvent(DriveAtNightEventType)
    }
  }

  private def addEvent(eventType: Int): Unit = {
    import SpatioTemporalFeatureParser._
    import com.dfssi.dataplatform.analysis.dbha.bean.EventBean._

    eventType match {
      case VehicleMovingEventType =>
//        val be = BasicEvent(EVENT_NAME_DRIVING,
//          getGPSTime(vehicleMovingEvent.startRow),
//          getGPSTime(vehicleMovingEvent.endRow))
//        eventContainer.append(be)
//        println(s"存在持续行驶，从${vehicleMovingOrStopEvent.startRow} 到 ${vehicleMovingOrStopEvent.endRow} \n")
        vehicleMovingEvent.reset()

      case VehicleStopEventType =>
        val be = BasicEventBean(EVENT_NAME_NUM_STOP,
        getGPSTime(vehicleStopEvent.startRow),
        getGPSTime(vehicleStopEvent.endRow))
        eventContainer.append(be)
        println(s"存在停车行为，从${vehicleStopEvent.startRow} 到 ${vehicleStopEvent.endRow} \n")
        vehicleStopEvent.reset()

      case DriveAtNightEventType =>
        val be2 = BasicEventBean(EVENT_NAME_MILE_RUNATNIGHT,
          getGPSTime(driveAtSpecialPeriodEvent.startRow),
          getGPSTime(driveAtSpecialPeriodEvent.endRow))

        val startMile = getMile(driveAtSpecialPeriodEvent.startRow)
        val endMile = getMile(driveAtSpecialPeriodEvent.endRow)
//        startMile.getClass.getSimpleName
        be2.columnValueMap.put("startMile", startMile)
        be2.columnValueMap.put("endMile", endMile)
//        println(be2.toString)

        eventContainer.append(be2)
        driveAtSpecialPeriodEvent.reset()

      case QuickStopToOffEventType =>
        val be = BasicEventBean(EVENT_NAME_NUM_SHUTDOWNONCESTOP,
          getGPSTime(vehicleStopEvent.startRow),
          getGPSTime(vehicleStopEvent.endRow))

        eventContainer.append(be)

      case CoastingWhileAccOffEventType =>
        println(s"发现熄火滑行从${coastingWhileOffEvent.startRow} 到 ${coastingWhileOffEvent.endRow}")
        val be = BasicEventBean(EVENT_NAME_NUM_SHUTDOWNANDCOASTING,
          getGPSTime(coastingWhileOffEvent.startRow),
          getGPSTime(coastingWhileOffEvent.endRow))

        val startMile = getMile(coastingWhileOffEvent.startRow)
        val endMile = getMile(coastingWhileOffEvent.endRow)
        //        startMile.getClass.getSimpleName
        be.columnValueMap.put("startMile", startMile)
        be.columnValueMap.put("endMile", endMile)

        eventContainer.append(be)
        coastingWhileOffEvent.reset()
    }

  }

}


object SpatioTemporalFeatureParser {

  val VehicleMovingEventType = 0
  val VehicleStopEventType = 1
  val DriveAtNightEventType = 2

  val QuickStopToOffEventType = 3

  val CoastingWhileAccOffEventType = 4

}