package com.dfssi.dataplatform.analysis.fuel.stats

import java.math.BigDecimal

import com.dfssi.common.databases.DBCommon
import com.dfssi.dataplatform.analysis.fuel.FuelConfig
import com.dfssi.spark.SparkContextFactory
import org.apache.commons.cli.{HelpFormatter, Options, ParseException, PosixParser}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{HashPartitioner, Logging}
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, Days}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks

/**
  * Description:
  *    车辆工况统计  离线任务
  * @author LiXiaoCong
  * @version 2018/5/10 8:33 
  */
class VehicleWorkConditionStatistics(fuelConfig: FuelConfig)  extends Serializable with Logging{

    def start(day:String, partitions: Int): Unit ={

        val sparkContext = SparkContextFactory.newSparkBatchContext("vehicle_workcondition_fuel")
        val hiveContext = new HiveContext(sparkContext)

        val hiveDataBase = fuelConfig.getConfigItemValue("fuel.condition.hive.database.name", "prod_analysis")
        logInfo(s"hive的数据库为：${hiveDataBase}")

        val resTable = fuelConfig.getConfigItemValue("fuel.out.database.conditionfuel.table", "vehicle_workcondition_fuel")
        logInfo(s"存储工况油耗结果的Greenplum表为：${resTable}")

        val areaTable = fuelConfig.getConfigItemValue("fuel.out.database.area.table", "vehicle_area_fuel")
        logInfo(s"存储区域油耗结果的Greenplum表为：${areaTable}")

        //选择对应的库
        hiveContext.sql(s"use ${hiveDataBase}")

        //获取需要处理的日期列表
        val days = getDays(day, resTable, hiveContext,
            fuelConfig.getConfigItemValue("fuel.condition.hive.0200.table", "terminal_0200"))
        logWarning(s"本次处理的日期如下：\n\t ${days.toList}")

        days.foreach(dayStr => {
            logWarning(s"开始处理${dayStr}的数据。")
            executeStatistics(hiveContext, dayStr, partitions, resTable, areaTable)
            logWarning(s"处理${dayStr}的数据完成。")
        })
        sparkContext.stop()
    }

    private def getDays(days: String,
                        conditionTable: String,
                        hiveContext: HiveContext,
                        hiveTable: String): Array[String] ={
        if(days == null){
            val res = new ArrayBuffer[String]()
            val maxDealDay = getMaxDealDayInGP(conditionTable)
            logWarning(s"GP表${conditionTable}中最大处理日期为：${maxDealDay}")
            if(maxDealDay == null){
                val partitions = getPartitions(hiveContext, hiveTable)
                logWarning(s"Hive表${hiveTable}中Partitions有：${partitions.toList}")
                res ++=  partitions
            }else{
                val maxDealDate = DateTime.parse(maxDealDay, DateTimeFormat.forPattern("yyyyMMdd"))
                val gapDay = Days.daysBetween(maxDealDate, DateTime.now()).getDays
                if(gapDay > 1){
                    for(i <- 1 until gapDay){
                        res += maxDealDate.plusDays(i).toString("yyyyMMdd")
                    }
                }
            }
            res.toArray
        }else{
            days.split(",")
        }
    }

    private def getPartitions(hiveContext: HiveContext, table: String): Array[String] ={
        val dataFrame = hiveContext.sql(s"show partitions ${table}")
        dataFrame.map(row =>{
            val partition = row.getAs[String]("result")
            partition.substring(partition.lastIndexOf("=") + 1)
        }).collect()
    }

    //从gp库中获取最大的处理日期
    private def getMaxDealDayInGP(conditionTable: String): String ={
        val connection = fuelConfig.getFuelConnection
        var res: String = null
        try{
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(s"select max(day) latest from ${conditionTable}")
            if(resultSet.next()){
                res = resultSet.getLong("latest").toString
            }
            DBCommon.close(resultSet)
            DBCommon.close(statement)
        }catch {
            case e => logError(s"查询表${conditionTable}失败", e)
        }finally {
            fuelConfig.closeFuelConnection()
        }
        res
    }

    private def executeStatistics(hiveContext: HiveContext,
                                  day:String,
                                  partitions: Int,
                                  workConditionTable: String,
                                  areaTable: String): Unit ={

        import hiveContext.implicits._
        val year =  day.substring(0, 4)

        //0200和0705关联后的数据
        val (rdd0200, unionRDD) = readData(hiveContext, year, day, partitions)

        //处理工况油耗
        val workConditionRes = executeWorkConditionStatistics(hiveContext, day, partitions, unionRDD).toDF
        val connectionProperties = new java.util.Properties()
        connectionProperties.put("driver", fuelConfig.getFuelDriver)
        workConditionRes.write.mode(SaveMode.Append).jdbc(fuelConfig.getUrl, workConditionTable, connectionProperties)

        //处理行政区划油耗
        val areaRes = executeAreaFuelStatistics(hiveContext, day, partitions, rdd0200).toDF()
        areaRes.write.mode(SaveMode.Append).jdbc(fuelConfig.getUrl, areaTable, connectionProperties)
        rdd0200.unpersist()
    }


    private def executeAreaFuelStatistics(hiveContext: HiveContext,
                                          day:String,
                                          partitions: Int,
                                          rdd: RDD[(String, List[Terminal0200RunningRecord])]): RDD[VehicleAreaFuelRecord] ={

        val areaRDD = rdd.flatMap(kv =>{
            val records = kv._2.filter(_.district != null).groupBy(_.district)
            records.map(data =>{
                var totaltime = 0L
                var totalMile = 0.0
                var totalfuel = 0.0
                var latest = data._2.head
                data._2.tail.foreach(r =>{
                    val (timeGap, mileGap, fuelGap) = r.calculateGap(latest)
                    if(timeGap <= 5 * 60 * 1000L && mileGap >= 0 && fuelGap >= 0) {
                        //累计总时长 、总里程、总油耗
                        totaltime = totaltime + timeGap
                        totalMile = totalMile + mileGap
                        totalfuel = totalfuel + fuelGap
                    }
                    latest = r
                })
                (data._1, new VehicleAreaFuelRecord(totaltime, totalMile, totalfuel,
                    latest.province, latest.city, latest.district, day.toLong))
            }).toList
        })

      areaRDD.reduceByKey((v1, v2) =>{
            v1.totaltime += v2.totaltime
            v1.totalfuel += v2.totalfuel
            v1.totalmile += v2.totalmile
            v1
        }).map(_._2)
    }

    private def executeWorkConditionStatistics(hiveContext: HiveContext,
                                               day:String,
                                               partitions: Int,
                                               rdd: RDD[List[Terminal0200RunningRecord]]): RDD[VehicleWorkConditionRecord] ={

        rdd.map(record0200List =>{

            var latest = record0200List.head
            var totaltime = 0L
            var totalMile = 0.0
            var totalBrakeMile = 0.0
            var totalfuel = 0.0
            var totalIdlefuel = 0.0

            val speedDistributedMap = new mutable.HashMap[Int, DistributedRecord]()
            val rpmDistributedMap   = new mutable.HashMap[Int, DistributedRecord]()
            val accDistributedMap   = new mutable.HashMap[Int, DistributedRecord]()
            val gearDistributedMap  = new mutable.HashMap[Int, DistributedRecord]()
            val gearSpeedDistributed = new mutable.HashMap[Int, mutable.HashMap[Int, DistributedRecord]]()

            //遍历统计
            record0200List.tail.foreach(record0200 =>{
                val (timeGap, mileGap, fuelGap) = record0200.calculateGap(latest)
                //时间间隔在五分钟以内
                if(timeGap <= 5 * 60 * 1000L && mileGap >= 0 && fuelGap >= 0){

                    //累计总时长 、总里程、总油耗
                    totaltime = totaltime + timeGap
                    totalMile = totalMile + mileGap
                    totalfuel = totalfuel + fuelGap

                    //统计制动里程  当有脚刹或手刹信号时
                    if(record0200.handbrake == 1 || record0200.footbrake == 1){
                        totalBrakeMile = totalBrakeMile + mileGap
                    }

                    //统计怠速油耗
                    val rpm = record0200.rpm
                    if(rpm > 0 && rpm <= 1000 && record0200.speed == 0){
                        totalIdlefuel = totalIdlefuel + fuelGap
                    }

                    //计算速度阶段油耗
                    var speedLevel = -1
                    if(record0200.speed > 0 || fuelGap > 0){
                        speedLevel = (record0200.speed / 10).toInt
                        val distributedRecord = speedDistributedMap.getOrElseUpdate(speedLevel,
                            new DistributedRecord(speedLevel, 0, 0L, 0.0, 0.0))
                        distributedRecord.add(1, timeGap, mileGap, fuelGap)
                    }

                    //计算转速阶段油耗
                    if(rpm > 0){
                        val rpmLevel = (rpm / 300)
                        val distributedRecord = rpmDistributedMap.getOrElseUpdate(rpmLevel,
                            new DistributedRecord(rpmLevel, 0, 0L, 0.0, 0.0))
                        distributedRecord.add(1, timeGap, mileGap, fuelGap)
                    }

                    //计算油门开度油耗
                    val acc = record0200.acc
                    if(acc > 0){
                        val accLevel = (acc / 5)
                        val distributedRecord = accDistributedMap.getOrElseUpdate(accLevel,
                            new DistributedRecord(accLevel, 0, 0L, 0.0, 0.0))
                        distributedRecord.add(1, timeGap, mileGap, fuelGap)
                    }

                    //计算档位油耗 速度分布
                    val gear = record0200.gear
                    if(gear > 0 || (gear ==0 && fuelGap > 0)){
                        val distributedRecord = gearDistributedMap.getOrElseUpdate(gear,
                            new DistributedRecord(gear, 0, 0L, 0.0, 0.0))
                        distributedRecord.add(1, timeGap, mileGap, fuelGap)
                        if(speedLevel > -1){
                            //不同档位的速度分布
                            val speedDistri = gearSpeedDistributed.getOrElseUpdate(gear,
                                new mutable.HashMap[Int, DistributedRecord]())

                            val distributedRecord1 = speedDistri.getOrElseUpdate(speedLevel,
                                new DistributedRecord(speedLevel, 0, 0L, 0.0, 0.0))
                            distributedRecord1.add(1, timeGap, mileGap, fuelGap)
                        }
                    }
                }else{
                    logError(s"数据累计油耗或累计里程有误：fuelGap = ${fuelGap}, mileGap = ${mileGap} \n\t"
                            + s"${latest} / ${record0200}")
                }
                latest = record0200
            })

            //汇总统计结果 生成最终结果
            var speedDistributedStr: String = null
            if(speedDistributedMap.nonEmpty){
                speedDistributedStr = speedDistributedMap.map(_._2.mkString()).mkString(";")
            }

            var rpmDistributedStr: String = null
            if(rpmDistributedMap.nonEmpty){
                rpmDistributedStr = rpmDistributedMap.map(_._2.mkString()).mkString(";")
            }

            //油门开度
            var accDistributedStr: String = null
            if(accDistributedMap.nonEmpty){
                accDistributedStr = accDistributedMap.map(_._2.mkString()).mkString(";")
            }

            var gearDistributedStr: String = null
            if(gearDistributedMap.nonEmpty){
                gearDistributedStr = gearDistributedMap.map(_._2.mkString()).mkString(";")
            }

            var gearSpeedDistributedStr: String = null
            if(gearSpeedDistributed.nonEmpty){
                gearSpeedDistributedStr = gearSpeedDistributed.map(kv => {
                    val speedStr = kv._2.map(_._2.mkString()).mkString(";")
                    s"${kv._1}:${speedStr}"
                }).mkString("#")
            }

            new VehicleWorkConditionRecord(latest.vid,
                totaltime,
                precisionDouble(totalMile, 1),
                precisionDouble(totalBrakeMile, 1),
                precisionDouble(totalfuel, 5),
                precisionDouble(totalIdlefuel, 5),
                speedDistributedStr,
                rpmDistributedStr,
                accDistributedStr,
                gearDistributedStr,
                gearSpeedDistributedStr,
                day.toLong)
        })
    }


    //读取出0200和0705的数据 并进行关联
    private def readData(hiveContext: HiveContext,
                         year: String,
                         day: String,
                         partitions: Int):
    (RDD[(String, List[Terminal0200RunningRecord])], RDD[List[Terminal0200RunningRecord]]) ={
        //查询0200的数据
        val rdd0200 = read0200(hiveContext, year, day, partitions).persist(StorageLevel.MEMORY_AND_DISK_SER)

        //查询出发动机转速
        val rdd0705 = read0705(hiveContext, year, day, partitions).persist(StorageLevel.MEMORY_AND_DISK_SER)

        //相同vid的数据放一块
        val rdd = rdd0200.leftOuterJoin(rdd0705)

        //数据关联 只有当0705的数据的时间 小于 0200的时间，并且间隔 在 2s以内 才能关联
        val resData = rdd.map(kv =>{
            val record0200List = kv._2._1
            val record0705List = kv._2._2.getOrElse(List.empty[Terminal0705RunningRecord])
            if(record0705List.nonEmpty){
                var index: Int = 0
                var timeGap: Long = 0
                var record0705: Terminal0705RunningRecord = null
                val record0705Size = record0705List.size
                record0200List.map(record0200 =>{
                    val loop = new Breaks
                    loop.breakable{
                        for(i <- index until record0705Size){
                            record0705 = record0705List(i)
                            timeGap = record0705.gpstime - record0200.gpstime
                            if(timeGap > 0){
                                index = i;
                                loop.break()
                            }else if(timeGap > -2000){
                                record0200.add0705Item(record0705.name, record0705)
                            }
                        }
                    }
                })
            }
            record0200List.map(_.extract0705Item())
        })
        rdd0705.unpersist()

        (rdd0200, resData)
    }


    //0200的数据读取及解析
    private def read0200(hiveContext: HiveContext,
                         year: String,
                         day: String,
                         partitions: Int): RDD[(String, List[Terminal0200RunningRecord])] ={

        val table = fuelConfig.getConfigItemValue("fuel.condition.hive.0200.table", "terminal_0200")
        logInfo(s"hive中0200数据的表名称为：${table}")

        //查询0200的数据
        val frame0200 = hiveContext.sql(s"select vid, speed, speed1, mile, cumulative_oil_consumption, gps_time, lon, lat from ${table} where gps_time is not null and  part_year=${year} and part_yearmonthday=${day}")

        val service = fuelConfig.getSsiGeoCodeService

        //从0200中提起 油耗 里程 高程等数据
        val fuelRDD = frame0200.map(row =>{
            var vid: String = null
            var record: Terminal0200RunningRecord = null
            try {
                vid = row.getAs[String]("vid")

                var speed = row.getAs[Long]("speed1") * 0.1
                if(speed == 0)speed = row.getAs[Long]("speed") * 0.1

                val mile = row.getAs[Long]("mile") * 0.1
                val fuel = row.getAs[Double]("cumulative_oil_consumption") / Math.pow(10, 5)
                val gpstime = row.getAs[Long]("gps_time")

                record = Terminal0200RunningRecord(vid, speed, mile, fuel, gpstime)

                //获取省市区的行政区划
                val lon = row.getAs[Long]("lon").toDouble / Math.pow(10, 6)
                val lat = row.getAs[Long]("lat").toDouble / Math.pow(10, 6)
                if(lon > 0.0 && lat > 0.0){
                    val district = service.rgeoCode(lon, lat)
                    if(district != null){
                        record.district = district.getCode
                        record.city = district.getCity.getCode
                        record.province = district.getCity.getProvince.getCode
                    }
                }
            }catch {
                case e => {
                    logError(s"解析表${table}数据失败: ${row.toString()}", e)
                }
            }
            (vid, record)
        }).filter(_._1 != null)

        //根据vid聚合并去重指定车辆的数据
        val createCombiner = (v: Terminal0200RunningRecord) => mutable.HashSet[Terminal0200RunningRecord](v)
        val mergeValue = (c: mutable.HashSet[Terminal0200RunningRecord], v: Terminal0200RunningRecord) => (c += v)
        val mergeCombiner = (c1: mutable.HashSet[Terminal0200RunningRecord],
                             c2: mutable.HashSet[Terminal0200RunningRecord]) => (c1 ++= c2)

        fuelRDD.combineByKey(createCombiner, mergeValue, mergeCombiner, new HashPartitioner(partitions))
                .map(kv => {
                    (kv._1, kv._2.toList.sorted)
                })
    }

    private def read0705(hiveContext: HiveContext,
                         year: String,
                         day: String,
                         partitions: Int): RDD[(String, List[Terminal0705RunningRecord])] ={

        val table = fuelConfig.getConfigItemValue("fuel.condition.hive.0705.table", "terminal_0705")
        logInfo(s"hive中0705数据的表名称为：${table}")

        //查询出发动机转速
        val frame0705 = hiveContext.sql(s"select vid, receive_time, signal_name, value from ${table} where part_year=${year} and part_yearmonthday=${day} " +
                s"and signal_name in ('453◎发动机转速_W', '664◎油门踏板开度_W', '1000025◎当前档位1_W', '1052◎手刹_W', '1053◎脚刹_W') and value > 0")

        //根据vid聚合并去重指定转速
        val createCombiner = (v: Terminal0705RunningRecord) => mutable.HashSet[Terminal0705RunningRecord](v)
        val mergeValue = (c: mutable.HashSet[Terminal0705RunningRecord], v: Terminal0705RunningRecord) => (c += v)
        val mergeCombiner = (c1: mutable.HashSet[Terminal0705RunningRecord],
                             c2: mutable.HashSet[Terminal0705RunningRecord]) => (c1 ++= c2)
        val vidRpmRDD = frame0705.map(row =>{
            try {
                val vid = row.getAs[String]("vid")
                val gpsTime = row.getAs[Long]("receive_time")
                val name = row.getAs[String]("signal_name")
                val value = row.getAs[Double]("value")

                (vid, Terminal0705RunningRecord(vid, name, value, gpsTime))
            }catch {
                case e =>{
                    logError(s"解析${table}数据失败：${row}", e)
                    (null, Terminal0705RunningRecord(null, null, 0, 1L))
                }
            }
        }).filter(kv => kv._1 != null)
                .combineByKey(createCombiner, mergeValue, mergeCombiner, new HashPartitioner(partitions))
                .map(kv => (kv._1, kv._2.toList.sorted))

        vidRpmRDD
    }

    private def precisionDouble(value: Double, precision: Int): Double = {
        val bg = new BigDecimal(value)
        bg.setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue
    }
}

object VehicleWorkConditionStatistics extends Logging{

    //默认参数
    private val _PARTITIONS = "3"

    def main(args: Array[String]): Unit = {

        val line = parseArgs(args)
        val day = line.getOptionValue("daytime")

        val partitions = line.getOptionValue("partitions", "3").toInt
        val env = line.getOptionValue("env", "NONE")

        logInfo(s" 	  daytime     ：  $day ")
        logInfo(s" 	  partitions  ：  $partitions ")
        logInfo(s" 	  env         ：  $env ")

        val fuelConfig = new FuelConfig(env, false)
        new VehicleWorkConditionStatistics(fuelConfig).start(day, partitions)
    }

    @throws[ParseException]
    private def parseArgs(args: Array[String]) = {

        val options = new Options

        options.addOption("help", false, "帮助 打印参数详情")
        options.addOption("daytime", true, "处理数据的日期格式：yyyyMMdd, 默认前一天")
        options.addOption("partitions", true, s"默认分区数 建议核数的整数倍 默认为 ${_PARTITIONS}")
        options.addOption("env", true, "运行环境: PRO, DEV, TEST, NONE, 默认为NONE")

        val formatter = new HelpFormatter
        formatter.setOptPrefix(HelpFormatter.DEFAULT_LONG_OPT_PREFIX)

        val parser = new PosixParser
        val lines = parser.parse(options, args)

        if(lines.hasOption("help")){
            formatter.printHelp("VehicleWorkConditionStatistics", options)
            System.exit(0)
        }

        lines
    }
}

