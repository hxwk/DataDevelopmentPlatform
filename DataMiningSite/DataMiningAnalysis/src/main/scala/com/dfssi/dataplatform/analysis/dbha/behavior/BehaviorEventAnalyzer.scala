package com.dfssi.dataplatform.analysis.dbha.behavior

import com.dfssi.dataplatform.analysis.dbha.bean.BasicEventBean
import com.dfssi.dataplatform.analysis.dbha.behavior.terminal0200.speed.SpeedControlBehaviorParser
import com.dfssi.dataplatform.analysis.dbha.behavior.terminal0200.st.SpatioTemporalFeatureParser
import com.dfssi.dataplatform.analysis.dbha.behavior.terminal0200.turnlight.TurnLightSwitchBehavior
import com.dfssi.dataplatform.analysis.dbha.behavior.terminal0705.DiscreteSignalEventParser
import com.dfssi.dataplatform.analysis.dbha.behavior.together.{BrakeSystemControlBehaviorParser, EngineRelatedBehaviorParser, ThrottleControlBehaviorParser}
import com.dfssi.hbase.v2.{HContext, HTableHelper}
import com.dfssi.hbase.v2.insert.HBaseInsertHelper
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.Row

/**
  * 行为事件分析
  * @param vehicleId 车辆id
  * @param tripId     行程id
  * @param paramsMap  配置参数
  */
class BehaviorEventAnalyzer(vehicleId: String, tripId: String, paramsMap: Map[String, String]) extends Serializable {

  import BehaviorEventAnalyzer._

  def getEvents(_0200Iter: Array[Row],
                _0705Iter: Array[Row]): Iterator[BasicEventBean] = {

    import collection.JavaConversions.seqAsJavaList

    // 数据预处理，去重、去异常值、填充等,最后要排序
    val (sorted0200Arr, sorted0705Arr) = dataCleanAndSort(_0200Iter, _0705Iter)

    // 识别所有的基础事件
    val basicEvents = parseBehaviorEvents(sorted0200Arr, sorted0705Arr)

//    basicEvents.foreach(println)

    // 再次封装，添加行程和车辆id
//    HTableHelper.createTable("VehicleEventTable", "VehicleEvent", false, -1, 1)

    val puts = basicEvents.map(toHBaseCell)
    HBaseInsertHelper.put("VehicleEventTable", puts.toSeq)

    basicEvents.map(be => be)
  }

  private def dataCleanAndSort(_0200Iter: Array[Row],
                       _0705Iter: Array[Row]) = {

    val sorted0200Arr = _0200Iter.sortBy(_.getAs[Long]("gps_time"))
    val sorted0705Arr = _0705Iter.sortBy(_.getAs[Long]("receive_time"))

    (sorted0200Arr, sorted0705Arr)
  }

  private def parseBehaviorEvents(sorted0200Arr: Array[Row],
                                  sorted0705Arr: Array[Row]) = {
    // 单独处理0200数据集
    val _0200DependEventInstances = eventInstancesNeed0200Source(paramsMap)

    if (_0200DependEventInstances.nonEmpty) {
      _0200DependEventInstances.foreach(_.setUp())

      sorted0200Arr.foreach{ row =>
        _0200DependEventInstances.foreach(_.parseCurrentRow(row))
      }

      _0200DependEventInstances.foreach(_.cleanUp())
    }

    // 单独处理0705的离散信号数据
    val discreteSignalEventParser = new DiscreteSignalEventParser(paramsMap)
    sorted0705Arr.foreach(discreteSignalEventParser.parseCurrentRow)

    // 两种信号数据同时处理
    val bothDependEventInstances = eventInstancesNeedBothSource(discreteSignalEventParser)(paramsMap)

    if (bothDependEventInstances.nonEmpty) {
      bothDependEventInstances.foreach(_.setUp())
      bothDependEventInstances.foreach(_.parseMultipleRows2(sorted0200Arr, sorted0705Arr))
      bothDependEventInstances.foreach(_.cleanUp())
    }

    // Output
    _0200DependEventInstances.flatMap(_.outputAllEvents()).toIterator ++
      bothDependEventInstances.flatMap(_.outputAllEvents())
  }

  private def toHBaseCell(be: BasicEventBean) = {
    val columnFamilyName = Bytes.toBytes("VehicleEvent")

    val rowKey = Bytes.toBytes(vehicleId + tripId)
    val version = be.startTime

    val eventNameQualifier = Bytes.toBytes("EventName")
    val endTimeQualifier = Bytes.toBytes("EventEndTime")

    val put = new Put(rowKey)
    put.addColumn(columnFamilyName, eventNameQualifier, version, Bytes.toBytes(be.eventName))
    put.addColumn(columnFamilyName, endTimeQualifier, version, Bytes.toBytes(be.endTime.toString))

    for (m <- be.columnValueMap) {
      val qualifierNameBytes = Bytes.toBytes(m._1)
      val qualifierValueBytes = Bytes.toBytes(m._2.toString)

      put.addColumn(columnFamilyName, qualifierNameBytes, version, qualifierValueBytes)
    }

    put

  }

}

object BehaviorEventAnalyzer {

  def apply(vehicleId: String, tripId: String, paramsMap: Map[String, String]): BehaviorEventAnalyzer =
    new BehaviorEventAnalyzer(vehicleId, tripId, paramsMap)

  private def eventInstancesNeed0200Source(paramsMap: Map[String, String]) = Array[BehaviorParserOnIndividualRow](
    new TurnLightSwitchBehavior(paramsMap),
    new SpeedControlBehaviorParser(paramsMap),
    new SpatioTemporalFeatureParser(paramsMap)
  )

  private def eventInstancesNeedBothSource(discreteSignalEventParser: DiscreteSignalEventParser)(paramsMap: Map[String, String]) =
    Array[BehaviorParserOnMultipleRows](
    new BrakeSystemControlBehaviorParser(paramsMap, discreteSignalEventParser),
      new ThrottleControlBehaviorParser(paramsMap, discreteSignalEventParser),
      new EngineRelatedBehaviorParser(paramsMap, discreteSignalEventParser)
  )

}