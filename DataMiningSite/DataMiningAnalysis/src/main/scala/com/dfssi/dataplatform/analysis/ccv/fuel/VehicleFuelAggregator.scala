package com.dfssi.dataplatform.analysis.ccv.fuel

import com.dfssi.common.math.Maths
import org.apache.spark.Logging
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{SQLContext, SaveMode}
import org.apache.spark.streaming.dstream.DStream

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/15 19:21 
  */
class VehicleFuelAggregator extends Serializable with Logging {

    def fuelAggregateAndWrite(route: DStream[(String, List[FuelDataRecord])],
                               bcConf: Broadcast[VehicleFuelConfig]): Unit ={

        val fuelDs = route.flatMap(kv => {
            //按行程分组
            val groupedRecords = kv._2
                    .filter(_.tripid != null)
                    .groupBy(_.tripid)
            //.filter(_._2.size > 1)

            //按行程聚合油耗数据
            groupedRecords.map(groupedKv =>{
                val tripRecords = groupedKv._2
                val firstRecord = tripRecords.head
                val lastRecord = tripRecords.last
                val fuel = newFuel(lastRecord)

                fuel.interval = tripRecords.map(_.timeGap).sum
                fuel.fuel = Maths.precision(tripRecords.map(_.fuelGap).sum, 5)
                fuel.mile = Maths.precision(tripRecords.map(_.mileGap).sum, 1)

                fuel.lon = tripRecords.map(_.lon).mkString(",")
                fuel.lat = tripRecords.map(_.lat).mkString(",")
                fuel.starttime = firstRecord.gpstime
                fuel.rjsk = System.currentTimeMillis()

                fuel
            })
        })

        writeFuelDataToGP(fuelDs, bcConf)
    }

    private def writeFuelDataToGP(fuelDataDs: DStream[Fuel],
                                  bcConf: Broadcast[VehicleFuelConfig]): Unit ={
        fuelDataDs.foreachRDD(fuelRDD =>{
            val sqlContext = SQLContext.getOrCreate(fuelRDD.sparkContext)
            import sqlContext.implicits._
            val fuelDF = fuelRDD.toDF
            val conf = bcConf.value

            val (url, connectionProperties) = conf.getConnectionProperties()
            fuelDF.write.mode(SaveMode.Append).jdbc(url, conf.fuelTable,  connectionProperties)

            fuelRDD.unpersist()
        })
    }

    private def newFuel(record:FuelDataRecord): Fuel ={
        Fuel(record.dataId,
            record.tripid,
            record.sim,
            record.vid,
            record.lon.toString,
            record.lat.toString,
            record.totalmile,
            0.0,
            record.isplateau,
            record.alt.toString,
            record.totalfuel,
            0.0,
            0,
            record.routeid,
            record.gpstime.toString,
            record.gpstime,
            record.gpstime,
            0L,
            0L)
    }

}

case class Fuel( var id: String, //对应的数据ID
                 var tripId: String,
                 var sim: String,  //汽车sim卡号
                 var vid: String,  //汽车唯一识别码号
                 var lon: String,  //经度
                 var lat: String,  //纬度
                 var totalmile: Double, //累计里程    单位：km
                 var mile: Double, //小计里程
                 var isplateau: Int, //是否为高原
                 var alt: String,     //高程    单位：m
                 var totalfuel: Double, //累计油耗
                 var fuel: Double,   //小计油耗    单位：L
                 var iseco: Int,   //是否为节能模式
                 var routeid: String, //线路ID
                 var uploadTime: String, //数据上传获取  精确到毫秒
                 var starttime: Long,
                 var endtime: Long,
                 var interval: Long, //聚合时间间隔
                 var rjsk: Long)


