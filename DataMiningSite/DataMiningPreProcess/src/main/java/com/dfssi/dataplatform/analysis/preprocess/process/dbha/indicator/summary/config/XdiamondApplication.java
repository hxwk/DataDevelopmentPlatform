package com.dfssi.dataplatform.analysis.preprocess.process.dbha.indicator.summary.config;

import io.github.xdiamond.client.XDiamondConfig;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * 读取Xdiamond配置
 */
public class XdiamondApplication {

    private static Logger logger = Logger.getLogger(XdiamondApplication.class);

    private XDiamondConfig xDiamondConfig;

    private CompositeConfiguration app_config = new CompositeConfiguration();

    private static final String CONFIG_FILE = "xdiamond.properties";

    private static XdiamondApplication instance = null;

    private XdiamondApplication() {
        readConfig();

        init();
    }

    public static XdiamondApplication getInstance() {
        if (null == instance) {
            instance = new XdiamondApplication();
        }

        return instance;
    }

    private void readConfig() {

        try {
//            InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream("xdiamond.properties");
            app_config.addConfiguration(new PropertiesConfiguration(CONFIG_FILE));
            logger.info(" 读取xdiamond配置文件： " + CONFIG_FILE);

        } catch (Exception e) {
            logger.error(null, e);
        }
    }

    public void init() {
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

        try {
        } catch (Exception e) {
            logger.error(null, e);
        }

    }

    public CompositeConfiguration getConfig() {
        return app_config;
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
