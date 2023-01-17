package com.dfssi.dataplatform.analysis.dbha.bean

import java.sql.Timestamp

import com.dfssi.dataplatform.analysis.dbha.config.IndicatorsConfig

import scala.collection.mutable

/**
  * Drive behavior events for each trip
  */
class EventBean(_tripId: String) extends Serializable {

  val events: mutable.Map[String, Long] = mutable.Map[String, Long]()
//  var tripId: String = _
  var tripEndTime: Timestamp = _

  def tripId: String = _tripId

  def put(eventName: String, integerValue: Long): Option[Long] ={
    this.events.put(eventName, integerValue)
  }

  def reset(): Unit = {
    this.events.clear()
//    tripId = null
    tripEndTime = null
  }

}


/**
  * 需保持一致
  */
object EventBean extends Serializable {

  /** 急加速 */
  val EVENT_NAME_NUM_HARSHSPEEDUP = "numOfHarshSpeedUp"
  /** 急减速 */
  val EVENT_NAME_NUM_HARSHSPEEDDOWN = "numOfHarshSpeedDown"
  /** 急转弯 */
  val EVENT_NAME_NUM_HARSHTURN = "numOfHarshTurn"
  /** 加速刹车 */
  val EVENT_NAME_NUM_SPEEDUPTHENBRAKE = "numOfSpeedUpThenBrake"
  /** 熄火滑行 */
  val EVENT_NAME_NUM_SHUTDOWNANDCOASTING = "numOfshutdownAndCoasting"
  /** 夜间行驶时长 */
  val EVENT_NAME_DURATION_RUNATNIGHT = "durationOfRunAtNight"
  /** 夜间行驶里程 */
  val EVENT_NAME_MILE_RUNATNIGHT = "mileageOfRunAtNight"
  /** 空档滑行 */
  val EVENT_NAME_NUM_RUNATNULLPOS = "numOfRunAtNullPos"
  /** 带故障行车 */
  val EVENT_NAME_NUM_RUNATTROUBLE = "numOfRunAtTrouble"
  /** 疲劳驾驶告警 */
  val EVENT_NAME_NUM_RUNATFATIGUE = "numOfRunAtFatigue"
  /** 超速告警 */
  val EVENT_NAME_NUM_RUNATOVERSPEEDLIMIT = "numOfRunAtOverSpeedLimit"
  /** 路线偏离*/
  val EVENT_NAME_NUM_RUNATBIASEDROUTE = "numOfRunAtBiasedRoute"
  /** 先离合后刹车次数 */
  val EVENT_NAME_NUM_CLUTCHBEFOREBRAKE = "numOfClutchBeforeBrake"
  /** 长时离合 */
  val EVENT_NAME_NUM_LONGCLUTCH = "numOfLongClutch"
  /** 长时刹车 */
  val EVENT_NAME_NUM_LONGBRAKE = "numOfLongBrake"
  /** 刹车里程 */
  val EVENT_NAME_MILE_BRAKE = "mileageOfBrake"
  /** 刹车时长 */
  val EVENT_NAME_DURATION_BRAKE = "durationOfBrake"
  /** 高转速起步 */
  val EVENT_NAME_NUM_STARTATHIGHROTATION = "numOfStartAtHighRotation"
  /** 大油门行驶 */
  val EVENT_NAME_DURATION_RUNATBIGACCELERATOR = "durationOfRunAtBigAccelerator"
  /** 满油行驶 */
    val EVENT_NAME_NUM_RUNATFULLACCELERATOR = "numOfRunAtFullAccelerator"
  /** 速度波动性 */
  val EVENT_NAME_WAVEOFSPEED = "waveOfSpeed"
  /** 停车踩油门 */
  val EVENT_NAME_NUM_STEPONTHROTTLEWHENSTOP = "numOfStepOnThrottleWhenStop"
  /** 超长怠速 */
  val EVENT_NAME_NUM_LONGIDLE = "numOfLongIdle"
  /** 怠速空调 */
  val EVENT_NAME_NUM_IDLEDAIRCONDITIONER = "numOfIdledAirConditioner"
  /** 最高档里程 */
  val EVENT_NAME_MILE_RUNATHIGHESTPOS = "mileageOfRunAtHighestPos"
  /** 最高档时长 */
  val EVENT_NAME_DURATION_RUNATHIGHESTPOS = "durationOfRunAtHighestPos"
  /** 冷车行驶 */
  val EVENT_NAME_NUM_RUNATCOLDMOTOR = "numOfRunAtColdMotor"
  /** 高档低速 */
  val EVENT_NAME_NUM_LOWSPEEDATHIGHPOS = "numOfLowSpeedAtHighPos"
  /** 低档高速 */
  val EVENT_NAME_NUM_HIGHSPEEDATLOWPOS = "numOfHighSpeedAtLowPos"
  /** 经济负荷行驶里程 */
  val EVENT_NAME_MILE_RUNATECOLOAD = "mileageOfRunAtEcoLoad"
  /** 经济负荷行驶时长 */
  val EVENT_NAME_DURATION_RUNATECOLOAD = "durationOfRunAtEcoLoad"
  /** 高速行驶里程 */
  val EVENT_NAME_MILE_RUNATHIGHSPEED = "mileageOfRunAtHighSpeed"
  /** 高速行驶时长 */
  val EVENT_NAME_DURATION_RUNATHIGHSPEED = "durationOfRunAtHighSpeed"
  /** 停车次数 */
  val EVENT_NAME_NUM_STOP = "numOfStop"
  /** 停车立即熄火 */
  val EVENT_NAME_NUM_SHUTDOWNONCESTOP = "numOfShutDownOnceStop"
  /** 机油压力低 */
  val EVENT_NAME_NUM_LOWPRESSUREOFMACHINEOIL = "numOfLowPressureOfMachineOil"
  /** 行车未放手刹 */
  val EVENT_NAME_NUM_RUNWITHBRAKE = "numOfRunWithBrake"
  /** 档位速度的匹配度 */
  val EVENT_NAME_POSMATCHSPEED = "posMatchSpeed"
  /** 随意变道次数 */
  val EVENT_NAME_NUM_CHANGEROADATWILL = "numOfChangeRoadAtWill"
  /** 乱打转向灯 */
  val EVENT_NAME_NUM_SWITCHWRONGDIRECTIONSIGNALLIGHT = "numOfSwitchWrongDirectionSignalLight"
  /** 转弯不打转向灯 */
  val EVENT_NAME_NUM_TURNCORNERWITHOUTDIRECTIONSIGNALLIGHT = "numOfTurnCornerWithoutDirectionSignalLight"
  /** 恶劣天气行车 */
  val EVENT_NAME_RUNATBADWEATHER = "runAtBadWeather"
  /** 行程违章次数 */
  val EVENT_NAME_NUM_VIOLATIONOFRULES = "numOfViolationOfRules"
  /** 经济速度里程 */
  val EVENT_NAME_MILE_RUNATECOSPEED = "mileageOfRunAtEcoSpeed"
  /** 离合时长 */
  val EVENT_NAME_DURATION_CLUTCH = "durationOfCluth"
  /** 怠速时长*/
  val EVENT_NAME_DURATION_IDLE = "durationOfIdle"
  /** 超载次数 */
  val EVENT_NAME_NUM_OVERLOAD = "numOfOverload"
  /** 带转向灯直行 */
  val EVENT_NAME_NUM_GOSTRAIGHTWITHTURNLIGHT = "numOfGoStraightWithTurnLight"


  /*val EVENTS = Array(EVENT_NAME_NUM_HARSHSPEEDUP ,
    EVENT_NAME_NUM_HARSHSPEEDDOWN,
    EVENT_NAME_NUM_HARSHTURN ,
    EVENT_NAME_NUM_SPEEDUPTHENBRAKE,
    EVENT_NAME_NUM_SHUTDOWNANDCOASTING ,
    EVENT_NAME_DURATION_RUNATNIGHT ,
    EVENT_NAME_MILE_RUNATNIGHT ,
    EVENT_NAME_NUM_RUNATNULLPOS ,
    EVENT_NAME_NUM_RUNATTROUBLE ,
    EVENT_NAME_NUM_RUNATFATIGUE ,
    EVENT_NAME_NUM_RUNATOVERSPEEDLIMIT ,
    EVENT_NAME_NUM_RUNATBIASEDROUTE ,
    EVENT_NAME_NUM_CLUTCHBEFOREBRAKE ,
    EVENT_NAME_NUM_LONGCLUTCH ,
    EVENT_NAME_NUM_LONGBRAKE ,
    EVENT_NAME_MILE_BRAKE,
    EVENT_NAME_DURATION_BRAKE ,
    EVENT_NAME_NUM_STARTATHIGHROTATION ,
    EVENT_NAME_DURATION_RUNATBIGACCELERATOR ,
    EVENT_NAME_NUM_RUNATFULLACCELERATOR,
    EVENT_NAME_WAVEOFSPEED,
    EVENT_NAME_NUM_STEPONTHROTTLEWHENSTOP,
    EVENT_NAME_NUM_LONGIDLE,
    EVENT_NAME_NUM_IDLEDAIRCONDITIONER ,
    EVENT_NAME_MILE_RUNATHIGHESTPOS,
    EVENT_NAME_DURATION_RUNATHIGHESTPOS,
    EVENT_NAME_NUM_RUNATCOLDMOTOR,
    EVENT_NAME_NUM_LOWSPEEDATHIGHPOS,
    EVENT_NAME_NUM_HIGHSPEEDATLOWPOS,
    EVENT_NAME_MILE_RUNATECOLOAD,
    EVENT_NAME_DURATION_RUNATECOLOAD,
    EVENT_NAME_MILE_RUNATHIGHSPEED,
    EVENT_NAME_DURATION_RUNATHIGHSPEED,
    EVENT_NAME_NUM_STOP,
    EVENT_NAME_NUM_SHUTDOWNONCESTOP,
    EVENT_NAME_NUM_LOWPRESSUREOFMACHINEOIL,
    EVENT_NAME_NUM_RUNWITHBRAKE,
    EVENT_NAME_POSMATCHSPEED,
    EVENT_NAME_NUM_CHANGEROADATWILL,
    EVENT_NAME_NUM_SWITCHWRONGDIRECTIONSIGNALLIGHT,
    EVENT_NAME_NUM_TURNCORNERWITHOUTDIRECTIONSIGNALLIGHT,
    EVENT_NAME_RUNATBADWEATHER,
    EVENT_NAME_NUM_VIOLATIONOFRULES ,
    EVENT_NAME_MILE_RUNATECOSPEED,
    EVENT_NAME_DURATION_CLUTCH,
    EVENT_NAME_DURATION_IDLE,
    EVENT_NAME_NUM_GOSTRAIGHTWITHTURNLIGHT,
    EVENT_NAME_NUM_OVERLOAD) // 每个事件对应@{IndicatorBean.ID_INDICATORS_MAP}*/

  // 此处id 与 {IndicatorBean.ID_INDICATORS_MAP}中的id 一一对应
  lazy val EVENTS_ID_MAP: Map[String, Int] = IndicatorsConfig.DBHA_RESOURCE_EVENT_VALUE_ID
//    EVENTS.zipWithIndex.map(f => f._1 -> (f._2+1)).toMap

}