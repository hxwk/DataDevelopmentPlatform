package com.dfssi.dataplatform.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/10/7 16:04
 */
public class PropertiesUtil {
    public static final String CONSUMER_SYNC_PROPERTIES = "conf.properties";
    public static Properties getProperties(String propertiesPath) {
        final Properties properties = new Properties();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
            try (final InputStream stream = classloader.getResourceAsStream(propertiesPath)) {
                properties.load(stream);
            }
            if (!properties.isEmpty())
                return properties;
            else
                throw new Exception("Properties from " + propertiesPath + " are empty.");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
