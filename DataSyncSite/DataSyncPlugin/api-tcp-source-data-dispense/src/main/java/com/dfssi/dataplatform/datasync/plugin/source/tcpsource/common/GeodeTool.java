package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common;

import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.config.XdiamondApplication;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hannibal on 2018-04-12.
 */
public class GeodeTool {

    private static Logger logger = LoggerFactory.getLogger(GeodeTool.class);

    private static final String GEODE_CONFIG_FILE = "com.dfssi.dataplatform.datasync.plugin.source.tcpsource/geode.properties";

    private static Map<String,Region> regionMap=new HashMap<>();

    private static GeodeTool instance = null;

    private ClientRegionFactory rf = null;

    static {
        instance = new GeodeTool();
    }

    private GeodeTool(){
        init();
    }

    private void init() {

        try {

            PropertiesConfiguration propertiesConfiguration= new PropertiesConfiguration(GEODE_CONFIG_FILE);

            Constants.GEODE_CONNECT=propertiesConfiguration.getString("geode.urls");

            XdiamondApplication.getInstance();

            if (StringUtils.isBlank(Constants.GEODE_CONNECT)) {
                logger.warn("木有geode的连接配置，退出进程");

                System.exit(0);
            }

            String[] geodeConns = Constants.GEODE_CONNECT.split(",");

            ClientCacheFactory ccf = new ClientCacheFactory();

            for (String geodeConn : geodeConns) {
                String[] conn = geodeConn.split(":");
                ccf.addPoolLocator(conn[0], Integer.parseInt(conn[1]));
            }
            ClientCache cache = ccf.create();

            rf = cache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);
        } catch (Exception e) {
            logger.error("初始化geode服务发生错误，程序终止", e);

            System.exit(1);
        }

    }

    private static GeodeTool getInstance() {
        if (null == instance) {
            instance = new GeodeTool();
        }

        return instance;
    }

    private ClientRegionFactory getRf(){
        return rf;
    }

    public static Region getRegeion(String region){
        if(regionMap.get(region)==null){
            regionMap.put(region,getInstance().getRf().create(region));
        }
        return regionMap.get(region);
    }

    public static void main(String[] args) {
        getInstance();
    }


}
