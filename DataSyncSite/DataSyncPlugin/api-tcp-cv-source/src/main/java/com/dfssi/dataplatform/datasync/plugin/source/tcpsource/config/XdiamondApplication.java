package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.config;

import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.Constants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.PropertiesFileUtil;
import io.github.xdiamond.client.XDiamondConfig;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 读取Xdiamond配置
 */
public class XdiamondApplication {

    private static Logger logger = Logger.getLogger(XdiamondApplication.class);

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
        xDiamondConfig.addAllKeyListener(configevent-> {
                String key = configevent.getKey();
                String value = configevent.getValue();
                try {
                    if(key.equals(Constants.TEST_KEY)) {
                        Constants.TEST_VALUE = value;
                        logger.info("Constants.TEST_VALUE 修改为 " + Constants.TEST_VALUE);
                    }else if (Constants.POSITIONINFORMATION_TOPIC_KEY.equalsIgnoreCase(key)) {
                        Constants.POSITIONINFORMATION_TOPIC = value;
                        logger.info("Constants.POSITIONINFORMATION_TOPIC 修改为 " + Constants.POSITIONINFORMATION_TOPIC);
                    } else if (Constants.POSITIONINFORMATIONS_TOPIC_KEY.equalsIgnoreCase(key)) {
                        Constants.POSITIONINFORMATIONS_TOPIC = value;
                        logger.info("Constants.POSITIONINFORMATIONS_TOPIC 修改为 " + Constants.POSITIONINFORMATIONS_TOPIC);
                    } else if (Constants.CANINFORMATION_TOPIC_KEY.equalsIgnoreCase(key)) {
                        Constants.CANINFORMATION_TOPIC = value;
                        logger.info("Constants.CANINFORMATION_TOPIC 修改为 " + Constants.CANINFORMATION_TOPIC);
                    }else if(Constants.GPSANDCANINFORMATIC_TOPIC.equalsIgnoreCase(key)){
                        Constants.GPSANDCANINFORMATIC_TOPIC = value;
                        logger.info("Constants.GPSANDCANINFORMATIC_TOPIC 修改为 "+Constants.GPSANDCANINFORMATIC_TOPIC);
                    } else if (Constants.DRIVERCARDINFORMATION_TOPIC_KEY.equalsIgnoreCase(key)) {
                        Constants.DRIVERCARDINFORMATION_TOPIC = value;
                        logger.info("Constants.DRIVERCARDINFORMATION_TOPIC 修改为 " + Constants.DRIVERCARDINFORMATION_TOPIC);
                    }else if (Constants.REGION_CVVEHICLEINFO_KEY.equalsIgnoreCase(key)) {
                        Constants.REGION_CVVEHICLEINFO = value;
                        logger.info("Constants.REGION_CVVEHICLEINFO 修改为 " + Constants.REGION_CVVEHICLEINFO);
                    }
                } catch (Exception e) {
                    logger.error(null, e);
                }
        });

        try {
            if (StringUtils.isNotBlank(xDiamondConfig.getProperty(Constants.POSITIONINFORMATION_TOPIC_KEY))) {
                Constants.POSITIONINFORMATION_TOPIC = xDiamondConfig.getProperty(Constants.POSITIONINFORMATION_TOPIC_KEY);
            }

            if (StringUtils.isNotBlank(xDiamondConfig.getProperty(Constants.POSITIONINFORMATIONS_TOPIC_KEY))) {
                Constants.POSITIONINFORMATIONS_TOPIC = xDiamondConfig.getProperty(Constants.POSITIONINFORMATIONS_TOPIC_KEY);
            }

            if (StringUtils.isNotBlank(xDiamondConfig.getProperty(Constants.CANINFORMATION_TOPIC_KEY))) {
                Constants.CANINFORMATION_TOPIC = xDiamondConfig.getProperty(Constants.CANINFORMATION_TOPIC_KEY);
            }

            if (StringUtils.isNotBlank(xDiamondConfig.getProperty(Constants.DRIVERCARDINFORMATION_TOPIC_KEY))) {
                Constants.DRIVERCARDINFORMATION_TOPIC = xDiamondConfig.getProperty(Constants.DRIVERCARDINFORMATION_TOPIC_KEY);
            }

            if (StringUtils.isNotBlank(xDiamondConfig.getProperty(Constants.GEODE_CONNECT_KEY))) {
                Constants.GEODE_CONNECT = xDiamondConfig.getProperty(Constants.GEODE_CONNECT_KEY);
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
