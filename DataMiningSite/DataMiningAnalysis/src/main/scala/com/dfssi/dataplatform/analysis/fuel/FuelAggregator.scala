package com.dfssi.dataplatform.analysis.fuel

import com.dfssi.common.math.Maths
import org.apache.spark.Logging
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.{SQLContext, SaveMode}
import org.apache.spark.streaming.dstream.DStream

import scala.collection.mutable.ArrayBuffer

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/1/22 9:32 
  */
class FuelAggregator extends Serializable with Logging{

  def fuelAggregateAndWrite2(route: DStream[(String, List[FuelRecord])],
                            bcConf: Broadcast[FuelConfig]): Unit ={

    val fuelDs = route.flatMap(kv => {
      //按行程分组
      val groupedRecords = kv._2
        .filter(_.getTripid != null)
        .groupBy(_.getTripid)
        //.filter(_._2.size > 1)

      //按行程聚合油耗数据
      groupedRecords.map(groupedKv =>{
        val tripRecords = groupedKv._2
        val firstRecord = tripRecords.head
        val lastRecord = tripRecords.last
        val fuel = newFuel(lastRecord)

        fuel.interval = tripRecords.map(_.getInterval).sum
        fuel.fuel = Maths.precision(tripRecords.map(_.getFuelGap).sum, 5)
        fuel.mile = Maths.precision(tripRecords.map(_.getMileGap).sum, 1)

        fuel.lon = tripRecords.map(_.getLon).mkString(",")
        fuel.lat = tripRecords.map(_.getLat).mkString(",")
        fuel.starttime = firstRecord.getUploadtime
        fuel.rjsk = System.currentTimeMillis()

        fuel
      })
    })

    writeFuelDataToGP(fuelDs, bcConf)
  }

  def fuelAggregateAndWrite(route: DStream[(String, List[FuelRecord])],
                            bcConf: Broadcast[FuelConfig]): Unit ={

    val fuelDs = route.flatMap(kv =>{
      val fuels = new ArrayBuffer[Fuel]()
      var fuel:Fuel = null
      kv._2.filter(_.getTripid != null).foreach(record =>{
        if(fuel == null){
          fuel = newFuel(record)
          fuels += fuel
        }else{
          if(record.getTripid.equals(fuel.tripId)){
            var m = record.getTotalmile - fuel.totalmile
            var f = record.getTotalfuel - fuel.totalfuel
            val fuelSpeed = f * 100.0/ (m + 1)
            if(m >= 0 && f >= 0) {
              if (fuelSpeed < 100) {
                fuel.mile += m
                fuel.totalmile = record.getTotalmile

                fuel.fuel += f
                fuel.totalfuel = record.getTotalfuel

                fuel.lon = s"${fuel.lon},${record.getLon}"
                fuel.lat = s"${fuel.lat},${record.getLat}"
                fuel.alt = s"${fuel.alt},${record.getAlt}"
                fuel.id = s"${fuel.id},${record.getId}"
                fuel.uploadTime = s"${fuel.uploadTime},${record.getUploadtime}"

                fuel.endtime = record.getUploadtime
                fuel.interval = fuel.endtime - fuel.starttime
                fuel.isplateau = record.getIsplateau
              } else {
                fuel = newFuel(record)
                fuels += fuel
              }
            }else {
              logError(s"数据异常：vid = ${record.getVid}, (${fuel.totalfuel}, ${fuel.totalmile}), (${record.getTotalfuel},${record.getTotalmile})")
              if(fuelSpeed < -100){
                fuel = newFuel(record)
                fuels += fuel
              }
            }
          }else{
            fuel = newFuel(record)
            fuels += fuel
          }
        }
      })
      //过滤掉只有单一点的数据
      fuels.filter(_.interval > 0)
        .map(f => {
          f.mile = Maths.precision(f.mile, 1)
          f.fuel = Maths.precision(f.fuel, 5)
          f.rjsk = System.currentTimeMillis()
          f
        })
    })
    writeFuelDataToGP(fuelDs, bcConf)
  }

  private def writeFuelDataToGP(fuelDataDs: DStream[Fuel],
                                bcConf: Broadcast[FuelConfig]): Unit ={
    fuelDataDs.foreachRDD(fuelRDD =>{
      val sqlContext = SQLContext.getOrCreate(fuelRDD.sparkContext)
      import sqlContext.implicits._
      val fuelDF = fuelRDD.toDF
      val conf = bcConf.value

      val connectionProperties = new java.util.Properties()
      connectionProperties.put("driver", conf.getFuelDriver)
      fuelDF.write.mode(SaveMode.Append).jdbc(conf.getUrl, conf.getFueltable,  connectionProperties)

      fuelRDD.unpersist()
    })
  }

  private def newFuel(record:FuelRecord): Fuel ={
    Fuel(record.getId,
      record.getTripid,
      record.getSim,
      record.getVid,
      record.getLon.toString,
      record.getLat.toString,
      record.getTotalmile,
      0.0,
      record.getIsplateau,
      record.getAlt.toString,
      record.getTotalfuel,
      0.0,
      0,
      record.getRouteid,
      record.getUploadtime.toString,
      record.getUploadtime,
      record.getUploadtime,
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

