package com.dfssi.kafka;

import com.dfssi.resources.ConfigDetail;
import com.dfssi.resources.Resources;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Properties;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/11/1 10:12
 */
public class KafkaBaseConf extends ConfigDetail {
    private static final String _CONFIG = "/kafka/kafka-base";
    private static final Map<String, String> ENVCONF = Maps.newHashMap();

    private final Logger logger = Logger.getLogger(KafkaBaseConf.class);

    static {
        System.getProperties().forEach((keyObj, valueObj) -> {
            String key = keyObj.toString();
            String value = valueObj.toString();
            if(key.toLowerCase().startsWith("kafka.")){
                ENVCONF.put(key.substring(6), value);
            }
        });
    }

    KafkaBaseConf(boolean loadEnv){
        super();
        if(loadEnv) this.configMap.putAll(ENVCONF);
        try {
            Resources resources = new Resources(Resources.Env.NONE, _CONFIG);
            this.configMap.putAll(resources.getConfigMap());
        } catch (Exception e) {
            logger.error("读取kafka基础配置失败。", e);
        }

    }

    public Properties newBaseProperties(){
        Properties properties = new Properties();
        properties.putAll(this.configMap);
        return properties;
    }

}
