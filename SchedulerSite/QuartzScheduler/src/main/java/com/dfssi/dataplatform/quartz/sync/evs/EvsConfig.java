package com.dfssi.dataplatform.quartz.sync.evs;

import com.dfssi.dataplatform.quartz.sync.SyncConfig;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/18 15:34
 */
public class EvsConfig extends SyncConfig {
    private final static String CONFIG_MODEL = "sync-evs";

    private final Logger logger = LoggerFactory.getLogger(EvsConfig.class);
    private Element configElement;

    private String dataBaseId;
    private String errdayTable, errtotalTable, datadayTable, datatotalTable;

    private String redisId;
    private int redisKeyPartitions;
    private int redisFetchBatch;

    private Set<String> detectFieldSet;

    public EvsConfig(String nameNodeHost, int nameNodeWebhdfsPort, String configDir){
        super(nameNodeHost, nameNodeWebhdfsPort, configDir);
        configElement = getConfigElement(CONFIG_MODEL);

        Element element = (Element) configElement.selectSingleNode("database");
        dataBaseId = element.attributeValue("id");
        errdayTable = element.attributeValue("errday");
        errtotalTable = element.attributeValue("errtotal");
        datadayTable = element.attributeValue("dataday");
        datatotalTable = element.attributeValue("datatotal");


        element = (Element) configElement.selectSingleNode("redis");
        redisId = element.attributeValue("id");
        redisKeyPartitions = Integer.parseInt(element.attributeValue("partitions", "20"));
        redisFetchBatch = Integer.parseInt(element.attributeValue("batch", "500"));

        initDetectField();

    }

    public Connection getConnection(){
        return getConnection(dataBaseId);
    }

    public Jedis getJedis(){
        return getJedis(redisId);
    }

    private void initDetectField() {
        Element element = (Element) configElement.selectSingleNode("rules-database");
        String dataBaseId = element.attributeValue("id");
        String table = element.attributeValue("table");

        Connection connection = getConnection(dataBaseId);
        try {
            Statement statement = connection.createStatement();

            String sql = String.format("select detect_name from %s", table);
            ResultSet resultSet = statement.executeQuery(sql);
            detectFieldSet = new HashSet<>();
            while (resultSet.next()){
                detectFieldSet.add(resultSet.getString("detect_name").toLowerCase());
            }
            resultSet.close();
            statement.close();
            logger.info(String.format("检测名称集合如下：\n\t %s", detectFieldSet));
        } catch (SQLException e) {
           logger.error("检测项查询失败。", e);
        }


    }

    public int getRedisKeyPartitions() {
        return redisKeyPartitions;
    }

    public int getRedisFetchBatch() {
        return redisFetchBatch;
    }

    public String getErrdayTable() {
        return errdayTable;
    }

    public String getErrtotalTable() {
        return errtotalTable;
    }

    public String getDatadayTable() {
        return datadayTable;
    }

    public String getDatatotalTable() {
        return datatotalTable;
    }

    public Set<String> getDetectFieldSet() {
        return detectFieldSet;
    }
}
