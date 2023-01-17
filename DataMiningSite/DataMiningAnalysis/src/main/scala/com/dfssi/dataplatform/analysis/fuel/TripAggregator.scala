package com.dfssi.dataplatform.analysis.fuel

import java.sql.Connection

import com.dfssi.common.math.Maths
import com.dfssi.dataplatform.analysis.utils.{AbnormalDrivingBehaviorStorage, TotalFuelCollector}
import org.apache.spark.Logging
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, State, StateSpec}

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/1/22 9:32 
  */
class TripAggregator extends Serializable with Logging{

  def tripAggreate(route: DStream[(String, List[FuelRecord])],
                   batchInterval : Int,
                   bcConf: Broadcast[FuelConfig],
                   group: String): DStream[(String, List[FuelRecord])] ={

    val ssc = route.context
    val initialRDD = ssc.sparkContext.parallelize(List[(String, (TripRecord, AbnormalFuels, AbnormalDrivingBehaviorStorage, TotalFuelCollector))]())
    val spec = StateSpec.function(
      (vid: String,
       tripMsg: Option[List[FuelRecord]],
       state: State[(TripRecord, AbnormalFuels, AbnormalDrivingBehaviorStorage, TotalFuelCollector)]) => tripAggreateAndUpdate(vid, tripMsg, state, bcConf)
    ).initialState(initialRDD)

    //进行行程切割或更新
    val ds = route.mapWithState(spec)
    ssc.checkpoint(s"/tmp/trip/${group}")
    ds.checkpoint(Seconds(batchInterval * 10))

    ds
  }

  private def tripAggreateAndUpdate(vid: String,
                                    route: Option[List[FuelRecord]],
                                    state: State[(TripRecord, AbnormalFuels, AbnormalDrivingBehaviorStorage, TotalFuelCollector)],
                                    bcConf: Broadcast[FuelConfig]): (String, List[FuelRecord]) = {

    val conf = bcConf.value
    var records = route.get
    if(records.size > 0) {
      try {
        val connection = conf.getFuelConnection
        val triptool = new VehicleTrips(conf.getTriptable, connection)
        var (finalTripState, abnormalFuels, abnormalDriving, totalFuelCollector) = getStateDetail(state, vid, triptool, conf)
        val (tripState ,n, fuelRecords) = checkTripAndInit(finalTripState, records, triptool, abnormalDriving, connection)
        finalTripState = tripState
        records = fuelRecords

        var neendUpdate = true
        (n until records.size).map(i =>{
            val record = records(i)

          //检查油耗是否异常
         //checkAbnormalFuel(abnormalFuels, record, finalTripState, conf.getFuelConnection)

          //检查驾驶行为异常告警
          checkAbnormalDriving(record, abnormalDriving, connection)

            //行程切割
          if(record.isOn){  //车辆处于运营
              if(finalTripState.getIsover == 1){  //状态中的行程处于结束状态

                  if(isSameTripInVehicleOnAndTripOver(record, finalTripState, conf)){
                    //属于同一行程
                    finalTripState.setIsover(0)
                    finalTripState.setIsvalid(1)

                    updateTripRecordAndTotalFuel(record, finalTripState, totalFuelCollector, connection)
                    triptool.updateTrip(finalTripState)
                    record.setTripid(finalTripState.getId)

                  }else{
                    //检查当前行程是否有效，无效则删除
                    val isValid = tripIsValid(finalTripState, conf)
                    if(isValid == 0  && finalTripState.getInterval == 0L){
                      triptool.deleteTrip(finalTripState.getId)
                    }

                    finalTripState = TripRecord.newTrip(record)
                    triptool.insertTrip(finalTripState)

                    record.setTripid(finalTripState.getId)
                  }
                neendUpdate = false
              }else{

                if(isSameTripInVehicleOnAndTripOn(record, finalTripState, conf)){
                  //属于同一行程
                  updateTripRecordAndTotalFuel(record, finalTripState, totalFuelCollector, connection)
                  record.setTripid(finalTripState.getId)
                  neendUpdate = true

                }else{
                  //当前行程已结束 开启新的行程
                  val isValid = tripIsValid(finalTripState, conf)
                  if(isValid == 0  && finalTripState.getInterval == 0L){
                    triptool.deleteTrip(finalTripState.getId)
                  }else{
                    finalTripState.setIsover(1)
                    finalTripState.setIsvalid(isValid)
                    triptool.updateTrip(finalTripState)
                  }

                  finalTripState = TripRecord.newTrip(record)
                  triptool.insertTrip(finalTripState)

                  record.setTripid(finalTripState.getId)
                  neendUpdate = false
                }
              }
          }else{
              if(finalTripState.getIsover == 0) { //状态中的行程处于运行状态
                if(record.getUploadtime - finalTripState.getEndtime <= conf.getTa) {
                  //属于同一行程
                  updateTripRecordAndTotalFuel(record, finalTripState, totalFuelCollector, connection)
                  record.setTripid(finalTripState.getId)
                }

                //当前行程已结束
                val isValid = tripIsValid(finalTripState, conf)
                finalTripState.setIsover(1)
                finalTripState.setIsvalid(isValid)
                triptool.updateTrip(finalTripState)
              }
              neendUpdate = false
          }

           if(neendUpdate)triptool.updateTrip(finalTripState)
        })

        //将累计数据刷到数据库
        totalFuelCollector.executeUpdate(conf.getFuelConnection)

        //更新状态
        state.update((finalTripState, abnormalFuels, abnormalDriving, totalFuelCollector))

      } catch {
        case e: Exception => logError(s"更新${vid}的状态失败。", e)
      }
    }

    (vid, records)
  }

  private def getStateDetail(state: State[(TripRecord, AbnormalFuels, AbnormalDrivingBehaviorStorage, TotalFuelCollector)],
                             vid: String,
                             tripTool: VehicleTrips,
                             conf: FuelConfig): (TripRecord, AbnormalFuels, AbnormalDrivingBehaviorStorage, TotalFuelCollector) ={
    var (finalTripState, abnormalFuels, abnormalDriving, totalFuelCollector) =
      state.getOption().getOrElse((null, null, null, null))

    //如果最近状态为null, 则从数据库中获取最近一次状态
    if (finalTripState == null) finalTripState = tripTool.queryTripByVid(vid)

    //测试油耗上限为30.0L/100KM
    /*if (abnormalFuels == null)
      abnormalFuels = new AbnormalFuels(conf.getAbnormalfueltable, vid, conf.getMaxConsumption)*/

    //异常驾驶行为告警监控
    if(abnormalDriving == null)
      abnormalDriving = new AbnormalDrivingBehaviorStorage(conf.getAbnormaldrivingtable,
        conf.getAlarmLabelMap, vid, conf.getAbnormaldrivingReportUrl)

    //累计油耗、里程、运行时长 相关
    if(totalFuelCollector == null)
      totalFuelCollector = new TotalFuelCollector(conf.getTotalfueltable, vid)

    (finalTripState, abnormalFuels, abnormalDriving, totalFuelCollector)
  }

  private def checkTripAndInit(finalTripRecord: TripRecord,
                               fuelRecords: List[FuelRecord],
                               tripTool: VehicleTrips,
                               abnormalDriving: AbnormalDrivingBehaviorStorage,
                               connection: Connection): (TripRecord, Int, List[FuelRecord]) ={
    if (finalTripRecord.isEmpty) {
      val fuelRecord = fuelRecords.head
      val finalTrip = TripRecord.newTrip(fuelRecord)
      tripTool.insertTrip(finalTrip)
      fuelRecord.setTripid(finalTrip.getId)

      checkAbnormalDriving(fuelRecord, abnormalDriving, connection)

      (finalTrip, 1, fuelRecords)
    }else{
      //排除掉误传数据
      val records = fuelRecords.filter(_.getUploadtime >= finalTripRecord.getEndtime)
      if(records.size < fuelRecords.size){
        logWarning(s"vid: ${finalTripRecord.getVid}存在数据时间错乱的问题, 当前时间点为：${finalTripRecord.getEndtime}, 错误时间为：\t\n" +
          s"${fuelRecords.filter(_.getUploadtime < finalTripRecord.getEndtime).map(_.getUploadtime)}")
      }
      (finalTripRecord, 0, records)
    }
  }

  private def checkAbnormalDriving(fuelRecord: FuelRecord,
                                   abnormalDriving: AbnormalDrivingBehaviorStorage,
                                   connection: Connection): Unit ={
    abnormalDriving.checkAbnormalDrivingBehavior(fuelRecord.getAlarms,
      fuelRecord.getAlarmDegree,
      fuelRecord.getUploadtime,
      fuelRecord.getLon,
      fuelRecord.getLat,
      fuelRecord.getSpeed,
      connection)
  }

  private def tripIsValid(finalTripRecord: TripRecord,
                          conf: FuelConfig): Int ={
    val mileGap = finalTripRecord.getEndtotalmile - finalTripRecord.getStarttotalmile
    if(mileGap >= conf.getDa) 1 else 0
  }

  //车辆在行驶中、当前行程已结束
  private def isSameTripInVehicleOnAndTripOver(fuelRecord: FuelRecord,
                         finalTripRecord: TripRecord,
                         conf: FuelConfig): Boolean ={
    val timeGap = fuelRecord.getUploadtime - finalTripRecord.getEndtime
    //时间在有效范围内则为有效
    var isSame = (timeGap <= conf.getTv)
    if(!isSame && timeGap <= conf.getTa){  //时间小于行程可接受范围
      isSame = (finalTripRecord.getEndtotalmile - finalTripRecord.getStarttotalmile <= conf.getDa)
    }

    isSame
  }
  //车辆在行驶中、当前行程未结束
  private def isSameTripInVehicleOnAndTripOn(fuelRecord: FuelRecord,
                         finalTripRecord: TripRecord,
                         conf: FuelConfig): Boolean ={
    val timeGap = fuelRecord.getUploadtime - finalTripRecord.getEndtime
    //时间在有效范围内则为有效
    var isSame = (timeGap <= conf.getTa)
    if(!isSame && timeGap <= conf.getTr){  //时间小于行程拒绝范围
      isSame = (finalTripRecord.getEndtotalmile - finalTripRecord.getStarttotalmile <= conf.getDa)
    }

    isSame
  }

  private def updateTripRecordAndTotalFuel(fuelRecord: FuelRecord,
                                           finalTripRecord: TripRecord,
                                           totalFuelCollector: TotalFuelCollector,
                                           connection: Connection): Unit ={

    //计算油耗 并更新到记录中 和行程中
    val (interval, mileGap,  fuelGap) = calculateDiff(fuelRecord, finalTripRecord)
    if(mileGap >= 0 && fuelGap >= 0){
      fuelRecord.setFuelMsg(interval, mileGap, fuelGap)

      //更新累计油耗  可能会存在异常更新！！
      totalFuelCollector.updateTotalFuel(fuelRecord.getUploadtime,
        fuelRecord.getTotalmile,
        fuelRecord.getTotalfuel,
        interval,
        connection)
    }

    totalFuelCollector.updateTotalFuelEndTime(fuelRecord.getUploadtime, interval)

    finalTripRecord.setEndtotalfuel(fuelRecord.getTotalfuel)
    finalTripRecord.setEndtotalmile(fuelRecord.getTotalmile)
    finalTripRecord.setEndlat(fuelRecord.getLat)
    finalTripRecord.setEndlon(fuelRecord.getLon)
    finalTripRecord.setEndtime(fuelRecord.getUploadtime)
  }

  private def calculateDiff(fuelRecord: FuelRecord,
                            finalTripRecord: TripRecord): (Long, Double, Double) ={

    val timeGap = fuelRecord.getUploadtime - finalTripRecord.getEndtime
    val timeGapHour = timeGap * 1.0 / (1000 * 60 * 60)

    //简单避免异常数据
    var mileGap = fuelRecord.getTotalmile - finalTripRecord.getEndtotalmile
    if(timeGap == 0)mileGap = 0.0
    var fuelGap = fuelRecord.getTotalfuel - finalTripRecord.getEndtotalfuel

    var mileError: Boolean = false
    if(timeGap != 0 && (Math.abs(mileGap) * 1.0 / timeGapHour) > 400){
      logError(s"${fuelRecord.getVid}速度在时间范围[${finalTripRecord.getEndtime}, ${fuelRecord.getUploadtime}]" +
        s"里的mileGap = ${mileGap}, timeGap = ${timeGap}车速超过了 400 km/h, 忽略此距离。")
      mileError = true
    }

    var fuelError: Boolean = false
    if(timeGap != 0 && (Math.abs(fuelGap) * 1000.0 / timeGap) >= 0.1){
      logError(s"${fuelRecord.getVid}油耗速度在时间范围[${finalTripRecord.getEndtime}, ${fuelRecord.getUploadtime}]" +
        s"里的fuelGap = ${fuelGap}, timeGap = ${timeGap}油耗速度超过了 360L/h, 忽略此油耗。")
      fuelError = true
    }

    //同时错误的情况下 全部置零
    if(mileError && fuelError){
      mileGap = 0.0
      fuelGap = 0.0
    }

    (timeGap, Maths.precision(mileGap, 1), Maths.precision(fuelGap, 5))
  }


  //检查油耗是否异常
  private def checkAbnormalFuel(abnormalFuels:AbnormalFuels,
                                fuelRecord: FuelRecord,
                                finalTripRecord: TripRecord,
                                connection: Connection): Unit ={

    if(finalTripRecord.isNotEmpty){
      val f = fuelRecord.getTotalfuel - finalTripRecord.getEndtotalfuel
      val m = fuelRecord.getTotalmile - finalTripRecord.getEndtotalmile

      if(f >= 0 && m >= 0) {
        abnormalFuels.checkAbnormalFuel(f, m, fuelRecord.getUploadtime,
          fuelRecord.getLon, fuelRecord.getLat, connection)
      }
    }
  }

}
