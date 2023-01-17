package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common;

import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.config.XdiamondApplication;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util.PropertiesFileUtil;
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
 * 需要在服务器上hosts文件上配置geode的映射，否则会一直报日志，频率很高
 */
public class GeodeTool {

    private static Logger logger = LoggerFactory.getLogger(GeodeTool.class);

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
            PropertiesConfiguration propertiesConfiguration= PropertiesFileUtil.instanceConfig();
            XdiamondApplication xdiamond=  XdiamondApplication.getInstance();
            if (StringUtils.isNotBlank(xdiamond.getxDiamondConfig().getProperty(Constants.GEODE_CONNECT_KEY))) {
                logger.info("当前客户端中通过xdiamond去获取GEODE_CONNECT配置");
                Constants.GEODE_CONNECT = xdiamond.getxDiamondConfig().getProperty(Constants.GEODE_CONNECT_KEY);
            }else{
                logger.info("当前客户端中去api-tcp-road-source包的tcproadsource.properties中加载GEODE_CONNECT配置");
                Constants.GEODE_CONNECT=propertiesConfiguration.getString(Constants.GEODE_CONNECT_KEY);
            }
            if( Constants.GEODE_CONNECT.isEmpty()){
                logger.info("Constants.GEODE_CONNECT地址为空，请检查当前客户端中是否含有xdiamond配置或GEODE_CONNECT配置");
            }else{
                logger.info("source获得GEODE_CONNECT配置："+Constants.GEODE_CONNECT);
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
            logger.error("初始化geode服务发生错误，车辆无法判断是否接入，请紧急处理！", e);
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
