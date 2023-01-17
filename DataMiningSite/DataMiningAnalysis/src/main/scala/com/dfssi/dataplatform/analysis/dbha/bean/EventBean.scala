package com.dfssi.dataplatform.analysis.dbha.bean

import scala.collection.mutable


case class BasicEventBean(eventName: String, startTime: Long, endTime: Long) {

  // 存放每个事件不同的列和值
  val columnValueMap: mutable.Map[String, Any] = mutable.Map[String, Any]()

  override def toString: String = {
    s"$eventName: $startTime $endTime \n $columnValueMap"
  }
}

class TripVehicleEvent(tripId: String, vehicleId: String) {

}

case class DriverVehicleEvent(driverId: String, vehicleId: String, eventName: String)



object EventBean {

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

  /** 行驶过程 */
  val EVENT_NAME_DRIVING = "driving"

}
