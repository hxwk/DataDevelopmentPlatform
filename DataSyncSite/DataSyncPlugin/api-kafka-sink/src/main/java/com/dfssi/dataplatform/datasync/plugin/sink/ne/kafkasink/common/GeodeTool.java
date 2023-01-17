package com.dfssi.dataplatform.datasync.plugin.sink.ne.kafkasink.common;

import org.apache.commons.lang.StringUtils;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hannibal on 2018-04-12.
 */
public class GeodeTool {

    private static Logger logger = LoggerFactory.getLogger(GeodeTool.class);


    private static GeodeTool instance = null;

    private static Map<String,Region> neregionMap=new HashMap<>();

    private ClientRegionFactory rf = null;

    private ClientCache cache = null;

    private QueryService queryService = null;

    static {
        instance = new GeodeTool();
    }

    private GeodeTool(){
        init();
    }

    private void init() {

        try {
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
            cache = ccf.create();

            queryService = cache.getQueryService();

            rf = cache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);
        } catch (Exception e) {
            logger.error("初始化geode服务发生错误，程序终止", e);

            System.exit(1);
        }
    }

    public static GeodeTool getInstance() {
        if (null == instance) {
            instance = new GeodeTool();
        }

        return instance;
    }

    public static Region getRegeion(String region){
        if(neregionMap.get(region)==null){
            neregionMap.put(region,getInstance().getCache().getRegion(region));
        }
        return neregionMap.get(region);
    }

    public ClientCache getCache() {
        return cache;
    }

    public QueryService getQueryService() {
        return queryService;
    }

    public static void main(String[] args) {
        getInstance();
    }

}
