package com.dfssi.dataplatform.quartz.sync;

import com.dfssi.common.SysEnvs;
import com.google.common.base.Preconditions;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/18 15:34
 */
public class SyncConfig {
    private final static String CONFIG_FILE = "sync-config.xml";

    private final Logger logger = LoggerFactory.getLogger(SyncConfig.class);

    private EnvironmentConfig environmentConfig;

    private Element root;
    private Map<String, Connection> connectionMap;
    private Map<String, JedisSentinelPool> jedisSentinelPoolMap;

    public SyncConfig(String nameNodeHost, int nameNodeWebhdfsPort, String configDir){

        try {
            if(configDir == null){
                configDir = getConfigPath(configDir);
            }

            this.environmentConfig = new EnvironmentConfig(nameNodeHost, nameNodeWebhdfsPort);

            URL config;
            if(configDir == null){
                config = SyncConfig.class.getResource("/" + CONFIG_FILE);
            }else{
                config = new File(configDir, CONFIG_FILE).toURI().toURL();
            }
            init(config);
        } catch (Exception e){
            logger.error(null, e);
            e.printStackTrace();
        }

    }

    public Connection getConnection(String key){
        return connectionMap.get(key);
    }

    public void closeConnection(){
        connectionMap.forEach((key, conn) ->{
            try {
                conn.close();
            } catch (SQLException e) { }
        });
    }

    public void closeJedisPool(){
        try {
            jedisSentinelPoolMap.forEach((key, pool) ->{
                pool.close();
                pool.destroy();
            });
        } catch (Exception e) { }
    }

    public JedisSentinelPool getJedisSentinelPool(String key){
        return jedisSentinelPoolMap.get(key);
    }

    public Jedis getJedis(String key){
        JedisSentinelPool jedisSentinelPool = getJedisSentinelPool(key);
        Jedis jedis = null;
        if(jedisSentinelPool != null){
            int sleepTime = 4;
            try {
                while (jedis == null && sleepTime < 500){
                    jedis = jedisSentinelPool.getResource();
                    if(jedis == null){
                        sleepTime = sleepTime * 2;
                        Thread.sleep(sleepTime);
                    }
                }
            } catch (InterruptedException e) {
                logger.error("获取redis连接失败。", e);
            }
        }
        return jedis;
    }

    public List<Element> getConfigElements(String xpathExpression){
        return root.selectNodes(xpathExpression);
    }

    public Element getConfigElement(String xpathExpression){
        Node node = root.selectSingleNode(xpathExpression);
        if(node != null){
            return (Element)node;
        }
        return null;
    }

    private void init(URL config) throws Exception {
        root = new SAXReader().read(config).getRootElement();

        logger.info("初始化数据库连接...");
        initDataBases();
        logger.info("初始化数据库连接成功。");

        logger.info("初始化Redis连接池...");
        initRedisPool();
        logger.info("初始化Redis连接池成功。");
    }

    private void initDataBases() throws Exception {
        List<Element> dataBaseNodes = root.selectNodes("databases/database");
        connectionMap = new HashMap<>(dataBaseNodes.size());

        StringBuilder sb;
        String id;
        String url;
        String user;
        String password;
        String driver;

        Connection conn;
        for(Element element : dataBaseNodes){
            sb = new StringBuilder();

            id = element.attributeValue("id");
            Preconditions.checkNotNull(id, "database连接id不能为空。");
            sb.append("id = ").append(id).append(", ");
            Map<String, String> environment = environmentConfig.getEnvironment(id);


            url =  environment.getOrDefault("url", element.attributeValue("url"));
            Preconditions.checkNotNull(url, "database连接url不能为空。");
            sb.append("url = ").append(url).append(", ");

            user = environment.getOrDefault("user", element.attributeValue("user"));
            Preconditions.checkNotNull(user, "database连接user不能为空。");
            sb.append("user = ").append(user).append(", ");

            password = environment.getOrDefault("password", element.attributeValue("password"));
            Preconditions.checkNotNull(password, "database连接password不能为空。");
            sb.append("password = ").append(password).append(", ");

            driver = environment.getOrDefault("driver", element.attributeValue("driver"));
            Preconditions.checkNotNull(driver, "database连接driver不能为空。");
            sb.append("driver = ").append(driver);

            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            connectionMap.put(id, conn);

            logger.info(String.format("创建一个数据库连接成功， 连接参数为：\n\t %s", sb));
        }
    }

    private void initRedisPool(){
        List<Element> redisNodes = root.selectNodes("rediss/redis");
        jedisSentinelPoolMap = new HashMap<>(redisNodes.size());

        Map<String, String> redis = environmentConfig.getEnvironment("redis");

        String id;
        String master;
        String sentinelServers;
        String password;
        String dbNum;
        String timeout;
        JedisPoolConfig poolConfig;
        JedisSentinelPool jedisSentinelPool;
        StringBuilder sb;
        for(Element element : redisNodes){
            sb = new StringBuilder();

            id = element.attributeValue("id");
            Preconditions.checkNotNull(id, "redis连接id不能为空。");
            sb.append("id = ").append(id).append(", ");

            master = redis.getOrDefault("master", element.attributeValue("master", "mymaster"));
            sb.append("master = ").append(master).append(", ");

            sentinelServers = redis.getOrDefault("sentinels", element.attributeValue("sentinels"));
            Preconditions.checkNotNull(sentinelServers, "redis连接sentinels不能为空。");
            sb.append("sentinels = ").append(sentinelServers).append(", ");

            password = redis.getOrDefault("password", element.attributeValue("password"));
            sb.append("password = ").append(password).append(", ");

            dbNum = element.attributeValue("dbNum", "6");
            sb.append("dbNum = ").append(dbNum).append(", ");

            timeout = element.attributeValue("timeout", "2000");
            sb.append("timeout = ").append(timeout).append(", ");

            poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(250);
            poolConfig.setMaxIdle(32);
            poolConfig.setTestOnBorrow(false);
            poolConfig.setTestOnReturn(false);
            poolConfig.setTestWhileIdle(false);
            poolConfig.setMinEvictableIdleTimeMillis(60000);
            poolConfig.setTimeBetweenEvictionRunsMillis(30000);
            poolConfig.setNumTestsPerEvictionRun(-1);

            String[] split = sentinelServers.split(",");
            Set<String> sentinels = new HashSet<>(split.length);
            for(String s : split){
                sentinels.add(s);
            }
            jedisSentinelPool = new JedisSentinelPool(master, sentinels,
                    poolConfig, Integer.parseInt(timeout), password, Integer.parseInt(dbNum));
            jedisSentinelPoolMap.put(id, jedisSentinelPool);

            logger.info(String.format("创建一个redis连接池成功， 连接参数为：\n\t %s", sb));
        }
    }

    public EnvironmentConfig getEnvironmentConfig() {
        return environmentConfig;
    }

    private String getConfigPath(String configDir){
        if(configDir == null){
            String jarFileDirByClass = SysEnvs.getJarFileDirByClass(this.getClass());
            File parentFile = new File(jarFileDirByClass).getParentFile();

            File file = new File(parentFile, "config/" + CONFIG_FILE);
            if(file.exists()){
                configDir = file.getParent();
            }
        }
        return configDir;
    }
}
