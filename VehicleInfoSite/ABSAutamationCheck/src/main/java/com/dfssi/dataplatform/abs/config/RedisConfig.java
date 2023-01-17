package com.dfssi.dataplatform.abs.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/20 9:31
 */
@Configuration
@Setter
@Getter
@ToString
@Slf4j
public class RedisConfig {

    @Value("${redis.master}")
    private String master;

    @Value("${redis.sentinels}")
    private String sentinels;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.dbNum}")
    private String dbNum;

    @Value("${redis.timeout}")
    private String timeout;

    private volatile JedisSentinelPool jedisSentinelPool;

    public JedisSentinelPool getSentinelPool(){

        if(jedisSentinelPool == null){
            synchronized (RedisConfig.class){
                if(jedisSentinelPool == null){
                    JedisPoolConfig poolConfig = new JedisPoolConfig();
                    poolConfig.setMaxTotal(250);
                    poolConfig.setMaxIdle(32);
                    poolConfig.setTestOnBorrow(false);
                    poolConfig.setTestOnReturn(false);
                    poolConfig.setTestWhileIdle(false);
                    poolConfig.setMinEvictableIdleTimeMillis(60000);
                    poolConfig.setTimeBetweenEvictionRunsMillis(30000);
                    poolConfig.setNumTestsPerEvictionRun(-1);

                    String[] split = sentinels.split(",");
                    Set<String> sentinels = new HashSet<>(split.length);
                    for(String s : split){
                        sentinels.add(s);
                    }
                    jedisSentinelPool = new JedisSentinelPool(master, sentinels,
                            poolConfig, Integer.parseInt(timeout), password, Integer.parseInt(dbNum));

                    log.info(String.format("创建一个redis连接池成功， 连接参数为：\n\t %s", toString()));
                }
            }
        }

        return jedisSentinelPool;
    }

    public Jedis getJedis() throws Exception{
        JedisSentinelPool sentinelPool = getSentinelPool();
        Jedis jedis = null;
        if(sentinelPool != null){
            int sleepTime = 2;
            while (jedis == null && sleepTime < 500){
                jedis = sentinelPool.getResource();
                if(jedis == null){
                    sleepTime = sleepTime * 4;
                    Thread.sleep(sleepTime);
                }
            }
        }
        return jedis;
    }

}
