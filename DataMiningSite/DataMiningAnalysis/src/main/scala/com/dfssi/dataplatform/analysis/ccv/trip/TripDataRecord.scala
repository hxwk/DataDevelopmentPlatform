package com.dfssi.dataplatform.analysis.ccv.trip

import java.nio.ByteBuffer

import io.netty.buffer.Unpooled
import org.apache.spark.network.protocol.Encoders


/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/15 11:26 
  */
class TripDataRecord(val id: String,
                     val vid: String,
                     val sim: String) extends Serializable{

    var starttime = 0L
    var endtime = 0L
    var interval = 0L

    var totalmile = 0.0
    var totalfuel = 0.0

    var starttotalmile = 0.0
    var endtotalmile = 0.0
    var starttotalfuel = 0.0
    var endtotalfuel = 0.0

    var startlat = 0.0
    var endlat = 0.0
    var startlon = 0.0
    var endlon = 0.0

    var iseco = 0
    var isover = 0
    var isvalid = 1

    var empty = false

    var updateTime = System.currentTimeMillis()

    def getInterval: Long = endtime - starttime

    def getMileGap: Double = endtotalmile - starttotalmile

    override def toString = s"TripDataRecord($starttime, $endtime, $interval, $starttotalmile," +
            s" $endtotalmile, $starttotalfuel, $endtotalfuel, $startlat, $endlat, " +
            s"$startlon, $endlon, $iseco, $isover, $isvalid, $empty, $id, $vid, $sim)"

    private def encodedLength(): Int ={
        Encoders.Strings.encodedLength(id)
        + Encoders.Strings.encodedLength(vid)
        + Encoders.Strings.encodedLength(sim)
        + Encoders.Strings.encodedLength(starttime.toString)
        + Encoders.Strings.encodedLength(endtime.toString)
        + Encoders.Strings.encodedLength(interval.toString)
        + Encoders.Strings.encodedLength(totalmile.toString)
        + Encoders.Strings.encodedLength(totalfuel.toString)
        + Encoders.Strings.encodedLength(starttotalmile.toString)
        + Encoders.Strings.encodedLength(endtotalmile.toString)
        + Encoders.Strings.encodedLength(starttotalfuel.toString)
        + Encoders.Strings.encodedLength(endtotalfuel.toString)
        + Encoders.Strings.encodedLength(startlat.toString)
        + Encoders.Strings.encodedLength(endlat.toString)
        + Encoders.Strings.encodedLength(startlon.toString)
        + Encoders.Strings.encodedLength(endlon.toString)
        + Encoders.Strings.encodedLength(iseco.toString)
        + Encoders.Strings.encodedLength(isover.toString)
        + Encoders.Strings.encodedLength(isvalid.toString)
        + Encoders.Strings.encodedLength(empty.toString)
    }

    def encode(): ByteBuffer ={
        val buf = Unpooled.buffer(encodedLength)

        Encoders.Strings.encode(buf, id)
        Encoders.Strings.encode(buf, vid)
        Encoders.Strings.encode(buf, sim)
        Encoders.Strings.encode(buf, starttime.toString)
        Encoders.Strings.encode(buf, endtime.toString)
        Encoders.Strings.encode(buf, interval.toString)
        Encoders.Strings.encode(buf, totalmile.toString)
        Encoders.Strings.encode(buf, totalfuel.toString)
        Encoders.Strings.encode(buf, starttotalmile.toString)
        Encoders.Strings.encode(buf, endtotalmile.toString)
        Encoders.Strings.encode(buf, starttotalfuel.toString)
        Encoders.Strings.encode(buf, endtotalfuel.toString)
        Encoders.Strings.encode(buf, startlat.toString)
        Encoders.Strings.encode(buf, endlat.toString)
        Encoders.Strings.encode(buf, startlon.toString)
        Encoders.Strings.encode(buf, endlon.toString)
        Encoders.Strings.encode(buf, iseco.toString)
        Encoders.Strings.encode(buf, isover.toString)
        Encoders.Strings.encode(buf, isvalid.toString)
        Encoders.Strings.encode(buf, empty.toString)

        buf.nioBuffer
    }
}

object TripDataRecord{
    def apply(id: String, vid: String, sim: String): TripDataRecord =
        new TripDataRecord(id, vid, sim)

    def decode(byteBuffer: ByteBuffer): TripDataRecord ={
        if(byteBuffer == null) return null
        val buf = Unpooled.wrappedBuffer(byteBuffer)

        val id = Encoders.Strings.decode(buf)
        val vid = Encoders.Strings.decode(buf)
        val sim = Encoders.Strings.decode(buf)

        val record = new TripDataRecord(id, vid, sim)

        record.starttime = Encoders.Strings.decode(buf).toLong
        record.endtime = Encoders.Strings.decode(buf).toLong
        record.interval = Encoders.Strings.decode(buf).toLong
        record.totalmile = Encoders.Strings.decode(buf).toDouble
        record.totalfuel = Encoders.Strings.decode(buf).toDouble
        record.starttotalmile = Encoders.Strings.decode(buf).toDouble
        record.endtotalmile = Encoders.Strings.decode(buf).toDouble
        record.starttotalfuel = Encoders.Strings.decode(buf).toDouble
        record.endtotalfuel = Encoders.Strings.decode(buf).toDouble
        record.startlat = Encoders.Strings.decode(buf).toDouble
        record.endlat = Encoders.Strings.decode(buf).toDouble
        record.startlon = Encoders.Strings.decode(buf).toDouble
        record.endlon = Encoders.Strings.decode(buf).toDouble
        record.iseco = Encoders.Strings.decode(buf).toInt
        record.isover = Encoders.Strings.decode(buf).toInt
        record.isvalid = Encoders.Strings.decode(buf).toInt
        record.empty = Encoders.Strings.decode(buf).toBoolean

        record
    }
}
