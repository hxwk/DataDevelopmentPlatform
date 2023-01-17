package com.dfssi.dataplatform.analysis.redis

import java.nio.ByteBuffer
import java.nio.charset.Charset

import com.dfssi.dataplatform.analysis.ccv.trip
import com.dfssi.dataplatform.analysis.ccv.trip.TripDataRecord
import com.dfssi.dataplatform.analysis.redis.SentinelConnectionPool.SentinelRedisEndpoint
import io.netty.buffer.Unpooled
import redis.clients.jedis.Jedis

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/5/15 14:09 
  */
class ByteBufferRedis(val redis: Jedis) {

    private val UTF_8 = Charset.forName("UTF-8")

    def close(): Unit ={
        if(redis != null){
            redis.close()
        }
    }

    def set(key: String, value: ByteBuffer): String ={
        redis.set(key.getBytes(UTF_8), value.array())
    }

    def get(key: String): ByteBuffer ={
        val bytes = redis.get(key.getBytes(UTF_8))
        if(bytes != null){
            return Unpooled.wrappedBuffer(bytes).nioBuffer
        }
        return null
    }

    def get(key: Array[Byte]): ByteBuffer ={
        if(key != null){
            return Unpooled.wrappedBuffer(key).nioBuffer
        }
        return null
    }

    def keys(keyPattern: String): mutable.Set[Array[Byte]] = redis.keys(keyPattern.getBytes(UTF_8)).asScala

}

object ByteBufferRedis{
    def apply(redis: Jedis): ByteBufferRedis = new ByteBufferRedis(redis)

    def main(args: Array[String]): Unit = {
        val redisEndpoint = SentinelRedisEndpoint("172.16.1.201:26379,172.16.1.201:26380", "mymaster", "112233", 9, 2000)
        val jedis = redisEndpoint.connect()

        val record = trip.TripDataRecord("1122334", "jechedo", "1234566")

        val redis = ByteBufferRedis(jedis)
        println(redis.set("jechedo-demo", record.encode()))

        println(TripDataRecord.decode(redis.get("jechedo-demo")))
    }
}
