package com.yaxon.vn.nd.tas.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Redis连接池管理器
 */
public class RedisPoolManager {

    private static final Logger logger = LoggerFactory.getLogger(RedisPoolManager.class);

    public  String REDIS_SERVER;
    public  int REDIS_PORT;
    public  int MAX_TOTAL;
    public  int MAX_IDLE;
    public  int MAX_WAIT_MILLIS;
    public  boolean TEST_ON_BORROW;
    public  boolean TEST_ON_RETURN;
    public int DATABASE = 2;
    public String PASSWORD = "112233";




    private JedisPool pool = null;
    //    读取redis配置文件
    private InputStream configurationStream = RedisPoolManager.class.getClassLoader().getResourceAsStream("redisconf.properties");

    public RedisPoolManager(){
        Properties props = new Properties();
        try {
            props.load(configurationStream);
            this.REDIS_SERVER = props.getProperty("redishostname");
            this.REDIS_PORT = Integer.parseInt(props.getProperty("redisport"));
            this.MAX_TOTAL = Integer.parseInt(props.getProperty("maxtotal"));
            this.MAX_IDLE = Integer.parseInt(props.getProperty("maxidle"));
            this.MAX_WAIT_MILLIS = Integer.parseInt(props.getProperty("maxwaitmillis"));
            this.TEST_ON_BORROW = Boolean.parseBoolean(props.getProperty("testonborrow"));
            this.TEST_ON_RETURN = Boolean.parseBoolean(props.getProperty("testonreturn"));
            this.DATABASE = Integer.parseInt(props.getProperty("redis.database"));
            this.PASSWORD = props.getProperty("redis.password");

        } catch (IOException e) {
            logger.info("redis cannot load conf",e);
        }
    }

    private JedisPool getInstance() {
        if (pool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_TOTAL);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT_MILLIS);
            config.setTestOnBorrow(TEST_ON_BORROW);
            config.setTestOnReturn(TEST_ON_RETURN);

            pool = new JedisPool(config, REDIS_SERVER, REDIS_PORT, 10, PASSWORD, DATABASE);
        }

        return pool;

    }

    /**
     * 获取jedis
     *
     * @return
     */
    public Jedis getJedis() {

        Jedis jedis = null;

        try {
            jedis = getInstance().getResource();
            // jedis.auth(REDIS_AUTH);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return jedis;

    }

    /**
     * 返回jedis
     *
     * @param jedis
     */
    public void returnJedis(Jedis jedis) {
        try {

            if (jedis != null) {
                getInstance().returnResource(jedis);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 返回关闭的redis
     *
     * @param jedis
     */
    public void returnBrokenJedis(Jedis jedis) {
        try {

            if (jedis != null) {
                getInstance().returnBrokenResource(jedis);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * 释放jedis
     *
     * @param jedis
     */
    public void releaseJedis(Jedis jedis) {
        pool.returnResource(jedis);

    }
}
