package com.dfssi.dataplatform.analysis.dbha.driverdescriptor

import java.sql.{Connection, Timestamp}

import com.dfssi.dataplatform.analysis.dbha.DriveBehaviorUtils._
import com.dfssi.dataplatform.analysis.dbha.SingleTripProcess.{produceTimeRangesByValueFilter, smoothByValueAvg}
import com.dfssi.dataplatform.analysis.dbha.driverbehavior.clutch._
import com.dfssi.dataplatform.analysis.dbha.driverbehavior.driverstyle.DriveBehaviorDriverStyleProcess
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.DriveStyleTable.DriveStyle
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.EngineRpmDistributionTable.ENGINE_RPM
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.ThrottleOpeningDistributionTable.ThrottleOpening
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row

import scala.collection.mutable.ArrayBuffer

class DriveBehaviorDescriptor(vid: String, paramsMap: Map[String, String]) {

  import DriveBehaviorDescriptor._
  import EngineRpmDistributionTable._
  import ThrottleOpeningDistributionTable._

  def tripProcess(tripId: String,
                  tripEndTime: Long,
                  _0200Rdd: RDD[Row],
                  _0705Rdd: RDD[Row],
                  conn: Connection): (ENGINE_RPM, ThrottleOpening, CustomOfUseClutch, DriveStyle) = {

    val rpmDis = distribution_rpm(_0705Rdd)
    val enginerpm: ENGINE_RPM = rpmCounts2CaseClass(tripId, tripEndTime, rpmDis)

    val throttleOpeningDis = distribution_throttleOpening(_0705Rdd)
    val opening: ThrottleOpening =
      toCounts2CaseClass(tripId, tripEndTime, throttleOpeningDis)

    val clutchData = distribution_clutch(tripId, tripEndTime, _0705Rdd)
//    OutputRdbms.deleteDistributionClutch(outputConfig, clutchData.tripId,
//                                         clutchData.tripEndTime)
    /* Driver style */
    val driveStyle = analysis_driveStyle(tripId, tripEndTime, _0200Rdd, _0705Rdd)
//    OutputRdbms.deleteDriveStyle(outputConfig, driveStyle.tripId, driveStyle.tripEndTime)

    (enginerpm, opening, clutchData, driveStyle)
  }

  /**
    * ?????????????????????????????????
    *
    * @param _0705Rdd CAN??????????????????
    * @return ?????????????????????
    */
  def distribution_rpm(_0705Rdd: RDD[Row]): Map[Int, Int] = {

    /** rpmLevels  rpm??????????????? */
    val rpmLevels = paramsMap.get("rpmLevels") match {
      case Some(x) => x.split(",").map(_.toInt)
      case None    => Default_RPM_Levels
    }

    val scRpmLevels = _0705Rdd.sparkContext.broadcast(rpmLevels)

    _0705Rdd
      .filter(
        _.getAs[String](Field_SignalName).contains(Keyword_EngineRotation))
      .map { row =>
        val value = row.getAs[Double](Field_Value)
        scRpmLevels.value.indexWhere(level => value <= level) -> 1

      /** note: -1 means rpm value > rpmLevels.last */
      }
      .reduceByKey(_ + _)
      .collect()
      .toMap
  }

  /**
    * ????????????????????????
    *
    * @param _0705Rdd CAN??????????????????
    * @return ?????????????????????
    */
  def distribution_throttleOpening(_0705Rdd: RDD[Row]) = {

    /** ????????????????????? */
    val toLevels = paramsMap.get("throttleOpeningLevels") match {
      case Some(x) => x.split(",").map(_.toInt)
      case None    => Default_TO_Levels
    }

    val scToLevels = _0705Rdd.sparkContext.broadcast(toLevels)
    _0705Rdd
      .filter(
        _.getAs[String](Field_SignalName).contains(Keyword_ThrottleOpening))
      .map { row =>
        val value = row.getAs[Double](Field_Value)
        scToLevels.value.indexWhere(level => value <= level) -> 1

      /** note: indexWhere should not return -1 */
      }
      .reduceByKey(_ + _)
      .collect
      .toMap
  }

  /**
    * ????????????
    *
    * @param _0200Rdd ?????????????????????
    * @return
    */
  def speedDistribution(_0200Rdd: RDD[Row],
                        accLevels: Array[Double] = Default_ACC_Levels) = {

    /** ?????????????????????????????????????????????????????????????????? */
    val accPartialRangeCount = ArrayBuffer[Int]()

    val velocityRdd = _0200Rdd
      .map { row =>
        (row.getAs[Long](Field_GPS_Time), row.getAs[Long](Field_VDR_Speed))
      }
      .sortBy(_._1)

    val firstElem = velocityRdd.first()
    val velocityRddWithoutFirst =
      velocityRdd.filter(f => f._1 != firstElem._1 && f._2 != firstElem._2)
    val lastElem = velocityRdd.max()(Ordering.by[(Long, Long), Long](_._1))
    val velocityRddWithoutLast =
      velocityRdd.filter(f => f._1 != lastElem._1 && f._2 != lastElem._2)

    velocityRddWithoutFirst
      .zip(velocityRddWithoutLast)
      .map {
        case (e1, e2) =>
          val timeDiff = e2._1 - e1._1
          if (timeDiff == 0) {
            -1
          } else {
            val acc = (e2._2 - e1._2) * 1.0 / timeDiff
            if (acc < 0.3) {
              0
            } else if (acc < 0.6) {
              1
            } else if (acc < 0.9) {
              2
            } else if (acc < 1.2) {
              3
            } else {
              4
            }
          }
      }
      .countByValue()
  }

  /**
    * ??????????????????
    */
  def analysis_driveStyle(tripId: String,
                          tripEndTime: Long,
                          _0200Rdd: RDD[Row],
                          _0705Rdd: RDD[Row],
                          CANCaptureRate: Int = 1,
                          /** CAN???????????????????????????1???/s */
                          ecoLoadRateRange: (Int, Int) = (40, 80),
                          ecoRpmRange: (Int, Int) = (800, 1800),
                          idleRpmRange: (Int, Int) = (20, 800),
                          superHighRpm: Int = 2200) = {

    /** ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????? */
    val nanoSecondsGapBetweenSignals = CANCaptureRate * 1000 + 1000

    /** ????????????
      * */
    val brakeRdd = _0705Rdd.filter{ row =>
      row.getAs[String](Field_SignalName).contains("??????")
    }.map(r => r.getAs[Long](Field_CAN_Time)-> r.getAs[Double](Field_Value)).distinct()
    // TODO ????????????????????????
    val brakeTimeSeries = smoothByValueAvg(brakeRdd).collect().sortBy(_.timestamp)
    // ??????????????????
    val validBrakeRanges = produceTimeRangesByValueFilter(brakeTimeSeries, 0, 2)
    // ????????????
    val totalBrakeDuration = if(validBrakeRanges.isEmpty)
      brakeRdd.filter(_._2 > 0).count() / CANCaptureRate
    else validBrakeRanges.map(p => p._2 - p._1).sum / 1000

    /** ??? */
    println("-------------------totalBrakeTime:" + totalBrakeDuration)

    var (preT, startTime) = (-1L, -1L)

    /** ???????????? */
    val idleRdd = smoothByValueAvg(_0705Rdd.filter(_.getAs[String](Field_SignalName).contains("???????????????"))
      .map(f => f.getAs[Long](Field_CAN_Time)->f.getAs[Double](Field_Value)))

    val rpmIdleRange = paramsMap.get("idleRotationRange") match {
      case Some(x) =>
        val arr = x.split("-")
        (arr(0).toInt, arr(1).toInt)
      case None => DefaultIdleRotationRange
    }

    val idleRanges = produceTimeRangesByValueFilter(idleRdd.collect().sortBy(_.timestamp), rpmIdleRange._2, -1)

    val totalEngineIdleTime1 = if(idleRanges.isEmpty) 0.0
    else idleRanges.map(p => (p._2-p._1+500)).sum/1000.0
    println("--------????????????totalEngineIdleTime1:" + totalEngineIdleTime1)

    val totalEngineIdleTime = new DriveBehaviorDriverStyleProcess(_0705Rdd).getIdleTime()
    println("-------------------totalEngineIdleTime:" + totalEngineIdleTime)

    /** ????????????????????????????????????????????????????????????????????? ???????????????????????? ?????????????????? ???????????????????????? ???????????? */
    preT = -1L
    startTime = -1L
    val relatedRdd = _0705Rdd
      .filter { row =>
        val signalName = row.getAs[String](Field_SignalName)
        signalName.contains(Keyword_EngineRotation) || signalName.contains(
          Keyword_EngineLoadRate)
      }
      .map { row =>
        val v = row.getAs[Double](Field_Value)
        val level =
          if (row
                .getAs[String](Field_SignalName)
                .contains(Keyword_EngineRotation)) {
            if (v < ecoRpmRange._1) 10

            /** ????????? */
            else if (v <= ecoRpmRange._2) 11

            /** ???????????? */
            else if (v <= superHighRpm) 12

            /** ????????? */
            else 15

            /** ???????????? */
          } else {
            if (v < ecoLoadRateRange._1) 20

            /** ????????? */
            else if (v <= ecoLoadRateRange._2) 21

            /** ???????????? */
            else 22

            /** ????????? */
          }
        (row.getAs[Long](Field_CAN_Time), level)
      }
      .cache()

    /** ????????????         ?????????1 */
    preT = -1L
    var ecoDriveTime = 0L
    relatedRdd
      .filter { case (_, flag) => flag % 10 == 1 }
      .map(_._1)
      .collect()
      .sorted
      .foreach { t =>
        if (-1 != preT) {
          if (t - preT <= nanoSecondsGapBetweenSignals) ecoDriveTime += t - preT
        }
        preT = t
      }
    println("---------------------ecoDriveTime:" + ecoDriveTime)

    /** ???????????????????????? ?????????0 */
    preT = -1L
    var lowLoadLowRotationTime = 0L
    relatedRdd
      .filter { case (_, flag) => flag % 10 == 0 }
      .map(_._1)
      .collect()
      .sorted
      .foreach { t =>
        if (-1 != preT) {
          if (t - preT <= nanoSecondsGapBetweenSignals)
            lowLoadLowRotationTime += t - preT
        }
        preT = t
      }
    println(
      "----------------------lowLoadLowRotationTime:" + lowLoadLowRotationTime)

    /** ??????????????????     ???4??????, xxx10,11,15,21,22xxx, 12 20 */
    preT = -1L
    var lowLoadHighRotationTime = 0L
    relatedRdd
      .filter { case (_, flag) => flag % 4 == 0 || flag == 15 }
      .map(_._1)
      .collect()
      .sorted
      .foreach { t =>
        if (-1 != preT) {
          if (t - preT <= nanoSecondsGapBetweenSignals)
            lowLoadHighRotationTime += t - preT
        }
        preT = t
      }
    println(
      "------------------lowLoadHighRotationTime:" + lowLoadHighRotationTime)

    /** ???????????????????????? %3==1 10,22  xxx11,12,15,20,21xxx */
    preT = -1L
    var highLoadlowRotationTime = 0L
    relatedRdd
      .filter { case (_, flag) => flag % 3 == 1 }
      .map(_._1)
      .collect()
      .sorted
      .foreach { t =>
        if (-1 != preT) {
          if (t - preT <= nanoSecondsGapBetweenSignals)
            highLoadlowRotationTime += t - preT
        }
        preT = t
      }
    println(
      "---------------------highLoadlowRotationTime:" + highLoadlowRotationTime)

    /** ???????????????????????? >11 and >21 */
    preT = -1L
    var aboveEcoAreaTime = 0L
    relatedRdd
      .filter { case (_, flag) => flag > 11 && flag > 21 }
      .map(_._1)
      .collect()
      .sorted
      .foreach { t =>
        if (-1 != preT) {
          if (t - preT <= nanoSecondsGapBetweenSignals)
            aboveEcoAreaTime += t - preT
        }
        preT = t
      }
    println("-----------------------aboveEcoAreaTime:" + aboveEcoAreaTime)

    /** ????????????         ==15 */
    preT = -1L
    var superHighRpmTime = 0L
    relatedRdd.filter(_._2 == 15).map(_._1).collect().sorted.foreach { t =>
      if (-1 != preT) {
        if (t - preT <= nanoSecondsGapBetweenSignals)
          superHighRpmTime += t - preT
      }
      preT = t
    }
    println("--------------------------superHighRpmTime:" + superHighRpmTime)

    DriveStyle(
      tripId,
      new Timestamp(tripEndTime),
      totalBrakeDuration,
      totalEngineIdleTime,
      ecoDriveTime,
      lowLoadLowRotationTime,
      lowLoadHighRotationTime,
      highLoadlowRotationTime,
      aboveEcoAreaTime,
      superHighRpmTime
    )
  }

  /**
    * ??????????????????
    *
    */
  def distribution_clutch(tripId: String,
                          tripEndTime: Long,
                          _0705Rdd: RDD[Row]): CustomOfUseClutch = {
    var totalOfRunAtNullPos: Long = 0
    var totalOfIdle: Long = 0
    var totalOfStartup: Long = 0
    var totalOfNormal: Long = 0

    val timeScopeOfRunAtNullPos: TimeScopeOfCheck =
      calcTimeScopesOfUseClutchEvent(_0705Rdd)

    _0705Rdd
      .filter(row =>
        row.getAs[String](Field_SignalName).contains(KEYWORD_CLUTCH))
      .collect()
      .foreach(row => {
        val receiveTime = row.getAs[Long](Field_CAN_Time)
        val value = row.getAs[Double](Field_Value)
        if (value > 0) {
          if (checkClutchScope(timeScopeOfRunAtNullPos.timeScopesOfRunAtNullPos,
                               receiveTime)) {
            totalOfRunAtNullPos += 1
          } else if (checkClutchScope(
                       timeScopeOfRunAtNullPos.timeScopesOfStartup,
                       receiveTime)) {
            totalOfStartup += 1
          } else if (checkClutchScope(timeScopeOfRunAtNullPos.timeScopesOfIdle,
                                      receiveTime)) {
            totalOfIdle += 1
          } else { totalOfNormal += 1 }
        }
      })

//    println("---------------------totalOfRunAtNullPos " + totalOfRunAtNullPos)
//    println("---------------------totalOfIdle " + totalOfIdle)
//    println("---------------------totalOfStartup " + totalOfStartup)
//    println("---------------------totalOfNormal " + totalOfNormal)
    new CustomOfUseClutch(tripId,
                          new Timestamp(tripEndTime),
                          totalOfRunAtNullPos,
                          totalOfIdle,
                          totalOfStartup,
                          totalOfNormal)
  }

  private def checkClutchScope(nullPosScop: ArrayBuffer[(Long, Long)],
                               receiveTime: Long): Boolean = {
    var result = false
    nullPosScop.map(trid =>
      if (receiveTime >= trid._1 && receiveTime < trid._2) {
        result = true
    })

    result
  }

  /**
    * ??????????????????????????????????????????????????? ??????>20km/h??? ????????????>2s??? ????????????=0??? ????????????????????? 5%
    *
    * @param _0705Rdd
    * @return
    */
  def calcTimeScopesOfUseClutchEvent(_0705Rdd: RDD[Row]): TimeScopeOfCheck = {
    val tiemScopes = new TimeScopeOfCheck()
    val statusParam = new DriveBehaviorStatusCheckParam()
    val nullPosStatusProcess = new DriveBehaviorNullPosStatus()
    val idleStatusProcess = new DriveBehaviorIdleStatus()
    val startupStatusProcess = new DriveBehaviorStartupStatus()

    val _0705SortedRdd =
      _0705Rdd.collect().sortBy(row => row.getAs[Long](Field_CAN_Time))

    _0705SortedRdd.map { row =>
      val signalName = row.getAs[String](Field_SignalName) /* collect RUN AT NULL POS event time scope*/
      val timeScopeOfRunAtNullPos =
        nullPosStatusProcess.checkEvent(row, statusParam)
      if (timeScopeOfRunAtNullPos.isDefined) {
        tiemScopes.timeScopesOfRunAtNullPos += timeScopeOfRunAtNullPos.get
      }

      val timeScopeOfIdle = idleStatusProcess.checkEvent(row, statusParam)
      if (timeScopeOfIdle.isDefined) {
        tiemScopes.timeScopesOfIdle += timeScopeOfIdle.get
      }

      val timeScopeOfStartup = startupStatusProcess.checkEvent(row, statusParam)
      if (timeScopeOfStartup.isDefined) {
        tiemScopes.timeScopesOfStartup += timeScopeOfStartup.get
      }
    }

    tiemScopes.timeScopesOfRunAtNullPos.foreach(trid =>
      println("---------null pos:" + trid._1 + "," + trid._2))
    tiemScopes.timeScopesOfIdle.foreach(trid =>
      println("---------idle:" + trid._1 + "," + trid._2))
    tiemScopes.timeScopesOfStartup.foreach(trid =>
      println("---------startup:" + trid._1 + "," + trid._2))

    tiemScopes
  }

}

object DriveBehaviorDescriptor {

  // Hive table fields
//  val Field_SignalName = "signal_name"
//  val Field_Value = "value"
//  val Field_Speed = "speed"
//  val Field_GPS_Time = "gps_time"
//  val Field_CAN_Time = "receive_time"

  // ???????????????????????????????????????CAN ID ??????
  val Keyword_EngineRotation = "???????????????"
  val Keyword_EngineLoadRate = "??????????????????"
  val Keyword_ThrottleOpening = "??????????????????"
  val Keyword_FootBrake = "??????"
  val KEYWORD_CLUTCH = "???????????????"
  val KEYWORD_VECHICLE_SPEED = "??????"
  val KEYWORD_TORSIONAL_MOMENT = "??????"

  val SIGNAL_TYPE_ENGINEROTATION = "EngineRotation"
  val SIGNAL_TYPE_THROTTLEOPENING = "ThrottleOpening"
  val SIGNAL_TYPE_VECHICLE_SPEED = "VechicleSpeed"
  val SIGNAL_TYPE_TORSIONAL_MOMENT = "TorsionalMoment"

  val Default_ACC_Levels = Array(0.3, 0.6, 0.9, 1.2)

  def rpmCounts2CaseClass(tripId: String, tripEndTime: Long, m: Map[Int, Int]) =
    if (m.isEmpty) {
      ENGINE_RPM(tripId, new Timestamp(tripEndTime))
    } else {
      val below_800: Int = m.getOrElse(0, 0)
      val below_1000: Int = m.getOrElse(1, 0)
      val below_1200: Int = m.getOrElse(2, 0)
      val below_1300: Int = m.getOrElse(3, 0)
      val below_1400: Int = m.getOrElse(4, 0)
      val below_1500: Int = m.getOrElse(5, 0)
      val below_1600: Int = m.getOrElse(6, 0)
      val below_1700: Int = m.getOrElse(7, 0)
      val below_1800: Int = m.getOrElse(8, 0)
      val below_2000: Int = m.getOrElse(9, 0)
      val below_2200: Int = m.getOrElse(10, 0)
      val above_2200: Int = m.getOrElse(-1, 0)
      val total_count: Int = m.values.sum
      ENGINE_RPM(
        tripId,
        new Timestamp(tripEndTime),
        below_800,
        below_1000,
        below_1200,
        below_1300,
        below_1400,
        below_1500,
        below_1600,
        below_1700,
        below_1800,
        below_2000,
        below_2200,
        above_2200,
        total_count
      )
    }

  def toCounts2CaseClass(tripId: String, tripEndTime: Long, m: Map[Int, Int]) =
    if (m.isEmpty) {
      ThrottleOpening(tripId, new Timestamp(tripEndTime))
    } else {
      val below_20 = m.getOrElse(0, 0)
      val below_40 = m.getOrElse(1, 0)
      val below_60 = m.getOrElse(2, 0)
      val below_80 = m.getOrElse(3, 0)
      val below_100 = m.getOrElse(4, 0)
      val total_count = m.values.sum
      ThrottleOpening(tripId,
                      new Timestamp(tripEndTime),
                      below_20,
                      below_40,
                      below_60,
                      below_80,
                      below_100,
                      total_count)
    }

}

object EngineRpmDistributionTable {

  val TABLE_NAME = "dbha_distribution_engine_rpm"

  val FIELD_ID = "id"
  val FIELD_VEHICLE_ID = "vehicle_id"
  val FIELD_DRIVER_ID = "driver_id"
  val FIELD_TRIP_ID = "trip_id"
  val FIELD_TRIP_ENDTIME = "trip_end_time"
  val FIELD_TOTAL_COUNT = "total_count"

  val FIELDS_RPM_LEVEL = Array(
    "below_800",
    "below_1000",
    "below_1200",
    "below_1300",
    "below_1400",
    "below_1500",
    "below_1600",
    "below_1700",
    "below_1800",
    "below_2000",
    "below_2200",
    "above_2200"
  )

  val Default_RPM_Levels =
    Array(800, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 2000, 2200)

  case class ENGINE_RPM(tripId: String,
                        tripEndTime: Timestamp,
                        below_800: Int = 0,
                        below_1000: Int = 0,
                        below_1200: Int = 0,
                        below_1300: Int = 0,
                        below_1400: Int = 0,
                        below_1500: Int = 0,
                        below_1600: Int = 0,
                        below_1700: Int = 0,
                        below_1800: Int = 0,
                        below_2000: Int = 0,
                        below_2200: Int = 0,
                        above_2200: Int = 0,
                        total_count: Long = 0) {
    override def toString =
      s"'$tripId','$tripEndTime',$below_800,$below_1000,$below_1200,$below_1300,$below_1400,$below_1500," + s"$below_1600,$below_1700,$below_1800,$below_2000,$below_2200,$above_2200,$total_count"
  }

}

object ThrottleOpeningDistributionTable {

  val TABLE_NAME = "dbha_distribution_throttle_opening"

  val FIELD_ID = "id"
  val FIELD_VEHICLE_ID = "vehicle_id"
  val FIELD_DRIVER_ID = "driver_id"
  val FIELD_TRIP_ID = "trip_id"
  val FIELD_TRIP_ENDTIME = "trip_end_time"
  val FIELD_TOTAL_COUNT = "total_count"

  val Default_TO_Levels = Array(20, 40, 60, 80, 100)
  val FIELDS_OPENING_LEVEL =
    Array("below_20", "below_40", "below_60", "below_80", "below_100")

  case class ThrottleOpening(tripId: String,
                             tripEndTime: Timestamp,
                             below_20: Int = 0,
                             below_40: Int = 0,
                             below_60: Int = 0,
                             below_80: Int = 0,
                             below_100: Int = 0,
                             total_count: Long = 0) {
    override def toString: String =
      s"'$tripId','$tripEndTime',$below_20,$below_40,$below_60,$below_80,$below_100,$total_count"
  }

}

object DriveStyleTable {

  val TABLE_NAME = "dbha_analysis_drive_style"
  val FIELD_ID = "id"
  val FIELD_VEHICLE_ID = "vehicle_id"
  val FIELD_DRIVER_ID = "driver_id"
  val FIELD_TRIP_ID = "trip_id"
  val FIELD_TRIP_ENDTIME = "trip_end_time"
  val FIELD_TOTAL_COUNT = "total_count"

  val FIELT_BRAKE_TIME = "total_brake_time"
  val FIELD_IDLE_TIME = "total_idle_time"
  //?????????????????????????????????????????????
  val FIELD_ECO_DRIVE = "ecoDrive"
  // ????????????????????????
  val FIELD_LOW_LOAD_LOW_RPM = "lowLoad_lowRpm"
  // ????????????????????????
  val FIELD_HIGH_LOAD_HIGH_RPM = "highLoad_lowRpm"
  // ??????????????????
  val FIELD_LOW_LOAD_HIGH_RPM = "lowLoad_highRpm"
  // ????????????????????????
  val FIELD_ABOCE_ECO_DRIVE = "above_ecoDrive"
  // ????????????
  val FIELD_SUPER_HIGH_RPM = "super_highRpm"

  case class DriveStyle(tripId: String,
                        tripEndTime: Timestamp,
                        totalBrakeTime: Long,
                        totalEngineIdleTime: Long,
                        ecoDrive: Long = 0,
                        lowLoadLowRpm: Long = 0,
                        highLoadLowRpm: Long = 0,
                        lowLoadHighRpm: Long = 0,
                        aboveEcoDrive: Long = 0,
                        superHighRpm: Long) {
    override def toString: String =
      s"'$tripId','$tripEndTime',$totalBrakeTime,$totalEngineIdleTime,$ecoDrive,$lowLoadLowRpm,$highLoadLowRpm," + s"$lowLoadHighRpm,$aboveEcoDrive,$superHighRpm"
  }

}
