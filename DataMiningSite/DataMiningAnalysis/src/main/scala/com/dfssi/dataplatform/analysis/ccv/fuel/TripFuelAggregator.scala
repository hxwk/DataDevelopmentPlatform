package com.dfssi.dataplatform.analysis.ccv.fuel

import java.sql.Connection

import com.dfssi.common.math.Maths
import com.dfssi.dataplatform.analysis.ccv.fuel.counts.{AlarmCountor, VehicleTotalFuelCountor}
import com.dfssi.dataplatform.analysis.ccv.trip.{TripDataRecord, TripDataStore}
import com.dfssi.dataplatform.analysis.redis.ByteBufferRedis
import org.apache.spark.Logging
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{State, StateSpec}

/**
  * Description:
  *  行程切割 以及行程油耗聚合
  * @author LiXiaoCong
  * @version 2018/5/15 11:17 
  */
class TripFuelAggregator extends Serializable with Logging{

    def executeAggregate(route: DStream[(String, List[FuelDataRecord])],
                         bcConf: Broadcast[VehicleFuelConfig]): DStream[(String, List[FuelDataRecord])] ={
        val ssc = route.context
        val tuples = List[(String, (TripDataRecord, AlarmCountor, VehicleTotalFuelCountor))]()
        val initialRDD = ssc.sparkContext.parallelize(tuples)
        val spec = StateSpec.function(
            (vid: String,
             tripMsg: Option[List[FuelDataRecord]],
             state: State[(TripDataRecord, AlarmCountor, VehicleTotalFuelCountor)]) =>
                tripAggreateAndUpdate(vid, tripMsg, state, bcConf)
        ).initialState(initialRDD)

        //进行行程切割或更新
        val ds = route.mapWithState(spec)
        ssc.checkpoint(s"/tmp/cvv/trip/")

        val value = ds.stateSnapshots()
        value.foreachRDD(rdd =>{
           //状态刷新
            rdd.foreach(kv =>{
                val conf = bcConf.value
                val (finalTripState, alarmCountor, totalFuelCollector) = kv._2

                //结束长时间未传数据行程
                if(!finalTripState.empty &&
                        System.currentTimeMillis() - finalTripState.updateTime > conf.tr){
                    val redisClient = ByteBufferRedis(conf.getRedisClient)
                    val triptool = new TripDataStore(conf.fueltripTable, null, redisClient, conf.redisKeyPartitions)

                    finalTripState.isover = 1
                    tripIsValid(finalTripState, conf)
                    triptool.insertTripDataToRedis(finalTripState)
                    finalTripState.empty = true
                    redisClient.close()
                }
            })
        })

        ds
    }

    private def tripAggreateAndUpdate(vid: String,
                                      route: Option[List[FuelDataRecord]],
                                      state: State[(TripDataRecord, AlarmCountor, VehicleTotalFuelCountor)],
                                      bcConf: Broadcast[VehicleFuelConfig]): (String, List[FuelDataRecord]) = {

        val conf = bcConf.value
        var records = route.get
        if(records.size > 0) {
            try {
                val connection = conf.getConnection()
                val redisClient = ByteBufferRedis(conf.getRedisClient)
                val triptool = new TripDataStore(conf.fueltripTable, connection, redisClient, conf.redisKeyPartitions)

                var (finalTripState, alarmCountor, totalFuelCollector) = getStateDetail(state, vid, triptool, conf)

                val (tripState, n, fuelRecords) = checkTripAndInit(finalTripState, records, triptool, alarmCountor, connection)
                finalTripState = tripState
                records = fuelRecords

                var neendUpdate = true
                (n until records.size).map(i =>{
                    val record = records(i)

                    //检查驾驶行为异常告警
                    checkAbnormalDriving(record, alarmCountor, connection)

                    //行程切割
                    if(record.isOn){  //车辆处于运营
                        if(finalTripState.isover == 1){  //状态中的行程处于结束状态

                            if(isSameTripInVehicleOnAndTripOver(record, finalTripState, conf)){
                                //属于同一行程
                                finalTripState.isover = 0
                                finalTripState.isvalid = 1

                                updateTripRecordAndTotalFuel(record, finalTripState, totalFuelCollector, redisClient, connection)
                                triptool.insertTripDataToRedis(finalTripState)
                                record.tripid = finalTripState.id
                            }else{
                                //检查当前行程是否有效，并更新到库中
                                tripIsValid(finalTripState, conf)
                                triptool.updateTripDataToDB(finalTripState)

                                //创建新行程， 并插入库中
                                finalTripState = record.toTripDataRecord()
                                triptool.insertTripDataToDB(finalTripState)
                                triptool.insertTripDataToRedis(finalTripState)

                                record.tripid = finalTripState.id
                            }
                            neendUpdate = false
                        }else{

                            if(isSameTripInVehicleOnAndTripOn(record, finalTripState, conf)){
                                //属于同一行程
                                updateTripRecordAndTotalFuel(record, finalTripState, totalFuelCollector, redisClient, connection)
                                record.tripid = finalTripState.id
                                neendUpdate = true

                            }else{
                                //当前行程已结束 并更新到库中
                                tripIsValid(finalTripState, conf)
                                finalTripState.isover = 1
                                triptool.updateTripDataToDB(finalTripState)

                                //创建新行程， 并插入库中
                                finalTripState = record.toTripDataRecord()
                                triptool.insertTripDataToDB(finalTripState)
                                triptool.insertTripDataToRedis(finalTripState)

                                record.tripid = finalTripState.id
                                neendUpdate = false
                            }
                        }
                    }else{
                        if(finalTripState.isover == 0) { //状态中的行程处于运行状态
                            if(record.gpstime - finalTripState.endtime <= conf.ta) {
                                //属于同一行程
                                updateTripRecordAndTotalFuel(record, finalTripState, totalFuelCollector, redisClient, connection)
                                record.tripid = finalTripState.id
                            }

                            //当前行程已结束
                            tripIsValid(finalTripState, conf)
                            finalTripState.isover = 1

                            triptool.insertTripDataToRedis(finalTripState)
                        }
                        neendUpdate = false
                    }

                    if(neendUpdate)triptool.insertTripDataToRedis(finalTripState)
                })

                //更新数据刷新时间
                finalTripState.updateTime = System.currentTimeMillis()

                //将累计数据刷到redis
                totalFuelCollector.executeUpdate(redisClient)

                //刷新告警数据
                alarmCountor.flush(connection)

                //更新状态
                state.update((finalTripState,  alarmCountor, totalFuelCollector))

                redisClient.close()
                connection.close()

            } catch {
                case e: Exception => logError(s"更新${vid}的状态失败。", e)
            }
        }

        (vid, records)
    }

    private def getStateDetail(state: State[(TripDataRecord, AlarmCountor, VehicleTotalFuelCountor)],
                               vid: String,
                               tripTool: TripDataStore,
                               conf: VehicleFuelConfig): (TripDataRecord, AlarmCountor, VehicleTotalFuelCountor) ={
        var (finalTripState, alarmCountor, totalFuelCollector) =
            state.getOption().getOrElse((null, null, null))

        //如果最近状态为null, 则从数据库中获取最近一次状态
        if (finalTripState == null) finalTripState = tripTool.queryTripByVid(vid)

        //异常驾驶行为告警监控
        if(alarmCountor == null)
            alarmCountor = new AlarmCountor(conf.abnormaldriving, vid)

        //累计油耗、里程、运行时长 相关
        if(totalFuelCollector == null)
            totalFuelCollector = new VehicleTotalFuelCountor(conf.totalfuelTable, vid, conf.redisKeyPartitions)

        (finalTripState, alarmCountor, totalFuelCollector)
    }

    private def checkTripAndInit(finalTripRecord: TripDataRecord,
                                 fuelRecords: List[FuelDataRecord],
                                 tripTool: TripDataStore,
                                 alarmCountor: AlarmCountor,
                                 connection: Connection): (TripDataRecord, Int, List[FuelDataRecord]) ={
        if (finalTripRecord.empty) {
            val fuelRecord = fuelRecords.head
            val finalTrip = fuelRecord.toTripDataRecord()
            tripTool.insertTripDataToDB(finalTrip)
            fuelRecord.tripid = finalTrip.id

            checkAbnormalDriving(fuelRecord, alarmCountor, connection)

            (finalTrip, 1, fuelRecords)
        }else{
            //排除掉误传数据
            val records = fuelRecords.filter(_.gpstime >= finalTripRecord.endtime)
            if(records.size < fuelRecords.size){
                logWarning(s"vid: ${finalTripRecord.vid}存在数据时间错乱的问题, 当前时间点为：${finalTripRecord.endtime}, 错误时间为：\t\n" +
                        s"${fuelRecords.filter(_.gpstime < finalTripRecord.endtime).map(_.gpstime)}")
            }
            (finalTripRecord, 0, records)
        }
    }


    private def checkAbnormalDriving(fuelRecord: FuelDataRecord,
                                     alarmCountor: AlarmCountor,
                                     connection: Connection): Unit ={
        alarmCountor.updateDrivingAlarm(fuelRecord.alarms,
            fuelRecord.gpstime,
            fuelRecord.lon,
            fuelRecord.lat,
            fuelRecord.speed,
            connection)
    }

    private def tripIsValid(finalTripRecord: TripDataRecord,
                            conf: VehicleFuelConfig): Int ={
        val mileGap = finalTripRecord.getMileGap

        if(mileGap >= conf.da)
            finalTripRecord.isvalid = 1
        else
            finalTripRecord.isvalid = 0

        finalTripRecord.isvalid
    }

    //车辆在行驶中、当前行程已结束
    private def isSameTripInVehicleOnAndTripOver(fuelRecord: FuelDataRecord,
                                                 finalTripRecord: TripDataRecord,
                                                 conf: VehicleFuelConfig): Boolean ={
        val timeGap = fuelRecord.gpstime - finalTripRecord.endtime
        //时间在有效范围内则为有效
        var isSame = (timeGap <= conf.tv)
        if(!isSame && timeGap <= conf.ta){  //时间小于行程可接受范围
            isSame = (finalTripRecord.getMileGap <= conf.da)
        }
        isSame
    }
    //车辆在行驶中、当前行程未结束
    private def isSameTripInVehicleOnAndTripOn(fuelRecord: FuelDataRecord,
                                               finalTripRecord: TripDataRecord,
                                               conf: VehicleFuelConfig): Boolean ={
        val timeGap = fuelRecord.gpstime - finalTripRecord.endtime
        //时间在有效范围内则为有效
        var isSame = (timeGap <= conf.ta)
        if(!isSame && timeGap <= conf.tr){  //时间小于行程拒绝范围
            isSame = (finalTripRecord.getMileGap <= conf.da)
        }

        isSame
    }

    private def updateTripRecordAndTotalFuel(fuelRecord: FuelDataRecord,
                                             finalTripRecord: TripDataRecord,
                                             totalFuelCollector: VehicleTotalFuelCountor,
                                             redis: ByteBufferRedis,
                                             connection: Connection): Unit ={

        //计算油耗 并更新到记录中 和行程中
        val (interval, mileGap,  fuelGap) = calculateDiff(fuelRecord, finalTripRecord)
        if(mileGap >= 0 && fuelGap >= 0){

            fuelRecord.setGapMsg(interval, mileGap, fuelGap)
            finalTripRecord.totalfuel += fuelGap
            finalTripRecord.totalmile += mileGap

            //更新累计油耗  可能会存在异常更新！！
            totalFuelCollector.updateTotalFuel(fuelRecord.gpstime,
                fuelRecord.totalmile,
                fuelRecord.totalfuel,
                interval,
                redis,
                connection)
        }

        totalFuelCollector.updateTotalFuelEndTime(fuelRecord.gpstime, interval)

        finalTripRecord.endtotalfuel = fuelRecord.totalfuel
        finalTripRecord.endtotalmile = fuelRecord.totalmile
        finalTripRecord.endlat = fuelRecord.lat
        finalTripRecord.endlon = fuelRecord.lon
        finalTripRecord.endtime = fuelRecord.gpstime

    }

    private def calculateDiff(fuelRecord: FuelDataRecord,
                              finalTripRecord: TripDataRecord): (Long, Double, Double) ={

        //单位毫秒
        val timeGap = fuelRecord.gpstime - finalTripRecord.endtime

        //简单避免异常数据
        var mileGap = fuelRecord.totalmile - finalTripRecord.endtotalmile
        if(timeGap == 0)mileGap = 0.0
        var fuelGap = fuelRecord.totalfuel - finalTripRecord.endtotalfuel

        var mileError: Boolean = false
        if((Math.abs(mileGap) / 60.0) > 0.1){
            logError(s"${fuelRecord.vid}速度在时间范围[${finalTripRecord.endtime}, ${fuelRecord.gpstime}]" +
                    s"里的mileGap = ${mileGap}, timeGap = ${timeGap}车速异常, 忽略此距离。")
            mileError = true
        }

        var fuelError: Boolean = false
        if((Math.abs(fuelGap) / 60.0) > 0.1){
            logError(s"${fuelRecord.vid}油耗速度在时间范围[${finalTripRecord.endtime}, ${fuelRecord.gpstime}]" +
                    s"里的fuelGap = ${fuelGap}, timeGap = ${timeGap}油耗速度异常, 忽略此油耗。")
            fuelError = true
        }

        //同时错误的情况下 全部置零
        if(mileError && fuelError){
            mileGap = 0.0
            fuelGap = 0.0
        }

        (timeGap, Maths.precision(mileGap, 1), Maths.precision(fuelGap, 5))
    }
}
