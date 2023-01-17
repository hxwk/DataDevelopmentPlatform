package com.dfssi.dataplatform.analysis.dbha.driverbehavior.local

import java.sql.Timestamp

import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.DriveStyleTable.DriveStyle
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.{EngineRpmDistributionTable, ThrottleOpeningDistributionTable}
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.EngineRpmDistributionTable.ENGINE_RPM
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.ThrottleOpeningDistributionTable.ThrottleOpening
import org.apache.spark.sql.Row

import scala.collection.mutable.ArrayBuffer

class SingleTripDescriptor(vid: String, paramsMap: Map[String, String]) extends Serializable {

  import SingleTripDescriptor._
  
  def tripProcess(tripId: String, tripEndtime: Long, _0200Rdd: Array[Row], _0705Rdd: Array[Row]) = {
    val rpmDis = distribution_rpm(_0705Rdd)
    val enginerpm: ENGINE_RPM = rpmCounts2CaseClass(tripId, tripEndtime, rpmDis)
//    OutputRdbms.saveRpmDistribution(enginerpm)
    //
    val throttleOpeningDis = distribution_throttleOpening(_0705Rdd)
    val opening: ThrottleOpening = toCounts2CaseClass(tripId, tripEndtime, throttleOpeningDis)
//    OutputRdbms.saveThrottleOpeingDistribution(opening)

    val driveStyle = analysis_driveStyle(tripId, tripEndtime, _0200Rdd, _0705Rdd)
//    OutputRdbms.saveDriveStyle(driveStyle)

  }

  /**
    * 发动机转速分布情况统计
    * @param _0705Rdd   CAN报文协议数据
    * @return 各级别出现频次
    */
  def distribution_rpm(_0705Rdd: Array[Row]): Map[Int, Int] = {
    // rpmLevels  rpm离散化级别
    val rpmLevels = paramsMap.get("rpmLevels") match {
      case Some(x) => x.split(",").map(_.toInt)
      case None => EngineRpmDistributionTable.Default_RPM_Levels
    }


    _0705Rdd.filter(_.getAs[String](Field_SignalName).contains(Keyword_EngineRotation))
      .map{ row =>
        val value = row.getAs[Double](Field_Value)
        rpmLevels.indexWhere(level => value <= level) -> 1
        // note: -1 means rpm value > rpmLevels.last
      }
        .groupBy(_._1)
        .mapValues(_.map(_._2).sum)
  }

  /**
    * 油门踏板开度分布
    * @param _0705Rdd CAN报文协议数据
    * @return 各级别出现频次
    */
  def distribution_throttleOpening(_0705Rdd: Array[Row]) = {
    // 开度离散化级别
    val toLevels = paramsMap.get("throttleOpeningLevels") match {
      case Some(x) => x.split(",").map(_.toInt)
      case None => ThrottleOpeningDistributionTable.Default_TO_Levels
    }

    _0705Rdd.filter(_.getAs[String](Field_SignalName).contains(Keyword_ThrottleOpening))
      .map{ row =>
        val value = row.getAs[Double](Field_Value)
        toLevels.indexWhere(level => value <= level) -> 1
        // note: indexWhere should not return -1
      }
      .groupBy(_._1)
      .mapValues(_.map(_._2).sum)
  }


  /**
    * 速度分布
    * @param _0200Rdd  行车记录仪数据
    * @return
    */
  def speedDistribution(_0200Rdd: Array[Row], accLevels: Array[Double] = Default_ACC_Levels) = {
    // 速度波动系数，连续加速度的微分差，并取绝对值
    val accPartialRangeCount = ArrayBuffer[Int]()

    val velocityRdd = _0200Rdd.map{ row =>
      (row.getAs[Long](Field_GPS_Time), row.getAs[Long](Field_GPS_Speed))
    }.sortBy(_._1)

    assert(velocityRdd.length >= 2)

    velocityRdd.sliding(2).map{case Array(e1, e2) =>
      val timeDiff = e2._1 - e1._1
      if(timeDiff == 0) -1 -> 1
      else {
        val acc = (e2._2- e1._2)*1.0/timeDiff
        if(acc < 0.3) 0 -> 1
        else if(acc < 0.6) 1 -> 1
        else if(acc < 0.9) 2 -> 1
        else if(acc < 1.2) 3 -> 1
        else 4 -> 1
      }
    }.toList
      .groupBy(_._1)
      .mapValues(_.length)
  }

  def analysis_driveStyle(tripId: String, tripEndTime: Long,
                          _0200Rdd: Array[Row],
                          _0705Rdd: Array[Row],
                          CANCaptureRate: Int = 1, // CAN信号采集频率，默认1次/s
                          ecoLoadRateRange: (Int, Int) = (40, 80),
                          ecoRpmRange: (Int, Int) = (800, 1800),
                          idleRpmRange: (Int, Int) = (20, 800),
                          superHighRpm: Int = 2200) = {
    // 连续两个相同信号的基于采集频率的最大时间间隔，如果在间隔内则认为是连续的信号
    val nanoSecondsGapBetweenSignals = CANCaptureRate * 1000 + 1000 // 给个1秒的弹性时间

    /** 刹车时长 */
    val brakeSignalData = _0705Rdd.filter { row =>
      row.getAs[String](Field_SignalName).contains(Keyword_FootBrake) &&
        row.getAs[Double](Field_Value).toInt == 1 // TODO 确定踩脚刹的信号值是1??
    }
    // 信号出现次数 除以 信号采集频率 即为信号时长
    val totalBrakeTime = brakeSignalData.length / CANCaptureRate // 秒
    println(totalBrakeTime)

    var (preT, startTime) = (-1L, -1L)
    /** 怠速时长 */
    val zeroSpeedRanges = ArrayBuffer[(Long, Long)]()
    _0200Rdd.filter(_.getAs[Long](Field_Speed) == 0).map(_.getAs[Long](Field_GPS_Time))
      .sortBy(t => t)
      .foreach { t =>
        if (-1 != preT) {
          if (t - preT > nanoSecondsGapBetweenSignals) {
            zeroSpeedRanges.append((startTime, preT))
            startTime = t
          }
        } else startTime = t
        preT = t
      }

    val totalEngineIdleTime = if(zeroSpeedRanges.isEmpty) {
      0
    } else {
      val c = _0705Rdd.count { row =>
        row.getAs[String](Field_SignalName).contains(Keyword_EngineRotation) &&
          row.getAs[Double](Field_Value) < idleRpmRange._2 &&
          zeroSpeedRanges.exists{ case(s,e) =>
            val t = row.getAs[Long](Field_CAN_Time)
            s <= t && t <= e
          }
      }
      c / CANCaptureRate // 秒
    }
    println(totalEngineIdleTime)

    /** 经济驾驶（经济转速、经济负荷）低负荷低转速时间 高负荷低转速时间 低负荷高转速 高于经济区域行驶 超高转速 */
    preT = -1L
    startTime = -1L
    val relatedRdd = _0705Rdd.filter { row =>
      val signalName = row.getAs[String](Field_SignalName)
      signalName.contains(Keyword_EngineRotation) || signalName.contains(Keyword_EngineLoadRate)
    }.map{ row =>
      val v = row.getAs[Double](Field_Value)
      val level =  if(row.getAs[String](Field_SignalName).contains(Keyword_EngineRotation)){
        if(v < ecoRpmRange._1) 10                         //低转速
        else if(v <= ecoRpmRange._2) 11                   // 经济转速
        else if(v <= superHighRpm) 12                     // 高转速
        else 15                                          // 超高转速
      } else {
        if(v < ecoLoadRateRange._1) 20                      // 低负荷
        else if(v <= ecoLoadRateRange._2) 21               // 经济负荷
        else 22                                                    // 高负荷
      }
      (row.getAs[Long](Field_CAN_Time), level)
    }

    // 经济驾驶         个位为1
    preT = -1L
    var ecoDriveTime = 0L
    relatedRdd.filter{ case(_,flag)=> flag%10 == 1}.map(_._1)
      .sorted.foreach{ t =>
      if(-1 != preT) {
        if(t - preT <= nanoSecondsGapBetweenSignals)
          ecoDriveTime += t-preT
      }
      preT = t
    }
    println(ecoDriveTime)

    // 低负荷低转速时间 个位为0
    preT = -1L
    var lowLoadLowRotationTime = 0L
    relatedRdd.filter{ case(_,flag)=> flag%10 == 0}.map(_._1)
       .sorted.foreach{ t =>
      if(-1 != preT) {
        if(t - preT <= nanoSecondsGapBetweenSignals)
          lowLoadLowRotationTime += t-preT
      }
      preT = t
    }
    println(lowLoadLowRotationTime)

    // 低负荷高转速     被4整除, xxx10,11,15,21,22xxx, 12 20
    preT = -1L
    var lowLoadHighRotationTime = 0L
    relatedRdd.filter{ case(_,flag)=> flag%4 == 0 || flag == 15}.map(_._1)
      .sorted.foreach{ t =>
      if(-1 != preT) {
        if(t - preT <= nanoSecondsGapBetweenSignals)
          lowLoadHighRotationTime += t-preT
      }
      preT = t
    }
    println(lowLoadHighRotationTime)
    // 高负荷低转速时间 %3==1 10,22  xxx11,12,15,20,21xxx
    preT = -1L
    var highLoadlowRotationTime = 0L
    relatedRdd.filter{ case(_,flag)=> flag%3 == 1}.map(_._1)
      .sorted.foreach{ t =>
      if(-1 != preT) {
        if(t - preT <= nanoSecondsGapBetweenSignals)
          highLoadlowRotationTime += t-preT
      }
      preT = t
    }
    println(highLoadlowRotationTime)
    // 高于经济区域行驶 >11 and >21
    preT = -1L
    var aboveEcoAreaTime = 0L
    relatedRdd.filter{ case(_,flag)=> flag>11 && flag>21}.map(_._1)
      .sorted.foreach{ t =>
      if(-1 != preT) {
        if(t - preT <= nanoSecondsGapBetweenSignals)
          aboveEcoAreaTime += t-preT
      }
      preT = t
    }
    println(aboveEcoAreaTime)
    // 超高转速         ==15
    preT = -1L
    var superHighRpmTime = 0L
    relatedRdd.filter(_._2 == 15).map(_._1)
      .sorted.foreach{ t =>
      if(-1 != preT) {
        if(t - preT <= nanoSecondsGapBetweenSignals)
          superHighRpmTime += t-preT
      }
      preT = t
    }
    println(superHighRpmTime)

    DriveStyle(tripId, new Timestamp(tripEndTime), totalBrakeTime, totalEngineIdleTime,
      ecoDriveTime,lowLoadLowRotationTime,lowLoadHighRotationTime,highLoadlowRotationTime,
      aboveEcoAreaTime, superHighRpmTime)
  }
}


object SingleTripDescriptor {
  // Hive table fields
  val Field_SignalName = "signal_name"
  val Field_Value = "value"
  val Field_Speed = "speed"
  val Field_GPS_Time = "gps_time"
  val Field_GPS_Speed = "speed1"
  val Field_CAN_Time = "receive_time"

  // 下面两个信号是出现在同一个CAN ID 中的
  val Keyword_EngineRotation = "发动机转速"
  val Keyword_EngineLoadRate = "发动机负荷率"

  def rpmCounts2CaseClass(tripId: String, tripEndTime: Long, m: Map[Int, Int])= if(m.isEmpty)
    ENGINE_RPM(tripId, new Timestamp(tripEndTime))
  else {
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
    ENGINE_RPM(tripId, new Timestamp(tripEndTime), below_800, below_1000, below_1200, below_1300, below_1400, below_1500, below_1600,
      below_1700, below_1800, below_2000, below_2200, above_2200, total_count)
  }



  val Keyword_ThrottleOpening = "油门踏板开度"

  def toCounts2CaseClass(tripId: String, tripEndTime: Long, m: Map[Int, Int]) = if(m.isEmpty)
    ThrottleOpening(tripId, new Timestamp(tripEndTime))
  else {
    val below_20 = m.getOrElse(0, 0)
    val below_40 = m.getOrElse(1, 0)
    val below_60 = m.getOrElse(2, 0)
    val below_80 = m.getOrElse(3, 0)
    val below_100 = m.getOrElse(4, 0)
    val total_count = m.values.sum
    ThrottleOpening(tripId, new Timestamp(tripEndTime), below_20, below_40, below_60, below_80, below_100, total_count)
  }

  val Keyword_FootBrake = "脚刹"

  val Default_ACC_Levels = Array(0.3, 0.6, 0.9, 1.2)
}