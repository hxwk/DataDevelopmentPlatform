package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common;

import com.dfssi.dataplatform.datasync.common.config.XdiamondApplication;
import io.github.xdiamond.client.XDiamondConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

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

    public RedisPoolManager(){
        XDiamondConfig config = XdiamondApplication.getInstance().getxDiamondConfig();

        try {
            this.REDIS_SERVER = config.getProperty("redishostname");
            this.REDIS_PORT = Integer.parseInt(config.getProperty("redisport"));
            this.MAX_TOTAL = Integer.parseInt(config.getProperty("maxtotal"));
            this.MAX_IDLE = Integer.parseInt(config.getProperty("maxidle"));
            this.MAX_WAIT_MILLIS = Integer.parseInt(config.getProperty("maxwaitmillis"));
            this.TEST_ON_BORROW = Boolean.parseBoolean(config.getProperty("testonborrow"));
            this.TEST_ON_RETURN = Boolean.parseBoolean(config.getProperty("testonreturn"));
            this.DATABASE = Integer.parseInt(config.getProperty("redis.database"));
            this.PASSWORD = config.getProperty("redis.password");

        } catch (Exception e) {
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

            pool = new JedisPool(config, REDIS_SERVER, REDIS_PORT, 10000, PASSWORD, DATABASE);
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
