package com.dfssi.dataplatform.analysis.dbha

import java.sql.Timestamp

import com.dfssi.dataplatform.analysis.dbha.bean.{EventBean, IndicatorBean}
import com.dfssi.dataplatform.analysis.dbha.driverbehavior.local.ProtocolDataProcess
import com.dfssi.dataplatform.analysis.dbha.driverbehavior.rdd.ProtocolRddProcess
import org.apache.spark.Logging
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * 单行程处理
  * @param vehicleId  车辆id
  * @param paramsMap  配置参数
  * @author lulin @ 2018/02/12
  */
class SingleTripProcess(vehicleId: String, paramsMap: Map[String, String]) extends Serializable with Logging {

  /** 从paramsMap获取全局参数 */
  // 采集频率，每秒多少条
  private val _0200CaptureTimesPerSec = paramsMap.getOrElse("0200CaptureTimesPerSec", "0.25").toFloat
  private val _0705CaptureTimesPerSec = paramsMap.getOrElse("0705CaptureTimesPerSec", "0.5").toFloat
  // 连续两条采集记录的时间间隔，单位为毫秒
  private val millisGapBetween2Same0200Records = 1000 / _0200CaptureTimesPerSec
  private val millisGapBetween2Same0705Records = 1000 / _0705CaptureTimesPerSec

  // 一般短时事件包含的连续记录之间的最大时间间隔，默认2s
  private val maxMillisIntervalPerEvent = paramsMap.getOrElse("maxIntervalPerEvent", "4.5").toFloat * 1000

  // 初始化事件容器和指标容器
  var tripEventsBean: EventBean = _ //new EventBean()
  var tripIndicatorsBean: IndicatorBean = _ //new IndicatorBean()

  import EventBean._
  import SingleTripProcess._
  import com.dfssi.dataplatform.analysis.dbha.DriveBehaviorUtils._


  @DeveloperApi
  def hasNonEmptyIndicators: Boolean = {
    tripIndicatorsBean.tripId != null
  }

  def resetBeans(): Unit = {
    tripEventsBean.reset()
    tripIndicatorsBean.reset()
  }

  /**
    * 处理一段行程入口
    * @param tripId  行程id
    * @param tripEndTime 行程结束时间，长整型精确到毫秒
    * @param terminal_0200_dvrRdd 行车记录仪数据
    * @param terminal_0705_canRdd  CAN报文协议数据
    */
  def tripProcess(driverId: String,
                    tripId: String,
                    tripEndTime: Long,  // MILLIS
                    totalMile: Double,  // 0.1 KM
                    terminal_0200_dvrRdd: RDD[Row],
                    terminal_0705_canRdd: RDD[Row]): Boolean = {
    /**********************************************************************/
    // debug
    val startRunTime = System.currentTimeMillis()
    logError("开始处理该段行程，开始时间："+ startRunTime)
    /**********************************************************************/
    // init other params
    tripEventsBean = new EventBean(tripId)
    tripIndicatorsBean = new IndicatorBean()
    tripIndicatorsBean.tripId = tripId
    tripIndicatorsBean.tripEndTime = new Timestamp(tripEndTime)
    tripIndicatorsBean.vehicleId = vehicleId
//    tripIndicatorsBean.driverId = driverId

    val protocolRddProcess = new ProtocolRddProcess( paramsMap,
                                                      _0200CaptureTimesPerSec,
                                                      millisGapBetween2Same0200Records,
                                                      _0705CaptureTimesPerSec,
                                                      millisGapBetween2Same0705Records,
                                                      maxMillisIntervalPerEvent)
    // 先把0200中会用到的数据抽取出来
    val valid0200Data = collectValidDvrData(terminal_0200_dvrRdd)

    if(valid0200Data.length < 2) return false

    // 告警消息处理
    val alarmEventCounts = protocolRddProcess.extractAlarmEvents_1(terminal_0200_dvrRdd)
    computeAlarmIndicatorsFrom0200Only(alarmEventCounts, totalMile)

    val res = new ProtocolDataProcess(paramsMap,
      _0200CaptureTimesPerSec,
      millisGapBetween2Same0200Records,
      _0705CaptureTimesPerSec,
      millisGapBetween2Same0705Records,
      maxMillisIntervalPerEvent).extractEventsFrom0200Data_1(valid0200Data)
    /* 计算如下指标：improperSpeedAndTurnEventCountArr
                        cumulateDeltaStatAtNightArr(时长（秒），里程（0.1KM）)
                        coastingWithEngineOffCount
                        cumulateDeltaEcoDriveStatArr
                        stopEventCount
                        totalDrivingDuration（秒）
                        speedStabilityCount
                        zeroSpeedRanges
                        improperTurnEventCounter
                        cumulateHighSpeedDriveStatArr*/
    computeIndicatorsFrom0200Only(res, totalMile)

    // 夜间行驶指标
    computeNightDriveIndicators(res.cumulateDriveAtNightStatArr(0), res.cumulateDriveAtNightStatArr(1), totalMile)

    /** 抽取会复用的一些信号量，比如广播0速度、发动机转速、油门踏板开度等 */
    // 缓存发动机转速
    val _0705RpmTsRdd = smoothByValueAvg(terminal_0705_canRdd.filter(_.getAs[String](Field_SignalName).contains("发动机转速"))
      .map(f => f.getAs[Long](Field_CAN_Time)->f.getAs[Double](Field_Value))).cache()

    // 缓存油门踏板开度
    val _0705ThrottleOpeningTsRdd = smoothByValueAvg(terminal_0705_canRdd.filter(_.getAs[String](Field_SignalName).contains("油门踏板"))
      .map(f => f.getAs[Long](Field_CAN_Time) -> f.getAs[Double](Field_Value))).cache()

    // 大油门行驶次数、满油行驶次数、大油门行驶时间（秒）
    val driveAtHighThrottleOpenEvents = protocolRddProcess.extractDriveAtHighThrottleOpenEvents_3(valid0200Data, _0705ThrottleOpeningTsRdd)
    computeHTOIndicators(driveAtHighThrottleOpenEvents, totalMile)

    // 空挡滑行次数
    val coastingEventCount = protocolRddProcess.extractCoastingWithNullPosEvent_4(valid0200Data, _0705RpmTsRdd, _0705ThrottleOpeningTsRdd, terminal_0705_canRdd)
    computeCoastingIndicators(coastingEventCount, totalMile)

    // 先离合后刹车 ……
    val clutchAndBrakeEvents = protocolRddProcess.extractClutchAndBrakeEvents_5(valid0200Data, terminal_0705_canRdd)
    /*          disconnectBeforeBrakeCount,
                longBrakeCount,
                longDisconnectCount,
                totalClutchDuration, 离合时长（秒）
                totalBrakeDuration   刹车时长（秒）*/
    computeClutchAndBrakeIndicators(clutchAndBrakeEvents, totalMile)

    val zeroSpeedRanges = terminal_0705_canRdd.sparkContext.broadcast(res.zeroSpeedRanges)

    // 停车立即熄火次数 高转速起步次数
    val (accOffOnceStopCount,highRpmStartCount) = protocolRddProcess.extractAccOffOnceStopHighRpmStartEvents_6(valid0200Data, zeroSpeedRanges, _0705RpmTsRdd)
    computeAccOffOnceStopHighRpmStartIndicators(accOffOnceStopCount, highRpmStartCount, totalMile)

    // 超长怠速次数、怠速空调次数
    val (longIdleCount, longIdleAirConditionerCount) = protocolRddProcess.extractLongIdleOrAirConditionerEvents_7(zeroSpeedRanges, terminal_0705_canRdd, _0705RpmTsRdd)
    computeLongIdleOrAirConditionerIndicators(longIdleCount, longIdleAirConditionerCount, totalMile)

    // 冷车行驶次数
    val driveWithColdStateCount = protocolRddProcess.extractDriveWithColdStateEvents_8(valid0200Data, terminal_0705_canRdd)
    computeColdDriveIndicators(driveWithColdStateCount, totalMile)

    // 行车未放手刹次数
    val driveWithHandCount = protocolRddProcess.extractDriveWithHandBrakeEvent_9(valid0200Data, terminal_0705_canRdd)
    computeDriveWithHandBrakeIndicators(driveWithHandCount, totalMile)

    // 停车踩油门
    val stepOnThrottleDuringStopCount = protocolRddProcess.stepOnThrottleDuringStopBy0705_10(_0705RpmTsRdd, _0705ThrottleOpeningTsRdd)
    computeStepOnThrottleWhenStopIndicator(stepOnThrottleDuringStopCount, totalMile)

    /**********************************************************************/
    // debug
    logError("行程处理结束，耗时："+ (System.currentTimeMillis()-startRunTime)/1000)
    /**********************************************************************/
    true
  }

  /**********************************************************************************/
  // 下面的方法将采集的事件转换成对应的指标

  private def computeAlarmIndicatorsFrom0200Only(alarmCountMap: Map[Int, Long], totalMileage: Double): Unit = {
    /** 告警指标频次，疲劳驾驶、超速、路线偏离、车身故障 */
    // countFatigue, countOverspeed,countRouteBias, countMalfunction
    if (alarmCountMap.get(0).nonEmpty) {
      tripEventsBean.events.put(EVENT_NAME_NUM_RUNATTROUBLE, alarmCountMap(0))
    }
    var indicatorValue = alarmCountMap.getOrElse(0, 0L) * 100.0 / totalMileage //Km
    var indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_RUNATTROUBLE)
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (alarmCountMap.get(1).nonEmpty) {
      tripEventsBean.events.put(EVENT_NAME_NUM_RUNATFATIGUE, alarmCountMap(1))
    }
    indicatorValue = alarmCountMap.getOrElse(1, 0L) * 100.0 / totalMileage
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_RUNATFATIGUE)
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (alarmCountMap.get(2).nonEmpty) {
      tripEventsBean.events
        .put(EVENT_NAME_NUM_RUNATOVERSPEEDLIMIT, alarmCountMap(2))
    }
    indicatorValue = alarmCountMap.getOrElse(2, 0L) * 100.0 / totalMileage
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_RUNATOVERSPEEDLIMIT)
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (alarmCountMap.get(3).nonEmpty) {
      tripEventsBean.events.put(EVENT_NAME_NUM_RUNATBIASEDROUTE, alarmCountMap(3))
    }
    indicatorValue = alarmCountMap.getOrElse(3, 0L) * 100.0 / totalMileage
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_RUNATBIASEDROUTE)
    tripIndicatorsBean.put(indicatorId, indicatorValue)
  }

  private def computeIndicatorsFrom0200Only(events: EventsBy0200Only, totalMileage: Double): Unit = {

    val EventsBy0200Only(_, improperSpeedAndTurnEventCountArr, _, coastingWithEngineOffCount,
    cumulateDeltaEcoDriveStatArr,stopEventCount, speedStabilityCount,
    _, improperTurnEventCounter, cumulateHighSpeedDriveStatArr) = events

    /** 熄火滑行 */
    if (coastingWithEngineOffCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_SHUTDOWNANDCOASTING, coastingWithEngineOffCount)
//      println("熄火滑行次数" + coastingWithEngineOffCount)
    }
    var indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_SHUTDOWNANDCOASTING)
    var indicatorValue = coastingWithEngineOffCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    /** 停车次数 */
    tripEventsBean.events.put(EVENT_NAME_NUM_STOP, stopEventCount + 1)
//      println("停车次数 " + stopEventCount)
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_STOP)
    indicatorValue = (stopEventCount + 1) * 100.0 / totalMileage  // 百公里停车次数
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    /** 变速行为 */
    val (harshSpeedUpCount, harshSpeedDownCount, harshTurnCount, speedUpAndDownCount) = ( improperSpeedAndTurnEventCountArr(0),
                                                                                          improperSpeedAndTurnEventCountArr(1),
                                                                                          improperSpeedAndTurnEventCountArr(2),
                                                                                          improperSpeedAndTurnEventCountArr(3))

    if (harshSpeedUpCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_HARSHSPEEDUP, harshSpeedUpCount)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_HARSHSPEEDUP)
    indicatorValue = harshSpeedUpCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (harshSpeedDownCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_HARSHSPEEDDOWN, harshSpeedDownCount)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_HARSHSPEEDDOWN)
    indicatorValue = harshSpeedDownCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (harshTurnCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_HARSHTURN, harshTurnCount)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_HARSHTURN)
    indicatorValue = harshTurnCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (speedUpAndDownCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_SPEEDUPTHENBRAKE, speedUpAndDownCount)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_SPEEDUPTHENBRAKE)
    indicatorValue = speedUpAndDownCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    /** 经济速度行驶里程和时间 */
    if (cumulateDeltaEcoDriveStatArr(1) > 0) {
      tripEventsBean.events.put(EVENT_NAME_MILE_RUNATECOSPEED, (cumulateDeltaEcoDriveStatArr(1)*10).toLong)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_MILE_RUNATECOSPEED)
    indicatorValue = cumulateDeltaEcoDriveStatArr(1) * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    /** 速度波动率 */
    if (speedStabilityCount > 0) {
      tripEventsBean.put(EVENT_NAME_WAVEOFSPEED, speedStabilityCount)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_WAVEOFSPEED)
    indicatorValue = speedStabilityCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    /** 转向灯指标 */
    if (improperTurnEventCounter.turnWithoutTurnLightCount > 0) {
      tripEventsBean.put(EVENT_NAME_NUM_TURNCORNERWITHOUTDIRECTIONSIGNALLIGHT, improperTurnEventCounter.turnWithoutTurnLightCount)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_TURNCORNERWITHOUTDIRECTIONSIGNALLIGHT)
    indicatorValue = improperTurnEventCounter.turnWithoutTurnLightCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (improperTurnEventCounter.turnWithIncorrectTurnLightCount > 0) {
      tripEventsBean.put(EVENT_NAME_NUM_SWITCHWRONGDIRECTIONSIGNALLIGHT, improperTurnEventCounter.turnWithIncorrectTurnLightCount)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_SWITCHWRONGDIRECTIONSIGNALLIGHT)
    indicatorValue = improperTurnEventCounter.turnWithIncorrectTurnLightCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (improperTurnEventCounter.straightForwardWithTurnLightCount > 0) {
      tripEventsBean.put(EVENT_NAME_NUM_GOSTRAIGHTWITHTURNLIGHT, improperTurnEventCounter.straightForwardWithTurnLightCount)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_GOSTRAIGHTWITHTURNLIGHT)
    indicatorValue = improperTurnEventCounter.straightForwardWithTurnLightCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (improperTurnEventCounter.changeLaneFrequentlyCount > 0) {
      tripEventsBean.put(EVENT_NAME_NUM_CHANGEROADATWILL, improperTurnEventCounter.changeLaneFrequentlyCount)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_CHANGEROADATWILL)
    indicatorValue = improperTurnEventCounter.changeLaneFrequentlyCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    /** 高速行驶时长指标 */
    if (cumulateHighSpeedDriveStatArr(0) > 0) {
      tripEventsBean.put(EVENT_NAME_DURATION_RUNATHIGHSPEED, cumulateHighSpeedDriveStatArr(0).toLong * 1000)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_CHANGEROADATWILL)
    indicatorValue = cumulateHighSpeedDriveStatArr(0) / 36 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

  }

  private def computeNightDriveIndicators( nightDriveDurationBy0200Data: Double,
                                            nightDriveMileBy0200Data: Double,
                                            totalMileage: Double): Unit = {
    if(nightDriveDurationBy0200Data > 0) {
      tripEventsBean.events.put(EVENT_NAME_DURATION_RUNATNIGHT, (nightDriveDurationBy0200Data * 1000).toLong)
    }
    var indicatorId = EVENTS_ID_MAP(EVENT_NAME_DURATION_RUNATNIGHT)
    var indicatorValue = nightDriveDurationBy0200Data / 36.0 / totalMileage // 小时
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if(nightDriveMileBy0200Data > 0) {
      tripEventsBean.events.put(EVENT_NAME_MILE_RUNATNIGHT, (nightDriveMileBy0200Data*10).toLong)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_MILE_RUNATNIGHT)
    indicatorValue = nightDriveMileBy0200Data * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)
  }

  private def computeCoastingIndicators(coastingEventCount: Int, totalMileage: Double): Unit = {
    if (coastingEventCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_RUNATNULLPOS, coastingEventCount)
//      println("0705rdd计算 空挡滑行次数 " + coastingEventCount)
    }
    val indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_RUNATNULLPOS)
    val indicatorValue = coastingEventCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)
  }

  private def computeHTOIndicators(eventsCount: (Long, Long, Double), totalMileage: Double): Unit = {
    val (highOpenDriveCount, fullOpenCount, durationOfHighThrottle) = (eventsCount._1,
      eventsCount._2, eventsCount._3)

    if (durationOfHighThrottle > 0) {
      tripEventsBean.events.put(EVENT_NAME_DURATION_RUNATBIGACCELERATOR, durationOfHighThrottle.toLong)
//      println(s"大油门行驶时长 $highOpenDriveCount")
    }
    var indicatorId = EVENTS_ID_MAP(EVENT_NAME_DURATION_RUNATBIGACCELERATOR)
    var indicatorValue = durationOfHighThrottle / 36.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (fullOpenCount > 0) {
      tripEventsBean.events
        .put(EVENT_NAME_NUM_RUNATFULLACCELERATOR, fullOpenCount)
//      println(s"满油门行驶次数$fullOpenCount")
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_RUNATFULLACCELERATOR)
    indicatorValue = fullOpenCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)
  }

  private def computeStepOnThrottleWhenStopIndicator(stepOnThrottleWhenStopCount: Int, totalMileage: Double): Unit = {
    if(stepOnThrottleWhenStopCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_STEPONTHROTTLEWHENSTOP, stepOnThrottleWhenStopCount)
//      println(s"综合计算 停车踩油门次数 $stepOnThrottleWhenStopCount")
    }
    val indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_STEPONTHROTTLEWHENSTOP)
    val indicatorValue = stepOnThrottleWhenStopCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)
  }

  private def computeBrakeMileIndicator(cumBrakeMile: Double, totalMileage: Double): Unit = {
    if (cumBrakeMile > 0) {
      tripEventsBean.events.put(EVENT_NAME_MILE_BRAKE, (cumBrakeMile*10).toLong)
//      println(s"综合计算 刹车里程 $cumBrakeMile")
    }
    val indicatorId = EVENTS_ID_MAP(EVENT_NAME_MILE_BRAKE)
    val indicatorValue = cumBrakeMile * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)
  }

  private def computeClutchAndBrakeIndicators(events: (Int, Int, Int, Float, Double, Double),
                                                 totalMileage: Double): Unit = {
    val (disconnectBeforeBrakeCount, longBrakeCount, longDisconnectCount,
                                      totalClutchDuration,totalBrakeDuration, totalBrakeMile) = events

    if (disconnectBeforeBrakeCount > 0) {
      tripEventsBean.events
        .put(EVENT_NAME_NUM_CLUTCHBEFOREBRAKE, disconnectBeforeBrakeCount)
    }
    var indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_CLUTCHBEFOREBRAKE)
    var indicatorValue = disconnectBeforeBrakeCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (longBrakeCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_LONGBRAKE, longBrakeCount)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_LONGBRAKE)
    indicatorValue = longBrakeCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (longDisconnectCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_LONGCLUTCH, longDisconnectCount)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_LONGCLUTCH)
    indicatorValue = longDisconnectCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (totalClutchDuration > 0) {
      tripEventsBean.events
        .put(EVENT_NAME_DURATION_CLUTCH, totalClutchDuration.toLong * 1000)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_DURATION_CLUTCH)
    indicatorValue = totalClutchDuration / 36.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (totalBrakeDuration > 0) {
      println("刹车时长 " + totalBrakeDuration)
      tripEventsBean.events.put(EVENT_NAME_DURATION_BRAKE, totalBrakeDuration.toLong * 1000)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_DURATION_BRAKE)
    indicatorValue = totalBrakeDuration / 36.0 / totalMileage // 小时
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (totalBrakeMile > 0) {
      tripEventsBean.events.put(EVENT_NAME_MILE_BRAKE, (totalBrakeMile*10).toLong)
      println(s"综合计算 刹车里程 $totalBrakeMile")
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_MILE_BRAKE)
    indicatorValue = totalBrakeMile * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

  }

  private def computeDriveWithHandBrakeIndicators(driveWithoutHandBrakeCount: Long, totalMileage: Double): Unit = {
    if (driveWithoutHandBrakeCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_RUNWITHBRAKE, driveWithoutHandBrakeCount)
      println("行车未放手刹" + driveWithoutHandBrakeCount)
    }
    val indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_RUNWITHBRAKE)
    val indicatorValue = driveWithoutHandBrakeCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)
  }

  private def computeColdDriveIndicators(coldDriveCount: Long, totalMileage: Double): Unit = {
    if (coldDriveCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_RUNATCOLDMOTOR, coldDriveCount)
//      println("冷车行驶次数 " + coldDriveCount)
    }
    val indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_RUNATCOLDMOTOR)
    val indicatorValue = coldDriveCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)
  }

  private def computeLongIdleOrAirConditionerIndicators(longIdleCount: Long,
                                                longIdleConditionerCount: Long,
                                                totalMileage: Double): Unit = {
    if (longIdleCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_LONGIDLE, longIdleCount)
      println(s"怠速指标 $longIdleCount")
    }
    var indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_LONGIDLE)
    var indicatorValue = longIdleCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (longIdleConditionerCount > 0) {
      tripEventsBean.events
        .put(EVENT_NAME_NUM_IDLEDAIRCONDITIONER, longIdleConditionerCount)
      println(s"空调怠速指标 $longIdleConditionerCount")
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_IDLEDAIRCONDITIONER)
    indicatorValue = longIdleConditionerCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)
  }

  private def computeAccOffOnceStopHighRpmStartIndicators(accOffOnceStopCount: Long,
                                                  highRotationStartCount: Long,
                                                  totalMileage: Double): Unit = {
    if (accOffOnceStopCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_SHUTDOWNONCESTOP, accOffOnceStopCount)
      println("停车立即熄火次数 " + accOffOnceStopCount)
    }
    var indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_SHUTDOWNONCESTOP)
    var indicatorValue = accOffOnceStopCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)

    if (highRotationStartCount > 0) {
      tripEventsBean.events.put(EVENT_NAME_NUM_STARTATHIGHROTATION, highRotationStartCount)
      println("高转速起步次数 " + highRotationStartCount)
    }
    indicatorId = EVENTS_ID_MAP(EVENT_NAME_NUM_STARTATHIGHROTATION)
    indicatorValue = highRotationStartCount * 100.0 / totalMileage
    tripIndicatorsBean.put(indicatorId, indicatorValue)
  }
}

object SingleTripProcess extends Logging {

  import com.dfssi.dataplatform.analysis.dbha.DriveBehaviorUtils._

  /**
    * 从原来的时序中过滤出大于，等于，小于compareValue的时间区间
    *
    * @param rawData        待过滤的时序
    * @param compareValue   比较的值
    * @param compareMethod 1：大于等于 0：等于 -1：小于等于
    */
  def produceTimeRangesByValueFilter(rawData: Array[Timeseries],
                                     compareValue: Double,
                                     compareMethod: Int): Array[(Long, Long)] = {

    val ranges = ArrayBuffer[(Long, Long)]()

    var startTimeIdx = -1
    rawData.indices.foreach{ idx =>
      val Timeseries(ts, v) = rawData(idx)
      val isSatisfy = compareMethod match {
        case 2 => v > compareValue
        case 1 => v >= compareValue
        case 0 => math.abs(v - compareValue) <= 1e-2
        case -1 => v <= compareValue
        case -2 => v < compareValue
      }
      if(isSatisfy) { //|| ts == rawData(if(0 == idx) 0 else idx-1).timestamp
        if(-1 == startTimeIdx) startTimeIdx = idx
      } else if(-1 != startTimeIdx) {
        var startTime = rawData(startTimeIdx).timestamp
        var endTime = rawData(idx-1).timestamp
        // 区间结束时刻与当前时刻相同，但当前时刻的值不满足条件
        if(rawData(idx-1).timestamp == ts) {
          // 起始点时刻相同，先向外扩展
          if(startTime == endTime) {
            endTime += 200
          } else {
            endTime += 400
          }
        }
        // 区间开始时刻与上一时刻也相同，但上一时刻的值不满足条件
        if(startTime == rawData(if(startTimeIdx == 0) 0 else startTimeIdx-1).timestamp) {
          // 起始点时刻相同，先向外扩展
          if(rawData(startTimeIdx).timestamp == rawData(idx-1).timestamp) {
            startTime -= 200
          } else {
            startTime -= 400
          }
        }
        if(startTime == endTime) {
//          logError(s"单个时间点${rawData(startTimeIdx)} 构成一个区间，将要扩展该区域")
          startTime = if(0 == startTimeIdx) startTime - 500 // 减去500毫秒
          else (startTime + rawData(startTimeIdx-1).timestamp) / 2
          endTime = if (startTimeIdx == rawData.length -1) endTime + 500 //加上500毫秒
          else (endTime + rawData(startTimeIdx+1).timestamp) / 2
        }
        ranges.append((startTime, endTime))
        startTimeIdx = -1
      }
    }
    if(-1 != startTimeIdx) {
      val startTime = rawData(startTimeIdx).timestamp
      val endTime = rawData.last.timestamp
      if(startTime == endTime)
        ranges.append((startTime, endTime + 500))
      else
        ranges.append((startTime, endTime))
    }

    ranges.toArray
  }

  /**
    * 对0200数据通过值比较过滤出满足条件的时间段
    * @param rawData    CollectedDvrItem数组
    * @param itemType  1->timestamp, 2->speed, 3->dir, 4->mile, 5->accState, 6->turnLightSignals(不可用)
    * @param compareValue   比较的值
    * @param compareMethod  1：大于等于 0：等于 -1：小于等于
    * @return   时间区间
    */
  def produceTimeRangesByValueFilter(rawData: Array[CollectedDvrItem],
                                     itemType: Int, // 对应CollectedDvrItem中的元素 1 timestamp 2 speed
                                     compareValue: Double,
                                     compareMethod: Int): Array[(Long, Long)] = {

    val ranges = ArrayBuffer[(Long, Long)]()

    var startTimeIdx = -1
    rawData.indices.foreach{ idx =>
      val v = itemType match {
        case 1 => rawData(idx).timestamp
        case 2 => rawData(idx).speed
        case 3 => rawData(idx).dir
        case 4 => rawData(idx).mile
//        case 5 => rawData(idx).accState
      }
      val isSatisfy = compareMethod match {
        case 2 => v > compareValue
        case 1 => v >= compareValue
        case 0 => math.abs(v - compareValue) <= 0.1
        case -1 => v <= compareValue
        case -2 => v < compareValue
      }

      if(isSatisfy) {
        if(-1 == startTimeIdx) startTimeIdx = idx
      } else if(-1 != startTimeIdx) {
        var startTime = rawData(startTimeIdx).timestamp
        var endTime = rawData(idx-1).timestamp
        // 区间结束时刻与当前时刻相同，但当前时刻的值不满足条件
        if(rawData(idx-1).timestamp == rawData(idx).timestamp) {
          // 起始点时刻相同，先向外扩展500毫秒
          if(startTime == endTime) {
            endTime += 200
          } else {
            endTime += 400
          }
        }
        // 区间开始时刻与上一时刻也相同，但上一时刻的值不满足条件
        if(startTime == rawData(if(startTimeIdx == 0) 0 else startTimeIdx-1).timestamp) {
          // 起始点时刻相同，先向外扩展500毫秒
          if(rawData(startTimeIdx).timestamp == rawData(idx-1).timestamp) {
            startTime -= 200
          } else {
            startTime -= 400
          }
        }
        if(startTime == endTime) {
//          logError(s"单个时间点${rawData(startTimeIdx)} 构成一个区间，将要扩展该区域")
          startTime -= 500
          endTime += 500
        }
        ranges.append((startTime, endTime))
        startTimeIdx = -1
      }
    }
    if(-1 != startTimeIdx) {
      val startTime = rawData(startTimeIdx).timestamp
      val endTime = rawData.last.timestamp
      if(startTime == endTime)
        ranges.append((startTime, endTime + 500))
      else
        ranges.append((startTime, endTime))
    }

    ranges.toArray
  }

  /**
    * 求两个时间区间的交集
    * @param left   第一个时间区间
    * @param right  第二个时间区间
    * @param minTimeIntervalMillis  交集时间区间最小时长，默认1秒
    * @return
    */
  def intersectTimeRanges(left: Array[(Long, Long)],
                          right: Array[(Long, Long)],
                          minTimeIntervalMillis: Int = 1000): Array[(Long, Long)] = {

    val intersectTimeRanges = ArrayBuffer[(Long, Long)]()
    var (l, r) = (0, 0)

    while(l < left.length && r < right.length) {
      // 不相交
      if(left(l)._2 < right(r)._1) l += 1
      else if(right(r)._2 < left(l)._1) r += 1
      // 包含
      else if(right(r)._1 <= left(l)._1 && left(l)._2 <= right(r)._2){
        intersectTimeRanges.append(left(l))
        l += 1
      }
      else if(left(l)._1 <= right(r)._1 && right(r)._2 <= left(l)._2) {
        intersectTimeRanges.append(right(r))
        r += 1
      }
      // 有交集
      else {
        val startTime = math.max(left(l)._1, right(r)._1)
        val endTime = math.min(left(l)._2, right(r)._2)
        if(endTime - startTime >= minTimeIntervalMillis)
          intersectTimeRanges.append((startTime, endTime))
        if(left(l)._1 <= right(r)._1 && left(l)._2 < right(r)._2) l += 1
        else if(right(r)._1 <= left(l)._1 && right(r)._2 < left(l)._2) r += 1
      }
    }
    intersectTimeRanges.toArray
  }


  def produceZeroSpeedRangesImpl(speedRdd: RDD[Timeseries],
                             continuousSpeedSignalMaxTimeDiffMillis: Float): Array[(Long, Long)] = {
    val zeroSpeedRanges = ArrayBuffer[(Long, Long)]()
    var prevT = -1L
    var startTime = -1L
    speedRdd.filter(_.value <= 1e-2).map(_.timestamp)
      .collect().sorted.foreach{ t =>
      if(-1 != prevT) {
        if(t - prevT > continuousSpeedSignalMaxTimeDiffMillis) {
//          if(startTime == prevT) {
//            println(s"采集单个速度为0的点构成的区间，起点时刻为$startTime 当前点时刻为$t 上一个时刻为$prevT")
//          }
          zeroSpeedRanges.append((startTime, prevT))
          startTime = t
        }
      } else startTime = t
      prevT = t
    }

    if(zeroSpeedRanges.last._2 < prevT) zeroSpeedRanges.append((startTime, prevT))

    if (zeroSpeedRanges.length > 100)
      println("0速度区间较多有 "+zeroSpeedRanges.length)

    zeroSpeedRanges.toArray
  }

  def midValue(t1: Long, t2: Long): Long = (t1 + t2) / 2

  def produceZeroSpeedRanges(signalRdd: RDD[Row],
                             continuousSpeedSignalMaxTimeDiffMillis: Float): Array[(Long, Long)] = {
    val _0200SpeedRdd = signalRdd.map{ row =>
      (row.getAs[Long](Field_GPS_Time), get0200Speed(row), row.getAs[mutable.WrappedArray[String]](Field_VehicleStatus))
    }.distinct()
      .filter(_._2 == 0)
      .collect().sortBy(_._1)
      .foreach(println)

    Array[(Long, Long)]()
//    produceZeroSpeedRangesImpl(_0200SpeedRdd, continuousSpeedSignalMaxTimeDiffMillis)
  }


  @DeveloperApi
  def collectValidDvrDataV2(terminal_0200_dvrRdd: RDD[Row]): Array[CollectedDvrItem] = {

    val startRunTime = System.currentTimeMillis()
    logError(s"开始执行collectValidDvrData，时刻为$startRunTime")

    val turnLightRdd = terminal_0200_dvrRdd
      .flatMap(r =>
        r.getAs[mutable.WrappedArray[String]](Field_SignalStates).filter(_.contains("转向灯"))
          .map(f => r.getAs[Long](Field_GPS_Time) -> f))
      .aggregateByKey(mutable.Set[String]())((U,v)=>{U.add(v);U}, (U1, U2)=>U1.++:(U2))

    val res = terminal_0200_dvrRdd.map{ row =>
      val speed = get0200Speed(row)
      val timestamp = row.getAs[Long](Field_GPS_Time)
      val dir = row.getAs[Long](Field_DirectionAngle)
      val mile = row.getAs[Long](Field_CumulativeMile)
      val vehicleStatus = row.getAs[mutable.WrappedArray[String]](Field_VehicleStatus)
      val accState = if(vehicleStatus.contains("ACC 关")) 0
      else if(vehicleStatus.contains("ACC 开")) 1
      else -1
      timestamp -> (speed, dir, mile, accState)
    }.groupByKey()
      .flatMap{ case(t, grp) =>
        val step = grp.size match {
          case 1 => 500
          case x => 1000/x
        }
        grp.zipWithIndex.map{ case (tuple, idx) =>
          (t - 500 + step * (idx+1)) -> tuple}
      }
      .join(turnLightRdd)
      .map{ case(key, (left, right)) =>
        CollectedDvrItem(key, left._1, left._2, left._3, left._4, right.toSet)
      }
      .collect()
      .sortBy(_.timestamp)

    val durationMinutes = (System.currentTimeMillis()- startRunTime)/1000/60.0
    logError(s"执行collectValidDvrData结束，耗时 $durationMinutes 分钟")

    res
  }

  // 信号量时间序列平滑操作
  // 进行平滑操作，按时刻分组再求平均值
  def smoothByValueAvg(timeseries: RDD[(Long, Double)]): RDD[Timeseries] = {
    timeseries
      .aggregateByKey(Array(0.0, 0.0))((U, v) => {U(0) += v; U(1)+=1; U}, (U1, U2)=>{U1(0)+=U2(0);U1(1)+=U2(1);U1})
      .mapValues(arr => arr(0)/arr(1))
      .map(f => Timeseries(f._1, f._2))
  }

  def smoothByValueAvg(timeseries: Array[(Long, Double)]): Iterable[Timeseries] = {
    timeseries.groupBy(_._1)
        .mapValues{ arr =>
          arr.map(_._2).sum / arr.length
        }
      .map(f => Timeseries(f._1, f._2))
  }

  @DeveloperApi
  def smoothByTimePartition(timeseries: RDD[(Long, Double)]): RDD[Timeseries] = {
    timeseries.groupByKey()
        .flatMap{ case(t, grp) =>
          val step = grp.size match{
            case 1 => 500
            case x => 1000/x
          }
          grp.zipWithIndex.map{ case(value, idx) =>
            Timeseries(t-500+(idx+1)*step, value)
          }
        }
  }

}
