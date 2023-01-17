package com.dfssi.dataplatform.plugin.tcpnesource.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 属性文件加载工具类
 * Created by yanghs on 2018/10/11.
 */
public class PropertiesFileUtil {

    private static final String PROPERTIES_CONFIG_FILE = "com.dfssi.dataplatform.plugin.tcpnesource/tcpnesource.properties";
    private static PropertiesConfiguration propertiesConfiguration = null;

    static {
        try {
            propertiesConfiguration = new PropertiesConfiguration(PROPERTIES_CONFIG_FILE);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static PropertiesConfiguration instanceConfig(){
        return propertiesConfiguration;
    }
}
