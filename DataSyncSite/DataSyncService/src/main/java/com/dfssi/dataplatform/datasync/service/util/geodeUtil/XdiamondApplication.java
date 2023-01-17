package com.dfssi.dataplatform.datasync.service.util.geodeUtil;

import io.github.xdiamond.client.XDiamondConfig;
import io.github.xdiamond.client.event.AllKeyListener;
import io.github.xdiamond.client.event.ConfigEvent;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取Xdiamond配置
 */
public class XdiamondApplication {

    private static final Logger logger = LoggerFactory.getLogger(XdiamondApplication.class);

    private XDiamondConfig xDiamondConfig;

    private static XdiamondApplication instance = null;

    private XdiamondApplication() {
        init();
    }

    public static XdiamondApplication getInstance() {
        if (null == instance) {
            instance = new XdiamondApplication();
        }

        return instance;
    }

    public void init() {
        PropertiesConfiguration app_config= PropertiesFileUtil.instanceConfig();

        xDiamondConfig = new XDiamondConfig();
        xDiamondConfig.setServerHost(app_config.getString("xdiamond.serverHost"));
        logger.info("xdiamond.serverHost = " + app_config.getString("xdiamond.serverHost"));
        xDiamondConfig.setServerPort(Integer.valueOf(app_config.getString("xdiamond.serverPort","5678")));
        logger.info("xdiamond.serverPort = " + app_config.getString("xdiamond.serverPort"));
        xDiamondConfig.setGroupId(app_config.getString("xdiamond.groupId"));
        logger.info("xdiamond.groupId = " + app_config.getString("xdiamond.groupId"));
        xDiamondConfig.setArtifactId(app_config.getString("xdiamond.artifactId"));
        logger.info("xdiamond.artifactId = " + app_config.getString("xdiamond.artifactId"));
        xDiamondConfig.setVersion(app_config.getString("xdiamond.version"));
        logger.info("xdiamond.version = " + app_config.getString("xdiamond.version"));
        xDiamondConfig.setProfile(app_config.getString("xdiamond.profile"));
        logger.info("xdiamond.profile = " + app_config.getString("xdiamond.profile"));
        xDiamondConfig.setSecretKey(app_config.getString("xdiamond.secretKey"));
        logger.info("xdiamond.secretKey = " + app_config.getString("xdiamond.secretKey"));

        xDiamondConfig.init();
        xDiamondConfig.addAllKeyListener(new AllKeyListener() {
            public void onConfigEvent(ConfigEvent configevent) {
                String key = configevent.getKey();
                String value = configevent.getValue();

                if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
                    logger.warn("xdiamond更新的key或者value为空");

                    return;
                }

                try {
                    if(Constants.PROPERTY_TOPIC_VECHILE_KEY.equals(key)) {
                        Constants.NE_VECHILE_LOGIN_TOPIC = value;
                        logger.info("Constants.NE_VECHILE_LOGIN_TOPIC 修改为 " + Constants.NE_VECHILE_LOGIN_TOPIC);
                    } else if (Constants.PROPERTY_TOPIC_PLATFORM_KEY.equalsIgnoreCase(key)) {
                        Constants.NE_PLATFORM_LOGIN_TOPIC = value;
                        logger.info("Constants.NE_PLATFORM_LOGIN_TOPIC 修改为 " + Constants.NE_PLATFORM_LOGIN_TOPIC);
                    } else if (Constants.PROPERTY_TOPIC_DATA_KEY.equalsIgnoreCase(key)) {
                        Constants.NE_VECHILE_DATA_TOPIC = value;
                        logger.info("Constants.NE_VECHILE_DATA_TOPIC 修改为 " + Constants.NE_VECHILE_DATA_TOPIC);
                    } else if (Constants.REGION_VEHICLEINFO_KEY.equalsIgnoreCase(key)) {
                        Constants.REGION_VEHICLEINFO = value;
                        logger.info("Constants.REGION_VEHICLEINFO 修改为 " + Constants.REGION_VEHICLEINFO);
                    } else if (Constants.REGION_PLATFORMINFO_KEY.equalsIgnoreCase(key)) {
                        Constants.REGION_PLATFORMINFO = value;
                        logger.info("Constants.REGION_PLATFORMINFO 修改为 " + Constants.REGION_PLATFORMINFO);
                    }
                } catch (Exception e) {
                    logger.error(null, e);
                }

            }
        });

        try {
            if (StringUtils.isNotBlank(xDiamondConfig.getProperty(Constants.PROPERTY_TOPIC_VECHILE_KEY))) {
                Constants.NE_VECHILE_LOGIN_TOPIC = xDiamondConfig.getProperty(Constants.PROPERTY_TOPIC_VECHILE_KEY);
            }

            if (StringUtils.isNotBlank(xDiamondConfig.getProperty(Constants.PROPERTY_TOPIC_PLATFORM_KEY))) {
                Constants.NE_PLATFORM_LOGIN_TOPIC= xDiamondConfig.getProperty(Constants.PROPERTY_TOPIC_PLATFORM_KEY);
            }

            if (StringUtils.isNotBlank(xDiamondConfig.getProperty(Constants.PROPERTY_TOPIC_DATA_KEY))) {
                Constants.NE_VECHILE_DATA_TOPIC = xDiamondConfig.getProperty(Constants.PROPERTY_TOPIC_DATA_KEY);
            }

            if (StringUtils.isNotBlank(xDiamondConfig.getProperty(Constants.GEODE_CONNECT_KEY))) {
                Constants.GEODE_CONNECT = xDiamondConfig.getProperty(Constants.GEODE_CONNECT_KEY);
            }

            if (StringUtils.isNotBlank(xDiamondConfig.getProperty(Constants.NUM_NE_DATA_KEY))) {
                String numOfNEData = xDiamondConfig.getProperty(Constants.NUM_NE_DATA_KEY);
                Integer threshold = Integer.parseInt(numOfNEData);
                if (StringUtils.isNotEmpty(numOfNEData)&&threshold>0) {
                    Constants.NUM_NE_DATA_VALUE = threshold;
                }
            }

            if (StringUtils.isNotBlank(xDiamondConfig.getProperty(Constants.REGION_VEHICLEINFO_KEY))) {
                Constants.REGION_VEHICLEINFO = xDiamondConfig.getProperty(Constants.REGION_VEHICLEINFO_KEY);
            }

            if (StringUtils.isNotBlank(xDiamondConfig.getProperty(Constants.REGION_PLATFORMINFO_KEY))) {
                Constants.REGION_PLATFORMINFO = xDiamondConfig.getProperty(Constants.REGION_PLATFORMINFO_KEY);
            }

        } catch (Exception e) {
            logger.error(null, e);
        }

    }


    public XDiamondConfig getxDiamondConfig() {
        return xDiamondConfig;
    }

//    @Bean
//    @ConfigurationProperties(prefix = "xdiamond")
//    public XDiamondConfig diamondConfig() {
//        XDiamondConfig xDiamondConfig = new XDiamondConfig();
////        xDiamondConfig.init();
////        xDiamondConfig.addAllKeyListener(new AllKeyListener() {
////            public void onConfigEvent(ConfigEvent configevent) {
////                String key = configevent.getKey();
////                if(key.equals(COURIER_KILOMETRE_PER_DAY)) {
////                    courierKilometrePerDay = Integer.valueOf(configevent.getValue());
////                }
////            }
////        });
//        return xDiamondConfig;
//    }

//    public XDiamondConfig getxDiamondConfig() {
//        return xDiamondConfig;
//    }
}
