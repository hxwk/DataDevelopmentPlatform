package com.dfssi.dataplatform.analysis.dbha.behavior.together

import com.dfssi.dataplatform.analysis.dbha.behavior.BehaviorParserOnMultipleRows
import com.dfssi.dataplatform.analysis.dbha.behavior.terminal0705.ParseBehaviorEventBy0705
import com.dfssi.dataplatform.analysis.dbha.utils.{_0200RowValueGetter, _0705RowValueGetter}
import org.apache.spark.Logging
import org.apache.spark.sql.Row


abstract class ParseBehaviorEventTogether(_0705Parser: ParseBehaviorEventBy0705)
  extends BehaviorParserOnMultipleRows with Logging with _0705RowValueGetter with _0200RowValueGetter{

  def get0200Speed(row: Row): Long = if (getVDRSpeed(row) > 0)
    getVDRSpeed(row)
  else getGPSSpeed(row)



}


class TimePeriod(private[this] var _startTime: Long) {

  private[this] var _endTime: Long = -1

  def endTime: Long = _endTime

  def endTime_=(value: Long): Unit = {
    _endTime = value
  }

  def startTime: Long = _startTime

  def startTime_=(value: Long): Unit = {
    _startTime = value
  }

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

  def timeDiffMillis: Long = endTime - startTime

  override def toString: String = s"TimePeriod $startTime $endTime"
}