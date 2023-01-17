package com.dfssi.dataplatform.analysis.ccv.fuel

import java.nio.ByteBuffer

import io.netty.buffer.Unpooled
import org.apache.spark.network.protocol.Encoders

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/15 17:24 
  */
class VehicleTotalFuelRecord(val vid: String) extends Serializable {

    var endtime = 0L
    var totaltime = 0L
    var totalmile = 0.0
    var totalfuel = 0.0

    private def encodedLength(): Int ={
       Encoders.Strings.encodedLength(vid)
        + Encoders.Strings.encodedLength(endtime.toString)
        + Encoders.Strings.encodedLength(totaltime.toString)
        + Encoders.Strings.encodedLength(totalmile.toString)
        + Encoders.Strings.encodedLength(totalfuel.toString)
    }

    def encode(): ByteBuffer ={
        val buf = Unpooled.buffer(encodedLength)

        Encoders.Strings.encode(buf, vid)
        Encoders.Strings.encode(buf, endtime.toString)
        Encoders.Strings.encode(buf, totaltime.toString)
        Encoders.Strings.encode(buf, totalmile.toString)
        Encoders.Strings.encode(buf, totalfuel.toString)
        buf.nioBuffer
    }
}

object VehicleTotalFuelRecord{
    def apply(vid: String): VehicleTotalFuelRecord = new VehicleTotalFuelRecord(vid)

    def decode(byteBuffer: ByteBuffer): VehicleTotalFuelRecord ={
        if(byteBuffer == null) return null
        val buf = Unpooled.wrappedBuffer(byteBuffer)

        val vid = Encoders.Strings.decode(buf)

        val record = new VehicleTotalFuelRecord(vid)

        record.endtime = Encoders.Strings.decode(buf).toLong
        record.totaltime = Encoders.Strings.decode(buf).toLong
        record.totalmile = Encoders.Strings.decode(buf).toDouble
        record.totalfuel = Encoders.Strings.decode(buf).toDouble

        record
    }
}