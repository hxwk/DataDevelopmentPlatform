package com.dfssi.dataplatform.analysis.dbha.driverbehavior.local

import java.util.Calendar

import com.dfssi.dataplatform.analysis.dbha.DriveBehaviorUtils._
import com.dfssi.dataplatform.analysis.dbha.SingleTripProcess.{intersectTimeRanges, produceTimeRangesByValueFilter, smoothByValueAvg}
import com.dfssi.dataplatform.analysis.dbha.driverbehavior.turnlight.ImproperTurnLightBehavior
import org.apache.spark.Logging
import org.apache.spark.sql.Row

import scala.collection.mutable.ArrayBuffer

class ProtocolDataProcess(paramsMap: Map[String, String],
                          _0200CaptureTimesPerSec: Float,
                          millisGapBetween2Same0200Records: Float,
                          _0705CaptureTimesPerSec: Float,
                          millisGapBetween2Same0705Records: Float,
                          maxMillisIntervalPerEvent: Float) extends Serializable with Logging {

  /**
    * 提取仅仅基于0200行车记录仪终端数据就可以得到的事件及对应指标
    * @param _0200PartialData  行车记录仪终端数据
    * @return EventsBy0200Only,包含：
    *         improperSpeedAndTurnEventCountArr (急加速、急减速、急转弯、连续急加速急减速)
                cumulateDeltaStatAtNightArr(时长（秒），里程（0.1KM）)
                coastingWithEngineOffCount
                cumulateDeltaEcoDriveStatArr
                stopEventCount
                totalDrivingDuration（秒）
                speedStabilityCount
                zeroSpeedRanges
                improperTurnEventCounter
                cumulateHighSpeedDriveStatArr
    */
  def extractEventsFrom0200Data_1(_0200PartialData: Array[CollectedDvrItem]): EventsBy0200Only = {

//    if(_0200PartialData.length < 2) return null
//    {
//      logError("行车记录仪数据至少需要两条")
//      return EventsBy0200Only(0,
//        Array.emptyIntArray,
//        Array.emptyDoubleArray,
//        0,
//        Array.emptyDoubleArray,
//        0, 0,
//        Array((0, 0)),
//        null)
//    }

    /************* 初始化过程，获取第一行，得到相关参数等 **********************/
    val _1stRow = _0200PartialData.head
    var (prevT,prevV, prevD, prevM, prevAccStatus) = (_1stRow.timestamp, _1stRow.speed, _1stRow.dir, _1stRow.mile, _1stRow.accState)

    // 0速度区间
    var (zeroSpeedStartTimeIdx, lastZeroSpeedEndTimeIdx) = if(prevV < 1e-1) (0, 0) else (-1, -1)
    val zeroSpeedRanges = ArrayBuffer[(Long, Long)]()

    // 三急 + "加速-减速"
    val improperSpeedAndTurnEventCountArr = Array.fill(4)(0)
    var harshDriveStartIdx = -1
    var eventType = -1 // 0->急加速 1->急减速 2->急转弯 3->连续加速-减速
    val minSpeedUpAcc = paramsMap.getOrElse("minAccelerateSpeed", "6").toFloat * 10 // 6 * 10km/h/s
    val maxSpeedDownDec = paramsMap.getOrElse("maxDecelerateSpeed", "-7.2").toFloat * 10 // -7.2 * 10km/h/h
    val minTurnRate = paramsMap.getOrElse("minTurnRate", "37.5").toFloat
    val minTurnV: Double = paramsMap.getOrElse("minTurnSpeed", "30").toFloat * 10 // 30* 10km/h

    // 夜间行驶时长(0)/里程(1)
    val cumulateDeltaStatAtNightArr = Array(0.0, 0.0)
    val calendar = Calendar.getInstance()
    calendar.setTimeInMillis(prevT)

    val nightHourSet = paramsMap.get("nightHours") match {
      case Some(x) => x.split(",").map(_.toInt).toSet
      case _       => DefaultNightHourSet
    }

    var firstDriveAtNightRecordIdx = if(isNightHour(calendar.get(Calendar.HOUR_OF_DAY), nightHourSet)) 0 else -1

    // 熄火滑行
    var coastingWithEngineOffCount = 0
    var engineOffCoastingStartIdx = if (prevAccStatus == 0) 0 else -1 //.contains("ACC 关")
    val minCoastingMile = paramsMap.getOrElse("minCoastingMile", "0.005").toFloat * 10 // KM 到 10KM
    val maxCoastingTime = paramsMap.getOrElse("maxCoastingTime", "5").toInt * 60 * 1000 // 分钟到毫秒

    // 车速波动
    val maxVibrationRate = paramsMap.getOrElse("reasonableVibrationRate", "0.3").toDouble
    val speedVariation = ArrayBuffer[Double]() // km/h/s

    // 经济速度行驶里程/时长
    val ecoDriveSpeedRange: (Double, Double) = paramsMap.get("ecoSpeedRange") match {
      case Some(x) =>
        val arr = x.split("-")
        (arr.head.toDouble * 10, arr.last.toDouble * 10) // * 10km/h
      case None => DefaultEcoSpeedRange
    }

    var startIdxOfEcoDrive = if (isEcoSpeed(prevV, ecoDriveSpeedRange)) 0 else -1
    val cumulateDeltaEcoDriveStatArr = Array(0.0, 0.0) // 时长（秒），里程（0.1km）

    // 高速行驶时长/里程
    val minHighSpeed = paramsMap.getOrElse("minHighSpeed", "120").toFloat * 10
    var startIdxOfHighSpeed = if(prevV > minHighSpeed) 0 else -1
    val cumulateHighSpeedDriveStatArr = Array(0.0, 0.0) // 时长（秒），里程（0.1km）

    // 行驶总时间（速度大于0的持续时间和）
    var totalDrivingDuration = 0.0

    // 错打转向灯、转弯不打转向灯、直行打转向灯
    val improperTurnLightBehavior = new ImproperTurnLightBehavior(paramsMap)
    improperTurnLightBehavior.parseSingleRecord(_0200PartialData.head, -1)


    /****************************** 迭代过程 ********************************************/
    for (i <- 1 until _0200PartialData.length) {

      val (ts, velocity, turn, mile, accState) = (_0200PartialData(i).timestamp, // 毫秒
        _0200PartialData(i).speed, // * 10km/h
        _0200PartialData(i).dir,
        _0200PartialData(i).mile, // * 10Km
        _0200PartialData(i).accState)

      // 不当转向灯行为
      improperTurnLightBehavior.parseSingleRecord(_0200PartialData(i), prevT)

      // 过滤掉相同的时刻
      if(ts > prevT) {
        /** 行驶总时间 */
        if (velocity > 0 || mile - prevM > 0) totalDrivingDuration += ts - prevT // + 1000

        /** 记录速度为0的区间 */
        if(velocity <= 1e-1) {
          if(prevV > 1e-1) zeroSpeedStartTimeIdx = i
          lastZeroSpeedEndTimeIdx = i
        } else if(-1 != zeroSpeedStartTimeIdx) {
          var startTime = _0200PartialData(zeroSpeedStartTimeIdx).timestamp
          var endTime = _0200PartialData(lastZeroSpeedEndTimeIdx).timestamp
          if(startTime == endTime) {
//            println(s"单独一个点构成0速度区间，该点数据为${_0200PartialData(zeroSpeedStartTimeIdx)}\n 上一个点数据为${_0200PartialData(zeroSpeedStartTimeIdx-1)}\n" +
//              s" 下一个点数据为${_0200PartialData(zeroSpeedStartTimeIdx+1)}")
//            val newStartTime = midValue(_0200PartialData(zeroSpeedStartTimeIdx-1).timestamp, _0200PartialData(zeroSpeedStartTimeIdx).timestamp)
//            val newEndTime = midValue(_0200PartialData(zeroSpeedStartTimeIdx+1).timestamp, _0200PartialData(zeroSpeedStartTimeIdx).timestamp)
            startTime -= 500
            endTime += 500
          }
          zeroSpeedRanges.append((startTime, endTime))
          zeroSpeedStartTimeIdx = -1
          lastZeroSpeedEndTimeIdx = -1
        }

        /** 三急 + "加速-减速" */
        // 前后时间相同或者时间差大于maxInterval则忽略 prevT != ts &&
        if (ts - prevT <= maxMillisIntervalPerEvent) {
          val deltaT = (ts - prevT) / 1000.0 + 1e-2 //秒
          // 先判断是否急转弯，因为急转弯通常伴随着急减速 TODO 解决角度突然从354 到 1 被判断为急转弯的问题
          val turnRate = math.abs(turn - prevD) / deltaT
          if (turnRate >= minTurnRate && velocity >= minTurnV) {
            if (-1 == harshDriveStartIdx) { // 急转弯起始点
              harshDriveStartIdx = i - 1; eventType = 2 // 2 -> harsh turn
            } else if (2 != eventType) {
              improperSpeedAndTurnEventCountArr(eventType) += 1
//              println(s"0200数据发现急加速$eventType\n 起点数据${_0200PartialData(harshDriveStartIdx)}\n 终点数据${_0200PartialData(i-1)}")
              harshDriveStartIdx = i - 1
              eventType = 2 // 2 -> harsh turn
            }
          }
          // 不构成急转弯的情况下，需要判断：当前时间点是否一段连续急转弯的终点，是则更新；然后不管是否都要判断速度
          else {
            if (-1 != harshDriveStartIdx && 2 == eventType) {
              improperSpeedAndTurnEventCountArr(eventType) += 1
//              println(s"0200数据发现事件$eventType\n 起点数据${_0200PartialData(harshDriveStartIdx)}\n 终点数据${_0200PartialData(i-1)}")
              harshDriveStartIdx = -1; eventType = -1
            }
            val accSpeed = (velocity - prevV) / deltaT // * 10km/h/s
            // 车速波动
            if(velocity> 0||prevV > 0) speedVariation.append(accSpeed)

            if (accSpeed >= minSpeedUpAcc) {
              if (-1 == harshDriveStartIdx) {
                harshDriveStartIdx = i - 1; eventType = 0 // 0 -> harsh speed up
              } else if (0 != eventType) {
                improperSpeedAndTurnEventCountArr(eventType) += 1
//                println(s"0200数据发现急减速事件$eventType\n 起点数据${_0200PartialData(harshDriveStartIdx)}\n 终点数据${_0200PartialData(i-1)}")
                harshDriveStartIdx = i - 1; eventType = 0 // 0 -> harsh speed up
              }
            } else if (accSpeed <= maxSpeedDownDec) {
              if (harshDriveStartIdx == -1) {
                harshDriveStartIdx = i - 1; eventType = 1 // 1 -> rapid dece
              } else if (3 == eventType) {
                // do nothing
              } else if (1 != eventType) { // 之前急加速，现在急减速
                val lastTime = (ts - _0200PartialData(harshDriveStartIdx).timestamp) / 1000.0
                if (lastTime <= maxMillisIntervalPerEvent) { //急加速-加减速连续5秒内出现，改变eventtype
                  eventType = 3
                } else {
                  improperSpeedAndTurnEventCountArr(eventType) += 1
//                  println(s"0200数据发现事件$eventType\n 起点数据${_0200PartialData(harshDriveStartIdx)}\n 终点数据${_0200PartialData(i-1)}")
                  harshDriveStartIdx = i - 1; eventType = 1 // 1 -> rapid dece
                }
              }
            }
            // 既不构成急减速也不构成急加速时，判断当前点是否一段变速行为的终点，是则更新
            else if (harshDriveStartIdx != -1) {
              improperSpeedAndTurnEventCountArr(eventType) += 1
//              println(s"0200数据发现事件$eventType\n 起点数据${_0200PartialData(harshDriveStartIdx)}\n 终点数据${_0200PartialData(i-1)}")
              harshDriveStartIdx = -1; eventType = -1
            }
          }
        }
        // 判断当前点是否一段变速行为或者转弯行为的终点，是则更新
        else if (harshDriveStartIdx != -1) {
          improperSpeedAndTurnEventCountArr(eventType) += 1
//          println(s"0200数据发现事件$eventType\n 起点数据${_0200PartialData(harshDriveStartIdx)}\n 终点数据${_0200PartialData(i-1)}")
          harshDriveStartIdx = -1; eventType = -1
        }

        /** 夜间行驶时长/里程 */
        val prevHour = calendar.get(Calendar.HOUR_OF_DAY)
        calendar.setTimeInMillis(ts)
        val currHour = calendar.get(Calendar.HOUR_OF_DAY)
        if(isNightHour(currHour, nightHourSet)) {
          if(-1 == firstDriveAtNightRecordIdx) firstDriveAtNightRecordIdx = i
          if(isNightHour(prevHour, nightHourSet) && velocity > 0) cumulateDeltaStatAtNightArr(0) += ts - prevT
        } else if(-1 != firstDriveAtNightRecordIdx) {
          cumulateDeltaStatAtNightArr(1) += mile - _0200PartialData(firstDriveAtNightRecordIdx).mile
          firstDriveAtNightRecordIdx = -1
//          println(s"夜间行驶里程起始点记录为${_0200PartialData(firstDriveAtNightRecordIdx)} ${_0200PartialData(i)}")
        }

        /** 熄火滑行 */
        if (accState == 1 && engineOffCoastingStartIdx != -1) { //.contains("ACC 开")
          if (mile - _0200PartialData(engineOffCoastingStartIdx).mile > minCoastingMile) {
            coastingWithEngineOffCount += 1
//            println(s"0200数据发现熄火滑行事件\n 起点数据${_0200PartialData(engineOffCoastingStartIdx)}\n " +
//              s"终点数据${_0200PartialData(i)}")
          }
          engineOffCoastingStartIdx = -1
        } else if (accState == 0) { //.contains("ACC 关")
          engineOffCoastingStartIdx = i
        }

        /** 经济速度行驶里程和时间 */
        if (isEcoSpeed(velocity, ecoDriveSpeedRange)) {
          if (-1 == startIdxOfEcoDrive) {
            startIdxOfEcoDrive = i
          }
        } else if (startIdxOfEcoDrive != -1) {
          cumulateDeltaEcoDriveStatArr(0) += ts -  _0200PartialData(startIdxOfEcoDrive).timestamp
          cumulateDeltaEcoDriveStatArr(1) += mile - _0200PartialData(startIdxOfEcoDrive).mile
          startIdxOfEcoDrive = -1
        }

        /** 高速行驶时间和里程 */
        if (velocity > minHighSpeed) {
          if (-1 == startIdxOfHighSpeed) {
            startIdxOfHighSpeed = i
          } else if (startIdxOfHighSpeed != -1) {
            cumulateHighSpeedDriveStatArr(0) += ts - _0200PartialData(startIdxOfHighSpeed).timestamp
            cumulateHighSpeedDriveStatArr(1) += mile - _0200PartialData(startIdxOfHighSpeed).mile
            startIdxOfHighSpeed = -1
          }
        }
      }

      prevT = ts; prevV = velocity; prevD = turn; prevAccStatus = accState
    }

    /*******************************善后*************************************************/
    /** 停车次数 0速度区间中大于minStopEventDuration的区间数 */
    val minStopEventDurationMillis = paramsMap.getOrElse("minStopEventDuration", "5").toFloat * 1000
    val stopEventCount = zeroSpeedRanges.count(p => p._2 - p._1 >= minStopEventDurationMillis) + 1

    // 波动率
    val mean = (speedVariation.sum + 1) / (speedVariation.length + 0.01)
    val speedVariationCount = speedVariation.map(_ - mean).count(_ <= maxVibrationRate)

    // 转向灯
    improperTurnLightBehavior.analysisTurnLightEvents()

    // TODO 处理各种变量不为-1的情况

    // 处理时长
    totalDrivingDuration /= 1000
    cumulateDeltaStatAtNightArr(0) /= 1000
    cumulateDeltaEcoDriveStatArr(0) /= 1000
    cumulateHighSpeedDriveStatArr(0) /= 1000
    // 处理里程
    if(-1 != firstDriveAtNightRecordIdx) {
      cumulateDeltaStatAtNightArr(1) += _0200PartialData.last.mile - _0200PartialData(firstDriveAtNightRecordIdx).mile
    }
    cumulateDeltaStatAtNightArr(1) /= 10

    cumulateDeltaEcoDriveStatArr(1) /= 10

    if(-1 != startIdxOfHighSpeed) {
      cumulateHighSpeedDriveStatArr(1) += _0200PartialData.last.mile - _0200PartialData(startIdxOfHighSpeed).mile
    }
    cumulateHighSpeedDriveStatArr(1) /= 10

    /***********************************************************************************/
    // Debug info
    /***********************************************************************************/

    EventsBy0200Only(totalDrivingDuration,
      improperSpeedAndTurnEventCountArr,
      cumulateDeltaStatAtNightArr,
      coastingWithEngineOffCount,
      cumulateDeltaEcoDriveStatArr,
      stopEventCount,
      speedVariationCount,
      zeroSpeedRanges.toArray,
      improperTurnLightBehavior.improperTurnEventCounter,
      cumulateHighSpeedDriveStatArr)
  }

  /**
    * 大油门行驶		速度>0km/h，连续油门开度>60%，持续5s
    * 满油行驶		速度>0km/h，连续油门开度>95%，持续2s以上
    *
    * @param _0705ThrottleOpeningTs     CAN报文协议数据
    * minSpeedWithHighOpening     最小行驶速度
    * minThrottleOpening 最小油门开度阈值
    * minHighOpeningMillisec          最小大油门持续时间
    * fullThrottleOpening  全油门开度阈值
    * minFullThrottheMillisec  最小全油门持续时间
    * @return   大油门行驶次数、满油行驶次数、大油门行驶时间（秒）
    */
  def extractDriveAtHighThrottleOpenEvents_3(_0200ValidData: Array[CollectedDvrItem],
                                              _0705ThrottleOpeningTs: Array[Timeseries]): (Long, Long, Double) = {

    /*******************************************************************************************/
    // debug info
    val startRunTime = System.currentTimeMillis()
    logError(s"CANProtocolDataProcess开始extractDriveAtHighThrottleOpenEvents过程，开始时间$startRunTime")
    /*********************************************************************************************/

    val minSpeedWithHighOpening: Double = paramsMap.getOrElse("minSpeedAtHighThrottleOpen", "20").toDouble * 10
    val minThrottleOpening: Double = paramsMap.getOrElse("minHighThrottleOpening", "60").toDouble
    val minHighOpeningMillis: Double = paramsMap.getOrElse("minHighOpeningMillisec", "5").toDouble * 1000
    val fullThrottleOpening: Double =paramsMap.getOrElse("minFullThrottleOpening", "95").toDouble
    val minFullThrottheMillis = paramsMap.getOrElse("minFullThrottheLastTime", "2").toDouble * 1000

    // 速度>0km/h 的区间
    val validSpeedRanges = produceTimeRangesByValueFilter(_0200ValidData, 2, 0, 2)
    if(validSpeedRanges.isEmpty) return (0, 0, 0)

    val highOpeningRanges= produceTimeRangesByValueFilter(_0705ThrottleOpeningTs, minThrottleOpening, 1)
    if(highOpeningRanges.isEmpty) return (0, 0, 0)

    val driveAtHighOpenningRanges = intersectTimeRanges(validSpeedRanges, highOpeningRanges)
      .filter(p => p._2 - p._1 >= minHighOpeningMillis)
    if(driveAtHighOpenningRanges.isEmpty) return (0, 0, 0)

    val fullOpeningRanges = produceTimeRangesByValueFilter(_0705ThrottleOpeningTs, fullThrottleOpening, 1)
    val driveAtFullOpeningRanges = intersectTimeRanges(validSpeedRanges, fullOpeningRanges)
      .filter(p => p._2 - p._1 >= minFullThrottheMillis)

    /*******************************************************************************************/
    // debug info
    logError("CANProtocolDataProcess结束extractDriveAtHighThrottleOpenEvents过程，" +
      s"耗时${(System.currentTimeMillis()-startRunTime)/1000}秒")
//    driveAtHighOpenningRanges.foreach(println)
//    driveAtFullOpeningRanges.foreach(println)
    /*********************************************************************************************/

    val sumDuration = driveAtHighOpenningRanges.map(p => p._2 - p._1).sum / 1000.0

    (driveAtHighOpenningRanges.length,
      driveAtFullOpeningRanges.length,
      sumDuration)
  }

  /**
    * 空挡滑行 发动机处于怠速范围 速度>30Km/h 持续时间>2s 油门开度=0 扭矩百分比<5%
    * @param _0705Data   CAN报文协议数据
    * @return  滑行次数
    */
  def extractCoastingWithNullPosEvent_4(_0200ValidData: Array[CollectedDvrItem],
                                        _0705RpmTs: Array[Timeseries],
                                        _0705ThrottleOpeningTs: Array[Timeseries],
                                        _0705Data: Iterator[Row]): Int = {

    /*******************************************************************************************/
    // debug info
    val startRunTime = System.currentTimeMillis()
    logError(s"CANProtocolDataProcess开始extractCoastingWithNullPosEvent_41过程，开始时间$startRunTime")
    /*********************************************************************************************/

    val idleRange = paramsMap.get("idleRotationRange") match {
      case Some(x) =>
        val arr = x.split("-")
        (arr(0).toInt, arr(1).toInt)
      case None => DefaultIdleRotationRange
    }
    val maxTorque = paramsMap.getOrElse("minTorque", "5").toFloat // %
    val minSpeedWhileCoasting = paramsMap.getOrElse("minSpeedWhileCoasting", "20").toFloat * 10
    val minCoastingDurationMillis = paramsMap.getOrElse("minCoastingDuration", "1.9").toFloat * 1000

    /* 先找到速度大于minSpeedWhileCoasting的时间段 */
    val validSpeedRanges = produceTimeRangesByValueFilter(_0200ValidData, 2, minSpeedWhileCoasting, 1)
    if (validSpeedRanges.isEmpty) return 0

    val validRpmRanges = produceTimeRangesByValueFilter(_0705RpmTs, idleRange._2, -1)
    if(validRpmRanges.isEmpty) return 0

    val validSpeedAndRpmRanges = intersectTimeRanges(validSpeedRanges, validRpmRanges)
    if(validSpeedAndRpmRanges.isEmpty) return 0

    val validTORanges=  produceTimeRangesByValueFilter(_0705ThrottleOpeningTs, 0, 0)
    val validSpeedRpmThrottleRanges = intersectTimeRanges(validSpeedAndRpmRanges, validTORanges)
    if(validSpeedRpmThrottleRanges.isEmpty) return 0

    val torqueRdd = _0705Data.filter(_.getAs[String](Field_SignalName).contains("扭矩"))
      .map (r => r.getAs[Long](Field_CAN_Time) -> r.getAs[Double](Field_Value))
      .toArray

    val torqueTimeSeries = smoothByValueAvg(torqueRdd).toArray.sortBy(_.timestamp)

    val validTorqueRanges = produceTimeRangesByValueFilter(torqueTimeSeries, maxTorque, -1)
    val validCoastingRanges = intersectTimeRanges(validSpeedRpmThrottleRanges, validTorqueRanges)
      .filter(p => p._2 - p._1 >= minCoastingDurationMillis)

    /*******************************************************************************************/
    // debug info
    logError(s"CANProtocolDataProcess结束extractCoastingWithNullPosEvent41过程，空挡滑行次数为${validCoastingRanges.length}次" +
      s"耗时${(System.currentTimeMillis()-startRunTime)/1000}秒, 明细如下：")

//    validCoastingRanges.foreach(println)
    /*********************************************************************************************/

    validCoastingRanges.length
  }

  /**
    * 先离合后刹车：速度>30km/h,刹车信号和离合信号同时出现，正常是先出现刹车信号再出现离合信号
    *
    * 注：离合器开关 脚刹 手刹 属于同一个CAN ID
    *
    * @param _0705Data     CAN报文协议数据
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
  def extractClutchAndBrakeEvents_51(_0200ValidData: Array[CollectedDvrItem],
                                     _0705Data: Iterator[Row]): (Int, Int, Int, Float, Double, Double) = {

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
    val clutchRdd = _0705Data.filter{ row =>
      row.getAs[String](Field_SignalName).contains("离合")
    }.map(r => r.getAs[Long](Field_CAN_Time) -> r.getAs[Double](Field_Value)).toArray
    // TODO 离散数据平滑改善
    val clutchTimeseries = smoothByValueAvg(clutchRdd).toArray.sortBy(_.timestamp)
    val validClutchRanges = produceTimeRangesByValueFilter(clutchTimeseries, 0.5, 2)

    // 总离合时间
    val totalClutchDuration = if(validClutchRanges.isEmpty) 0
    else validClutchRanges.map(p => p._2 - p._1).sum / 1000.0

    // 再处理刹车数据，离散数据不需要平滑
    val brakeRdd = _0705Data.filter{ row =>
      row.getAs[String](Field_SignalName).contains("脚刹")
    }.map(r => r.getAs[Long](Field_CAN_Time)-> r.getAs[Double](Field_Value)).toArray
    // TODO 离散数据平滑改善
    val brakeTimeSeries = smoothByValueAvg(brakeRdd).toArray.sortBy(_.timestamp)
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
        } else if (-1 != firstBrakeMile) {
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
      cumBrakeMile3 += sumSpeedPerRange/countSpeedPerRange * (validBrakeRanges(i-1)._2 - validBrakeRanges(i-1)._1) / 36000.0
    }

    // 先识别 先离合后刹车 事件次数, 因为离合信号和脚刹信号属于同一个CAN ID
    val bothBrakeAndClutch = clutchRdd.map(_._1).intersect(brakeRdd.map(_._1))
    var disconnectBeforeBrakeCount = 0

    if(validSpeedRanges.nonEmpty) {
      var i = 0
      var continueFlag = true
      bothBrakeAndClutch.sorted.foreach{ t =>
        if(continueFlag) {
          if(validSpeedRanges(i)._1 <= t && t <= validSpeedRanges(i)._2) {
            disconnectBeforeBrakeCount += 1
//            println(s"0705rdd 先离合后刹车次数位$disconnectBeforeBrakeCount 当前明细为")
//            bothBrakeAndClutch.filter(_ == t).foreach(println)
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
    * 停车踩油门：怠速范围、油门开度大于0
    * @param _0705RpmTs     CAN报文协议数据
    * @return 停车踩油门次数
    */
  def stepOnThrottleDuringStopBy0705_11(_0705RpmTs: Array[Timeseries],
                                        _0705ThrottleOpeningTs: Array[Timeseries]): Int = {
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
    val idleRanges = produceTimeRangesByValueFilter(_0705RpmTs, rpmIdleRange._2, -1)

    if(idleRanges.isEmpty) return 0

    // 在从中找到油门开度大于0的
    var stepOnThrottleDuringStopEventCount = 0
    var prevT = -1L
    var startTime = -1L
    var i = 0
    _0705ThrottleOpeningTs.filter{ _.value > 0}.map(_.timestamp)
      .sorted
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
