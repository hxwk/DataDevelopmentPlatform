package com.dfssi.dataplatform.analysis.dbha.driverbehavior.rdd

import com.dfssi.dataplatform.analysis.dbha.DriveBehaviorUtils._
import com.dfssi.dataplatform.analysis.dbha.SingleTripProcess.{intersectTimeRanges, produceTimeRangesByValueFilter, smoothByValueAvg}
import org.apache.spark.Logging
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer


class ProtocolRddProcess(paramsMap: Map[String, String],
                        _0200CaptureTimesPerSec: Float,
                         millisGapBetween2Same0200Records: Float,
                         _0705CaptureTimesPerSec: Float,
                         millisGapBetween2Same0705Records: Float,
                         maxMillisIntervalPerEvent: Float) extends Serializable with Logging {

  /**
    * 获取相关告警数据，包括：车身故障报警、疲劳驾驶、超市行驶、路线偏离、超重……
    * 告警频率
    *
    * @param terminal_0200Rdd 行驶数据
    * @param alarmFrequency   告警频率, 单位 次/s，默认为1
    * @return Map(0 -> xx, 1 -> xx, 2->xx, 3->xx)
    *         其中0：故障告警，1：疲劳驾驶告警，2：超速行驶告警，3：路线偏离告警
    */
  def extractAlarmEvents_1(terminal_0200Rdd: RDD[Row], alarmFrequency: Int = 1): Map[Int, Long] = {
    /********************************************************************************/
    // Debug info
    val startRunTimeMillis = System.currentTimeMillis()
    logError(s"DVRProtocolDataProcess运行extractAlarmEvents，开始时间${startRunTimeMillis/1000}秒")
    /********************************************************************************/

    val res = terminal_0200Rdd.mapPartitions { part =>
      part.flatMap { row =>
        val alarms = row.getAs[mutable.WrappedArray[String]](Field_Alarms)
        val alarmCountArr = Array(0, 0, 0, 0)
        updateAlramCount(alarmCountArr, alarms)
        // (countFatigue, countOverspeed, countRouteBias, countMalfunction)
        alarmCountArr.indices.flatMap { idx =>
          if(alarmCountArr(idx) > 0) Some(idx) else None
        }
      }
    }
      .countByValue()
      //      .reduceByKey(_ + _)
      .mapValues(_ / alarmFrequency)
      .toMap

    /***********************************************************************************/
    // Debug info
    val durationTimeMillis = (System.currentTimeMillis() - startRunTimeMillis) / 1000
    logError(s"运行extractAlarmEvents结束，耗时$durationTimeMillis 秒")
    /***********************************************************************************/

    res
  }

  /**
    * 大油门行驶		速度>0km/h，连续油门开度>60%，持续5s
    * 满油行驶		速度>0km/h，连续油门开度>95%，持续2s以上
    *
    * @param _0705ThrottleOpeningRdd     CAN报文协议数据
    * minSpeedWithHighOpening     最小行驶速度
    * minThrottleOpening 最小油门开度阈值
    * minHighOpeningMillisec          最小大油门持续时间
    * fullThrottleOpening  全油门开度阈值
    * minFullThrottheMillisec  最小全油门持续时间
    * @return   大油门行驶次数、满油行驶次数、大油门行驶时间（秒）
    */
  def extractDriveAtHighThrottleOpenEvents_3(_0200ValidData: Array[CollectedDvrItem],
                                             _0705ThrottleOpeningRdd: RDD[Timeseries]): (Long, Long, Double) = {

    /********************************************************************************/
    // Debug info
    val startRunTimeMillis = System.currentTimeMillis()
    logError(s"DVRProtocolDataProcess运行extractDriveAtHighThrottleOpenEvents，开始时间${startRunTimeMillis/1000}秒")
    /********************************************************************************/

    val minSpeedWithHighOpening: Double = paramsMap.getOrElse("minSpeedAtHighThrottleOpen", "20").toDouble * 10
    val minThrottleOpening: Double = paramsMap.getOrElse("minHighThrottleOpening", "60").toDouble
    val minHighOpeningMillis: Double = paramsMap.getOrElse("minHighOpeningMillisec", "5").toDouble * 1000
    val fullThrottleOpening: Double =paramsMap.getOrElse("minFullThrottleOpening", "95").toDouble
    val minFullThrottheMillis = paramsMap.getOrElse("minFullThrottheLastTime", "2").toDouble * 1000

    val validSpeedRdd = _0200ValidData.filter(_.speed >= minSpeedWithHighOpening).map(_.timestamp -> 2)

    val sorted_throttle = _0705ThrottleOpeningRdd.filter(_.value >= minThrottleOpening)
      .map { case Timeseries(ts,v) =>
        if(v >= fullThrottleOpening)  (ts, 0)
        else (ts, 1)
      }.distinct()
      .collect().union(validSpeedRdd)
      .sortBy(_._1)

    // 大油门行驶次数，满油行驶次数，大油门行驶时间
    val driveWithHighThrottleEventCount = Array(0L, 0L, 0L)

    val firstSatifyTime = mutable.Map(0-> -1L, 1-> -1L, 2-> -1L)
    var preT = -1L
    val prevTime4ecahFlag = mutable.Map(0-> -1L, 1-> -1L, 2-> -1L)

    sorted_throttle.foreach{ case(ts, flag) =>
      if(-1 != preT) {
        if(ts - preT > millisGapBetween2Same0705Records) {
          if(-1 != firstSatifyTime(2)) {
            // 大油门判断
            if(-1 != firstSatifyTime(0)) {
              val startTime = math.max(firstSatifyTime(0), firstSatifyTime(2))
              val endTime = math.min(prevTime4ecahFlag(0), prevTime4ecahFlag(2))
              if(endTime - startTime >= minHighOpeningMillis) {
                driveWithHighThrottleEventCount(0) += 1
                driveWithHighThrottleEventCount(2) += endTime - startTime
//                println(s"发现大油门行驶事件，起始点时间$startTime-$endTime")
              }
            }
            // 满油门判断
            if(-1 != firstSatifyTime(1)) {
              val startTime = math.max(firstSatifyTime(1), firstSatifyTime(2))
              val endTime = math.min(prevTime4ecahFlag(1), prevTime4ecahFlag(2))
              if(endTime - startTime >= minFullThrottheMillis)
                driveWithHighThrottleEventCount(1) += 1
//              println(s"发现满油门行驶事件，起始点时间$startTime-$endTime")
            }
          }
          firstSatifyTime.keys.foreach{ k =>
            firstSatifyTime(k)= -1
            prevTime4ecahFlag(k) = ts
          }
        } else flag match {
          case 0 => // 大油门
            if(-1 == firstSatifyTime(flag)) firstSatifyTime(flag) = ts
            else if(ts - prevTime4ecahFlag(flag) > maxMillisIntervalPerEvent) {
              if(-1 != firstSatifyTime(2)) {
                val startTime = math.max(firstSatifyTime(flag), firstSatifyTime(2))
                val endTime = math.min(prevTime4ecahFlag(flag), prevTime4ecahFlag(2))
                if(endTime - startTime >= minHighOpeningMillis) {
                  driveWithHighThrottleEventCount(0) += 1
                  driveWithHighThrottleEventCount(2) += endTime - startTime
//                  println(s"发现大油门行驶事件，起始点时间$startTime-$endTime")
                }
              }
              firstSatifyTime(flag) = -1
            }
            prevTime4ecahFlag(flag) = ts
          case 1 => // 满油门
            if(-1 == firstSatifyTime(flag)) {
              firstSatifyTime(flag) = ts
            } else if(ts - prevTime4ecahFlag(flag) > maxMillisIntervalPerEvent) {
              if(-1 != firstSatifyTime(2)) {
                val startTime = math.max(firstSatifyTime(flag), firstSatifyTime(2))
                val endTime = math.min(prevTime4ecahFlag(flag), prevTime4ecahFlag(2))
                if(endTime - startTime >= minFullThrottheMillis)
                  driveWithHighThrottleEventCount(1) += 1
//                println(s"发现满油门行驶事件，起始点时间$startTime-$endTime")
              }
              firstSatifyTime(flag) = -1
            }
            prevTime4ecahFlag(flag) = ts

            // 满足满油门也同时满足大油门
            if(-1 == firstSatifyTime(0)) firstSatifyTime(0) = ts
            else if(ts - prevTime4ecahFlag(0) > maxMillisIntervalPerEvent) {
              if(-1 != firstSatifyTime(2)) {
                val startTime = math.max(firstSatifyTime(0), firstSatifyTime(2))
                val endTime = math.min(prevTime4ecahFlag(0), prevTime4ecahFlag(2))
                if(endTime - startTime >= minHighOpeningMillis) {
                  driveWithHighThrottleEventCount(0) += 1
                  driveWithHighThrottleEventCount(2) += endTime - startTime
//                  println(s"发现大油门行驶事件，起始点时间$startTime-$endTime")
                }
              }
              firstSatifyTime(flag) = -1
            }
            prevTime4ecahFlag(0) = ts
          case 2 => // 速度
            if(-1 == firstSatifyTime(flag)) firstSatifyTime(flag) = ts
            else if(ts - prevTime4ecahFlag(flag) > maxMillisIntervalPerEvent) {
              // 大油门判断
              if(-1 != firstSatifyTime(0)) {
                val startTime = math.max(firstSatifyTime(0), firstSatifyTime(2))
                val endTime = math.min(prevTime4ecahFlag(0), prevTime4ecahFlag(2))
                if(endTime - startTime >= minHighOpeningMillis) {
                  driveWithHighThrottleEventCount(0) += 1
                  driveWithHighThrottleEventCount(2) += endTime - startTime
//                  println(s"发现大油门行驶事件，起始点时间$startTime-$endTime")
                }
              }
              // 满油门判断
              if(-1 != firstSatifyTime(1)) {
                val startTime = math.max(firstSatifyTime(1), firstSatifyTime(2))
                val endTime = math.min(prevTime4ecahFlag(1), prevTime4ecahFlag(2))
                if(endTime - startTime >= minFullThrottheMillis)
                  driveWithHighThrottleEventCount(1) += 1
//                println(s"发现满油门行驶事件，起始点时间$startTime-$endTime")
              }
              firstSatifyTime(flag) = -1
            }
            prevTime4ecahFlag(flag) = ts
        }
      }
      preT = ts
    }

    /***********************************************************************************/
    // Debug info
    val durationTimeMillis = (System.currentTimeMillis() - startRunTimeMillis) / 1000
    logError(s"运行extractDriveAtHighThrottleOpenEvents结束，耗时$durationTimeMillis 秒")
    /***********************************************************************************/

    (driveWithHighThrottleEventCount(0),
      driveWithHighThrottleEventCount(1),
      driveWithHighThrottleEventCount(2)/1000.0)
  }



  /**
    * 空挡滑行
    * @param _0705Rdd   CAN报文协议数据
    * @return  滑行次数
    */
  def extractCoastingWithNullPosEvent_4(_0200ValidData: Array[CollectedDvrItem],
                                        cached0705RpmRdd: RDD[Timeseries],
                                        cached0705ThrottleOpening: RDD[Timeseries],
                                        _0705Rdd: RDD[Row]): Int = {
    /********************************************************************************/
    // Debug info
    val startRunTimeMillis = System.currentTimeMillis()
    logError(s"DVRProtocolDataProcess运行extractCoastingWithNullPosEvent，开始时间${startRunTimeMillis/1000}秒")
    /********************************************************************************/

    val idleRange = paramsMap.get("idleRotationRange") match {
      case Some(x) =>
        val arr = x.split("-")
        (arr(0).toInt, arr(1).toInt)
      case None => DefaultIdleRotationRange
    }
    val maxTorque = paramsMap.getOrElse("minTorque", "5").toFloat // %
    val minSpeedWhileCoasting = paramsMap.getOrElse("minSpeedWhileCoasting", "20").toFloat

    var startTime = -1L
    var preT = -1L
    val aboveMinSpeedRanges = ArrayBuffer[(Long, Long)]()

    // 先找到速度大于minSpeedWhileCoasting的时间段
    _0200ValidData.filter(_.speed >= minSpeedWhileCoasting).map(_.timestamp)
      .sorted
      .foreach { t =>
        if (-1 == preT) startTime = t
        else if (t - preT > millisGapBetween2Same0705Records) {
          if (startTime < preT) {
            aboveMinSpeedRanges.append((startTime, preT))
          }
          startTime = t
        }
        preT = t
      }

    if (aboveMinSpeedRanges.isEmpty) return 0

    // 先广播时间段
    val bcAboveMinSpeedRanges = _0705Rdd.sparkContext.broadcast(aboveMinSpeedRanges)

    val validRpmRdd = cached0705RpmRdd.filter{ case Timeseries(ts, value) =>
      isInRange(idleRange, value) &&
        bcAboveMinSpeedRanges.value.exists(p => p._1 <= ts && ts <= p._2)
    }
      .map(_.timestamp -> 0)

    val validTORdd =  cached0705ThrottleOpening.filter{ case Timeseries(ts, value) =>
      (value <= 1e-2) && bcAboveMinSpeedRanges.value.exists(p => p._1 <= ts && ts <= p._2)
    }
      .map(_.timestamp -> 1)


    val validSignals = _0705Rdd
      .filter { row =>
        val signal = row.getAs[String](Field_SignalName)
        val ts = row.getAs[Long](Field_CAN_Time)
        val v = row.getAs[Double](Field_Value)
        (signal.contains("扭矩") && v <= maxTorque) &&
          bcAboveMinSpeedRanges.value.exists(p => p._1 <= ts && ts <= p._2)
      }
      .map (_.getAs[Long](Field_CAN_Time) -> 2)
      .union(validRpmRdd)
      .union(validTORdd)
      .collect().sortBy(_._1)

    val satisfyMap = mutable.Map(0 -> -1L, 1 -> -1L, 2 -> -1L) //, 3 -> -1L
    var coastingCount = 0
    var firstSatisfyTime = -1L

    validSignals.foreach {
      case (t, v) =>
        if (-1 == firstSatisfyTime) { // 第一条记录
          firstSatisfyTime = t
          satisfyMap(v) = firstSatisfyTime
        } else {
          val deltaT = t - firstSatisfyTime
          if (deltaT >= maxMillisIntervalPerEvent) {
            // 该信号与satisfyMap最早出现的其他信号时差大于2秒，则重置最早时间对应信号
            val tmp = firstSatisfyTime
            satisfyMap.foreach {
              case (flag, time) =>
                if (time <= tmp) satisfyMap(flag) = -1
                else if (firstSatisfyTime == tmp || firstSatisfyTime > time)
                  firstSatisfyTime = time
            }
            satisfyMap(v) = t
          } else if (-1 != satisfyMap(v)) {
            // 该信号已经出现，如果t和firstSatisfyTime相同则忽略
            if (t > firstSatisfyTime) {
              satisfyMap(v) = t
              firstSatisfyTime =
                satisfyMap.filterNot(_._2 != -1).minBy(_._2)._2
            }
          } else satisfyMap(v) = t
        }
        // 四个条件是否都满足
        if (satisfyMap.forall(_._2 != -1)) {
          coastingCount += 1
          firstSatisfyTime = -1
          0 until satisfyMap.size foreach (idx => satisfyMap(idx) = -1)
        }
    }
    /***********************************************************************************/
    // Debug info
    val durationTimeMillis = (System.currentTimeMillis() - startRunTimeMillis) / 1000
    logError(s"运行extractCoastingWithNullPosEvent结束，耗时$durationTimeMillis 秒")
    /***********************************************************************************/
    coastingCount
  }

  /**
    * 先离合后刹车：
    *
    * 注：离合器开关 脚刹 手刹 属于同一个CAN ID
    *
    * 速度>30km/h,刹车信号和离合信号同时出现，正常是先出现刹车信号再出现离合信号
    * @param _0705Rdd     CAN报文协议数据
    * maxMillisecIntervalPerEvent 事件的最大毫秒持续间隔
    * maxMillisecGap   最大时间差
    * minSpeed     最小速度
    * minDisLastMillisec   最小离合持续时间
    * minBrakeLastMillisec 最小刹车持续时间
    * @return   disconnectBeforeBrakeCount,
                longBrakeCount,
                longDisconnectCount,
                totalClutchDuration, 离合时长（秒）
                totalBrakeDuration   刹车时长（秒）
    */
  def extractClutchAndBrakeEvents_5(_0200ValidData: Array[CollectedDvrItem],
                                     _0705Rdd: RDD[Row]): (Int, Int, Int, Float, Double, Double) = {

    /*******************************************************************************************/
    // debug info
    val startRunTime = System.currentTimeMillis()
    logError(s"CANProtocolDataProcess开始extractClutchAndBrakeEvents过程，开始时间$startRunTime")
    /*********************************************************************************************/

    val minSpeedBeforeBrake = paramsMap.getOrElse("minSpeedBeforeBrake", "30").toDouble * 10 // 0.1Km/h
    val minDisLastMillisec = paramsMap.getOrElse("minDisconnectLastTime", "4").toInt * 1000
    val minBrakeLastMillisec = paramsMap.getOrElse("minBrakeLastTime", "4").toInt * 1000

    val validSpeedRanges = produceTimeRangesByValueFilter(_0200ValidData, 2, minSpeedBeforeBrake, 1)

    // 先处理离合数据，
    val clutchRdd = _0705Rdd.filter{ row =>
      row.getAs[String](Field_SignalName).contains("离合")
    }.map(r => r.getAs[Long](Field_CAN_Time) -> r.getAs[Double](Field_Value)).distinct()
    // TODO 离散数据平滑改善
    val clutchTimeseries = smoothByValueAvg(clutchRdd).collect().sortBy(_.timestamp)
    val validClutchRanges = produceTimeRangesByValueFilter(clutchTimeseries, 0.5, 2)

    // 总离合时间
    val totalClutchDuration = if(validClutchRanges.isEmpty) 0
    else validClutchRanges.map(p => p._2 - p._1).sum / 1000.0

    // 再处理刹车数据，离散数据不需要平滑
    val brakeRdd = _0705Rdd.filter{ row =>
      row.getAs[String](Field_SignalName).contains("脚刹")
    }.map(r => r.getAs[Long](Field_CAN_Time)-> r.getAs[Double](Field_Value)).distinct()
    // TODO 离散数据平滑改善
    val brakeTimeSeries = smoothByValueAvg(brakeRdd).collect().sortBy(_.timestamp)
    // 总的刹车时长
    val validBrakeRanges = produceTimeRangesByValueFilter(brakeTimeSeries, 0.5, 2)
    // 刹车时长
    val totalBrakeDuration = if(validBrakeRanges.isEmpty) 0
    else validBrakeRanges.map(p => p._2 - p._1).sum / 1000.0

    // 刹车里程
    var i = 0
    var firstBrakeMile = -1L
    var cumBrakeMile = 0L
    var cumBrakeMile2 = 0.0
    var cumBrakeMile3 = 0.0
    var sumSpeedPerRange = 0.0
    var countSpeedPerRange = 0

    _0200ValidData.foreach { item =>
      val (t,v,m) = (item.timestamp, item.speed, item.mile)
      if (i < validBrakeRanges.length) {
        val (start, end) = validBrakeRanges(i)
        if (start <= t && t <= end) {
          if (-1 == firstBrakeMile) firstBrakeMile = m
          cumBrakeMile2 += v/36000.0 //没出现一次就用瞬时速度乘以1/3600换算成距离
          // TODO 计算该区间的平均速度再乘以这段时长得到距离
          countSpeedPerRange += 1
          sumSpeedPerRange += v
        } else if (-1 != firstBrakeMile && i > 0) {
          cumBrakeMile += m - firstBrakeMile
          firstBrakeMile = -1
          // TODO 计算该区间的平均速度再乘以这段时长得到距离
          cumBrakeMile3 += sumSpeedPerRange/countSpeedPerRange * (validBrakeRanges(i-1)._2 - validBrakeRanges(i-1)._1) / 36000.0
          sumSpeedPerRange = 0
          countSpeedPerRange = 0
        }
        while (i < validBrakeRanges.length && t > validBrakeRanges(i)._2) {
          i += 1
        }
      }
    }
    if(-1 != firstBrakeMile || countSpeedPerRange > 0) {
//      println("-------最后一条刹车里程区间需要计算")
      val (t,v,m) = (_0200ValidData.last.timestamp, _0200ValidData.last.speed, _0200ValidData.last.mile)
      cumBrakeMile += m - firstBrakeMile
      cumBrakeMile2 += v/36000.0
      if(validBrakeRanges.nonEmpty) {
        val idx = if(i == 0) i else i -1
        cumBrakeMile3 +=
          sumSpeedPerRange/countSpeedPerRange * (validBrakeRanges(idx)._2 - validBrakeRanges(idx)._1) / 36000.0 //-1
      }
    }

    // 先识别 先离合后刹车 事件次数, 因为离合信号和脚刹信号属于同一个CAN ID
    val bothBrakeAndClutch = clutchRdd.join(brakeRdd)
    var disconnectBeforeBrakeCount = 0

    if(validSpeedRanges.nonEmpty) {
      var i = 0
      var continueFlag = true
      bothBrakeAndClutch.map(_._1).collect().sorted.foreach{ t =>
        if(continueFlag) {
          if(validSpeedRanges(i)._1 <= t && t <= validSpeedRanges(i)._2) {
            disconnectBeforeBrakeCount += 1
//            println(s"0705rdd 先离合后刹车次数位$disconnectBeforeBrakeCount 当前明细为")
//            bothBrakeAndClutch.filter(_._1 == t).foreach(println)
          }
          else while(i < validSpeedRanges.length && t > validSpeedRanges(i)._2 ) i += 1
          if(i >= validSpeedRanges.length) continueFlag = false
        }
      }
    }

    // 长时间刹车 长时间离合
    val longBrakeCount = validBrakeRanges.count(arr => arr._2 - arr._1 >= minBrakeLastMillisec)
    val longDisconnectCount = validClutchRanges.count(arr => arr._2 - arr._1 >= minDisLastMillisec)

    /*******************************************************************************************/
    // debug info
//    println(s"0705根据采集频率计算的总的离合时间为$totalClutchDuration 秒")
//    println(s"0705根据采集频率计算的总的刹车时间为$totalBrakeDuration 秒")
//    println(s"0705分别采集到的刹车里程数据为${cumBrakeMile/10.0} $cumBrakeMile2 $cumBrakeMile3")
//    println(s"0705rdd 先离合后刹车数据为 ${(disconnectBeforeBrakeCount,
//      longBrakeCount,
//      longDisconnectCount,
//      totalClutchDuration)}")
    logError("CANProtocolDataProcess结束extractClutchAndBrakeEvents过程，" +
      s"耗时${(System.currentTimeMillis()-startRunTime)/1000}秒")
    /*********************************************************************************************/

    (disconnectBeforeBrakeCount,
      longBrakeCount,
      longDisconnectCount,
      totalClutchDuration.toFloat,
      totalBrakeDuration,
      math.max(math.max(cumBrakeMile/10.0, cumBrakeMile2), cumBrakeMile3))
  }


  /**
    * 停车立即熄火，一段行程中acc为关的记录较少，故先找到此记录，再从该时刻往前推advanceTime的不同的时间区间
    * 对这些时间区间进行合并，合并后对每个区间进行是否停车立即熄火的判断
    * 检测到车速降为0 & 发动机转速>0，2秒内ACC熄火
    *
    * 高转速起步:上一次速度=0,下次速度>0,转速>2000,连续时间超过2秒
    *
    * @param _0200ValidData    行车记录仪数据
    *   提前时间，精确到毫秒
    *   停车立即熄火最大持续时间
    * @return
    */
  def extractAccOffOnceStopHighRpmStartEvents_6(_0200ValidData: Array[CollectedDvrItem],
                                                bcZeroSpeedTimeRanges: Broadcast[Array[(Long, Long)]],
                                                _0705RpmRdd: RDD[Timeseries]): (Long, Long) = {
    /********************************************************************************/
    // Debug info
    val startRunTimeMillis = System.currentTimeMillis()
    logError(s"DVRProtocolDataProcess运行extractAccOffOnceStopHighRpmStartEvents，开始时间${startRunTimeMillis/1000}秒")
    /********************************************************************************/

    val zeroSpeedTimeRanges = bcZeroSpeedTimeRanges.value
    if(zeroSpeedTimeRanges.isEmpty) return (0L, 0L)

    val minHighRpm = paramsMap.getOrElse("minHighRpm", "1800").toFloat
    val highRpmStartDurationMillis = paramsMap.getOrElse("highRpmStartDuration", "2").toFloat * 1000
    val maxInstantOffIntervalMillis = paramsMap.getOrElse("maxInstantOffInterval", "2").toFloat * 1000

    var i = 0 // 迭代zeroSpeedTimeRanges

    /** 停车立即熄火 */
    val zeroSpeed2AccOffRanges = ArrayBuffer[(Long, Long)]()

    _0200ValidData
      .filter(_.accState == 0) //.contains("ACC 关")
      .map(_.timestamp).sorted
      .foreach{ ts =>
        if(i < zeroSpeedTimeRanges.length) {
          // 找到0速度区间第一个acc off的时间点
          if(zeroSpeedTimeRanges(i)._1 <= ts && ts <= zeroSpeedTimeRanges(i)._2) {
            if(ts - zeroSpeedTimeRanges(i)._1 <= maxInstantOffIntervalMillis) {
              zeroSpeed2AccOffRanges.append((zeroSpeedTimeRanges(i)._1, ts))
            }
            i += 1
          } else while(i < zeroSpeedTimeRanges.length && ts > zeroSpeedTimeRanges(i)._2) {
            i += 1
          }
        }
      }
    val bcZeroSpeed2AccOffRanges = _0705RpmRdd.sparkContext
      .broadcast(zeroSpeed2AccOffRanges)

    val instantOffEventCount =  if(zeroSpeed2AccOffRanges.isEmpty) 0L
    else {
      _0705RpmRdd.filter(_.value > 0).flatMap{ case Timeseries(ts, _)=>
        val zeroSpeed2AccOffRanges = bcZeroSpeed2AccOffRanges.value
        var (accOffStartTime, accOffLastTime) = (-1L, -1L)
        var i = 0
        while (i < zeroSpeed2AccOffRanges.length) {
          val (s, e) = zeroSpeed2AccOffRanges(i)
          // 如果从速度为0到acc off之间存在rpm > 0
          if(s < ts && ts <= e) {
//            println(s"停车立即熄火事件$s speed=0 $ts rpm>0 $e acc off")
            accOffStartTime = s
            accOffLastTime = e
            i = zeroSpeed2AccOffRanges.length // 直接跳出该循环
          } else if(ts > e) {
            i += 1
          }
        }
        if(-1 != accOffStartTime)
          Some((accOffStartTime, accOffLastTime))
        else None
      }.distinct().count()
    }

    /** 高转速起步 */
    i = 0
    var (highRpmStartTime, lastHighRpmTime) = (-1L, -1L)
    val highRpmStartRanges = ArrayBuffer[(Long, Long)]()
    _0705RpmRdd.filter(_.value > minHighRpm)
      .map(_.timestamp).collect().sorted
      .foreach{ ts =>
        if(i < zeroSpeedTimeRanges.length -1) {
          val (_, e) = zeroSpeedTimeRanges(i)
          // ts 在速度不为0的区间
          if(e < ts && ts < zeroSpeedTimeRanges(i+1)._1) {
            // 先速度为0 然后速度>0 且转速要大于2000 相差2秒以内
            if(-1 == highRpmStartTime && ts - e <= highRpmStartDurationMillis) {
              highRpmStartTime = e
              lastHighRpmTime = ts
            }
            else if(-1 != lastHighRpmTime && ts - lastHighRpmTime <= millisGapBetween2Same0705Records ) {
              lastHighRpmTime = ts
            } else i += 1
          }
          else if(-1 != highRpmStartTime) {
            highRpmStartRanges.append((highRpmStartTime, lastHighRpmTime))
            highRpmStartTime = -1
            if(lastHighRpmTime >= zeroSpeedTimeRanges(i+1)._1) i += 1
            lastHighRpmTime = -1
          }
          // 接下来判断是否移动到下一个0速度区间
          if(i < zeroSpeedTimeRanges.length-1 && lastHighRpmTime >= zeroSpeedTimeRanges(i+1)._1) i += 1
        }
      }

    val highRpmStartEventCount = highRpmStartRanges.count { case (s, e) =>
      e - s >= highRpmStartDurationMillis
    }

    /***********************************************************************************/
    // Debug info
    val durationTimeMillis = (System.currentTimeMillis() - startRunTimeMillis) / 1000
    logError(s"运行extractAccOffOnceStopHighRpmStartEvents结束，耗时$durationTimeMillis 秒")
    /***********************************************************************************/
    (instantOffEventCount, highRpmStartEventCount)
  }


  /**
    * 超长怠速次数、怠速空调次数
    * 速度=0，转速>0,持续时长>60s，
    * 速度=0，转速>0,持续时长>60s，空调开启
    */
  def extractLongIdleOrAirConditionerEvents_7(bcZeroSpeedTimeRanges: Broadcast[Array[(Long, Long)]],
                                               _0705Rdd: RDD[Row],
                                               _0705RpmTs: RDD[Timeseries]): (Int, Int) = {

    /*******************************************************************************************/
    // debug info
    val startRunTime = System.currentTimeMillis()
    logError(s"CANProtocolDataProcess开始extractLongIdleOrAirConditionerEvents81过程，开始时间$startRunTime")
    /*********************************************************************************************/

    val minLongIdleDurationMillis: Float = paramsMap.getOrElse("minLongIdleDuration", "60").toFloat * 1000

    // 先找到速度为0的时间段
    val longLastzeroSpeedRanges = bcZeroSpeedTimeRanges.value.filter(p => p._2 - p._1 >= minLongIdleDurationMillis)
    if (longLastzeroSpeedRanges.isEmpty) return (0, 0)

    val validRpmRanges = produceTimeRangesByValueFilter(_0705RpmTs.collect().sortBy(_.timestamp), 0, 2)

    val longIdleRanges = intersectTimeRanges(longLastzeroSpeedRanges, validRpmRanges)
      .filter(p => p._2 - p._1 >= minLongIdleDurationMillis)

    var ildeConditionerPerRange = 0
    var longIdleConditionerCount = 0

    val longIdleCount = if(longIdleRanges.isEmpty) 0
    else {
      // 再从中找到怠速空调的时间段
      var i = 0
      var continueFlag = true
      _0705Rdd.filter { row =>
        val ts = row.getAs[Long](Field_CAN_Time)
        val signal = row.getAs[String](Field_SignalName)
        val v = row.getAs[Double](Field_Value)
        signal.contains("空调") && v > 0 &&
          longIdleRanges.exists(p => p._1 <= ts && ts <= p._2)
      }.map(_.getAs[Long](Field_CAN_Time)).distinct()
        .collect().sorted.foreach{ t =>
        if(longIdleRanges(i)._1 <= t && t <= longIdleRanges(i)._2)
          ildeConditionerPerRange += 1
        else if(t > longIdleRanges(i)._2 && ildeConditionerPerRange > 0) {
          longIdleConditionerCount += 1
//          println(s"发现怠速空调事件 ${longIdleRanges(i-1)} ")
          ildeConditionerPerRange = 0
        }
        while(i < longIdleRanges.length && t > longIdleRanges(i)._2) i += 1
        if(i >= longIdleRanges.length) continueFlag = false
      }

//      println("发现长时间怠速事件")
//      longIdleRanges.foreach(println)
      longIdleRanges.length
    }
    /*******************************************************************************************/
    // debug info
    logError("CANProtocolDataProcess结束extractLongIdleOrAirConditionerEvents过程，" +
      s"耗时${(System.currentTimeMillis()-startRunTime)/1000}秒")
    /*********************************************************************************************/

    (longIdleCount, longIdleConditionerCount)
  }

  /**
    * 冷车行驶次数
    * 发动机冷却液温度、车速、时间	发动机水温<45度,速度>20km/h，持续时间>30s
    */
  def extractDriveWithColdStateEvents_8(_0200ValidData: Array[CollectedDvrItem],
                                         _0705Rdd: RDD[Row]): Int = {

    /*******************************************************************************************/
    // debug info
    val startRunTime = System.currentTimeMillis()
    logError(s"CANProtocolDataProcess开始extractDriveWithColdStateEvents91过程，开始时间$startRunTime")
    /*********************************************************************************************/

    val minSpeedWhileColdDrive = paramsMap.getOrElse("minSpeedWhileColdDrive", "20").toFloat * 10
    val maxEngineTemp = paramsMap.getOrElse("maxEngineTempreture", "45").toDouble
    val minColdDriveDurationMillis = paramsMap.getOrElse("minColdDriveDuration", "30").toFloat * 1000

    val validSpeedRanges = produceTimeRangesByValueFilter(_0200ValidData, 2, minSpeedWhileColdDrive, 1)

    val engineTempRdd = _0705Rdd.filter(_.getAs[String](Field_SignalName).contains("发动机水温"))
      .map(r => r.getAs[Long](Field_CAN_Time) -> r.getAs[Double](Field_Value))

    val engineTempTs = smoothByValueAvg(engineTempRdd).collect().sortBy(_.timestamp)

    val validEngineTempRanges = produceTimeRangesByValueFilter(engineTempTs, maxEngineTemp, -1)

    val intersetRanges = intersectTimeRanges(validSpeedRanges, validEngineTempRanges)

    val coldDriveCount = intersetRanges.count(p => p._2 - p._1 >= minColdDriveDurationMillis)

    /*******************************************************************************************/
    // debug info
    logError("CANProtocolDataProcess结束extractDriveWithColdStateEvents过程，" +
      s"耗时${(System.currentTimeMillis()-startRunTime)/1000}秒")
//    intersetRanges.foreach(println)
    /*********************************************************************************************/

    coldDriveCount
  }


  /**
    * 行车未放手刹		速度>0km/h，手刹未放，持续2s
    *
    * @param _0200ValidData       行车记录仪数据
    * @param _0705Rdd       CAN报文协议数据（）
    *  minMilliSecondsGap     0200跟0705采集间隔区间
    * @return
    */
  def extractDriveWithHandBrakeEvent_9(_0200ValidData: Array[CollectedDvrItem],
                                         _0705Rdd: RDD[Row]): Int = {
    /*******************************************************************************************/
    // debug info
    val startRunTime = System.currentTimeMillis()
    logError(s"CANProtocolDataProcess开始extractDriveWithHandBrakeEvent101过程，开始时间$startRunTime")
    /*********************************************************************************************/

    val minRunWithHandBrakeDurationMillis = paramsMap.getOrElse("minRunWithHandBrakeDuration", "1.9").toFloat * 1000

    val handBrakeRdd = _0705Rdd.filter(_.getAs[String](Field_SignalName).contains("手刹"))
      .map(row => row.getAs[Long](Field_CAN_Time) -> row.getAs[Double](Field_Value))

    val handBrakeTs = smoothByValueAvg(handBrakeRdd).collect.sortBy(_.timestamp) //TODO 平滑离散数据

    val validHandBrakeRanges = produceTimeRangesByValueFilter(handBrakeTs, 0, 2)

    val validSpeedRanges = produceTimeRangesByValueFilter(_0200ValidData, 2, 0, 2)

    val bothValidRanges = intersectTimeRanges(validSpeedRanges, validHandBrakeRanges)
      .filter(p => p._2 - p._1 >= minRunWithHandBrakeDurationMillis)

    val driveWithHandBrakeEventCount = bothValidRanges.length

    /*******************************************************************************************/
    // debug info
    logError("CANProtocolDataProcess结束extractDriveWithHandBrakeEvent过程，" +
      s"耗时${(System.currentTimeMillis()-startRunTime)/1000}秒")
    /*********************************************************************************************/
    driveWithHandBrakeEventCount
  }


  /**
    * 停车踩油门：怠速范围、油门开度大于0
    * @param _0705RpmTs     CAN报文协议数据
    * @return 停车踩油门次数
    */
  def stepOnThrottleDuringStopBy0705_10(_0705RpmTs: RDD[Timeseries],
                                        _0705ThrottleOpeningTs: RDD[Timeseries]): Int = {
    /*******************************************************************************************/
    // debug info
    val startRunTime = System.currentTimeMillis()
    logError(s"CANProtocolDataProcess开始stepOnThrottleDuringStopBy0705过程，开始时间$startRunTime")
    /*********************************************************************************************/

    val rpmIdleRange = paramsMap.get("idleRotationRange") match {
      case Some(x) =>
        val arr = x.split("-")
        (arr(0).toInt, arr(1).toInt)
      case None => DefaultIdleRotationRange
    }
    // 先找到所有的怠速范围
    val idleRanges = produceTimeRangesByValueFilter(_0705RpmTs.collect().sortBy(_.timestamp), rpmIdleRange._2, -1)

    if(idleRanges.isEmpty) return 0

    // 在从中找到油门开度大于0的
    var stepOnThrottleDuringStopEventCount = 0
    var prevT = -1L
    var startTime = -1L
    var i = 0
    _0705ThrottleOpeningTs.filter{ _.value > 0}.map(_.timestamp)
        .collect().sorted
      .foreach{ t =>
        val (s,e) = idleRanges(i)
        if(s <= t && t < e) {
          if(-1 == startTime) startTime = t
        } else if(t >= e && -1 != startTime) {
          stepOnThrottleDuringStopEventCount += 1
          startTime = -1
        }
        while (i < idleRanges.length && t >= e) i += 1
        if (i >= idleRanges.length) {
          if(-1 != startTime) stepOnThrottleDuringStopEventCount += 1
          return stepOnThrottleDuringStopEventCount
        }
      }

    /*******************************************************************************************/
    // debug info
    logError("CANProtocolDataProcess结束stepOnThrottleDuringStopBy0705过程，" +
      s"耗时${(System.currentTimeMillis()-startRunTime)/1000}秒")
    /*********************************************************************************************/

    stepOnThrottleDuringStopEventCount
  }

  /**
    * 经济负荷行驶里程/时长	负荷率、里程、时间	按车型出厂的标定经济负荷进行计算里程
    * 经济速度行驶里程/时长	车速、里程、时间	按车型出厂的标定经济速度进行计算里程
    * 高速行驶里程/时长	车速、里程、时间	超过车型出厂的标定经济速度行驶
    */
  @DeveloperApi
  def indicator_ecoDrive(_0200Rdd: Array[Row],
                         _0705Rdd: Array[Row],
                         ecoSpeedRange: (Int, Int),
                         ecoLoadRateRange: (Int, Int)) = {
    val ecoLoadRateTime = _0705Rdd.count { row =>
      row.getAs[String](Field_SignalName).contains("发动机负荷率") &&
        isInRange(ecoLoadRateRange, row.getAs[Double](Field_Value))
    } / 1000

    val ecoSpeedTime = _0200Rdd
      .count { row =>
        isInRange(ecoSpeedRange, row.getAs[Long](Field_VDR_Speed))
      } / 1000

    val overEcoSpeedTime = _0200Rdd
      .count { row =>
        val speed = row.getAs[Long](Field_VDR_Speed)
        speed > ecoSpeedRange._2
      } / 1000

    // 计算经济速度行驶里程、高速行驶里程
    var preT = -1L
    var startMile = -1L
    var preV = -1L

    var ecoMile = 0L
    var overEcoMile = 0L
    _0200Rdd
      .filter(_.getAs[Long](Field_VDR_Speed) < ecoSpeedRange._1)
      .map(
        f =>
          (f.getAs[Long](Field_GPS_Time),
            f.getAs[Long](Field_VDR_Speed),
            f.getAs[Long](Field_CumulativeMile)))
      .sortBy(_._1)
      .foreach {
        case (t, v, m) =>
          if (-1 == preT) startMile = m
          else if (t - preT > 2) {
            if (v > ecoSpeedRange._2 && preV <= ecoSpeedRange._2) {
              // 当前高速，之前经济速度
              ecoMile += m - startMile
              startMile = m
            }
            if (v <= ecoSpeedRange._2 && preV > ecoSpeedRange._2) {
              // 当前经济速度，之前高速
              overEcoMile += m - startMile
              startMile = m
            }
          }
          preT = t
          preV = v
      }

    (ecoLoadRateTime, ecoSpeedTime, ecoMile, overEcoSpeedTime, overEcoMile)
  }

}
