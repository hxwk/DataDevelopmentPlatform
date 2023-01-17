package com.dfssi.dataplatform.analysis.dbha.behavior

import org.apache.spark.sql.Row

trait Duration {

  def durationMillis: Long

  def durationHours: Float

}


class BehaviorEvent {

  private[this] var _startRow: Row = _

  def startRow: Row = _startRow

  def startRow_=(value: Row): Unit = {
    _startRow = value
  }

  private[this] var _endRow: Row = _

  def endRow: Row = _endRow

  def endRow_=(value: Row): Unit = {
    _endRow = value
  }

  def reset(): Unit = {
    _startRow = null
    _endRow = null
  }

}

