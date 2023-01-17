package com.dfssi.dataplatform.analysis.dbha.behavior.together
import com.dfssi.dataplatform.analysis.dbha.bean.BasicEventBean
import com.dfssi.dataplatform.analysis.dbha.behavior.BehaviorEvent
import com.dfssi.dataplatform.analysis.dbha.behavior.terminal0705.DiscreteSignalEventParser
import org.apache.spark.sql.Row
import org.junit.Test

import scala.collection.mutable.ListBuffer

/**
  * 先离合后刹车：速度>30km/h,刹车信号和离合信号同时出现，正常是先出现刹车信号再出现离合信号
  * 刹车（长时间刹车、刹车里程/时长）：刹车连续踩踏时间>20s，不超过2秒的间隔也算
  * 行车未放手刹：速度大于0，手刹信号同时出现
  *
  * 注：离合器开关 脚刹 手刹 属于同一个CAN ID
  */
class BrakeSystemControlBehaviorParser(paramsMap: Map[String, String], discreteSignalEventParser: DiscreteSignalEventParser)
    extends ParseBehaviorEventTogether(discreteSignalEventParser) {

  private val eventContainer = ListBuffer[BasicEventBean]()

  private val handBrakeOnPeriods = discreteSignalEventParser.handBrakeOnPeriods // ListBuffer[TimePeriod]()
  private val footBrakeOnPeriods = discreteSignalEventParser.footBrakeOnPeriods // ListBuffer[TimePeriod]()
  private val clutchOnPeriods = discreteSignalEventParser.clutchOnPeriods //ListBuffer[TimePeriod]()

  private val drivingPeriods = ListBuffer[TimePeriod]()
  private val drivingInClutchBeforeBrakeEventPeriods = ListBuffer[TimePeriod]()

  private val max0705EventDurationMillis = paramsMap
    .getOrElse("max0705EventDuration", "5")
    .toFloat * 1000
  private val max0200EventDurationMillis = paramsMap
    .getOrElse("max0200EventDuration", "5")
    .toFloat * 1000
  private val minSpeedInClutchBeforeBrake = paramsMap
    .getOrElse("minSpeedInClutchBeforeBrake", "30")
    .toFloat * 10

  private val minLongFootBrakeDurationMillis = paramsMap.getOrElse("minLongFootBrakeDuration", "20").toFloat * 1000

  @Test
  private var brakeEvent = new BehaviorEvent()
  @Test
  private val container = ListBuffer[BehaviorEvent]()
  @Test
  private var previous0200Row: Row = _

  import BrakeSystemControlBehaviorParser._

  override def parseMultipleRows2(rows1: Array[Row],
                                  rows2: Array[Row]): Unit = {
    // 处理0200数据
    rows1.foreach(produceDrivingSpeedPeriods)

    if (drivingPeriods.last.startTime == -1)
      drivingPeriods.-=(drivingPeriods.last)
    if (drivingInClutchBeforeBrakeEventPeriods.last.startTime == -1)
      drivingInClutchBeforeBrakeEventPeriods -= drivingInClutchBeforeBrakeEventPeriods.last

    // 处理0705数据
//    rows2.foreach { row =>
//      val signalName = getSignalName(row)
//      val signalValue = getSignalValue(row)
//
//      if (signalName.contains("离合") && signalValue > 0) {
//        produceClutchOnPeriods(row)
//      }
//      if (signalName.contains("脚刹") && signalValue > 0) {
//        produceFootBrakeOnPeriods(row)
//      }
//      if (signalName.contains("手刹") && signalValue > 0) {
//        produceHandBrakeOnPeriods(row)
//      }
//    }



    checkFootBrakeEvent(rows1)
    checkDrivingWithHandBrake()
    checkClutchBeforeBrakeEvent()
  }

  // 收集单个信号的持续时间
  private def produceDrivingSpeedPeriods(row: Row): Unit = {
    val speed = get0200Speed(row)
    val gpsTime = getGPSTime(row)

    // 行车带手刹的速度要求
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

    // 先离合后刹车的速度要求
    if (speed < minSpeedInClutchBeforeBrake) {
      if (drivingInClutchBeforeBrakeEventPeriods.last.startTime != -1)
        drivingInClutchBeforeBrakeEventPeriods.append(new TimePeriod(-1))
    } else {
      if (drivingInClutchBeforeBrakeEventPeriods.last.startTime == -1)
        drivingInClutchBeforeBrakeEventPeriods.last.startTime = gpsTime
      else if (gpsTime - drivingInClutchBeforeBrakeEventPeriods.last.endTime > max0200EventDurationMillis) {
        drivingInClutchBeforeBrakeEventPeriods.append(new TimePeriod(gpsTime))
      }

      drivingInClutchBeforeBrakeEventPeriods.last.endTime = gpsTime
    }

  }

  private def produceFootBrakeOnPeriods(row: Row): Unit = {
    val canTs = getCaptureTime(row)

    if (footBrakeOnPeriods.isEmpty || canTs - footBrakeOnPeriods.last.endTime > max0705EventDurationMillis) {
      footBrakeOnPeriods.append(new TimePeriod(canTs))
      footBrakeOnPeriods.last.startRow = row
      footBrakeOnPeriods.last.endTime = footBrakeOnPeriods.last.startTime
      footBrakeOnPeriods.last.endRow = row
    } else {
      footBrakeOnPeriods.last.endTime = canTs
      footBrakeOnPeriods.last.endRow = row
    }
  }

  private def produceClutchOnPeriods(row: Row): Unit = {
    val canTs = getCaptureTime(row)

    if (clutchOnPeriods.isEmpty || canTs - clutchOnPeriods.last.endTime > max0705EventDurationMillis) {
      clutchOnPeriods.append(new TimePeriod(canTs))
      clutchOnPeriods.last.endTime = clutchOnPeriods.last.startTime
    } else {
      clutchOnPeriods.last.endTime = canTs
    }
  }

  private def produceHandBrakeOnPeriods(row: Row): Unit = {
    val canTs = getCaptureTime(row)

    if (handBrakeOnPeriods.isEmpty || canTs - handBrakeOnPeriods.last.endTime > max0705EventDurationMillis) {
      handBrakeOnPeriods.append(new TimePeriod(canTs))
      handBrakeOnPeriods.last.endTime = handBrakeOnPeriods.last.startTime
    } else {
      handBrakeOnPeriods.last.endTime = canTs
    }
  }


  // 识别相关事件
  /**
    * 识别事件：
    * 1. 长时间刹车
    * 2. 刹车总时长
    * 3. 刹车总里程
    * @param _0200Rows 0200数据
    */
  private def checkFootBrakeEvent(_0200Rows: Array[Row]): Unit = {
    println("长时间刹车")
    val longTimeBrakePeriods = footBrakeOnPeriods.filter(_.timeDiffMillis >= minLongFootBrakeDurationMillis)
    longTimeBrakePeriods.foreach(println)
    addEvent(LongBrakeEventType, longTimeBrakePeriods)

//    val totalBrakeDurationSeconds = footBrakeOnPeriods.map(_.timeDiffMillis).sum / 1000.0
//    println("刹车总时长为 " + totalBrakeDurationSeconds)
    addEvent(TotalBrakeTimeEventType, footBrakeOnPeriods)

    // 先获取每一段刹车前后时间最近的0200数据中的速度值
    var (i, j) = (0, 0)
    while (i < footBrakeOnPeriods.length && j < _0200Rows.length) {
      val currGpsTime = getGPSTime(_0200Rows(j))
      val nextGpsTime = if (j == _0200Rows.length) currGpsTime + 500 else getGPSTime(_0200Rows(j + 1))

      val footBrakeOnPeriod = footBrakeOnPeriods(i)

      if (footBrakeOnPeriod.endTime < currGpsTime) i += 1
      else {
        if (currGpsTime <= footBrakeOnPeriod.startTime && footBrakeOnPeriod.startTime <= nextGpsTime) {
            footBrakeOnPeriod.startRow = _0200Rows(j)
        }

        if (currGpsTime <= footBrakeOnPeriod.endTime && footBrakeOnPeriod.endTime <= nextGpsTime) {
            footBrakeOnPeriod.endRow = _0200Rows(j)
          i += 1
        }
        j += 1
      }
    }

    addEvent(TotalBrakeMileEventType, footBrakeOnPeriods)

    // 刹车总里程，利用平均速度计算，不够精准
//    val totalBrakeMileKm = footBrakeOnPeriods.map{ tp =>
//      val startSpeed = getGPSSpeed(tp.startRow)
//      val endSpeed = getGPSSpeed(tp.endRow)
//
//      val avgSpeed = (math.abs(endSpeed - startSpeed) / 36000.0) / 2 //((endTime - startTime + 500)/1000.0)  //km/h
//
//      avgSpeed * (tp.timeDiffMillis/1000.0)
//    }.sum
//    println("刹车总里程为 "+totalBrakeMileKm) //

  }


  @Test
  // 基于0200里面的制动信号计算
  private def checkBrakeOnPeriodsV2(_0200Row: Row): Unit = {
    if (getSignalStates(_0200Row).contains("制动信号")) {
      if (brakeEvent.startRow == null)
        brakeEvent.startRow = _0200Row
    }
    else if (brakeEvent.startRow != null) {
      brakeEvent.endRow = previous0200Row
      container.append(brakeEvent)
      brakeEvent = new BehaviorEvent()
    }

    previous0200Row = _0200Row
  }

  /**
    * 识别事件：先离合后刹车
    */
  private def checkClutchBeforeBrakeEvent(): Unit = {
    val temp = ListBuffer[TimePeriod]()

    var (brakeIdx, clutchIdx) = (0, 0)
    while (clutchIdx < clutchOnPeriods.length && brakeIdx < footBrakeOnPeriods.length) {

      val timeDiff = footBrakeOnPeriods(brakeIdx).startTime - clutchOnPeriods(clutchIdx).startTime
      if (timeDiff < 0) {
        brakeIdx += 1
      } else if (timeDiff >= 0 && timeDiff < max0705EventDurationMillis) {

        println("离合区间" +clutchOnPeriods(clutchIdx))
        println("刹车区间" +footBrakeOnPeriods(brakeIdx))

        val tp = new TimePeriod(clutchOnPeriods(clutchIdx).startTime)
        tp.endTime = footBrakeOnPeriods(brakeIdx).startTime
        temp.append(tp)

        brakeIdx += 1
        clutchIdx += 1
      } else {
        clutchIdx += 1
      }
    }

    var (i, j) = (0, 0)
    while (i < drivingInClutchBeforeBrakeEventPeriods.length && j < temp.length) {
      val speedPeriod = drivingInClutchBeforeBrakeEventPeriods(i)
      val clutchBeforeBrakePeriod = temp(j)

      if (clutchBeforeBrakePeriod.endTime < speedPeriod.startTime) {
        j += 1
      }
      else if (speedPeriod.endTime < clutchBeforeBrakePeriod.startTime) {
        i += 1
      }
      else {
        println("出现先离合后刹车")
        println("速度区间为 "+speedPeriod)
        println("先离合后刹车区间为 "+clutchBeforeBrakePeriod)
        addEvent(ClutchBeforeBrakeEventType, clutchBeforeBrakePeriod)
        j += 1
      }
    }

//    println("最后一个速度区间为 "+drivingInClutchBeforeBrakeEventPeriods.last)

//    while (j < temp.length) {
//      println(temp(j))
//      j += 1
//    }
  }

  /**
    * 识别事件：行车带手刹
    */
  private def checkDrivingWithHandBrake(): Unit = {
    var (brakeIdx, speedIdx) = (0, 0)

    while (brakeIdx < handBrakeOnPeriods.length && speedIdx < drivingPeriods.length) {
      val handBrakeOnPeriod = handBrakeOnPeriods(brakeIdx)
      val drivingPeriod = drivingPeriods(speedIdx)

      if (handBrakeOnPeriod.endTime < drivingPeriod.startTime) {
        brakeIdx += 1
      }
      else if (drivingPeriod.endTime < handBrakeOnPeriod.startTime) {
        speedIdx += 1
      }
      else {
        println("发现行车带手刹事件")
        println("速度区间 " +drivingPeriod)
        println("手刹区间" + handBrakeOnPeriod)
        addEvent(DrivingWithHandBrakeEventType, handBrakeOnPeriod)

        brakeIdx += 1
      }
    }

//    println("最后一个速度区间为 "+drivingPeriods.last)

//    while (brakeIdx < handBrakeOnPeriods.length) {
//      println("速度区间之外，发现行车带手刹事件")
//      println(handBrakeOnPeriods(brakeIdx))
//      brakeIdx += 1
//    }

  }

  private def addEvent(eventType: Int, tps: ListBuffer[TimePeriod]): Unit = {
    import com.dfssi.dataplatform.analysis.dbha.bean.EventBean._

    eventType match {
      case TotalBrakeTimeEventType =>
        val totalBrakeDurationSeconds = tps.map(_.timeDiffMillis).sum / 1000.0
        val be = BasicEventBean(EVENT_NAME_DURATION_BRAKE, -1, -1)
        be.columnValueMap.put("totalBrakeTime", totalBrakeDurationSeconds.toFloat)
        eventContainer.append(be)

      case TotalBrakeMileEventType =>
        val totalBrakeMileKm = tps.map{ tp =>
          val startSpeed = getGPSSpeed(tp.startRow)
          val endSpeed = getGPSSpeed(tp.endRow)

          val avgSpeed = (math.abs(endSpeed - startSpeed) / 36000.0) / 2 //((endTime - startTime + 500)/1000.0)  //km/h

          avgSpeed * (tp.timeDiffMillis/1000.0)
        }.sum
        val be = BasicEventBean(EVENT_NAME_MILE_BRAKE, -1, -1)
        be.columnValueMap.put("totalBrakeMile", totalBrakeMileKm.toFloat)
        eventContainer.append(be)

      case LongBrakeEventType =>
        tps.foreach{ tp =>
          eventContainer.append(BasicEventBean(EVENT_NAME_NUM_LONGBRAKE, tp.startTime, tp.endTime))
        }

      case ClutchBeforeBrakeEventType =>
        tps.foreach{ tp =>
          eventContainer.append(BasicEventBean(EVENT_NAME_NUM_CLUTCHBEFOREBRAKE, tp.startTime, tp.endTime))
        }

      case DrivingWithHandBrakeEventType =>
        tps.foreach{ tp =>
          eventContainer.append(BasicEventBean(EVENT_NAME_NUM_RUNWITHBRAKE, tp.startTime, tp.endTime))
        }
    }

  }

  private def addEvent(eventType: Int, tp: TimePeriod): Unit = {
    import com.dfssi.dataplatform.analysis.dbha.bean.EventBean._

    eventType match {
      case TotalBrakeTimeEventType =>
        val totalBrakeDurationSeconds = tp.timeDiffMillis / 1000.0
        val be = BasicEventBean(EVENT_NAME_DURATION_BRAKE, -1, -1)
        be.columnValueMap.put("totalBrakeTime", totalBrakeDurationSeconds.toFloat)
        eventContainer.append(be)

      case TotalBrakeMileEventType =>
        val totalBrakeMileKm = {
          val startSpeed = getGPSSpeed(tp.startRow)
          val endSpeed = getGPSSpeed(tp.endRow)

          val avgSpeed = (math.abs(endSpeed - startSpeed) / 36000.0) / 2 //((endTime - startTime + 500)/1000.0)  //km/h

          avgSpeed * (tp.timeDiffMillis/1000.0)
        }
        val be = BasicEventBean(EVENT_NAME_MILE_BRAKE, -1, -1)
        be.columnValueMap.put("totalBrakeMile", totalBrakeMileKm.toFloat)
        eventContainer.append(be)

      case LongBrakeEventType =>
          eventContainer.append(BasicEventBean(EVENT_NAME_NUM_LONGBRAKE, tp.startTime, tp.endTime))

      case ClutchBeforeBrakeEventType =>
          eventContainer.append(BasicEventBean(EVENT_NAME_NUM_CLUTCHBEFOREBRAKE, tp.startTime, tp.endTime))

      case DrivingWithHandBrakeEventType =>
          eventContainer.append(BasicEventBean(EVENT_NAME_NUM_RUNWITHBRAKE, tp.startTime, tp.endTime))
    }

  }

  override def outputAllEvents(): Iterator[BasicEventBean] =
    eventContainer.toIterator

  override def setUp(): Unit = {
    drivingPeriods.append(new TimePeriod(-1))
    drivingInClutchBeforeBrakeEventPeriods.append(new TimePeriod(-1))
  }

  override def cleanUp(): Unit = {
//    checkClutchBeforeBrake()
//    drivingPeriods.foreach(println)
//    drivingInClutchBeforeBrakeEventPeriods.foreach(println)
//    clutchOnPeriods.foreach(println)
  }
}


object BrakeSystemControlBehaviorParser {

  val TotalBrakeTimeEventType = 0
  val TotalBrakeMileEventType = 1
  val LongBrakeEventType = 2

  val ClutchBeforeBrakeEventType = 3

  val DrivingWithHandBrakeEventType = 4

}