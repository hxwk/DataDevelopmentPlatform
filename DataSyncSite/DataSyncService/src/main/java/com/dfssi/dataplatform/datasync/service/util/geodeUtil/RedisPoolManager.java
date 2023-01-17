package com.dfssi.dataplatform.datasync.service.util.geodeUtil;

import io.github.xdiamond.client.XDiamondConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Redis连接池管理器
 */
public class RedisPoolManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisPoolManager.class);

    protected static ReentrantLock lockPool = new ReentrantLock();
    protected static ReentrantLock lockJedis = new ReentrantLock();

    public static String REDIS_SERVER;
    public static  int REDIS_PORT;
    public static  int MAX_TOTAL;
    public static  int MAX_IDLE;
    public static  int MAX_WAIT_MILLIS;
    public static  boolean TEST_ON_BORROW;
    public static  boolean TEST_ON_RETURN;
    public static int DATABASE = 2;
    public static String NODES;
    public static String PASSWORD = "112233";




    private static JedisSentinelPool jedisPool = null;
    //    读取redis配置文件
//    private InputStream configurationStream = RedisPoolManager.class.getClassLoader().getResourceAsStream("com.dfssi.dataplatform.plugin.tcpnesource/redisconf.properties");

    private static void initPoolConfig(){
        XDiamondConfig config = XdiamondApplication.getInstance().getxDiamondConfig();

        try {
            REDIS_SERVER = config.getProperty("redishostname");
            REDIS_PORT = Integer.parseInt(config.getProperty("redisport"));
            MAX_TOTAL = Integer.parseInt(config.getProperty("maxtotal"));
            MAX_IDLE = Integer.parseInt(config.getProperty("maxidle"));
            MAX_WAIT_MILLIS = Integer.parseInt(config.getProperty("maxwaitmillis"));
            TEST_ON_BORROW = Boolean.parseBoolean(config.getProperty("testonborrow"));
            TEST_ON_RETURN = Boolean.parseBoolean(config.getProperty("testonreturn"));
            DATABASE = Integer.parseInt(config.getProperty("redis.database"));
            PASSWORD = config.getProperty("redis.password");
            NODES=config.getProperty("redis.sentinels");
        } catch (Exception e) {
            logger.info("redis cannot load conf",e);
        }
    }

    private static void initialPool() {
        initPoolConfig();
        if (jedisPool == null) {

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT_MILLIS);
            config.setTestOnBorrow(TEST_ON_BORROW);
            Set<String> sentinels=new HashSet<>(Arrays.asList(NODES.split(",")));
            config.setTestOnReturn(TEST_ON_RETURN);
            jedisPool = new JedisSentinelPool(REDIS_SERVER,sentinels,config, 100000, PASSWORD, DATABASE);
        }
    }

    //从默认库0读写
    public JedisSentinelPool getInstanceDb0(){
        if( null == jedisPool){
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT_MILLIS);
            config.setTestOnBorrow(TEST_ON_BORROW);
            Set<String> sentinels=new HashSet<>(Arrays.asList(NODES.split(",")));
            config.setTestOnReturn(TEST_ON_RETURN);
            jedisPool = new JedisSentinelPool(REDIS_SERVER,sentinels,config, 10, PASSWORD, 0);
        }
        return jedisPool;
    }


    /**
     * 在多线程环境同步初始化
     */
    private static void poolInit() {
        lockPool.lock();
        try {
            if (jedisPool == null) {
                initialPool();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lockPool.unlock();
        }
    }

    public static Jedis getJedis() {
        lockJedis.lock();
        if (jedisPool == null) {
            poolInit();
        }
        Jedis jedis = null;
        try {
            if (jedisPool != null) {
                jedis = jedisPool.getResource();
            }
        } catch (Exception e) {
            logger.error("Get jedis error : " + e);
        } finally {
            lockJedis.unlock();
        }
        return jedis;
    }
    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null && jedisPool != null) {
            jedisPool.returnResource(jedis);
        }
    }
}
