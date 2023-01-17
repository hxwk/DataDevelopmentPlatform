package com.dfssi.dataplatform.analysis.redis

import java.util
import java.util.concurrent.ConcurrentHashMap

import org.apache.spark.SparkConf
import redis.clients.jedis.exceptions.JedisConnectionException
import redis.clients.jedis.{Jedis, JedisPoolConfig, JedisSentinelPool, Protocol}

import scala.collection.JavaConversions._

/**
  * Description:
  *
  * @author LiXiaoCong
  * @version 2018/4/11 14:51 
  */
object SentinelConnectionPool {
    @transient private lazy val pools: ConcurrentHashMap[SentinelRedisEndpoint, JedisSentinelPool] =
        new ConcurrentHashMap[SentinelRedisEndpoint, JedisSentinelPool]()
    def connect(re: SentinelRedisEndpoint): Jedis = {
        val pool = pools.getOrElseUpdate(re,
            {
                val poolConfig: JedisPoolConfig = new JedisPoolConfig()
                poolConfig.setMaxTotal(250)
                poolConfig.setMaxIdle(32)
                poolConfig.setTestOnBorrow(false)
                poolConfig.setTestOnReturn(false)
                poolConfig.setTestWhileIdle(false)
                poolConfig.setMinEvictableIdleTimeMillis(60000)
                poolConfig.setTimeBetweenEvictionRunsMillis(30000)
                poolConfig.setNumTestsPerEvictionRun(-1)

                val sentinels = re.sentinels
                val strings = sentinels.split(",")
                val set = new util.HashSet[String]()
                strings.foreach(s => set.add(s))

                new JedisSentinelPool(re.masterName, set, poolConfig, re.timeout, re.auth, re.dbNum)
            }
        )
        var sleepTime: Int = 4
        var conn: Jedis = null
        while (conn == null) {
            try {
                conn = pool.getResource
            }
            catch {
                case e: JedisConnectionException if e.getCause.toString.
                        contains("ERR max number of clients reached") => {
                    if (sleepTime < 500) sleepTime *= 2
                    Thread.sleep(sleepTime)
                }
                case e: Exception => throw e
            }
        }
        conn
    }

    /**
      *
      * @param sentinels    host1:port1,host2:port2
      * @param masterName
      * @param auth
      * @param dbNum
      * @param timeout
      */
    case class SentinelRedisEndpoint(val sentinels: String,
                                     val masterName: String = "mymaster",
                                     val auth: String = null,
                                     val dbNum: Int = Protocol.DEFAULT_DATABASE,
                                     val timeout: Int = Protocol.DEFAULT_TIMEOUT)
            extends Serializable {

        /**
          * Constructor from spark config. set params with redis.host, redis.port, redis.auth and redis.db
          *
          * @param conf spark context config
          */
        def this(conf: SparkConf) {
            this(
                conf.get("redis.sentinels", null),
                conf.get("redis.master", "mymaster"),
                conf.get("redis.auth", null),
                conf.getInt("redis.db", Protocol.DEFAULT_DATABASE),
                conf.getInt("redis.timeout", Protocol.DEFAULT_TIMEOUT)
            )
        }

        /**
          * Connect tries to open a connection to the redis endpoint,
          * optionally authenticating and selecting a db
          *
          * @return a new Jedis instance
          */
        def connect(): Jedis = {
            SentinelConnectionPool.connect(this)
        }
        def close(): Unit ={
            SentinelConnectionPool.close(this)
        }
    }

    def close(re: SentinelRedisEndpoint): Unit ={
        val pool = pools.get(re)
        if(pool != null){
            try {
                pool.close()
                pool.destroy()
                pools.remove(re)
            } catch {
                case e: Throwable => null
            }
        }
    }

    def main(args: Array[String]): Unit = {
        val redisEndpoint = SentinelRedisEndpoint("172.16.1.201:26379,172.16.1.201:26380", "mymaster", "112233", 9, 2000)
        val jedis = redisEndpoint.connect()

        val item = new util.HashMap[String, String]()
        item.put("jechedo", "0")
        item.put("dew", "023")
        jedis.hmset("demo:dee", item)

        val map = jedis.hgetAll("demo:dee")
        println(map)

        jedis.close()
    }
}
