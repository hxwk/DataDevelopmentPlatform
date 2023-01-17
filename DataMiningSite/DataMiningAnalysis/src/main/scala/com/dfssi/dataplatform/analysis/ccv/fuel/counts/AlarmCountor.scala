package com.dfssi.dataplatform.analysis.ccv.fuel.counts

import java.sql.Connection
import java.util
import java.util.UUID

import com.dfssi.common.databases.DBCommon
import com.dfssi.dataplatform.analysis.ccv.fuel.VehicleAlarmRecord

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/15 17:05 
  */
class AlarmCountor(val table: String,
                   val vid: String) extends Serializable {

    private val records = new util.HashMap[String, AlarmRecord]()

    def updateDrivingAlarm(alarms: mutable.HashSet[VehicleAlarmRecord],
                           gpsTime: Long,
                           lon: Double,
                           lat: Double,
                           speed: Double,
                           connection: Connection): Unit ={

        if(alarms != null && alarms.nonEmpty){
            alarms.foreach(alarm =>{
                var alarmRecord = records.get(alarm.getName())
                if(alarmRecord == null) {
                    alarmRecord = newAlarmRecord(alarm, gpsTime, lon, lat, speed)
                    records.put(alarm.getName(), alarmRecord)
                    insertDrivingAlarmToDB(alarmRecord, connection)
                }else{
                    val timeGap = gpsTime - alarmRecord.gpsTime
                    if(timeGap > 0 && timeGap <= 5 * 60 * 1000L){
                        alarmRecord.gpsTime = gpsTime
                        alarmRecord.lon = lon
                        alarmRecord.lat = lat
                        alarmRecord.speed = speed
                        alarmRecord.count += alarm.count

                        alarmRecord.changed = true
                    }else{
                        updateDrivingAlarmToDB(alarmRecord, connection)
                        alarmRecord = newAlarmRecord(alarm, gpsTime, lon, lat, speed)
                        records.put(alarm.getName(), alarmRecord)
                        insertDrivingAlarmToDB(alarmRecord, connection)
                    }
                }
            })
        }
    }

    def flush(connection: Connection): Unit ={
       records.asScala.foreach(kv =>{
           if(kv._2.changed){
               updateDrivingAlarmToDB(kv._2, connection)
               kv._2.changed = false
           }
       })
    }


    private def insertDrivingAlarmToDB(alarmRecord:AlarmRecord,
                                       connection: Connection): Unit = {

        val sql = s"insert into $table (id, vid, starttime, endtime, lon, lat, speed, count, alarm, degree, alarmlabel, alarmtype, alarmcode) values (?,?,?,?,?,?,?,?,?,?,?,?,?) "
        val preparedStatement = connection.prepareStatement(sql)
        preparedStatement.setString(1, alarmRecord.id)
        preparedStatement.setString(2, alarmRecord.vid)
        preparedStatement.setLong(3, alarmRecord.gpsTime)
        preparedStatement.setLong(4, alarmRecord.gpsTime)
        preparedStatement.setDouble(5, alarmRecord.lon)
        preparedStatement.setDouble(6, alarmRecord.lat)
        preparedStatement.setDouble(7, alarmRecord.speed)
        preparedStatement.setInt(8, alarmRecord.count)
        preparedStatement.setString(9, alarmRecord.alarm)
        preparedStatement.setInt(10, alarmRecord.degree)
        preparedStatement.setInt(11, alarmRecord.alarmlabel)
        preparedStatement.setInt(12, alarmRecord.alarmtype)
        preparedStatement.setInt(13, alarmRecord.code)
        preparedStatement.executeUpdate()

        DBCommon.close(preparedStatement)
    }

    private def updateDrivingAlarmToDB(alarmRecord: AlarmRecord,
                                       connection: Connection): Unit = {
        val sql = s"update $table set endtime=?, lon=?, lat=?, count=?, speed=? where id=? "
        val preparedStatement = connection.prepareStatement(sql)
        preparedStatement.setLong(1, alarmRecord.gpsTime)
        preparedStatement.setDouble(2, alarmRecord.lon)
        preparedStatement.setDouble(3, alarmRecord.lat)
        preparedStatement.setInt(4, alarmRecord.count)
        preparedStatement.setDouble(5, alarmRecord.speed)
        preparedStatement.setString(6, alarmRecord.id)
        preparedStatement.executeUpdate()

        DBCommon.close(preparedStatement)
    }

    private def newAlarmRecord(alarm: VehicleAlarmRecord,
                               gpsTime: Long,
                               lon: Double,
                               lat: Double,
                               speed: Double): AlarmRecord ={
        val drivingAlarmRecord = new AlarmRecord(UUID.randomUUID().toString,
            vid, alarm.alarmType, alarm.alarm, alarm.alarmLabel)
        drivingAlarmRecord.degree = alarm.alarmDegree
        drivingAlarmRecord.code = alarm.alarmCode
        drivingAlarmRecord.count = alarm.count
        drivingAlarmRecord.gpsTime = gpsTime
        drivingAlarmRecord.lon = lon
        drivingAlarmRecord.lat = lat
        drivingAlarmRecord.speed = speed

        drivingAlarmRecord
    }
}
