package com.dfssi.dataplatform.analysis.preprocess.process.rateindicator

import java.sql.Timestamp
import java.text.SimpleDateFormat

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.dbha.bean.{BasicEventBean, TripDetailBean}
import com.dfssi.dataplatform.analysis.dbha.behavior.BehaviorEventAnalyzer
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.preprocess.process.resource.HttpRequestUtils
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.Partitioner
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.hive.HiveContext
import org.joda.time.DateTime
import com.dfssi.dataplatform.analysis.dbha.db.{DbReader, DbUtils}

import scala.xml.Elem


/**
  * 行程处理调度主类，处理过程有
  * 1. 获取新增的行程
  * 2. 对每辆车，遍历其行程，然后进行：
  *   2.1 获取行程信息，包括形成起始点地址、驾驶员信息
  *   2.2 行程驾驶行为指标提取
  *   2.3 行程指标分布分析
  * 3.保存行程数据
  * 4.将处理过得行程进行标记
  *
  * @author lulin
  */
class PreprocessTripsV2 extends AbstractProcess {

  import PreprocessTripsV2._

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
    val paramsMap: Map[String, String] = extractSimpleParams(defEl).toMap

    val inputIds: Array[String] = getInputs(defEl)

    val optionDF = processContext.dataFrameMap.get(inputIds(0))//0?
    require(optionDF.nonEmpty && !optionDF.get.rdd.isEmpty())//require?

    val df = optionDF.get
      .where("id='597c34d123c538daa8c9ba07778f8090'")
    // 超长怠速 4b09b15f7e4039aa97b7a435ae8c60c1  bd11b436d2ff3d03bc5d82ec83c13e55
    // 发动机转速 bd11b436d2ff3d03bc5d82ec83c13e55
    // 冷车行驶 597c34d123c538daa8c9ba07778f8090
//    a027d98cd7043cea807c4cdc269bae30  a6155b2d34d533d4a5d0d3ad7dac1177 a027d98cd7043cea807c4cdc269bae30
//    55de71935c1f3ee391c8d8ea2fb45797  24582d61091434babb7132a14e4b0f69
//      .where("vid='d79929c429754206a7e787488a0db8f0'")

    /** step 1  */
    val newTripRdd = getNewFinishedTrips(df)

    /** step 2.1 get trip details */
    val tripDetailRdd = getTripDetailInfo(newTripRdd, paramsMap)

    /** step 2.2 and 2.3  */


    // step2 驾驶行为识别
//    val eventRdd = processParallel(tripDetailRdd, paramsMap, processContext.hiveContext)
    val vehicleTripEventRdd = processTripsOneByOne(tripDetailRdd, paramsMap, processContext.hiveContext)

    // step2 评价指标获取

  }


  private def getNewFinishedTrips(gpVehicleTripInput: DataFrame, maxAcceptTripNum: Int = 5): RDD[Row] = {
    val currentTimestamp = System.currentTimeMillis()

    val newTripRdd = gpVehicleTripInput.rdd
      .filter{ row =>
        row.getAs[Int]("isover") == 1 || (currentTimestamp - row.getAs[Long]("endtime")) >= 24 * 3600 * 1000
        // TODO 还需要判断是否有效行程
      }

    val tripCount = newTripRdd.count() + 0.1

    val fraction = math.min(maxAcceptTripNum, tripCount) * 1.0 /  tripCount

    val sampledTripRdd = newTripRdd.sample(withReplacement = false, fraction, 11L)

    logError(s"step 1 -----获取新增的行程，需要处理的行程数有 ${sampledTripRdd.count()}")

    sampledTripRdd
  }

  @DeveloperApi
  private def processParallel(tripDetailRdd: RDD[TripDetailBean],
                              paramsMap: Map[String, String],
                              hiveContext: HiveContext): Unit = {

    val vidSet =  tripDetailRdd.map(_.vehicleId).distinct().collect()

    vidSet.foreach{ vid =>
      println("当前车辆id "+vid)
      val tripTecords = tripDetailRdd.filter(_.vehicleId == vid)
        .collect()
        .sortBy(_.tripStartTime)

      val rangeBounds = tripTecords.map(_.tripEndTime)
      println(s"可划分的区域有 ${rangeBounds.mkString(",")}")
      val tripPartitioner = new TripPartitioner(rangeBounds)

      val minStartTimeOfAllTrips = tripTecords.head.tripStartTime
      val maxEndTimeOfAllTrips = tripTecords.last.tripEndTime

      val startDate = convertTimeStamp2DateStr(minStartTimeOfAllTrips, DAY_DATE_FORMAT)
      val endDate = convertTimeStamp2DateStr(maxEndTimeOfAllTrips, DAY_DATE_FORMAT)

      // 从Hive表中读取每段行程的数据进行处理
      val terminal0705DF = hiveContext
        .sql(s"SELECT vid, receive_time,signal_name,value FROM prod_analysis.$terminal_0705_tableName WHERE " +
          s" part_yearmonthday>=$startDate AND part_yearmonthday<=$endDate AND vid='$vid' " +
          s" AND receive_time IS NOT NULL AND receive_time>=$minStartTimeOfAllTrips AND receive_time<=$maxEndTimeOfAllTrips")

      val repartitioned0705Rdd = terminal0705DF.map{ row =>
        row.getAs[Long]("receive_time") -> row
      }.partitionBy(tripPartitioner)
//          .mapPartitions(_.map(_._2), preservesPartitioning = true)

      val terminal0200DF = hiveContext
        .sql(s"SELECT vid,alarms,dir,gps_time,mile,speed,speed1,vehicle_status,signal_states FROM prod_analysis.$terminal_0200_tableName " +
          s" WHERE part_yearmonthday>=$startDate AND part_yearmonthday<=$endDate AND vid='$vid' " +
          s" AND gps_time IS NOT NULL AND gps_time>=$minStartTimeOfAllTrips AND gps_time<=$maxEndTimeOfAllTrips")

      val eventsRdd: RDD[BasicEventBean] = terminal0200DF.map{ row =>
        row.getAs[Long]("gps_time") -> row
      }.partitionBy(tripPartitioner)
        .zipPartitions(repartitioned0705Rdd, preservesPartitioning = true) {
        (rdd1Iter,rdd2Iter) => {
          val _0200Data = rdd1Iter.map(_._2).toArray
          val _0705Data = rdd2Iter.map(_._2).toArray

          logError(s"该分区内0200数据大小为${_0200Data.length}")
          logError(s"该分区内0705数据大小为${_0705Data.length}")

          // TODO 提取该段行程的事件、指标
//          BehaviorEventAnalyzer.apply(paramsMap, _0200Data, _0705Data).getEvents
          null
        }
      }

      eventsRdd.collect().foreach(println)

      // 接下来，评价指标获取


    }
  }


  private def getTripDetailInfo(df: RDD[Row],
                                paramsMap: Map[String, String]): RDD[TripDetailBean] = {

    df.mapPartitions{ part =>
      val props = DbUtils.getMySqlDbProps("MotorcadeDB_",paramsMap)
      val conn = DbUtils.getSingleConn(props)

      val latLonRequestUrl = paramsMap("latLngUrl")

      val tmp = part.toArray.map{ row =>
        val vid = row.getAs[String]("vid")
        val tripId = row.getAs[String]("id")

        val startTime = row.getAs[Long]("starttime") // 毫秒
        val endTime = row.getAs[Long]("endtime") // 毫秒
        val startMile = row.getAs[Double]("starttotalmile") //km
        val endMile = row.getAs[Double]("endtotalmile") // km
        val startCumFuel = row.getAs[Double]("starttotalfuel") // L
        val endCumFuel = row.getAs[Double]("endtotalfuel") // L
        val startLat = row.getAs[Double]("startlat")
        val startLon = row.getAs[Double]("startlon")
        val endLat = row.getAs[Double]("endlat")
        val endLon = row.getAs[Double]("endlon")

        val tripDetailBean = new TripDetailBean(vid, tripId, startTime, endTime, startLat, startLon, endLat, endLon)

        tripDetailBean.totalMile = endMile - startMile // KM
        tripDetailBean.totalFuel = endCumFuel - startCumFuel

        tripDetailBean.fromPlace = HttpRequestUtils.getPositionIfno(latLonRequestUrl, startLat, startLon).toString
        tripDetailBean.toPlace =  HttpRequestUtils.getPositionIfno(latLonRequestUrl, endLat, endLon).toString

        // 获取车牌数据
        val (plateNo,dealer_id)= DbReader.getVehiclePlateNo(conn, vid)
        tripDetailBean.vehiclePlateNo =plateNo
        tripDetailBean.fleetId=dealer_id

        tripDetailBean
      }

      conn.close()
      tmp.toIterator
    }
  }


  private def processTripsOneByOne(tripDetailRdd: RDD[TripDetailBean],
                              paramsMap: Map[String, String], //这到底是啥样的参数？
                              hiveContext: HiveContext): Unit = {
    logError("step 2.2&2.3 --------- 行程分析")
    /** get related DB connection properties */
    val motorcadeDbProps = DbUtils.getMySqlDbProps("MotorcadeDB_", paramsMap)

    /** process each trip */
    val vehicleTripEvents = tripDetailRdd.collect().flatMap{ trip =>
      // parse the drive behavior events first
      val vehicleEvents: Iterator[BasicEventBean] = parseDriveBehavior(trip, paramsMap, hiveContext)
      vehicleEvents
      // then
    }

    // parallel these events for furthur process
    val vehicleTripEventsRdd = hiveContext.sparkContext.parallelize(vehicleTripEvents, 20)

    // associate each event with a driver

    /** get unique vehicle ids
    val vidSet = newTripRdd.map(_.getAs[String](GP_FIELD_VID)).distinct().collect()
    vidSet.foreach { vid =>
      logError("************车辆标识为： " + vid)
      val tripRecords = tripDetailRdd.filter(_.vehicleId == vid).collect().sortBy(_.tripStartTime)

      val behaviorEventAnalyzer = BehaviorEventAnalyzer(paramsMap)

      // 提取事件和指标并更新入库
      tripRecords.foreach { trip =>
        /**********************************************************************/
        // debug
        val startRunTime = System.currentTimeMillis()
        logError("行程id为： " + trip.tripId + "开始时间："+ startRunTime)
        /**********************************************************************/

        val tripStartDate = convertTimeStamp2DateStr(trip.tripStartTime, DAY_DATE_FORMAT)
        val tripEndDate = convertTimeStamp2DateStr(trip.tripEndTime, DAY_DATE_FORMAT)

        // 从Hive表中读取每段行程的数据进行处理
        val terminal0705Rdd = hiveContext
          .sql(s"SELECT receive_time,signal_name,value FROM prod_analysis.$terminal_0705_tableName WHERE vid='$vid' " +
            s" AND part_yearmonthday>=$tripStartDate AND part_yearmonthday<=$tripEndDate" +
            s" AND receive_time IS NOT NULL AND receive_time>=${trip.tripStartTime} AND receive_time<=${trip.tripEndTime}")
          .coalesce(20)
          .rdd.persist(StorageLevel.MEMORY_AND_DISK)

        val terminal0200Rdd = hiveContext
          .sql(s"SELECT alarms,dir,gps_time,mile,speed,speed1,vehicle_status,signal_states" +
            s" FROM prod_analysis.$terminal_0200_tableName WHERE vid='$vid' AND gps_time IS NOT NULL" +
            s" AND part_yearmonthday>=$tripStartDate AND part_yearmonthday<=$tripEndDate" +
            s" AND gps_time>=${trip.tripStartTime} AND gps_time<=${trip.tripEndTime}")
          .coalesce(20)
          .rdd.persist(StorageLevel.MEMORY_AND_DISK)

        logError("************提取这一段行程中的事件与指标")
        behaviorEventAnalyzer

        /** 提取这段行程的各种分布情况 */

        /**********************************************************************/
        // debug
        logDebug("行程id为： " + trip.tripId + "耗时："+ (System.currentTimeMillis()-startRunTime)/1000)
        /**********************************************************************/

        terminal0705Rdd.unpersist()
        terminal0200Rdd.unpersist()
      }
    }*/
  }

  private def parseDriveBehavior(trip: TripDetailBean,
                                 paramsMap: Map[String, String],
                                 hiveContext: HiveContext): Iterator[BasicEventBean] = {
    /**********************************************************************/
    // debug
    val startRunTime = System.currentTimeMillis()
    logError("行程id为： " + trip.tripId + "开始时间："+ startRunTime)
    /**********************************************************************/

    val vid = trip.vehicleId

    val tripStartDate = convertTimeStamp2DateStr(trip.tripStartTime, DAY_DATE_FORMAT)
    val tripEndDate = convertTimeStamp2DateStr(trip.tripEndTime, DAY_DATE_FORMAT)

    // 从Hive表中读取每段行程的数据进行处理
    val terminal0705 = hiveContext
      .sql(s"SELECT receive_time,signal_name,value FROM prod_analysis.$terminal_0705_tableName WHERE vid='$vid' " +
        s" AND part_yearmonthday>=$tripStartDate AND part_yearmonthday<=$tripEndDate" +
        s" AND receive_time IS NOT NULL AND receive_time>=${trip.tripStartTime} AND receive_time<=${trip.tripEndTime}")
      .collect()

    val terminal0200 = hiveContext
      .sql(s"SELECT alarms,dir,gps_time,mile,speed,speed1,vehicle_status,signal_states" +
        s" FROM prod_analysis.$terminal_0200_tableName WHERE vid='$vid' AND gps_time IS NOT NULL" +
        s" AND part_yearmonthday>=$tripStartDate AND part_yearmonthday<=$tripEndDate" +
        s" AND gps_time>=${trip.tripStartTime} AND gps_time<=${trip.tripEndTime}")
      .collect()

    logError("************提取这一段行程中的事件与指标")
    val behaviorEvents = BehaviorEventAnalyzer(vid, trip.tripId, paramsMap).getEvents(terminal0200, terminal0705)

    /**********************************************************************/
    // debug
    logDebug("行程id为： " + trip.tripId + "耗时："+ (System.currentTimeMillis()-startRunTime)/1000)
    /**********************************************************************/

    behaviorEvents
  }


  private def assocDriverAndEvent(trip: TripDetailBean,
                                  paramsMap: Map[String, String],
                                  events: Iterator[BasicEventBean]) = {
    val startTime = new Timestamp(trip.tripStartTime)
    val endTime = new Timestamp(trip.tripEndTime)

    // 打卡类型，0-休息，1-驾驶
    // 人车的状态，0-休息中，无绑定,1-运行中，已绑定
    s"SELECT UID, TIME, TYPE, STATUS FROM SSI_APP_LOCK WHERE VID='${trip.vehicleId}' AND TIME"


  }

}


object PreprocessTripsV2 {

  val terminal_0200_tableName = "terminal_0200"

  val terminal_0705_tableName = "terminal_0705"
//  val terminal_0705_tableName = "terminal_0705_inner"

  val GP_FIELD_VID = "vid"

  case class SimpleTripDetail(vehicle_id: String, fleet_id: String, trip_end_time: Long)

  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_TRIPS_V2

  final val DAY_DATE_FORMAT = "yyyyMMdd"

  def convertTimeStamp2DateStr(timestamp: Long, pattern: String): String = {
    val format = new SimpleDateFormat(pattern)

    new DateTime(timestamp).toString(pattern)
  }

}

/**
  * 自定义分区器，将一辆车的每个行程划分到一个分区中
  * @param rangeBounds 所有行程结束时间构成的数组
  * @param ascending    是否升序
  */
class TripPartitioner(rangeBounds: Array[Long],
                      private var ascending: Boolean = true) extends Partitioner {

  override def numPartitions: Int = rangeBounds.length //+ 1

  def getPartition(key: Any): Int = {
    val k = key.asInstanceOf[Long]
    var partition = 0

    while (partition < rangeBounds.length && k > rangeBounds(partition)) {
      partition += 1
    }
    // If we have more than 128 partitions naive search, consider binary search
    if (ascending) {
      partition
    } else {
      rangeBounds.length - partition
    }
  }
}
