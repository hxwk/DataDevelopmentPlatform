package com.dfssi.dataplatform.analysis.dbha.bean

import java.sql.Timestamp

import scala.collection.mutable.ArrayBuffer

class TripDetailBean(_vehicleId: String, _tripId: String,
                     _tripStartTime: Long,_tripEndTime: Long,
                     _startLat: Double, _startLon: Double,
                     _endLat: Double, _endLon: Double) extends Serializable {

  def vehicleId: String = _vehicleId

  def tripId: String = _tripId

  def tripStartTime: Long = _tripStartTime
  def tripEndTime: Long = _tripEndTime

  def startLat: Double = _startLat
  def startLon: Double = _startLon

  def endLat: Double = _endLat
  def endLon: Double = _endLon

  private[this] var _fleetId: String = _

  def fleetId: String = _fleetId

  def fleetId_=(value: String): Unit = {
    _fleetId = value
  }

  private[this] var _CYZGZH: String = ""
  def cyzgzhId: String = _CYZGZH
  def cyzgzhId_=(value: String): Unit = {
    _CYZGZH = value
  }

  private[this] val _driverIds: ArrayBuffer[String] = ArrayBuffer[String]()
  def driverIds: Array[String] = _driverIds.toArray
  def addDriverId(value: String): Unit = {
    _driverIds.append(value)
  }

  private[this] val _driverNames: ArrayBuffer[String] = ArrayBuffer[String]()
  def driverNames: Array[String] = _driverNames.toArray
  def addDriverName(value: String): Unit = {
    _driverNames.append(value)
  }

  def addDriverInfo(id: String, name: String): Unit = {
    addDriverId(id)
    addDriverName(name)
  }

  private[this] var _vehiclePlateNo: String = ""
  def vehiclePlateNo: String = _vehiclePlateNo
  def vehiclePlateNo_=(value: String): Unit = {
    _vehiclePlateNo = value
  }

  private[this] var _fromPlace: String = ""
  def fromPlace: String = _fromPlace
  def fromPlace_=(value: String): Unit = _fromPlace = value

  private[this] var _toPlace: String = ""
  def toPlace: String = _toPlace
  def toPlace_=(value: String): Unit = _toPlace = value

  private[this] var _totalMile: Double = _
  def totalMile: Double = _totalMile
  def totalMile_=(value: Double): Unit = _totalMile = value

  private[this] var _totalFuel: Double = _
  def totalFuel_=(value: Double): Unit = _totalFuel = value
  def totalFuel: Double = _totalFuel

  def getDriverString(driverIdx: Int): String = {
    s"'$tripId','$fleetId','$vehicleId','$vehiclePlateNo','${_driverIds(driverIdx)}','${_driverNames(driverIdx)}'" +
      s",'${new Timestamp(tripStartTime)}','${new Timestamp(tripEndTime)}'" +
      s",$startLat,$startLon,$endLat,$endLon,'$fromPlace','$toPlace',$totalMile,$totalFuel"
  }

  // only for tests
  //  private[this] var _driverId: String = ""
  //  def driverId: String = _driverId
  //  def driverId_=(value: String): Unit = {
  //    _driverId = value
  //  }
  //  // only for tests
  //  private[this] var _driverName: String = ""
  //  def driverName: String = _driverName
  //  def driverName_=(value: String): Unit = {
  //    _driverName = value
  //  }

  //  trip_id,fleet_id,vehicle_id,vehicle_plate_no,driver_id,driver_name,trip_start_time,trip_end_time," +
  //  "trip_start_lat,trip_start_lon,trip_end_lat,trip_end_lon,start,destination,distance,fuelConsumption" +
  //  ",score_safety,score_economy,score_maintenance,score_civilization,score_overall

  override def toString: String = s"'$tripId','$fleetId','$vehicleId','$vehiclePlateNo'" +
    s",'${driverIds.mkString(",")}','${driverNames.mkString(",")}'" +
    s",'${new Timestamp(tripStartTime)}','${new Timestamp(tripEndTime)}'" +
    s",$startLat,$startLon,$endLat,$endLon,'$fromPlace','$toPlace',$totalMile,$totalFuel"

}

object TripDetailBean {

  val TABLE_NAME = "dbha_trip_detail"

  val FIELD_TRIP_ID = "trip_id"
  val FIELD_FLEET_ID = "fleet_id"
  val FIELD_TRIP_END_TIME = "trip_end_time"
  val FIELD_VEHICLE_ID = "vehicle_id"
  val FIELD_DRIVER_ID = "driver_id"
  val FIELD_SCORE_SAFETY = "score_safety"
  val FIELD_SCORE_ECONOMY = "score_economy"
  val FIELD_SCORE_MAINTENANCE = "score_maintenance"
  val FIELD_SCORE_CIVILIZATION = "score_civilization"
  val FIELD_SCORE_OVERRALL = "score_overall"

  val FIELDS = Array(FIELD_FLEET_ID, FIELD_VEHICLE_ID, FIELD_DRIVER_ID, FIELD_TRIP_ID, FIELD_TRIP_END_TIME,
    FIELD_SCORE_SAFETY, FIELD_SCORE_ECONOMY, FIELD_SCORE_MAINTENANCE, FIELD_SCORE_CIVILIZATION, FIELD_SCORE_OVERRALL)

}