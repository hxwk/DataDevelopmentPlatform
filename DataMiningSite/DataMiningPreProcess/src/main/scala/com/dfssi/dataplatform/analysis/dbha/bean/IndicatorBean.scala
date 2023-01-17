package com.dfssi.dataplatform.analysis.dbha.bean

import java.sql.Timestamp
import java.util

import com.dfssi.dataplatform.analysis.dbha.config.IndicatorsConfig

import scala.collection.JavaConverters._
import scala.collection.mutable

class IndicatorBean extends Serializable {

  val indicators: mutable.Map[Int, Float] = new util.LinkedHashMap[Int, Float]().asScala
  @Deprecated
  val indicators_score: mutable.Map[Int, Float] = mutable.Map[Int, Float]()
  var tripId: String = _
  var tripEndTime: Timestamp = _
  var vehicleId: String = _
  var driverId: String = _

  def put(indicatorId: Int, floatValue: Double): IndicatorBean ={
    this.indicators.put(indicatorId, floatValue.toFloat)
    this
  }

  @Deprecated
  def putScore(indicatorId: Int, floatValue: Double): IndicatorBean ={
    this.indicators_score.put(indicatorId, floatValue.toFloat)
    this
  }

  @Deprecated
  def getScore(indicatorId: Int): Float = this.indicators_score.getOrElse(indicatorId, 0F)

  def allIndicatorIds: Iterable[Int] = this.indicators.keys

  def reset(): Unit = {
    this.indicators.clear()
    this.indicators_score.clear()
    this.tripId = null
    this.tripEndTime = null
    this.vehicleId = null
    this.driverId = null
  }
}


object IndicatorBean {

  @Deprecated
  val INDICATORS = Array(
    "百公里急加速次数",
    "百公里急减速次数",
    "百公里急转弯次数",
    "百公里连续加减速次数",
    "百公里熄火滑行次数",
    "百公里夜间行驶时长",
    "百公里夜间行驶里程",
    "百公里空档滑行次数",
    "百公里故障行车次数",
    "百公里疲劳驾驶次数",
    "百公里超速行驶次数",
    "百公里路线偏离告警次数",
    "百公里先离合后刹车次数",
    "百公里长时离合次数",
    "百公里长时刹车次数",
    "百公里刹车里程",
    "百公里刹车时长",
    "百公里高转速起步次数",
    "百公里大油门时长",
    "百公里满油门行驶次数",
    "速度波动性",
    "百公里停车踩油门次数",
    "百公里超长怠速次数",
    "百公里怠速空调次数",
    "百公里高档行驶里程",
    "百公里高档行驶时长",
    "百公里冷车行驶次数",
    "百公里高档低速次数",
    "百公里高速低档次数",
    "百公里经济负荷里程",
    "百公里经济负荷时长",
    "百公里高速行驶里程",
    "百公里高速行驶时长",
    "百公里停车次数",
    "百公里停车立即熄火次数",
    "百公里机油压力低次数",
    "百公里行车带手刹次数",
    "档位速度匹配度",
    "百公里随意变道次数",
    "百公里错打转向灯转弯次数",
    "百公里未打转向灯转弯次数",
    "百公里恶劣天气行驶次数",
    "百公里违章次数",
    "百公里经济速度里程",
    "百公里离合时长",
    "百公里怠速时长",
    "百公里带转向灯直行次数",
    "百公里超载次数"
  )

  val INDICATORS_ID_MAP: Map[String, Int] = IndicatorsConfig.DBHA_RESOURCE_INDICATOR_ID_NAME.map(_.swap)
//    INDICATORS.zipWithIndex.map(f => f._1 -> (f._2+1)).toMap

  @Deprecated
  val EVENT_ID_TO_INDICATORS_MAP = Map(
    1 -> "百公里急加速次数",
    2 -> "百公里急减速次数",
    3 -> "百公里急转弯次数",
    4 -> "百公里连续加减速次数",
    5 -> "百公里熄火滑行次数",
    6 -> "百公里夜间行驶时长",
    7 -> "百公里夜间行驶里程",
    8 -> "百公里空档滑行次数",
    9 -> "百公里故障行车次数",
    10 -> "百公里疲劳驾驶次数",
    11 -> "百公里超速行驶次数",
    12 -> "百公里路线偏离告警次数",
    13 -> "百公里先离合后刹车次数",
    14 -> "百公里长时离合次数",
    15 -> "百公里长时刹车次数",
    16 -> "百公里刹车里程",
    17 -> "百公里刹车时长",
    18 -> "百公里高转速起步次数",
    19 -> "百公里大油门时长",
    20 -> "百公里满油门行驶次数",
    21 -> "速度波动性",
    22 -> "百公里停车踩油门次数",
    23 -> "百公里超长怠速次数",
    24 -> "百公里怠速空调次数",
    25 -> "百公里高档行驶里程",
    26 -> "百公里高档行驶时长",
    27 -> "百公里冷车行驶次数",
    28 -> "百公里高档低速次数",
    29 -> "百公里高速低档次数",
    30 -> "百公里经济负荷里程",
    31 -> "百公里经济负荷时长",
    32 -> "百公里高速行驶里程",
    33 -> "百公里高速行驶时长",
    34 -> "百公里停车次数",
    35 -> "百公里停车立即熄火次数",
    36 -> "百公里机油压力低次数",
    37 -> "百公里行车带手刹次数",
    38 -> "档位速度匹配度",
    39 -> "百公里随意变道次数",
    40 -> "百公里错打转向灯转弯次数",
    41 -> "百公里未打转向灯转弯次数",
    42 -> "百公里恶劣天气行驶次数",
    43 -> "百公里违章次数",
    44 -> "百公里经济速度里程",
    45 -> "百公里离合时长",
    46 -> "百公里怠速时长",
    47 -> "百公里带转向灯直行次数",
    48 -> "百公里超载次数") // 如果添加新的指标，必须对应的在@{EventBean.EVENTS}添加对应的事件，保证事件下标与指标id对应

  lazy val INDICATOR_CONTRIB: Map[Int, Int] = IndicatorsConfig.DBHA_RESOURCE_INDICATOR_ID_CONTRIB
    /*Map(
    1 ->  -1,
    2 ->  -1,
    3 ->  -1,
    4 ->  -1,
    5 ->  -1,
    6 ->  -1,
    7 ->  -1,
    8 ->  -1,
    9 ->  -1,
    10 -> -1,
    11 -> -1,
    12 -> -1,
    13 -> -1,
    14 -> -1,
    15 -> -1,
    16 -> -1,
    17 -> -1,
    18 -> -1,
    19 -> -1,
    20 -> -1,
    21 -> -1,
    22 -> -1,
    23 -> -1,
    24 -> -1,
    25 -> -1,
    26 -> -1,
    27 -> -1,
    28 -> -1,
    29 -> -1,
    30 -> 1 ,
    31 -> 1 ,
    32 -> -1,
    33 -> -1,
    34 -> -1,
    35 -> -1,
    36 -> -1,
    37 -> -1,
    38 -> 1 ,
    39 -> -1,
    40 -> -1,
    41 -> -1,
    42 -> -1,
    43 -> -1,
    44 -> 1 ,
    45 -> -1,
    46 -> -1,
    47 -> -1,
    48 -> -1
  )*/

}


/** Bean for rdbms table ==> indicator  */
object IndicatorTable {

  val TABLE_NAME = "dbha_indicators"

  val FIELD_INDICATOR_ID = "indicator_id"
  val FIELD_INDICATOR_NAME = "indicator_name"
  val FIELD_INDICATOR_LEVEL = "indicator_level"
  val FIELD_PARENT_INDICATOR = "parent_indicator"
  val FIELD_WEIGHT = "weight"
  val FIELD_MIN_SCORE = "min_score"
  val FIELD_MAX_SCORE = "max_score"

  case class IndicatorInfo(indicator_id: Int, indicator_name: String, indicator_level: Int, parent_indicator: Int) {
    override def equals(obj: scala.Any): Boolean = {
      obj match {
        case x:IndicatorInfo =>
          x.indicator_id == indicator_id && x.indicator_name == indicator_name &&
          x.indicator_level == indicator_level //&& x.parent_indicator == parent_indicator
        case _ => false
      }
    }

    override def hashCode(): Int = {
      (indicator_name + indicator_id + indicator_level).hashCode()
    }
  }

  val DEFAULT_DIMENSION_INDICATORS: Map[String, Array[String]] = IndicatorsConfig.DBHA_RESOURCE_DIM_INDICATORS
  //    Map(
  //    DIMENSION_NAME_SAFETY -> DEFAULT_INDICATORS_FOR_SAFETY,
  //    DIMENSION_NAME_ECONOMY -> DEFAULT_INDICATORS_FOR_ECONOMY,
  //    DIMENSION_NAME_MAINTENANCE -> DEFAULT_INDICATORS_FOR_MAINTENANCE,
  //    DIMENSION_NAME_CIVILIZATION -> DEFAULT_INDICATORS_FOR_CIVILIZATION
  //  )

  /*val DEFAULT_INDICATORS_FOR_SAFETY: Array[String] = Array(
      "百公里急减速次数",
      "百公里急加速次数",
      "百公里急转弯次数",
      "百公里连续加减速次数",
      "百公里超速行驶次数",
      "百公里疲劳驾驶次数",
      "百公里空档滑行次数",
      "百公里熄火滑行次数",
      "百公里先离合后刹车次数",
      "百公里故障行车次数",
      "百公里夜间行驶时长",
      "百公里夜间行驶里程",
      "百公里恶劣天气行驶次数"
  )

  val DEFAULT_INDICATORS_FOR_ECONOMY = Array(
    "百公里刹车里程",
    "百公里高转速起步次数",
    "百公里大油门时长",
    "百公里满油门行驶次数",
    "速度波动性",
    "百公里停车踩油门次数",
//      "档位速度匹配",
//      "百公里高档行驶里程",
//      "百公里高档行驶时长",
    "百公里超长怠速次数",
    "百公里怠速空调次数",
    "百公里停车次数",
    "百公里经济负荷里程",
    "百公里经济负荷时长",
//    "百公里高速行驶里程",
    "百公里高速行驶时长",
    "百公里经济速度里程",
    "百公里怠速时长"
//      "百公里高速低档次数",
  )

  val DEFAULT_INDICATORS_FOR_MAINTENANCE = Array(
    "百公里长时离合次数",
    "百公里长时刹车次数",
    "百公里刹车时长",
    "百公里离合时长",
    "百公里停车立即熄火次数",
    "百公里冷车行驶次数",
//      "百公里机油压力低次数",
//      "百公里高档低速次数",
    "百公里行车带手刹次数",
    "百公里恶劣天气行驶次数"
  )

  val DEFAULT_INDICATORS_FOR_CIVILIZATION = Array(
    "百公里超载次数",
    "百公里随意变道次数",
    "百公里违章次数",
    "百公里路线偏离告警次数",
    "百公里错打转向灯转弯次数",
    "百公里未打转向灯转弯次数",
    "百公里带转向灯直行次数"
  )*/

}

/** Bean for rdbms table ==> trip_indicators  */
object TripIndicatorsTable {

  /* Trip_Indicator Table Fields */
  val TABLE_NAME = "dbha_trip_indicators"
  val FIELD_ID = "id"
  val FIELD_INDICATOR_ID = "indicator_id"
  val FIELD_INDICATOR_NAME = "indicator_name"
  val FIELD_INDICATOR_VALUE = "indicator_value"
  val FIELD_VEHICLE_ID = "vehicle_id"
  val FIELD_DRIVER_ID ="driver_id"
  val FIELD_TRIP_ID = "trip_id"
  val FIELD_TRIP_ENDTIME = "trip_end_time"
  val FIELD_INDICATOR_SCORE = "indicator_score"

  val TRIP_INDICATOR_FIELDS = Array(FIELD_ID,FIELD_VEHICLE_ID, FIELD_DRIVER_ID, FIELD_TRIP_ID, FIELD_TRIP_ENDTIME,
    FIELD_INDICATOR_ID, FIELD_INDICATOR_NAME, FIELD_INDICATOR_VALUE)


}