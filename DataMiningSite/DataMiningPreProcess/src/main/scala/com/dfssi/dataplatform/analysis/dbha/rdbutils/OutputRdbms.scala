package com.dfssi.dataplatform.analysis.dbha.rdbutils

import java.sql.{Connection, Timestamp}
import java.util.{Properties, UUID}

import com.dfssi.dataplatform.analysis.dbha.bean.IndicatorTable._
import com.dfssi.dataplatform.analysis.dbha.bean._
import com.dfssi.dataplatform.analysis.dbha.driverbehavior.clutch.CustomOfUseClutch
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.DriveStyleTable.DriveStyle
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.EngineRpmDistributionTable.ENGINE_RPM
import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.ThrottleOpeningDistributionTable.ThrottleOpening
import com.dfssi.dataplatform.analysis.dbha.testutils.RandomFleetUtil
import org.apache.spark.Logging
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.sql.{DataFrame, SaveMode}

import scala.collection.mutable



object OutputRdbms extends Serializable with Logging {

  /** 驾驶行为初始化过程所需的方法 */
  def saveDbhaIndicators(paramsMap: Map[String, String], indicatorSet: Set[IndicatorInfo]): Unit = {

    val fields = Array(FIELD_INDICATOR_ID, FIELD_INDICATOR_NAME, FIELD_INDICATOR_LEVEL, FIELD_PARENT_INDICATOR)
    val questionSymbols = fields.map(_ => "?").mkString(",")

    val defaultSql = s"INSERT INTO $TABLE_NAME(${fields.mkString(",")}) VALUES($questionSymbols)"

    val dbProps = DbPropsParser.getMySqlMotocadeDbProps(paramsMap)
    try {
      val conn: Connection = DbManager.getConnection(dbProps)
      val ps = conn.prepareStatement(defaultSql)
      indicatorSet.foreach{ case IndicatorInfo(v1, v2, v3, v4) =>
        ps.setInt(1, v1)
        ps.setString(2, v2)
        ps.setInt(3, v3)
        ps.setInt(4, v4)
        ps.addBatch()
      }
      ps.executeBatch()

      DbManager.closeCon(null, ps, conn)
    } catch {
      case exception: Exception =>
        // TODO replace println with logerror
        println("---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception.printStackTrace())
    }
  }

  def saveEvent(conn: Connection, eventBean: EventBean): Unit = {

    val insertEventSql: String =
      "insert into dbha_trip_events(id, trip_id, trip_end_time, event_name, event_value) values(?,?,?,?,?)"

    // 不存在则插入SQL语句
    val insertIfNotExist = "INSERT INTO dbha_trip_events(id, trip_id, trip_end_time, event_name, event_value) " +
    " SELECT FROM DUAL WHERE NOT EXISTS(SELECT * FROM dbha_trip_events WHERE id=?)"

//    INSERT INTO UM_CUSTOMER(customercode,CompanyFlag,InputTime,LocalVersion)
//    VALUES('201801010001','0','2017-02-22 12:00:00',1)
//    ON conflict(customercode)
//    DO UPDATE SET CompanyFlag = '1', InputTime ='2017-02-22 12:00:00',LocalVersion=1

    if (eventBean.events.isEmpty) return

    try {
      val ps = conn.prepareStatement(insertEventSql)
      for ((k, v) <- eventBean.events) {
        ps.setString(1, UUID.randomUUID().toString)
        ps.setString(2, eventBean.tripId)
        ps.setTimestamp(3, eventBean.tripEndTime)
        ps.setString(4, k)
        ps.setLong(5, v)

        ps.addBatch()
      }

      ps.executeBatch()
    } catch {
      case exception: Exception =>
        logError(
          "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
            .printStackTrace())
    }
  }

  // FIELD_ID,FIELD_VEHICLE_ID, FIELD_TRIP_ID, FIELD_TRIP_ENDTIME, FIELD_INDICATOR_ID, FIELD_INDICATOR_NAME, FIELD_INDICATOR_VALUE
  def saveTripIndicators(connection: Connection,
                         indicatorBean: IndicatorBean): Unit = {
    if (indicatorBean.indicators.isEmpty) return

    val insertIndicatorSql: String =
      s"insert into ${TripIndicatorsTable.TABLE_NAME}(${TripIndicatorsTable.TRIP_INDICATOR_FIELDS
        .mkString(",")}) " +
        s"values(${TripIndicatorsTable.TRIP_INDICATOR_FIELDS.map(_ => "?").mkString(",")})"

    try {
      val ps = connection.prepareStatement(insertIndicatorSql)
      for ((k, v) <- indicatorBean.indicators) {
        ps.setString(1, UUID.randomUUID().toString)
        ps.setString(2, indicatorBean.vehicleId)
        ps.setString(3, indicatorBean.driverId)
        ps.setString(4, indicatorBean.tripId)
        ps.setTimestamp(5, indicatorBean.tripEndTime)
        ps.setInt(6, k)
        ps.setString(7, IndicatorBean.EVENT_ID_TO_INDICATORS_MAP(k))
        ps.setFloat(8, v)
//        ps.setFloat(9, indicatorBean.getScore(k))
        ps.addBatch()
      }

      ps.executeBatch()
    } catch {
      case exception: Exception =>
        logError(
          "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
            .printStackTrace())
    }
  }

  /**
    * 保存新增的行程数据到行程明细表中，同时更新到驾驶员最新行程表
    * @param connection
    * @param tripDetailBean
    */
  def saveTripDetail(connection: Connection,
                     tripDetailBean: TripDetailBean): Unit = {

    val latestTripTableName = LatestTripDetailBean.latestTripDetailTableName

    try {
      val ps = connection.createStatement()

      if (tripDetailBean.driverIds.nonEmpty) {
        tripDetailBean.driverIds.indices.foreach{ idx =>
          val driverId = tripDetailBean.driverIds(idx)
          val driverName = tripDetailBean.driverNames(idx)

          // 更新dbha_latest_trip表
          val checkSql = s"SELECT trip_end_time FROM $latestTripTableName WHERE driver_id='$driverId'"
          val res = ps.executeQuery(checkSql)

          val insertOrUpdateSql = if(res!= null && res.next()) {
            val trip_end_time = res.getTimestamp("trip_end_time")

            if(tripDetailBean.tripEndTime > trip_end_time.getTime) {
              s"UPDATE $latestTripTableName SET id='${tripDetailBean.tripId}', trip_end_time='${new Timestamp(tripDetailBean.tripEndTime)}' " +
                s"WHERE driver_id = '$driverId'"
            } else ""
          }
          else {
            s"INSERT INTO $latestTripTableName(${LatestTripDetailBean.INSERT_FIELDS}) VALUES(" +
              s"'${tripDetailBean.tripId}','${tripDetailBean.fleetId}','$driverId','$driverName'" +
              s",'${new Timestamp(tripDetailBean.tripEndTime)}')"
          }
          if (insertOrUpdateSql.nonEmpty) ps.execute(insertOrUpdateSql)

          // 插入到dbha_trip_detail表
          val driverString = tripDetailBean.getDriverString(idx)
          val insertSql = s"INSERT INTO ${TripDetailBean.TABLE_NAME}" +
            "(trip_id,fleet_id,vehicle_id,vehicle_plate_no,driver_id,driver_name,trip_start_time,trip_end_time," +
            "trip_start_lat,trip_start_lon,trip_end_lat,trip_end_lon,start,destination,distance,fuelConsumption" +
            s") VALUES($driverString)"
          ps.execute(insertSql)
        }
      }
      else {
        /********************************** 以下为测试用代码 ********************************************
        val driverId = tripDetailBean.driverId
        val driverName = tripDetailBean.driverName

        // 更新dbha_latest_trip表
        val checkSql = s"SELECT trip_end_time FROM $latestTripTableName WHERE driver_id='$driverId'"
        val res = ps.executeQuery(checkSql)

        val insertOrUpdateSql = if(res.next()) {
          val trip_end_time = res.getTimestamp("trip_end_time")
          if(tripDetailBean.tripEndTime > trip_end_time.getTime) {
            s"UPDATE $latestTripTableName SET id='${tripDetailBean.tripId}', " +
              s"trip_end_time='${new Timestamp(tripDetailBean.tripEndTime)}' " +
              s"WHERE driver_id = '$driverId'"
          } else ""
        } else {
          s"INSERT INTO $latestTripTableName(${LatestTripDetailBean.INSERT_FIELDS}) VALUES(" +
            s"'${tripDetailBean.tripId}','${tripDetailBean.fleetId}','$driverId','$driverName'" +
            s",'${new Timestamp(tripDetailBean.tripEndTime)}')"
        }
        if (insertOrUpdateSql.nonEmpty) ps.execute(insertOrUpdateSql)
        ********************************* 以上为测试用代码 *********************************************/

        val insertSql = s"INSERT INTO ${TripDetailBean.TABLE_NAME}" +
          "(trip_id,fleet_id,vehicle_id,vehicle_plate_no,driver_id,driver_name,trip_start_time,trip_end_time," +
          "trip_start_lat,trip_start_lon,trip_end_lat,trip_end_lon,start,destination,distance,fuelConsumption" +
          s") VALUES(${tripDetailBean.toString()})"
        ps.execute(insertSql)
      }
    }
    catch {
      case exception: Exception =>
        logError(
          "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
            .printStackTrace())
    }

  }

  def saveRpmDistribution(conn: Connection,
                          enginerpm: ENGINE_RPM): Unit = {
    import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.EngineRpmDistributionTable._

    val insertSql = s"INSERT INTO $TABLE_NAME($FIELD_TRIP_ID, $FIELD_TRIP_ENDTIME,${FIELDS_RPM_LEVEL
      .mkString(",")},$FIELD_TOTAL_COUNT)" +
      s" VALUES(${enginerpm.toString})"

    try {
      val ps = conn.createStatement()
      ps.execute(insertSql)

    } catch {
      case exception: Exception =>
        logError(
          "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
            .printStackTrace())
    }
  }

  def saveThrottleOpeingDistribution( conn: Connection,
                                      opening: ThrottleOpening): Unit = {
    import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.ThrottleOpeningDistributionTable._

    val insertSql = s"INSERT INTO $TABLE_NAME($FIELD_TRIP_ID,$FIELD_TRIP_ENDTIME," +
      s"${FIELDS_OPENING_LEVEL.mkString(",")},$FIELD_TOTAL_COUNT) VALUES(${opening.toString})"

    try {
      val ps = conn.createStatement()

      ps.execute(insertSql)
    } catch {
      case exception: Exception =>
        logError(
          "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
            .printStackTrace())
    }
  }

  def saveDistributionClutch(conn: Connection,
                             customOfUseCluth: CustomOfUseClutch): Unit = {
    import com.dfssi.dataplatform.analysis.dbha.driverbehavior.clutch.CustomOfUseClutchTable._

    val insertSql = s"INSERT INTO $TABLE_NAME($FIELD_TRIP_ID, $FIELD_TRIP_ENDTIME, $FIELD_NUM_RUN_NULL_POS, " +
      s"$FIELD_NUM_NORMAL, $FIELD_NUM_AT_IDLE, $FIELD_NUM_STARTUP" +
      s") VALUES(${customOfUseCluth.toString})"

    executeSqlStatement(conn, insertSql)
  }

  def saveDriveStyle(connection: Connection,
                     style: DriveStyle): Unit = {
    import com.dfssi.dataplatform.analysis.dbha.driverdescriptor.DriveStyleTable._

    val insertSql = s"INSERT INTO $TABLE_NAME($FIELD_TRIP_ID,$FIELD_TRIP_ENDTIME,$FIELT_BRAKE_TIME, $FIELD_IDLE_TIME," +
      s"$FIELD_ECO_DRIVE,$FIELD_LOW_LOAD_LOW_RPM, $FIELD_HIGH_LOAD_HIGH_RPM, $FIELD_LOW_LOAD_HIGH_RPM, $FIELD_ABOCE_ECO_DRIVE," +
      s"$FIELD_SUPER_HIGH_RPM) VALUES(${style.toString})"

    executeSqlStatement(connection, insertSql)
  }

  def updateTripProcessState(connection: Connection,
                             updateSql: String): Unit = {
    try {
      val ps = connection.createStatement()
      val rs = ps.execute(updateSql)

    } catch {
      case exception: Exception =>
        logError(
          "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
            .printStackTrace())
    }
  }

  def executeSqlStatement(connection: Connection,
                          sql: String): Unit = {
    try {
      val ps = connection.createStatement()
      ps.execute(sql)
    } catch {
      case e: Exception =>
        logError(
          "---------Error in execution of query " + e.getMessage + "\n-----------------------\n" + e
            .printStackTrace())
    }
  }

  @DeveloperApi
  def getDriverInfo(conn: Connection,
                    tripDetailBean: TripDetailBean): Set[DriverInfo] = {

    val driverInfos = mutable.Set[DriverInfo]()

    /** 先基于vehicle_id从SSI_DRIVER_CAR中关联出驾驶员 */
    val driverTableName = "SSI_DRIVER" // 驾驶员id、姓名、所属车队id
    val driverCarTableName = "SSI_DRIVER_CAR" // 驾驶员id与车辆id绑定表

    // 根据SSI_DRIVER_CAR表中vehicleId与driverId关联，获取driver的id和姓名
    val vinSql = s"SELECT sd.ID, sd.NAME, sd.OGR_ID, sd.NUMBER FROM $driverTableName sd, $driverCarTableName sdc" +
      s" WHERE sdc.driver_id=sd.ID AND sdc.car_id='${tripDetailBean.vehicleId}' AND sd.OGR_ID='${tripDetailBean.fleetId}'"
    try {
      val stmt = conn.createStatement()
      val rs = stmt.executeQuery(vinSql)

      if(rs != null) {
        while (rs.next()) {
          driverInfos.add(
            DriverInfo(rs.getString("ID"), rs.getString("NAME"),rs.getString("OGR_ID"), rs.getString("NUMBER")))
        }
      }
    } catch {
      case exception: Exception =>
        logError( "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
            .printStackTrace())
    }
    // 如果查到了数据，就返回
//    if (driverInfos.nonEmpty) return
    driverInfos.toSet

    /** 然后基于fleet_id从SSI_DRIVER中随机关联出一个驾驶员
    val fleetSql = s"SELECT ID, NAME, OGR_ID, CSYE_CAR_NB FROM $driverTableName WHERE OGR_ID='${tripDetailBean.fleetId}'"
    try {
      val stmt = conn.createStatement()
      val rs = stmt.executeQuery(fleetSql)

      if(rs != null) {
        while (rs.next()) {
          driverInfos
            .add(DriverInfo(rs.getString("ID"), rs.getString("NAME"),rs.getString("OGR_ID"), rs.getString("CSYE_CAR_NB")))
        }
      }
    } catch {
      case exception: Exception =>
        logError( "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
          .printStackTrace())
    }

    if (driverInfos.size <= 1) driverInfos.toSet
    else {
      if (math.random < 0.5) Set(driverInfos.head)
      else Set(driverInfos.last)
    } */
  }


  def getVehiclePlateNo(conn: Connection, vid: String): (String,String) = {
    val vehicleTableName = "SSI_VEHICLE"      // 车辆id、车牌号

    val querySql = s"SELECT sv.ID, sv.LPN,sv.dealer_id FROM $vehicleTableName sv WHERE sv.ID='$vid'"

    try {
      val stmt = conn.createStatement()
      stmt.setMaxRows(1)

      val rs = stmt.executeQuery(querySql)

      if(rs != null && rs.next()) {
        return (rs.getString("LPN"), rs.getString("dealer_id"))
      }

    } catch {
      case exception: Exception =>
        logError( "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
            .printStackTrace())

    }

    logError(s"无法从SSI_VEHICLE表中获取$vid 的车队信息，随机分配")
    ("鄂ATEST", RandomFleetUtil.randomChoose)
  }

  def getDriverIdByPractionerId(conn: Connection, idcard: String): (String, String) = {
    val driverTableName = "SSI_DRIVER" // 驾驶员id、姓名、所属车队id

    val querySql = s"SELECT id, OGR_ID FROM $driverTableName WHERE NUMBER='idcard'"

    try {
      val stmt = conn.createStatement()

      val rs = stmt.executeQuery(querySql)

      if(rs != null && rs.next()) {
        return (rs.getString("ID"), rs.getString("OGR_ID"))
      }

    } catch {
      case exception: Exception =>
        logError(
          "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
            .printStackTrace())
    }
    null
  }


  def getTripDriverInfo(conn: Connection, tripId: String): (String, String, Timestamp) = {
    val querySql = s"select fleet_id,driver_id,trip_end_time FROM dbha_trip_detail WHERE trip_id='$tripId'"

    try {
      val stmt = conn.createStatement()

      val rs = stmt.executeQuery(querySql)

      if(rs != null && rs.next()) {
        return (rs.getString("fleet_id"), rs.getString("driver_id"),rs.getTimestamp("trip_end_time"))
      }
    } catch {
      case exception: Exception =>
        logError(
          "---------Error in execution of query " + exception.getMessage + "\n-----------------------\n" + exception
            .printStackTrace())
    }
    null
  }

  def saveDailySummaryScore(paramsMap: Map[String, String],
                            df: DataFrame,
                            mode: SaveMode) = {
    val properties = new Properties()

    val dbName = paramsMap("MotorcadeDB_jdbcDatabase")
    val jdbcHostname = paramsMap("MotorcadeDB_jdbcHostname")
    val jdbcPort = paramsMap("MotorcadeDB_jdbcPort")
    val url = s"jdbc:mysql://$jdbcHostname:$jdbcPort/$dbName?useSSL=false"

    val driverClassName = paramsMap("MotorcadeDB_driver")
    properties.put("driver", driverClassName)

    val username = paramsMap("MotorcadeDB_user")
    properties.put("user", username)

    val password = paramsMap("MotorcadeDB_password")
    properties.put("password", password)

    val dbtable = paramsMap("MotorcadeDB_dbtable")

    df.write.mode(mode).jdbc(url, dbtable, properties)
  }

}

/// CSYE_CAR_NB: 从业资格证号
case class DriverInfo(driverId: String, driverName: String, ogrId: String, CSYE_CAR_NB: String)