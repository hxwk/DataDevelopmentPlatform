package com.dfssi.dataplatform.analysis.redis

import java.net.URI

import org.apache.spark.SparkConf
import redis.clients.jedis.{Jedis, Protocol}
import redis.clients.util.JedisURIHelper


/**
  * RedisEndpoint represents a redis connection endpoint info: host, port, auth password
  * db number, and timeout
  *
  * @param host the redis host or ip
  * @param port the redis port
  * @param auth the authentication password
  * @param dbNum database number (should be avoided in general)
  */
case class RedisEndpoint(val host: String = Protocol.DEFAULT_HOST,
                         val port: Int = Protocol.DEFAULT_PORT,
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
        conf.get("redis.host", Protocol.DEFAULT_HOST),
        conf.getInt("redis.port", Protocol.DEFAULT_PORT),
        conf.get("redis.auth", null),
        conf.getInt("redis.db", Protocol.DEFAULT_DATABASE),
        conf.getInt("redis.timeout", Protocol.DEFAULT_TIMEOUT)
      )
  }

  /**
    * Constructor with Jedis URI
    *
    * @param uri connection URI in the form of redis://:$password@$host:$port/[dbnum]
    */
  def this(uri: URI) {
    this(uri.getHost, uri.getPort, JedisURIHelper.getPassword(uri), JedisURIHelper.getDBIndex(uri))
  }

  /**
    * Constructor with Jedis URI from String
    *
    * @param uri connection URI in the form of redis://:$password@$host:$port/[dbnum]
    */
  def this(uri :String) {
    this(URI.create(uri))
  }

  /**
    * Connect tries to open a connection to the redis endpoint,
    * optionally authenticating and selecting a db
    *
    * @return a new Jedis instance
    */
  def connect(): Jedis = {
    ConnectionPool.connect(this)
  }
}

case class RedisNode(val endpoint: RedisEndpoint,
                     val startSlot: Int,
                     val endSlot: Int,
                     val idx: Int,
                     val total: Int) {
  def connect(): Jedis = {
    endpoint.connect()
  }
}

/**
  * RedisConfig holds the state of the cluster nodes, and uses consistent hashing to map
  * keys to nodes
  */
class RedisConfig(val initialHost: RedisEndpoint) extends  Serializable {

  val initialAddr = initialHost.host

  /**
    * @return initialHost's auth
    */
  def getAuth: String = {
    initialHost.auth
  }

  /**
    * @return selected db number
    */
  def getDB :Int = {
    initialHost.dbNum
  }



  /**
    * @param initialHost any redis endpoint of a cluster or a single server
    * @return true if the target server is in cluster mode
    */
  private def clusterEnabled(initialHost: RedisEndpoint): Boolean = {
    val conn = initialHost.connect()
    val info = conn.info.split("\n")
    val version = info.filter(_.contains("redis_version:"))(0)
    val clusterEnable = info.filter(_.contains("cluster_enabled:"))
    val mainVersion = version.substring(14, version.indexOf(".")).toInt
    val res = mainVersion>2 && clusterEnable.length>0 && clusterEnable(0).contains("1")
    conn.close
    res
  }

  /**
    * @param initialHost any redis endpoint of a single server
    * @return list of nodes
    */
  private def getNonClusterNodes(initialHost: RedisEndpoint): Array[RedisNode] = {
    val master = (initialHost.host, initialHost.port)
    val conn = initialHost.connect()

    val replinfo = conn.info("Replication").split("\n")
    conn.close

    // If  this node is a slave, we need to extract the slaves from its master
    if (replinfo.filter(_.contains("role:slave")).length != 0) {
      val host = replinfo.filter(_.contains("master_host:"))(0).trim.substring(12)
      val port = replinfo.filter(_.contains("master_port:"))(0).trim.substring(12).toInt

      //simply re-enter this function witht he master host/port
      getNonClusterNodes(initialHost = new RedisEndpoint(host, port,
        initialHost.auth, initialHost.dbNum))

    } else {
      //this is a master - take its slaves

      val slaves = replinfo.filter(x => (x.contains("slave") && x.contains("online"))).map(rl => {
        val content = rl.substring(rl.indexOf(':') + 1).split(",")
        val ip = content(0)
        val port = content(1)
        (ip.substring(ip.indexOf('=') + 1), port.substring(port.indexOf('=') + 1).toInt)
      })

      val nodes = master +: slaves
      val range = nodes.size
      (0 until range).map(i =>
        RedisNode(new RedisEndpoint(nodes(i)._1, nodes(i)._2, initialHost.auth, initialHost.dbNum,
                    initialHost.timeout),
          0, 16383, i, range)).toArray
    }
  }

}
