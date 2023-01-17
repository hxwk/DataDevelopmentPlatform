package com.dfssi.dataplatform.plugin.tcpnesource.common;

import com.dfssi.dataplatform.plugin.tcpnesource.config.XdiamondApplication;
import com.dfssi.dataplatform.plugin.tcpnesource.util.PropertiesFileUtil;
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
 * geode数据库连接
 * Created by Hannibal on 2018-04-12.
 */
public class GeodeTool {

    private static Logger logger = LoggerFactory.getLogger(GeodeTool.class);

    private static GeodeTool instance = null;

    private static Map<String,Region> neregionMap=new HashMap<>();

    private ClientRegionFactory rf = null;

    static {
        instance = new GeodeTool();
    }

    private GeodeTool(){
        init();
    }

    private void init() {

        try {

            PropertiesConfiguration propertiesConfiguration = PropertiesFileUtil.instanceConfig();

            XdiamondApplication xdiamond=  XdiamondApplication.getInstance();
            if (StringUtils.isNotBlank(xdiamond.getxDiamondConfig().getProperty(Constants.GEODE_CONNECT_KEY))) {
                Constants.GEODE_CONNECT = xdiamond.getxDiamondConfig().getProperty(Constants.GEODE_CONNECT_KEY);
            }else{
                Constants.GEODE_CONNECT=propertiesConfiguration.getString(Constants.GEODE_CONNECT_KEY);
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
        if(neregionMap.get(region)==null){
            neregionMap.put(region,getInstance().getRf().create(region));
        }
        return neregionMap.get(region);
    }

    public void clearRegionMap(){
        neregionMap.clear();
    }

    public static void main(String[] args) {
        getInstance();
    }


}
