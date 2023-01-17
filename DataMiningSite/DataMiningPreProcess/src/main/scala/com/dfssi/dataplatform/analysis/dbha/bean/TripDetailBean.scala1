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

  // Test
//  def randomSetDriverInfo: Unit = {
//    val idx = (math.random * TripDetailBean.driverIds.length).toInt
//    this._driverId = TripDetailBean.driverIds(idx)
//    this._driverName = TripDetailBean.driverNames(idx)
//  }
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

  // Test drivers
  val driverIds = Array(
    "04586a451ff5436fbdec27434ab02936",
    "0b3dbb857f9e4a0e8f61a61d8dd5335e",
    "1256e1e9a8cd4959827cf733bc909304",
    "26c0f6e4d9b74d67a4790feeef7654a1",
    "36c90e802c2f453880cdf7da095498c6",
    "3889ac0a4f474a50aac32759c59b327e",
    "5fb6953e30ab42ee9ca8b88809f65c6d",
    "61767a4ad7964331aaf94b10d1a6f2de",
    "72318cfbcc834e21b67d72e78e4e7d53",
    "9a8efbde2cae4cefb514b656f0ebc21b",
    "a16350ecc4ab4f0ab58aecbdb4c0f11d",
    "b53d157cbd5340bbb76110fa324f21de",
    "bd4cef4db25d4ed6b0047a995087b4c8",
    "d3e05fd211fb4324bc1bca46520f8933",
    "da58cabbaba34e10b2f09ea113c93a30"
  )

  val driverNames = Array("0000",
    "44444",
    "11111111",
    "fuck001",
    "11111111",
    "yyyuuu",
    "谢谢谢",
    "test001",
    "xiongdh",
    "8888888",
    "dhxiong",
    "xxxiii",
    "hfdps",
    "adfa",
    "888888")
}