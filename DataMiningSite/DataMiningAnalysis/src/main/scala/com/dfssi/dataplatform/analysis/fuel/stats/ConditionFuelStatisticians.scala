package com.dfssi.dataplatform.analysis.fuel.stats

import com.dfssi.common.databases.DBCommon
import com.dfssi.common.math.Maths
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
  *   离线油耗工况统计
  *
  * @author LiXiaoCong
  * @version 2018/1/30 10:25 
  */
class ConditionFuelStatisticians(fuelConfig: FuelConfig) extends Serializable with Logging{

  def start(day:String, partitions: Int): Unit ={

    val sparkContext = SparkContextFactory.newSparkBatchContext("vehicle_workcondition_fuel")
    val hiveContext = new HiveContext(sparkContext)

    val hiveDataBase = fuelConfig.getConfigItemValue("fuel.condition.hive.database.name", "prod_analysis")
    logInfo(s"hive的数据库为：${hiveDataBase}")

    val resTable = fuelConfig.getConfigItemValue("fuel.out.database.conditionfuel.table", "vehicle_workcondition_fuel")
    logInfo(s"存储工况油耗结果的Greenplum表为：${resTable}")

    //选择对应的库
    hiveContext.sql(s"use ${hiveDataBase}")
    import hiveContext.implicits._

    //获取需要处理的日期列表
    val days = getDays(day, resTable, hiveContext,
      fuelConfig.getConfigItemValue("fuel.condition.hive.0200.table", "terminal_0200"))
    logWarning(s"本次处理的日期如下：\n\t ${days.toList}")

    days.foreach(dayStr => {
      logWarning(s"开始处理${dayStr}的数据。")
      val resDF = conditionFuelRDD(hiveContext, dayStr, partitions).toDF

      val connectionProperties = new java.util.Properties()
      connectionProperties.put("driver", fuelConfig.getFuelDriver)
      resDF.write.mode(SaveMode.Append).jdbc(fuelConfig.getUrl, resTable, connectionProperties)

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

  private def conditionFuelRDD(hiveContext: HiveContext,
                               day:String,
                               partitions: Int): RDD[Condition] ={

    val year =  day.substring(0, 4)

    //0200和0705关联后的数据
    val fuelRecordRDD = readData(hiveContext, year, day, partitions)

    //计算油耗
    val conditionFuelRDD = fuelRecordRDD.map(kv =>{
      val fuels = kv._2
      val speedFuel = new mutable.HashMap[Int, (Int, Long, Double, Double)]()
      val rpmFuel   = new mutable.HashMap[Int, (Int, Long, Double, Double)]()

      //当天行车时长
      var totaltime = 0L
      var totalMile = 0.0
      var totalfuel = 0.0
      var lastItem = fuels.head

      fuels.tail.foreach(fuel =>{
        //连续两条数据的时间、里程、油耗的间隔
        val (interval, mileGap,  fuelGap) = calculateDiff(kv._1, lastItem, fuel)
        if(interval <= 5 * 60 * 1000L && mileGap >= 0 && fuelGap >= 0){
          //累计总时长 、总里程、总油耗
          totaltime = totaltime + interval
          totalMile = totalMile + mileGap
          totalfuel = totalfuel + fuelGap

          //计算速度阶段油耗
          if(fuel.speed > 0 || fuelGap > 0){
            val speedLevel = (fuel.speed / 10).toInt
            val speedLevelFuel = speedFuel.getOrElse(speedLevel, (0, 0L, 0.0, 0.0))
            speedFuel.put(speedLevel, (1 + speedLevelFuel._1, interval + speedLevelFuel._2,
              speedLevelFuel._3 + mileGap, speedLevelFuel._4 + fuelGap))
          }

          //计算转速阶段油耗, 过滤掉 未关联到转速的数据
          val rpmLevel = fuel.rpmLevel
          val rpmLevelFuel = rpmFuel.getOrElse(rpmLevel, (0, 0L, 0.0, 0.0))
          rpmFuel.put(rpmLevel, (1 + rpmLevelFuel._1, interval + rpmLevelFuel._2,
            rpmLevelFuel._3 + mileGap, rpmLevelFuel._4 + fuelGap))

        }else if(mileGap < 0 || fuelGap < 0){
          logError(s"数据累计油耗或累计里程有误：fuelGap = ${fuelGap}, mileGap = ${mileGap} \n\t"
            + s"${kv._1}: ${lastItem} / ${fuel}")
        }

        //重置lastItem
        lastItem = fuel
      })

      if(!speedFuel.isEmpty) {
        //速度、时间、油耗分布：level,count,interval,fuel
        val speedpair = speedFuel.map(kv => s"${kv._1},${kv._2._1},${kv._2._2},${Maths.precision(kv._2._3, 1)},${Maths.precision(kv._2._4, 5)}")
          .foldLeft("")((res, value) => s"${res};${value}")
          .substring(1)

        val rpmpair = rpmFuel.map(kv => s"${kv._1},${kv._2._1},${kv._2._2},${Maths.precision(kv._2._3, 1)},${Maths.precision(kv._2._4, 5)}")
          .foldLeft("")((res, value) => s"${res};${value}")
          .substring(1)

        //vid, 当天行驶时长，当然行驶里程，当天行驶油耗，速度、时间、油耗分布，高程、时间、油耗分布，当天
        Condition(kv._1, totaltime, Maths.precision(totalMile, 1),
          Maths.precision(totalfuel, 5), speedpair, rpmpair, day.toLong)
      }else{
        null
      }

    }).filter(_ != null)

    fuelRecordRDD.unpersist()

    conditionFuelRDD
  }


  private def calculateDiff(vid:String,
                            firstFuelItem: FuelItem,
                            secondFuelItem: FuelItem): (Long, Double, Double) ={

    val timeGap = secondFuelItem.time - firstFuelItem.time
    val timeGapHour = timeGap * 1.0 / (1000 * 60 * 60)

    //简单避免异常数据
    var mileGap = secondFuelItem.mile - firstFuelItem.mile
    if(timeGap == 0)mileGap = 0.0
    var fuelGap = secondFuelItem.fuel - firstFuelItem.fuel

    var mileError: Boolean = false
    if(timeGap != 0 && (Math.abs(mileGap) * 1.0 / timeGapHour) > 400){
       logError(s"${vid}速度在时间范围[${firstFuelItem.time}, ${secondFuelItem.time}]" +
        s"里的车速超过了 400 km/h, 忽略此距离。")
      mileError = true
    }

    var fuelError: Boolean = false
    if(timeGap != 0 && (Math.abs(fuelGap) * 1000.0 / timeGap) >= 0.1){
      logError(s"${vid}油耗速度在时间范围[${firstFuelItem.time}, ${secondFuelItem.time}]" +
        s"里的油耗速度超过了 360L/h, 忽略此油耗。")
      fuelError = true
    }

    //同时错误的情况下 全部置零
    if(mileError && fuelError){
      mileGap = 0.0
      fuelGap = 0.0
    }

    (timeGap, Maths.precision(mileGap, 1), Maths.precision(fuelGap, 5))
  }

  private def readData(hiveContext: HiveContext,
                       year: String,
                       day: String,
                       partitions: Int): RDD[(String, List[FuelItem])] ={
    //查询0200的数据
    val rdd0200 = read0200(hiveContext, year, day, partitions).persist(StorageLevel.MEMORY_AND_DISK_SER)
    //frame0200.show()

    //查询出发动机转速
    val rdd0705 = read0705(hiveContext, year, day, partitions).persist(StorageLevel.MEMORY_AND_DISK_SER)

    //相同vid的数据放一块
    val rdd = rdd0200.leftOuterJoin(rdd0705)

    //发动机转速 和 油耗数据关联
    val resData = rdd.map(kv =>{

      val res = kv._2._1.map(fuel =>{
        val rpms = new mutable.HashMap[Int, Int]()
        if(kv._2._2.nonEmpty) {
          val loop = new Breaks
          loop.breakable {
            kv._2._2.get.foreach(rpm => {
              if (Math.abs(fuel._4 - rpm._2) <= 1000) {
                val i = rpms.getOrElse(rpm._1, 0)
                rpms.put(rpm._1, i + 1)
              } else if (rpm._2 - fuel._4 > 2) {
                loop.break()
              }
            })
          }
        }

        //出现次数最多的一个转速
        var rpmLevel = -1
        if(rpms.size > 0){
          val last = rpms.toList.sortBy(_._2).last
          rpmLevel = last._1
        }
        FuelItem(fuel._1, fuel._2, fuel._3, fuel._4, rpmLevel)
      })

      (kv._1, res)
    }).persist(StorageLevel.MEMORY_AND_DISK_SER)

    rdd0200.unpersist()
    rdd0705.unpersist()

    resData
  }


  //0200的数据读取及解析
  private def read0200(hiveContext: HiveContext,
                       year: String,
                       day: String,
                       partitions: Int): RDD[(String, List[(Double, Double, Double, Long)])] ={

      val table = fuelConfig.getConfigItemValue("fuel.condition.hive.0200.table", "terminal_0200")
      logInfo(s"hive中0200数据的表名称为：${table}")

       //查询0200的数据
      val frame0200 = hiveContext.sql(s"select vid, speed, speed1, mile, cumulative_oil_consumption, gps_time from ${table} where gps_time is not null and  part_year=${year} and part_yearmonthday=${day}")

      //从0200中提起 油耗 里程 高程等数据
      val fuelRDD = frame0200.map(row =>{
        try {
          val vid = row.getAs[String]("vid")

          var speed = row.getAs[Long]("speed1") * 0.1
          if(speed == 0)speed = row.getAs[Long]("speed") * 0.1

          val mile = row.getAs[Long]("mile") * 0.1
          val fuel = row.getAs[Double]("cumulative_oil_consumption") / Math.pow(10, 5)
          val gpstime = row.getAs[Long]("gps_time")

          (vid, (speed, mile, fuel, gpstime))
        }catch {
          case e => {
            logError(s"解析表${table}数据失败: ${row.toString()}", e)
            (null, (0.0, 0.0, 0.0, 1L))
          }

        }
    }).filter(_._1 != null)//.persist(StorageLevel.MEMORY_AND_DISK_SER)

    //根据vid聚合并去重指定车辆的数据
    val createCombiner = (v: (Double, Double, Double, Long)) => mutable.HashSet[(Double, Double, Double, Long)](v)
    val mergeValue = (c: mutable.HashSet[(Double, Double, Double, Long)], v: (Double, Double, Double, Long)) => (c += v)
    val mergeCombiner = (c1: mutable.HashSet[(Double, Double, Double, Long)],
                         c2: mutable.HashSet[(Double, Double, Double, Long)]) => (c1 ++= c2)

    fuelRDD.combineByKey(createCombiner, mergeValue, mergeCombiner, new HashPartitioner(partitions))
             .map(kv => {
               val sorted = kv._2.toList.sortWith((f,s) =>{
                 var result = f._4.compareTo(s._4)

                 if (result == 0) {
                   result = f._2.compareTo(s._2)
                 }

                 if (result == 0) {
                   result = f._3.compareTo(s._3)
                 }
                 if(result > 0 )
                   false
                 else
                   true
               })
               (kv._1, sorted)
             })
  }

  //0705的数据读取及解析
  private def read0705(hiveContext: HiveContext,
                       year: String,
                       day: String,
                       partitions: Int): RDD[(String, List[(Int, Long)])] ={

    val table = fuelConfig.getConfigItemValue("fuel.condition.hive.0705.table", "terminal_0705")
    logInfo(s"hive中0705数据的表名称为：${table}")

    //查询出发动机转速
    val frame0705 = hiveContext.sql(s"select vid, receive_time, value from ${table} where part_year=${year} and part_yearmonthday=${day} and signal_name='453◎发动机转速_W' and value > 0.0")

    //根据vid聚合并去重指定转速
    val createCombiner = (v: (Int, Long)) => mutable.HashSet[(Int, Long)](v)
    val mergeValue = (c: mutable.HashSet[(Int, Long)], v: (Int, Long)) => (c += v)
    val mergeCombiner = (c1: mutable.HashSet[(Int, Long)],
                         c2: mutable.HashSet[(Int, Long)]) => (c1 ++= c2)
   val vidRpmRDD = frame0705.map(row =>{
     try {
       val vid = row.getAs[String]("vid")
       val receive_time = row.getAs[Long]("receive_time")

       //发动机转速提取 以及转速分级
       var rpmLevel = -2

       val rpm = row.getAs[Double]("value")
       if (rpm >= 0) rpmLevel = (rpm / 500).toInt

       (vid, (rpmLevel, receive_time))
     }catch {
       case e =>{
         logError(s"解析${table}数据失败：${row}", e)
         (null, (0, 1L))
       }
     }
    }).filter(kv => kv._1 != null && kv._2._1 >= 0).combineByKey(createCombiner, mergeValue, mergeCombiner, new HashPartitioner(partitions))
     .map(kv => (kv._1, kv._2.toList.sortBy(_._2)))

    vidRpmRDD
  }


}

case class Condition(vid: String, totaltime: Long, totalmile: Double, totalfuel: Double, speedpair: String, rpmpair: String, day: Long)

case class FuelItem(speed: Double, mile: Double, fuel: Double, time: Long, rpmLevel: Int) extends Serializable

object ConditionFuelStatisticians extends Logging{

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
    new ConditionFuelStatisticians(fuelConfig).start(day, partitions)
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
      formatter.printHelp("ConditionFuelStatisticians", options)
      System.exit(0)
    }

    lines
  }
}
