package com.dfssi.dataplatform.analysis.dbha.behavior.together

import com.dfssi.dataplatform.analysis.dbha.bean.BasicEventBean
import com.dfssi.dataplatform.analysis.dbha.behavior.terminal0705.DiscreteSignalEventParser
import org.apache.spark.sql.Row

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * 发动机相关的驾驶行为：
  * 高转速起步：上一次速度=0,下次速度>0,转速>2000,连续时间超过2秒
  * 超长怠速：速度=0，转速>0,持续时长>60s，空调非开启模式(可配置)
  * 怠速空调：速度=0，转速>0,持续时长>60s，空调开启模式(可配置)
  * 冷车行驶：发动机冷却液温度<45度,速度>20km/h，持续时间>30s
  * 经济负荷行驶里程/时长：按车型出厂的标定经济负荷进行计算里程
  *
  *
  * @param paramsMap 配置参数
  * @param discreteSignalEventParser  0705离散信号区间解析类实例
  */
class EngineRelatedBehaviorParser(paramsMap: Map[String, String], discreteSignalEventParser: DiscreteSignalEventParser)
  extends ParseBehaviorEventTogether(discreteSignalEventParser){

  private val minSpeedWhileColdDrive = paramsMap.getOrElse("minSpeedWhileColdDrive", "10").toFloat * 10
  private val maxEngineTemp = paramsMap.getOrElse("maxEngineTemperature", "45").toDouble
  private val minColdDriveDurationMillis = paramsMap.getOrElse("minColdDriveDuration", "30").toFloat * 1000

  private val minHighRpm = paramsMap.getOrElse("minHighRpm", "1700").toFloat
  private val highRpmStartDurationMillis = paramsMap.getOrElse("highRpmStartDuration", "2").toFloat * 1000

  private val minLongIdleDurationMillis = paramsMap.getOrElse("minLongIdleDuration", "30").toFloat * 1000

  private val max0705EventDurationMillis = paramsMap
    .getOrElse("max0705EventDuration", "5")
    .toFloat * 1000

  private val max0200EventDurationMillis = paramsMap
    .getOrElse("max0200EventDuration", "5")
    .toFloat * 1000

  private val eventContainer = ArrayBuffer[BasicEventBean]()

  private val zeroSpeedRangeIdx = ListBuffer[Int]()
  private val rpmOnRangeIdx = ListBuffer[Int]()
  private val highRpmRangeIdx = ListBuffer[Int]()
  private val coldDriveSpeedRangeIdx = ListBuffer[Int]()
  private val ecoDriveLoadRangeIdx = ListBuffer[Int]()
  private val coldEngineRangeIdx = ListBuffer[Int]()

  import EngineRelatedBehaviorParser._

  override def parseMultipleRows2(rows1: Array[Row], rows2: Array[Row]): Unit = {

    produce0200SpeedRanges(rows1)
    produce0705SignalRanges(rows2)

//    println("0速度区间")
//    zeroSpeedRangeIdx.foreach{ idx =>
//      println(rows1(idx))
//    }

//    println("有转速区间")
//    rpmOnRangeIdx.foreach{ idx =>
//      println(rows2(idx))
//    }

//    println("高转速区间有")
//    highRpmRangeIdx.indices.foreach{ idx =>
//      println(rows2(highRpmRangeIdx(idx)))
//    }

//    println("冷车行驶温度区间")
//    println(coldEngineRangeIdx.mkString(","))
//    coldEngineRangeIdx.foreach{ idx =>
//      println(rows2(idx))
//    }

//    println("冷车行驶速度区间")
//    coldDriveSpeedRangeIdx.foreach{ idx =>
//      println(rows1(idx))
//    }

    checkStartMoveWithHighRpmEvent(rows1, rows2)

    checkDriveWithColdEngineEvent(rows1, rows2)

    checkEngineIdleEvent(rows1, rows2)


  }

  private def produce0200SpeedRanges(_0200Rows: Array[Row]): Unit = {
    var i = 0
    while (i < _0200Rows.length) {
      val speed = get0200Speed(_0200Rows(i))

      if (speed == 0) {
        if (i == 0|| i == _0200Rows.length || get0200Speed(_0200Rows(i-1)) > 0)
          zeroSpeedRangeIdx.append(i)
        else {
          val prevRow = _0200Rows(i - 1)
          if (get0200Speed(prevRow) == 0 && getGPSTime(_0200Rows(i)) - getGPSTime(prevRow) > max0200EventDurationMillis) {
            zeroSpeedRangeIdx.append(i-1)
            zeroSpeedRangeIdx.append(i)
          }
        }
      }
      else {
        if (i > 0 && get0200Speed(_0200Rows(i-1)) == 0) zeroSpeedRangeIdx.append(i-1) //终点
      }

      if (speed >= minSpeedWhileColdDrive) {
        if (i == 0 || i == _0200Rows.length || get0200Speed(_0200Rows(i-1)) < minSpeedWhileColdDrive)
          coldDriveSpeedRangeIdx.append(i)
        else {
          val prevRow = _0200Rows(i - 1)
          if (get0200Speed(prevRow) >= minSpeedWhileColdDrive && getGPSTime(_0200Rows(i)) - getGPSTime(prevRow) > max0200EventDurationMillis) {
            coldDriveSpeedRangeIdx.append(i-1)
            coldDriveSpeedRangeIdx.append(i)
          }
        }
      }
      else if (i > 0 && get0200Speed(_0200Rows(i-1)) >= minSpeedWhileColdDrive)
        coldDriveSpeedRangeIdx.append(i-1)

      i += 1
    }

    if (i > 0) {
      if (get0200Speed(_0200Rows(i-1)) == 0) zeroSpeedRangeIdx.append(i-1)
      if (get0200Speed(_0200Rows(i-1)) >= minSpeedWhileColdDrive) coldDriveSpeedRangeIdx.append(i - 1)
    }
  }

  private def produce0705SignalRanges(_0705Rows: Array[Row]): Unit = {
    import EngineRelatedBehaviorParser.eps
    var (prevRpmIdx, prevLoadIdx, prevTempIdx) = (-1, -1, -1)

    _0705Rows.indices.foreach{ i =>
      val row = _0705Rows(i)

      val signalName = getSignalName(row)
      val signalValue = getSignalValue(row)
      val receiveTime = getCaptureTime(row)

      // 记录转速大于0 和 高转速 区间
      if (signalName.contains("发动机转速")) {
        val prevRpm = if (-1 == prevRpmIdx) -1.0 else getSignalValue(_0705Rows(prevRpmIdx))

        if (signalValue > eps) {
          if (i == 0 || i == _0705Rows.length || prevRpm <= eps || -1 == prevRpmIdx)
            rpmOnRangeIdx.append(i)
          else {
            val prevRpmTime = getCaptureTime(_0705Rows(prevRpmIdx))
            if (prevRpm > eps && receiveTime - prevRpmTime > max0705EventDurationMillis){
              rpmOnRangeIdx.append(prevRpmIdx)
              rpmOnRangeIdx.append(i)
            }
          }
        }
        else {
          if (i > 0 && prevRpm > eps) rpmOnRangeIdx.append(prevRpmIdx)
        }

        // 高转速 区间
        if (signalValue >= minHighRpm) {
          if (i == 0 || i == _0705Rows.length || -1 == prevRpmIdx) highRpmRangeIdx.append(i)
          else {
            val prevRpmTime = getCaptureTime(_0705Rows(prevRpmIdx))
            if (prevRpm < minHighRpm) highRpmRangeIdx.append(i)
            else if (receiveTime - prevRpmTime > max0705EventDurationMillis) {
              highRpmRangeIdx.append(prevRpmIdx)
              highRpmRangeIdx.append(i)
            }
          }
        }
        else {
          if (i > 0  && prevRpmIdx != -1 && prevRpm >= minHighRpm)
            highRpmRangeIdx.append(prevRpmIdx)
        }

        prevRpmIdx = i
      }

      // TODO 记录经济负荷区间，需要分析师给定一个经济负荷范围
//      if (signalName.contains("负荷率")) {
//        val prevLoad = if (-1 == prevLoadIdx) -1 else getSignalValue(_0705Rows(prevLoadIdx))
//
//        if (true) {
//          if (i == 0 || i == _0705Rows.length) ecoDriveLoadRangeIdx.append(i)
//          else {
//            val prevLoadTime = getCaptureTime(_0705Rows(prevLoadIdx))
//            if (receiveTime - prevLoadTime > max0705EventDurationMillis) {
//
//            }
//            if (i > 0) ecoDriveLoadRangeIdx.append(prevLoadIdx)
//          }
//        }
//
//        prevLoadIdx = i
//      }

      // 记录低温区间
      if(signalName.contains("水温")) {
        val prevTemp = if (-1 == prevTempIdx) -1 else getSignalValue(_0705Rows(prevTempIdx))

        if (signalValue <= maxEngineTemp) {
          if (i == 0 || i == _0705Rows.length || prevTempIdx == -1)
            coldEngineRangeIdx.append(i)
          else {
            val prevTempTime =  getCaptureTime(_0705Rows(prevTempIdx))
            if (prevTemp > maxEngineTemp) coldEngineRangeIdx.append(i)
            else if (receiveTime - prevTempTime > max0705EventDurationMillis) {
              coldEngineRangeIdx.append(prevTempIdx)
              coldEngineRangeIdx.append(i)
            }
          }
        }
        else {
          if (i > 0  && prevTempIdx != -1 && prevTemp <= maxEngineTemp)
            coldEngineRangeIdx.append(prevTempIdx)
        }

        prevTempIdx = i
      }
    }

    // 处理最后一个点是一段区间起点的情况
    val prevRpm = if (-1 == prevRpmIdx) 0 else getSignalValue(_0705Rows(prevRpmIdx))
    if (prevRpm > eps) rpmOnRangeIdx.append(prevRpmIdx)
    if (prevRpm > minHighRpm) highRpmRangeIdx.append(prevRpmIdx)


    val prevTemp = if (-1 == prevTempIdx) maxEngineTemp + 1 else getSignalValue(_0705Rows(prevTempIdx))
    if (prevTemp <=  maxEngineTemp) coldEngineRangeIdx.append(prevTempIdx)

  }

  private def checkStartMoveWithHighRpmEvent(_0200Rows: Array[Row], _0705Rows: Array[Row]): Unit = {
    var (i, j) = (0, 0)
    while (i < zeroSpeedRangeIdx.length / 2 && j < highRpmRangeIdx.length - 1) {
      // 上一次速度为0的下标为 2 * i + 1
      val lastZeroSpeedIdx = 2 * i + 1
      val gpsTime =getGPSTime(_0200Rows(zeroSpeedRangeIdx(lastZeroSpeedIdx)))
//      val nextNoneZero

      val highRpmStartRow = _0705Rows(highRpmRangeIdx(j))
//      val highRpmEndRow = _0705Rows(highRpmRangeIdx(j+1))

      val startReceiveTime = getCaptureTime(highRpmStartRow)

      if (math.abs(gpsTime - startReceiveTime) <=  2000) { // 固定两秒内
//        val endReceiveTime = getCaptureTime(highRpmEndRow)

//        if (endReceiveTime - startReceiveTime >= highRpmStartDurationMillis) {
          println("发现高转速起步")
          println("上一次为0的记录为：" +_0200Rows(zeroSpeedRangeIdx(lastZeroSpeedIdx)))
          println("下一次不为0的记录为：" +_0200Rows(zeroSpeedRangeIdx(lastZeroSpeedIdx)+1))
          println("发动机转速为："+highRpmStartRow)

          addEvent(StartMoveWithHighRpmEventType, gpsTime, startReceiveTime)
//        }
      }

      if (gpsTime < startReceiveTime) i += 1
      else j += 1

    }
  }

  private def checkDriveWithColdEngineEvent(_0200Rows: Array[Row], _0705Rows: Array[Row]): Unit = {
    var (speedIdx, engineTempIdx) = (0, 0)

    while (speedIdx < coldDriveSpeedRangeIdx.length/2 && engineTempIdx < coldEngineRangeIdx.length/2) {
      val startSpeedRow = _0200Rows(coldDriveSpeedRangeIdx(2 * speedIdx))
      val endSpeedRow = _0200Rows(coldDriveSpeedRangeIdx(2 * speedIdx + 1))

      val startColdEngineRow = _0705Rows(coldEngineRangeIdx(2*engineTempIdx))
      val endColdEngineRow = _0705Rows(coldEngineRangeIdx(2*engineTempIdx + 1))

      if (getGPSTime(endSpeedRow) < getCaptureTime(startColdEngineRow)) speedIdx += 1
      else if (getCaptureTime(endColdEngineRow) < getGPSTime(startSpeedRow)) engineTempIdx += 1
      else {
        val coldDriveRangeStartTime = math.max(getGPSTime(startSpeedRow), getCaptureTime(startColdEngineRow))
        val coldDriveRangeEndTime = math.min(getGPSTime(endSpeedRow), getCaptureTime(endColdEngineRow))

        if (coldDriveRangeEndTime - coldDriveRangeStartTime >= minColdDriveDurationMillis) { //
          println("发现冷车行驶")
          println(s"$startColdEngineRow \n $endColdEngineRow")
          println(s"$startSpeedRow \n $endSpeedRow")

          addEvent(DriveInColdStateEventType, coldDriveRangeStartTime, coldDriveRangeEndTime)

        }

        speedIdx += 1
        engineTempIdx += 1
      }

    }
  }

  // 速度=0，转速>0,持续时长>60s
  private def checkEngineIdleEvent(_0200Rows: Array[Row], _0705Rows: Array[Row]): Unit = {
    var (speedIdx, engineIdx, airOnIdx) = (0, 0, 0)

    while (speedIdx < zeroSpeedRangeIdx.length / 2 && engineIdx < rpmOnRangeIdx.length / 2) {
      val speedStartRow = _0200Rows(zeroSpeedRangeIdx(2 * speedIdx))
      val speedEndRow = _0200Rows(zeroSpeedRangeIdx(2 * speedIdx + 1))

      val engineStartRow = _0705Rows(rpmOnRangeIdx(2*engineIdx))
      val engineEndRow = _0705Rows(rpmOnRangeIdx(2*engineIdx + 1))

      if (getGPSTime(speedEndRow) < getCaptureTime(engineStartRow)) speedIdx += 1
      else if (getGPSTime(speedStartRow) > getCaptureTime(engineEndRow)) engineIdx += 1
      else {
        val longIdleRangeStartTime = math.max(getGPSTime(speedStartRow), getCaptureTime(engineStartRow))
        val longIdleRangeEndTime = math.min(getGPSTime(speedEndRow), getCaptureTime(engineEndRow))

        if (longIdleRangeEndTime - longIdleRangeStartTime >= minLongIdleDurationMillis) { //minColdDriveDurationMillis
          println("发现超长怠速事件")
          println(s"$engineStartRow \n $engineEndRow")
          println(s"$speedStartRow \n $speedEndRow")

          addEvent(LongEngineIdleEventType, longIdleRangeStartTime, longIdleRangeEndTime)

          // 继续检测怠速空调事件
          while (airOnIdx < discreteSignalEventParser.airOnPeriods.length && discreteSignalEventParser.airOnPeriods(airOnIdx).endTime < longIdleRangeStartTime)
            airOnIdx += 1

          if (airOnIdx < discreteSignalEventParser.airOnPeriods.length && longIdleRangeEndTime >= discreteSignalEventParser.airOnPeriods(airOnIdx).startTime) {
            println("发现怠速空调")
            println(s"${discreteSignalEventParser.airOnPeriods(airOnIdx)}")

            val longIdleAirConditionerEventSt = math.max(discreteSignalEventParser.airOnPeriods(airOnIdx).startTime, longIdleRangeStartTime)
            val longIdelAirConditionerEventEt = math.min(discreteSignalEventParser.airOnPeriods(airOnIdx).endTime, longIdleRangeEndTime)

            addEvent(IdleAirConditionerEventType, longIdleAirConditionerEventSt, longIdelAirConditionerEventEt)
          }
        }

        speedIdx += 1
        engineIdx += 1
      }
    }

  }

  override def outputAllEvents(): Iterator[BasicEventBean] = eventContainer.toIterator

  override def setUp(): Unit = {}

  override def cleanUp(): Unit = {}

  private def addEvent(eventType: Int, startTime: Long, endTime: Long): Unit = {
    import com.dfssi.dataplatform.analysis.dbha.bean.EventBean._

    eventType match {
      case StartMoveWithHighRpmEventType =>
        val be = BasicEventBean(EVENT_NAME_NUM_STARTATHIGHROTATION, startTime, endTime)
        eventContainer.append(be)

      case LongEngineIdleEventType =>
        val be = BasicEventBean(EVENT_NAME_NUM_LONGIDLE, startTime, endTime)
        eventContainer.append(be)

      case IdleAirConditionerEventType =>
        val be = BasicEventBean(EVENT_NAME_NUM_IDLEDAIRCONDITIONER, startTime, endTime)
        eventContainer.append(be)

      case DriveInColdStateEventType =>
        val be = BasicEventBean(EVENT_NAME_NUM_RUNATCOLDMOTOR, startTime, endTime)
        eventContainer.append(be)

      case DriveInEcoEngineLoadEventType =>
        val be = BasicEventBean(EVENT_NAME_DURATION_RUNATECOLOAD, startTime, endTime)
//        be.columnValueMap.put()
        // TODO 经济负荷行驶时长、里程
        eventContainer.append(be)

    }
  }

}

object EngineRelatedBehaviorParser {

  val StartMoveWithHighRpmEventType = 1
  val LongEngineIdleEventType = 2
  val IdleAirConditionerEventType = 3
  val DriveInColdStateEventType = 4
  val DriveInEcoEngineLoadEventType = 5

  val eps = 1e-2
}