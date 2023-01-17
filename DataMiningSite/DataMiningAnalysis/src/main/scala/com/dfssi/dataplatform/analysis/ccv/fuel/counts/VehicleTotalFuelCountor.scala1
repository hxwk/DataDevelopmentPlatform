package com.dfssi.dataplatform.analysis.ccv.fuel.counts

import java.sql.Connection

import com.dfssi.common.databases.DBCommon
import com.dfssi.dataplatform.analysis.ccv.fuel.VehicleTotalFuelRecord
import com.dfssi.dataplatform.analysis.redis.ByteBufferRedis
import com.dfssi.dataplatform.analysis.util.{MathUtil, PartitionUtil}

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/15 18:37 
  */
class VehicleTotalFuelCountor(val table: String,
                              val vid: String,
                              val redisPartitions: Int) extends Serializable {

    private var vehicleTotalFuelRecord: VehicleTotalFuelRecord = null

    private def init(redis: ByteBufferRedis, conn: Connection): Unit ={
        initByRedis(redis)
        if(vehicleTotalFuelRecord == null){
            initByDB(conn)
        }
    }

    private def initByRedis(redis: ByteBufferRedis): Unit ={
        val buffer = redis.get(getRedisKey)
        vehicleTotalFuelRecord = VehicleTotalFuelRecord.decode(buffer)
    }

    private def initByDB(conn: Connection): Unit ={

        val sql = s"select endtime, totalmile, totalfuel, totaltime from ${table} where vid = '${vid}'"

        val preparedStatement = conn.prepareStatement(sql)
        val resultSet = preparedStatement.executeQuery

        if (resultSet.next) {
            vehicleTotalFuelRecord = VehicleTotalFuelRecord(vid)
            vehicleTotalFuelRecord.endtime = resultSet.getLong("endtime")
            vehicleTotalFuelRecord.totalmile = resultSet.getDouble("totalmile")
            vehicleTotalFuelRecord.totalfuel = resultSet.getDouble("totalfuel")
            vehicleTotalFuelRecord.totaltime = resultSet.getLong("totaltime")
        }
        DBCommon.close(resultSet)
        DBCommon.close(preparedStatement)
    }

    private def insertDataToDB(gpsTime: Long,
                               totalmile: Double,
                               totalfuel: Double,
                               interval: Long,
                               conn: Connection): Unit ={
        val sql = s"insert into $table (vid, starttime, endtime, totalmile, totalfuel, totaltime) values (?,?,?,?,?,?) "
        val preparedStatement = conn.prepareStatement(sql)
        preparedStatement.setString(1, vid)

        preparedStatement.setLong(2, gpsTime)
        preparedStatement.setLong(3, gpsTime)

        preparedStatement.setDouble(4, MathUtil.rounding(totalmile, 1))
        preparedStatement.setDouble(5, MathUtil.rounding(totalfuel, 5))

        preparedStatement.setLong(6, interval)

        preparedStatement.executeUpdate
        DBCommon.close(preparedStatement)
    }

    def updateTotalFuel(gpsTime: Long,
                        totalmile: Double,
                        totalfuel: Double,
                        interval: Long,
                        redis: ByteBufferRedis,
                        conn: Connection): Unit ={

        if(vehicleTotalFuelRecord == null){
            init(redis, conn)
            if (vehicleTotalFuelRecord == null) {
                insertDataToDB(gpsTime, totalmile, totalfuel, interval, conn)
                vehicleTotalFuelRecord = new VehicleTotalFuelRecord(vid)
                vehicleTotalFuelRecord.totaltime = interval
                vehicleTotalFuelRecord.endtime = gpsTime
                vehicleTotalFuelRecord.totalfuel = totalfuel
                vehicleTotalFuelRecord.totalmile = totalmile
            }
            else updateTotalFuelDetail(gpsTime, totalmile, totalfuel, interval)
        }

    }

    private def updateTotalFuelDetail(gpsTime: Long,
                                      totalmile: Double,
                                      totalfuel: Double,
                                      interval: Long): Unit = {
        vehicleTotalFuelRecord.endtime = gpsTime
        vehicleTotalFuelRecord.totalmile = totalmile
        vehicleTotalFuelRecord.totalfuel = totalfuel
        vehicleTotalFuelRecord.totaltime += interval
    }

    def updateTotalFuelEndTime(gpsTime: Long,
                               interval: Long): Unit = {
        if (vehicleTotalFuelRecord != null) {
            vehicleTotalFuelRecord.endtime = gpsTime
            vehicleTotalFuelRecord.totaltime += interval
        }
    }


    def executeUpdate(redis: ByteBufferRedis): Unit = {
        if (vehicleTotalFuelRecord != null) {
            redis.set(getRedisKey, vehicleTotalFuelRecord.encode())
        }
    }

    private def getRedisKey: String = s"ccv:total:${PartitionUtil.getPartition(vid, redisPartitions)}:$vid"

}
