package com.dfssi.dataplatform.analysis.preprocess.process.rateindicator

import java.sql.Connection
import java.text.SimpleDateFormat
import java.util.Properties

import com.dfssi.dataplatform.analysis.common.{AbstractProcess, ProcessContext}
import com.dfssi.dataplatform.analysis.dbha.SingleTripProcess
import com.dfssi.dataplatform.analysis.dbha.associnfo.DriverInfo
import com.dfssi.dataplatform.analysis.dbha.associnfo.DriverInfo.DriverRange
import com.dfssi.dataplatform.analysis.dbha.bean.TripDetailBean
import com.dfssi.dataplatform.analysis.dbha.driverbehavior.clutch.CustomOfUseClutch
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.DriveBehaviorDescriptor
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.DriveStyleTable.DriveStyle
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.EngineRpmDistributionTable.ENGINE_RPM
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.ThrottleOpeningDistributionTable.ThrottleOpening
import com.dfssi.dataplatform.analysis.dbha.rdbutils.{DbManager, DbPropsParser, OutputRdbms}
import com.dfssi.dataplatform.analysis.preprocess.ProcessFactory
import com.dfssi.dataplatform.analysis.preprocess.process.resource.HttpRequestUtils
import com.dfssi.dataplatform.analysis.utils.{SparkDefTag, XmlUtils}
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.storage.StorageLevel
import org.joda.time.DateTime

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
class PreprocessTrips extends AbstractProcess {

  import PreprocessTrips._

  override def execute(processContext: ProcessContext, defEl: Elem, sparkTaskDefEl: Elem): Unit = {
    val id = XmlUtils.getAttrValue(defEl, SparkDefTag.SPARK_DEF_ATTR_TAG_ID)
    val paramsMap: Map[String, String] = extractSimpleParams(defEl).toMap

    val inputIds: Array[String] = getInputs(defEl)

    val optionDF = processContext.dataFrameMap.get(inputIds(0))//0?
    require(optionDF.nonEmpty && !optionDF.get.rdd.isEmpty())//require?

    /** step 1 */
    val newTripRdd = getNewFinishedTrips(optionDF.get)

    /** step 2.1 get trip details */
    val tripDetailRdd = getTripDetailInfoV2(newTripRdd, paramsMap, processContext.hiveContext)

    /** step 2.2 and 2.3  */
    processOneByOne(newTripRdd,tripDetailRdd, paramsMap, processContext.hiveContext)

    /** step 3 更新trip_detail和latest_trip_detail表内容 */
    saveTripDetails(paramsMap, tripDetailRdd)

    /** step 4 更改vehicle_trip表中isprocessed 和 processtime 字段，标识已经处理过 */
    markProcessedTrips(paramsMap, tripDetailRdd)

    /** setp 5 output */
    val simpleTripDetailDF = toSimpleTripDetailDF(tripDetailRdd, processContext.hiveContext)

    processContext.dataFrameMap.put(id, simpleTripDetailDF)
  }


  private def getNewFinishedTrips(gpVehicleTripInput: DataFrame): RDD[Row] = {
    val currentTimestamp = System.currentTimeMillis()

    val newTripRdd = gpVehicleTripInput.rdd
      .filter{ row =>
        row.getAs[Int]("isover") == 1 || (currentTimestamp - row.getAs[Long]("endtime")) >= 24 * 3600 * 1000
        // TODO 还需要判断是否有效行程
      }

    val tripCount = newTripRdd.count() + 0.1

    val fraction = math.min(5.0, tripCount) / tripCount

    val sampledTripRdd = newTripRdd.sample(withReplacement = false, fraction, 11L)

    logError(s"step 1 -----获取新增的行程，需要处理的行程数有 ${sampledTripRdd.count()}")

    sampledTripRdd
  }

  @DeveloperApi
  private def getTripDetailInfoV2(newTripRdd: RDD[Row],
                                paramsMap: Map[String, String],
                                hiveContext: HiveContext): RDD[TripDetailBean] = {
    logError("step 2.1 --------获取行程明细信息")
    val serviceDbProps = DbPropsParser.getMySqlMotocadeDbProps(paramsMap)

    val startTime = newTripRdd.map(_.getAs[Long]("starttime")).min()
    val endtTime = newTripRdd.map(_.getAs[Long]("endtime")).max()

    val vid2DriverRangesMap = DriverInfo.collectVidDriverInfo(startTime, endtTime, hiveContext)

    val latLonRequestUrl = paramsMap("latLngUrl")

    newTripRdd.groupBy(_.getAs[String]("vid")).mapPartitions{ part =>
      val conn = DbManager.apply.getSingleConn(serviceDbProps)

      val t = part.flatMap{ case(vid, iter) =>
        val basicTripDetailIter = iter.toArray.map{ row =>
          val tripId = row.getAs[String]("id")
          val startTime = row.getAs[Long]("starttime") // 毫秒
          val endTime = row.getAs[Long]("endtime")    // 毫秒
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
          val (plateNo,dealer_id)= OutputRdbms.getVehiclePlateNo(conn, vid)
          tripDetailBean.vehiclePlateNo =plateNo
          tripDetailBean.fleetId=dealer_id

          tripDetailBean
        }

        // 将车与驾驶员进行关联
        associateDriverInfo(vid, vid2DriverRangesMap, basicTripDetailIter, conn)

        basicTripDetailIter
      }.toArray

      conn.close()
      t.toIterator
    }
      .sortBy(_.tripStartTime)
  }

  private def associateDriverInfo(vid: String,
                                  vid2DriverRangesMap: collection.Map[String, List[DriverRange]],
                                   basicTripDetailIter: Array[TripDetailBean],
                                  conn: Connection): Unit = {

    vid2DriverRangesMap.get(vid) match {
      case Some(driverRanges) if driverRanges.nonEmpty =>
        var i = 0
        basicTripDetailIter.foreach{ trip =>
          // i 左移 直到与当前行程的时间段有交集
          while (i < driverRanges.length && driverRanges(i).endTime < trip.tripStartTime) i += 1

          // i 到尽头 或者 当前行程小于当前 驾驶区间
          if(i == driverRanges.length || driverRanges(i).startTime >= trip.tripEndTime) {
            // do nothing
          }
          else {
            // 根据从业资格证号获取diverId
            val queryRes = OutputRdbms.getDriverIdByPractionerId(conn, driverRanges(i).idcard)
            if (queryRes == null) {
              logError(s"根据从业资格证号${driverRanges(i).idcard}无法获取驾驶员id")
              trip.addDriverInfo(null, driverRanges(i).driverName)
            }
            else {
              trip.addDriverInfo(queryRes._1, driverRanges(i).driverName)
            }
          }
        }

      case _ =>
        logError(s"车辆$vid 没有插卡记录，从数据库SSI_DRIVER_CAR表中查询关联")
    }

    // 权宜之计：直接从关联表里面去取一个
    basicTripDetailIter.foreach { trip =>
      if (trip.driverIds.isEmpty) {
        val driverInfos = OutputRdbms.getDriverInfo(conn, trip)
        if (driverInfos.nonEmpty) {
          driverInfos.foreach{ driver =>
            trip.addDriverInfo(driver.driverId, driver.driverName)
          }
        }
      }
    }

//    basicTripDetailIter.foreach { trip =>
//      if (trip.driverIds.isEmpty) {
//        val driverInfos = OutputRdbms.getDriverInfo(conn, trip)
//        if (driverInfos.nonEmpty) {
//          trip.addDriverInfo(driverInfos.head.driverId, driverInfos.head.driverName)
//        }
//      }
//    }
  }

  private def processOneByOne(newTripRdd: RDD[Row],
                              tripDetailRdd: RDD[TripDetailBean],
                              paramsMap: Map[String, String], //这到底是啥样的参数？
                              hiveContext: HiveContext): Unit = {
    logError("step 2.2&2.3 --------- 行程分析")
    /** get unique vehicle ids */
    val vidSet = newTripRdd.map(_.getAs[String](GP_FIELD_VID)).distinct().collect()

    /** get related DB connection properties */
    val motorcadeDbProps: Properties = DbPropsParser.getMySqlMotocadeDbProps(paramsMap)

    /** process each trip */
    // init db pool
    DbManager.initDataSource(motorcadeDbProps)

    vidSet.foreach { vid =>
      logError("************车辆标识为： " + vid)
      val tripRecords = tripDetailRdd.filter(_.vehicleId == vid).collect().sortBy(_.tripStartTime)

      val singleTripProcess = new SingleTripProcess(vid, paramsMap)
      val tripDescriptor = new DriveBehaviorDescriptor(vid, paramsMap)

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
        val terminal_0705_canRdd = hiveContext
          .sql(s"SELECT receive_time,signal_name,value FROM prod_analysis.$terminal_0705_tableName WHERE vid='$vid' " +
            s" AND part_yearmonthday>=$tripStartDate AND part_yearmonthday<=$tripEndDate" +
            s" AND receive_time IS NOT NULL AND receive_time>=${trip.tripStartTime} AND receive_time<=${trip.tripEndTime}")
            .coalesce(20)
          .rdd.persist(StorageLevel.MEMORY_AND_DISK)

        val terminal_0200_trajectoryRdd = hiveContext
          .sql(s"SELECT alarms,dir,gps_time,mile,speed,speed1,vehicle_status,signal_states" +
            s" FROM prod_analysis.$terminal_0200_tableName WHERE vid='$vid' AND gps_time IS NOT NULL" +
            s" AND part_yearmonthday>=$tripStartDate AND part_yearmonthday<=$tripEndDate" +
            s" AND gps_time>=${trip.tripStartTime} AND gps_time<=${trip.tripEndTime}")
          .coalesce(20)
          .rdd.persist(StorageLevel.MEMORY_AND_DISK)

        logError("************提取这一段行程中的事件与指标")
        val tripIndicatorProcessRes = singleTripProcess
          .tripProcess(trip.driverIds.mkString(","), trip.tripId, trip.tripStartTime, trip.totalMile,
          terminal_0200_trajectoryRdd, terminal_0705_canRdd)

        val motorcadeDbConn = DbManager.apply.getSingleConn(motorcadeDbProps)
//          .getConnection(motorcadeDbProps)
        saveTripProcessResult(motorcadeDbConn, singleTripProcess)

        /** 提取这段行程的各种分布情况 */
        if (tripIndicatorProcessRes) {
          logError("************提取这段行程的各种分布情况")
          val desriptionRes = tripDescriptor
            .tripProcess(trip.tripId, trip.tripEndTime,terminal_0200_trajectoryRdd, terminal_0705_canRdd, motorcadeDbConn)

          saveTripDescriptResult(motorcadeDbConn, desriptionRes)
        }
        else {
          logError(s"XXXXXXXX行程${trip.tripId} 处理结果为空")
        }

        motorcadeDbConn.close()
        /**********************************************************************/
        // debug
        logDebug("行程id为： " + trip.tripId + "耗时："+ (System.currentTimeMillis()-startRunTime)/1000)
        /**********************************************************************/

        terminal_0705_canRdd.unpersist()
        terminal_0200_trajectoryRdd.unpersist()
      }
    }
  }


  private def saveTripProcessResult(motorcadeDbConn: Connection, singleTripProcess: SingleTripProcess): Unit = {
    logError("保存这一段行程中的事件与指标")
    OutputRdbms.saveEvent(motorcadeDbConn, singleTripProcess.tripEventsBean)

    if(singleTripProcess.hasNonEmptyIndicators) {
      OutputRdbms.saveTripIndicators(motorcadeDbConn, singleTripProcess.tripIndicatorsBean)
    }
    else logError("trip id 为空")

    singleTripProcess.resetBeans()
  }

  private def saveTripDescriptResult(motorcadeDbConn: Connection,
                                     res: (ENGINE_RPM, ThrottleOpening, CustomOfUseClutch, DriveStyle)): Unit = {
    val (enginerpm, opening, clutchData, driveStyle) = res

    logError("保存这段行程的各种分布情况")
    OutputRdbms.saveRpmDistribution(motorcadeDbConn, enginerpm)
    OutputRdbms.saveThrottleOpeingDistribution(motorcadeDbConn, opening)
    OutputRdbms.saveDistributionClutch(motorcadeDbConn, clutchData)
    OutputRdbms.saveDriveStyle(motorcadeDbConn, driveStyle)
  }

  private def saveTripDetails(paramsMap: Map[String, String], tripDetailRdd: RDD[TripDetailBean]): Unit = {
    logError("step 3 --------保存新增trip_detail表内容")
    val motorcadeDbProps: Properties = DbPropsParser.getMySqlMotocadeDbProps(paramsMap)

    tripDetailRdd.foreachPartition{ part =>
        val motorcadeDbConn = DbManager.apply.getSingleConn(motorcadeDbProps)
        part.toArray.foreach { trip =>
          OutputRdbms.saveTripDetail(motorcadeDbConn, trip)
        }
        motorcadeDbConn.close()
      }
  }

  private def markProcessedTrips(paramsMap: Map[String, String], tripDetails: RDD[TripDetailBean]): Unit = {
    logError("step 4---------- 标记已处理的行程")
    val analysisDbProps = DbPropsParser.getGreenplumAnalysisDbProps(paramsMap)
    tripDetails.foreachPartition { part =>
      val analysisConn = DbManager.apply.getSingleConn(analysisDbProps)
      part.toArray.foreach { trip =>
          val gpVehicleTripTableName = paramsMap.getOrElse("vehicleTripName", "vehicle_trip")
          val updateSql = s"UPDATE $gpVehicleTripTableName SET isprocessed=1, processtime='${System.currentTimeMillis()}' " +
            s"WHERE id ='${trip.tripId}'"
          OutputRdbms.updateTripProcessState(analysisConn, updateSql)
      }
      analysisConn.close()
    }
  }

  @Deprecated
  private def markProcessedTrips(paramsMap: Map[String, String], tripRecords: Array[TripDetailBean]): Unit = {

    val analysisDbProps = DbPropsParser.getGreenplumAnalysisDbProps(paramsMap)
    DbManager.shutDownDataSource()
    DbManager.initDataSource(analysisDbProps)

    tripRecords.foreach{ trip =>
      val analysisConn = DbManager.getConnection(analysisDbProps)
      val gpVehicleTripTableName = paramsMap.getOrElse("vehicleTripName", "vehicle_trip")
      val updateSql = s"UPDATE $gpVehicleTripTableName SET isprocessed=1, processtime='${System.currentTimeMillis()}' " +
        s"WHERE id ='${trip.tripId}'"
      OutputRdbms.updateTripProcessState(analysisConn, updateSql)
      analysisConn.close()
    }

    DbManager.shutDownDataSource()
  }

  private def toSimpleTripDetailDF(tripDetailRdd: RDD[TripDetailBean], hiveContext: HiveContext): DataFrame = {
    logError("step 5 -----output")
    import hiveContext.implicits._
    tripDetailRdd.map{ trip =>
      SimpleTripDetail(trip.vehicleId, trip.fleetId, trip.tripEndTime)
    }.distinct()
      .toDF()
  }

}


object PreprocessTrips {

  val terminal_0200_tableName = "terminal_0200"

  val terminal_0705_tableName = "terminal_0705"
//  val terminal_0705_tableName = "terminal_0705_inner"

  val GP_FIELD_VID = "vid"

  case class SimpleTripDetail(vehicle_id: String, fleet_id: String, trip_end_time: Long)

  val processType: String = ProcessFactory.PROCESS_NAME_PREPROCESS_TRIPS


  final val DAY_DATE_FORMAT = "yyyyMMdd"

  def convertTimeStamp2DateStr(timestamp: Long, pattern: String): String = {
    val format = new SimpleDateFormat(pattern)

    new DateTime(timestamp).toString(pattern)
  }

}


