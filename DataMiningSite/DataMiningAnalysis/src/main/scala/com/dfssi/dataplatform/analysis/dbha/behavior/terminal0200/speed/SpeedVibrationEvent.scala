package com.dfssi.dataplatform.analysis.dbha.behavior.terminal0200.speed

import com.dfssi.dataplatform.analysis.dbha.behavior.{BehaviorEvent, Duration}

abstract class SpeedBehaviorEvent extends BehaviorEvent with Duration {

  var startSpeed: Float = _
  var endSpeed: Float = _

  def eventSpeedDiffKmH: Float = {
    endSpeed - startSpeed
  }

  // unit km/h/s
  def accKmHS: Float = {
    eventSpeedDiffKmH / durationMillis / 1000F
  }

}

// 速度波动性
class SpeedVibrationEvent extends BehaviorEvent {

  private[this] var _sumValue: Double = 0.0

  def sumValue: Double = _sumValue

  def sumValue_=(value: Double): Unit = {
    _sumValue = value
  }

  private[this] var _sumSquaredValue: Double = 0.0

  def sumSquaredValue: Double = _sumSquaredValue

  def sumSquaredValue_=(value: Double): Unit = {
    _sumSquaredValue = value
  }

  private[this] var _count: Int = 0

  def count: Int = _count

  def count_=(value: Int): Unit = {
    _count = value
  }

  def updateAccSpeed(accSpeed: Double): Unit = {
    _count += 1
    _sumValue += accSpeed
    _sumSquaredValue += accSpeed*accSpeed
  }

  def accSpeedVariance: Double = {
    require(count > 0, "样本数目为0")
    math.pow(sumValue/ count, 2) - sumSquaredValue/count
  }

  override  def reset(): Unit = {
    _count = 0
    _sumValue = 0
    _sumSquaredValue = 0

    startRow = null
    endRow = null
  }

}