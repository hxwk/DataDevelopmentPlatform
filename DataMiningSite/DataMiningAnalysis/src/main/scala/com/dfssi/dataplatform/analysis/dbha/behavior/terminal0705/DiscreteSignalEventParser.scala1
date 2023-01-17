package com.dfssi.dataplatform.analysis.dbha.behavior.terminal0705
import com.dfssi.dataplatform.analysis.dbha.bean.BasicEventBean
import com.dfssi.dataplatform.analysis.dbha.behavior.together.TimePeriod
import org.apache.spark.sql.Row

import scala.collection.mutable.ListBuffer

/**
  * 离合信号、脚刹信号、手刹信号、ACC信号、空调开信号
  *
  * 长时间离合
  * 离合总时长
  * 长时间刹车
  * 刹车总时长
  * 刹车总里程
  * @param paramsMap
  */
class DiscreteSignalEventParser(paramsMap: Map[String, String]) extends ParseBehaviorEventBy0705 {

  val handBrakeOnPeriods: ListBuffer[TimePeriod] = ListBuffer[TimePeriod]()
  val footBrakeOnPeriods: ListBuffer[TimePeriod] = ListBuffer[TimePeriod]()
  val clutchOnPeriods: ListBuffer[TimePeriod] = ListBuffer[TimePeriod]()
  val airOnPeriods: ListBuffer[TimePeriod] = ListBuffer[TimePeriod]()

  private val max0705EventDurationMillis = paramsMap
    .getOrElse("max0705EventDuration", "5")
    .toFloat * 1000

  override def parseCurrentRow(row: Row): Unit = {
    val signalName = getSignalName(row)
    val signalValue = getSignalValue(row)

    if (signalName.contains("离合") && signalValue > 0) {
      produceClutchOnPeriods(row)
    }
    if (signalName.contains("脚刹") && signalValue > 0) {
      produceFootBrakeOnPeriods(row)
    }
    if (signalName.contains("手刹") && signalValue > 0) {
      produceHandBrakeOnPeriods(row)
    }
    if (signalName.contains("空调") && signalValue > 0) {
      produceAirConditionerOnPeriods(row)
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

  private def produceAirConditionerOnPeriods(row: Row): Unit = {
    val canTs = getCaptureTime(row)

    if (airOnPeriods.isEmpty || canTs - airOnPeriods.last.endTime > max0705EventDurationMillis) {
      airOnPeriods.append(new TimePeriod(canTs))
      airOnPeriods.last.endTime = airOnPeriods.last.startTime
    } else {
      airOnPeriods.last.endTime = canTs
    }
  }

  def toPeriodsMap: Map[String, List[TimePeriod]] = Map(handBrakeOnPeriods.getClass.getSimpleName -> handBrakeOnPeriods.toList,
    footBrakeOnPeriods.getClass.getSimpleName -> footBrakeOnPeriods.toList,
    clutchOnPeriods.getClass.getSimpleName -> clutchOnPeriods.toList)

  override def outputAllEvents(): Iterator[BasicEventBean] = ???

  override def setUp(): Unit = ???

  override def cleanUp(): Unit = ???

}
