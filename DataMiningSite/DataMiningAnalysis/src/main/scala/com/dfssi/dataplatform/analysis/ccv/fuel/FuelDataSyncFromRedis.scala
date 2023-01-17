package com.dfssi.dataplatform.analysis.ccv.fuel

import java.sql.{Connection, PreparedStatement}

import akka.actor.{Actor, ActorSystem, Cancellable, Props}
import com.dfssi.dataplatform.analysis.ccv.trip.TripDataRecord
import com.dfssi.dataplatform.analysis.fuel.FuelConfig
import com.dfssi.dataplatform.analysis.redis.ByteBufferRedis
import com.dfssi.dataplatform.analysis.util.MathUtil
import org.apache.spark.Logging

import scala.collection.mutable
import scala.concurrent.duration._

/**
  * Description:
  *   负责从redis中将数据同步到数据库中
  *
  * @author LiXiaoCong
  * @version 2018/5/15 20:22 
  */
class FuelDataSyncFromRedis extends Actor with Logging{
    override def receive: Receive = {
        case fuelConf: FuelConfig =>{
            sync(fuelConf)
        }
        case "stop" => sys.exit()
        case _ => ()
    }

    private def sync(fuelConf: FuelConfig): Unit ={
        val client = ByteBufferRedis(fuelConf.getRedisClient)
        val connection = fuelConf.getFuelConnection

        logInfo(s"开始同步行程数据..")
        syncTrip(client,
            connection,
            fuelConf.getTriptable,
            fuelConf.getRedisPartition)
        logInfo(s"同步行程数据完成。")

        logInfo(s"开始同步累计数据..")
        syncTotal(client,
            connection,
            fuelConf.getTotalfueltable,
            fuelConf.getRedisPartition)
        logInfo(s"同步累计数据完成。")

        client.close()
    }

    private def syncTrip(client: ByteBufferRedis,
                         connection: Connection,
                         table: String,
                         partitions: Int): Unit ={
        val sql = s"update ${table} set isover=?, endtime=?, interval=?, endtotalmile=?, endtotalfuel=?, startlat=?, endlon=?, isvalid=?, totalmile=?, totalfuel=? where id=? "
        val preparedStatement = connection.prepareStatement(sql)
        var keys: mutable.Set[Array[Byte]] = null
        for(i <- 0 to partitions){
            keys = client.keys(s"ccv:trip:${i}:*")
            if(keys.nonEmpty)
                syncTripByKeys(client, preparedStatement, table,  keys)
        }
        preparedStatement.close()

    }

    private def syncTripByKeys(client: ByteBufferRedis,
                               preparedStatement: PreparedStatement,
                               table: String,
                               keys: mutable.Set[Array[Byte]]): Unit ={
        keys.sliding(500, 500).foreach(keySet =>{
            keySet.foreach(key =>{
                val trip = TripDataRecord.decode(client.get(key))
                if(trip != null){
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

                    preparedStatement.addBatch()
                }
            })
            preparedStatement.executeBatch()
        })
    }

    private def syncTotal(client: ByteBufferRedis,
                          connection: Connection,
                          table: String,
                          partitions: Int): Unit ={
        val sql = s"update ${table} set endtime=?, totaltime=?, totalmile=?, totalfuel=? where vid=? "
        val preparedStatement = connection.prepareStatement(sql)
        var keys: mutable.Set[Array[Byte]] = null
        for(i <- 0 to partitions){
            keys = client.keys(s"ccv:total:${i}:*")
            if(keys.nonEmpty)
                syncTotalByKeys(client, preparedStatement, table,  keys)
        }
        preparedStatement.close()
    }

    private def syncTotalByKeys(client: ByteBufferRedis,
                               preparedStatement: PreparedStatement,
                               table: String,
                               keys: mutable.Set[Array[Byte]]): Unit ={
        keys.sliding(500, 500).foreach(keySet =>{
            keySet.foreach(key =>{
                val total = VehicleTotalFuelRecord.decode(client.get(key))
                if(total != null){
                    preparedStatement.setLong(1, total.endtime)
                    preparedStatement.setLong(2, total.totaltime)
                    preparedStatement.setDouble(3, MathUtil.rounding(total.totalmile, 1))
                    preparedStatement.setDouble(4, MathUtil.rounding(total.totalfuel, 5))
                    preparedStatement.setString(5, total.vid)

                    preparedStatement.addBatch()
                }
            })
            preparedStatement.executeBatch()
        })
    }

}

object FuelDataSyncFromRedis{

    val system = ActorSystem("SyncSystem")
    val fuelDataSyncAct = system.actorOf(Props[FuelDataSyncFromRedis],"fuelDataSyncAct")
    //implicit val time = Timeout(5 seconds)

    def startSync(fuelConf: FuelConfig): Cancellable ={
        import scala.concurrent.ExecutionContext.Implicits.global
        system.scheduler.schedule(10 minutes,
            10 minutes,
            fuelDataSyncAct, fuelConf)
    }
}
