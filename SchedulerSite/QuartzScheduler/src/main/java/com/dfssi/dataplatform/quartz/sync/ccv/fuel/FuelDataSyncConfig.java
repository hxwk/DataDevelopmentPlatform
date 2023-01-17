package com.dfssi.dataplatform.quartz.sync.ccv.fuel;

import com.dfssi.dataplatform.quartz.sync.SyncConfig;
import org.dom4j.Element;
import redis.clients.jedis.Jedis;

import java.sql.Connection;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/16 20:09
 */
public class FuelDataSyncConfig extends SyncConfig {

    private final static String CONFIG_MODEL = "sync-fuel";
    private Element configElement;

    private String dataBaseId;
    private String totalTable;
    private String tripTable;

    private String redisId;
    private int redisKeyPartitions;
    private int redisFetchBatch;

    public FuelDataSyncConfig(String nameNodeHost, int nameNodeWebhdfsPort, String configDir) {
        super(nameNodeHost, nameNodeWebhdfsPort, configDir);
        configElement = getConfigElement(CONFIG_MODEL);

        Element databaseElement = (Element) configElement.selectSingleNode("database");
        dataBaseId = databaseElement.attributeValue("id");
        totalTable = databaseElement.attributeValue("total");
        tripTable = databaseElement.attributeValue("trip");

        Element redisElement = (Element) configElement.selectSingleNode("redis");
        redisId = redisElement.attributeValue("id");
        redisKeyPartitions = Integer.parseInt(redisElement.attributeValue("partitions", "20"));
        redisFetchBatch = Integer.parseInt(redisElement.attributeValue("batch", "500"));
    }

    public Connection getConnection(){
        return getConnection(dataBaseId);
    }

    public Jedis getJedis(){
        return getJedis(redisId);
    }

    public String getTotalTable() {
        return totalTable;
    }

    public String getTripTable() {
        return tripTable;
    }

    public int getRedisKeyPartitions() {
        return redisKeyPartitions;
    }

    public int getRedisFetchBatch() {
        return redisFetchBatch;
    }
}
