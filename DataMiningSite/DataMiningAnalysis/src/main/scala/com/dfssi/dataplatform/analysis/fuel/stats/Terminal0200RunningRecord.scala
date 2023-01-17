package com.dfssi.dataplatform.analysis.fuel.stats

import com.dfssi.common.math.Maths
import org.apache.spark.Logging

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/10 8:55 
  */
class Terminal0200RunningRecord (val vid: String,
                                 val speed: Double,
                                 val mile: Double,
                                 val fuel: Double,
                                 val gpstime: Long) extends Comparable[Terminal0200RunningRecord]
        with Serializable with Logging{

    val item0705Map =  new mutable.HashMap[String, ListBuffer[Terminal0705RunningRecord]]()

    var rpm: Int = -1
    var acc: Int = -1
    var gear: Int = -1
    var handbrake: Int = -1
    var footbrake: Int = -1

    var province: String = null
    var city: String = null
    var district: String = null

    def add0705Item(name: String, record0705: Terminal0705RunningRecord): Unit ={
        val records = item0705Map.getOrElseUpdate(name, new ListBuffer[Terminal0705RunningRecord])
        records += record0705
    }

    //在于将 油门开度 ， 档位、发动机转速等信息 拼接到记录中
    //'453◎发动机转速_W', '664◎油门踏板开度_W', '1000025◎当前档位1_W', '1052◎手刹_W', '1053◎脚刹_W'
    def extract0705Item(): Terminal0200RunningRecord ={
        if(item0705Map.nonEmpty){
            item0705Map.foreach(kv =>{
                //取值最大的一个
                val last = kv._2.sortBy(_.value).last
                kv._1 match {
                    case "453◎发动机转速_W" =>    rpm = last.value.toInt
                    case "664◎油门踏板开度_W" =>  acc = last.value.toInt
                    case "1000025◎当前档位1_W" => gear = last.value.toInt
                    case "1052◎手刹_W" => handbrake = last.value.toInt
                    case "1053◎脚刹_W" => footbrake = last.value.toInt
                }
            })
            item0705Map.clear()
        }
        this
    }

    //计算连续两条数据 时间、里程、油耗间隔
    def calculateGap(o: Terminal0200RunningRecord): (Long, Double, Double) ={

        val timeGap = gpstime - o.gpstime
        val timeGapHour = timeGap * 1.0 / (1000 * 60 * 60)

        //简单避免异常数据
        var mileGap = mile - o.mile
        if(timeGap == 0)mileGap = 0.0
        var fuelGap = fuel - o.fuel

        var mileError: Boolean = false
        if(timeGap != 0 && (Math.abs(mileGap) * 1.0 / timeGapHour) > 400){
            logError(s"${vid}速度在时间范围[${gpstime}, ${o.gpstime}]" +
                    s"里的车速超过了 400 km/h, 忽略此距离。")
            mileError = true
        }

        var fuelError: Boolean = false
        if(timeGap != 0 && (Math.abs(fuelGap) * 1000.0 / timeGap) >= 0.1){
            logError(s"${vid}油耗速度在时间范围[${gpstime}, ${o.gpstime}]" +
                    s"里的油耗速度超过了 360L/h, 忽略此油耗。")
            fuelError = true
        }

        //同时错误的情况下 全部置零
        if(mileError && fuelError){
            mileGap = 0.0
            fuelGap = 0.0
        }

        (timeGap, Maths.precision(mileGap, 1), Maths.precision(fuelGap, 5))
    }

    override def compareTo(o: Terminal0200RunningRecord): Int = {
        var result = gpstime.compareTo(o.gpstime)
        if (result == 0) {
            result = mile.compareTo(o.mile)
            if (result == 0) result = fuel.compareTo(o.fuel)
        }
        result
    }

    override def toString = s"Terminal0200RunningRecord($vid, $speed, $mile, $fuel, $gpstime)"

    def canEqual(other: Any): Boolean = other.isInstanceOf[Terminal0200RunningRecord]

    override def equals(other: Any): Boolean = other match {
        case that: Terminal0200RunningRecord =>
            (that canEqual this) &&
                    vid == that.vid &&
                    speed == that.speed &&
                    mile == that.mile &&
                    fuel == that.fuel &&
                    gpstime == that.gpstime
        case _ => false
    }

    override def hashCode(): Int = {
        val state = Seq(vid, speed, mile, fuel, gpstime)
        state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
    }
}

object Terminal0200RunningRecord{

    def apply(vid: String,
              speed: Double,
              mile: Double,
              fuel: Double,
              gpstime: Long): Terminal0200RunningRecord = new Terminal0200RunningRecord(vid, speed, mile, fuel, gpstime)

    def main(args: Array[String]): Unit = {
       val list = List(new Terminal0200RunningRecord("11", 20.1, 123.3, 0.4, 12345),
             new Terminal0200RunningRecord("12", 20.1, 123.4, 0.5, 12346),
             new Terminal0200RunningRecord("13", 20.1, 123.3, 0.3, 12344),
             new Terminal0200RunningRecord("13", 20.1, 123.3, 0.3, 12344),
             new Terminal0200RunningRecord("14", 20.1, 123.4, 0.6, 12346))

        list.map(_.add0705Item("1", null))
       val set = mutable.HashSet(list:_*)
        set.foreach(k=> println(k.item0705Map))

        println(set)
    }
}
