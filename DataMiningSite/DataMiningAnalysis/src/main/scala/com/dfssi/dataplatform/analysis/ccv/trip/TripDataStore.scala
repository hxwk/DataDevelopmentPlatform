package com.dfssi.dataplatform.analysis.ccv.trip

import java.sql.Connection

import com.dfssi.common.databases.DBCommon
import com.dfssi.dataplatform.analysis.redis.ByteBufferRedis
import com.dfssi.dataplatform.analysis.util.{MathUtil, PartitionUtil}

/**
  * Description:
  *   行程数据 存储 读取
  *
  * @author LiXiaoCong
  * @version 2018/5/15 13:12 
  */
class TripDataStore(val table: String,
                    val conn: Connection,
                    val byteBufferRedis: ByteBufferRedis,
                    val redisPartitions: Int) {

    def queryTripByVid(vid: String): TripDataRecord ={

        //从redis中查询
        var tripDataRecord = queryTripByVidInRedis(vid)
        if(tripDataRecord == null){
            //从数据库中查询
            tripDataRecord = queryTripByVidInDB(vid)
        }

        //是否为已结束的有效行程
        if (!tripDataRecord.empty
                && tripDataRecord.isover == 1
                && tripDataRecord.isvalid == 1){

            tripDataRecord = TripDataRecord(null, null, null)
            tripDataRecord.empty = true
        }

        tripDataRecord
    }

    def insertTripDataToRedis(tripDataRecord: TripDataRecord): Unit ={
        val key = getRedisKey(tripDataRecord.vid)
        byteBufferRedis.set(key, tripDataRecord.encode())
    }

    def insertTripDataToDB(tripDataRecord: TripDataRecord): Unit ={
        val sql = s"insert into $table (isover, sim, starttime, endtime, interval, starttotalmile, endtotalmile, starttotalfuel, endtotalfuel, startlat, startlon, endlat, endlon, isvalid, id, iseco, vid, totalmile, totalfuel) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
        val preparedStatement = conn.prepareStatement(sql)

        preparedStatement.setInt(1, tripDataRecord.isover)
        preparedStatement.setString(2, tripDataRecord.sim)
        preparedStatement.setLong(3, tripDataRecord.starttime)
        preparedStatement.setLong(4, tripDataRecord.endtime)
        preparedStatement.setLong(5, tripDataRecord.interval)
        preparedStatement.setDouble(6, MathUtil.rounding(tripDataRecord.starttotalmile, 1))
        preparedStatement.setDouble(7, MathUtil.rounding(tripDataRecord.endtotalmile, 1))
        preparedStatement.setDouble(8, MathUtil.rounding(tripDataRecord.starttotalfuel, 5))
        preparedStatement.setDouble(9, MathUtil.rounding(tripDataRecord.endtotalfuel, 5))
        preparedStatement.setDouble(10, tripDataRecord.startlat)
        preparedStatement.setDouble(11, tripDataRecord.startlon)
        preparedStatement.setDouble(12, tripDataRecord.endlat)
        preparedStatement.setDouble(13, tripDataRecord.endlon)
        preparedStatement.setInt(14, tripDataRecord.isvalid)
        preparedStatement.setString(15, tripDataRecord.id)
        preparedStatement.setInt(16, tripDataRecord.iseco)
        preparedStatement.setString(17, tripDataRecord.vid)
        preparedStatement.setDouble(18, MathUtil.rounding(tripDataRecord.totalmile, 1))
        preparedStatement.setDouble(19, MathUtil.rounding(tripDataRecord.totalfuel, 5))

        preparedStatement.executeUpdate
        DBCommon.close(preparedStatement)
    }

    def updateTripDataToDB(trip: TripDataRecord): Unit = {
        val sql = s"update ${table} set isover=?, endtime=?, interval=?, endtotalmile=?,  endtotalfuel=?, endlat=?, endlon=?, isvalid=?, totalmile=?, totalfuel=? where id=? "
        val preparedStatement = conn.prepareStatement(sql)
        preparedStatement.setInt(1, trip.isover)
        preparedStatement.setLong(2, trip.endtime)
        preparedStatement.setLong(3, trip.getInterval)
        preparedStatement.setDouble(4, MathUtil.rounding(trip.endtotalmile, 1))
        preparedStatement.setDouble(5, MathUtil.rounding(trip.endtotalfuel, 5))
        preparedStatement.setDouble(6, trip.endlat)
        preparedStatement.setDouble(7, trip.endlon)
        preparedStatement.setInt(8, trip.isvalid)
        preparedStatement.setDouble(9, MathUtil.rounding(trip.totalmile, 1))
        preparedStatement.setDouble(10, MathUtil.rounding(trip.totalfuel, 5))
        preparedStatement.setString(11, trip.id)
        preparedStatement.executeUpdate
        DBCommon.close(preparedStatement)
    }

    private def queryTripByVidInDB(vid: String): TripDataRecord ={
        val sql = String.format("select * from %s where vid = '%s' order by endtime desc limit 1", table, vid)
        val statement = conn.createStatement
        val resultSet = statement.executeQuery(sql)

        var tripDataRecord: TripDataRecord = null
        if(resultSet.next()){
            tripDataRecord = TripDataRecord(resultSet.getString("id"),
                resultSet.getString("vid"),
                resultSet.getString("sim"))

            tripDataRecord.starttime = resultSet.getLong("starttime")
            tripDataRecord.endtime = resultSet.getLong("endtime")
            tripDataRecord.interval = resultSet.getLong("interval")

            tripDataRecord.totalmile = resultSet.getDouble("totalmile")
            tripDataRecord.totalfuel = resultSet.getDouble("totalfuel")

            tripDataRecord.starttotalmile = resultSet.getDouble("starttotalmile")
            tripDataRecord.endtotalmile = resultSet.getDouble("endtotalmile")

            tripDataRecord.starttotalfuel = resultSet.getDouble("starttotalfuel")
            tripDataRecord.endtotalfuel = resultSet.getDouble("endtotalfuel")

            tripDataRecord.startlat = resultSet.getDouble("startlat")
            tripDataRecord.endlat = resultSet.getDouble("endlat")

            tripDataRecord.startlon = resultSet.getDouble("startlon")
            tripDataRecord.endlon = resultSet.getDouble("endlon")

            tripDataRecord.iseco = resultSet.getInt("iseco")
            tripDataRecord.isover = resultSet.getInt("isover")
            tripDataRecord.isvalid = resultSet.getInt("isvalid")
        }else{
            tripDataRecord = TripDataRecord(null, null, null)
            tripDataRecord.empty = true
        }

        DBCommon.close(resultSet)
        DBCommon.close(statement)

        tripDataRecord
    }

    private def queryTripByVidInRedis(vid: String): TripDataRecord ={
        val buffer = byteBufferRedis.get(getRedisKey(vid))
        TripDataRecord.decode(buffer)
    }

    private def getRedisKey(vid: String): String = s"ccv:trip:${PartitionUtil.getPartition(vid, redisPartitions)}:$vid"

}
