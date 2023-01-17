package com.dfssi.dataplatform.quartz.sync;

import com.dfssi.common.WebHDFSUtil;
import com.google.common.collect.Maps;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/6/22 16:21
 */
public class EnvironmentConfig {
    private final Logger logger = LoggerFactory.getLogger(EnvironmentConfig.class);

    private final String CONFIG_FILE = "/user/hdfs/config/env/environment.xml";

    private Map<String, Map<String, String>> environments = Maps.newHashMap();

    public EnvironmentConfig(String nameNodeHost, int nameNodeWebhdfsPort) throws Exception {

        String file = WebHDFSUtil.openfile(nameNodeHost, nameNodeWebhdfsPort, CONFIG_FILE);
        StringReader stringReader = new StringReader(file);

        Element rootElement = new SAXReader().read(stringReader).getRootElement();
        init(rootElement);

        logger.info(String.format("集群环境配置参数如下：\n\t %s", environments));
    }

    private void init(Element element){
        List<Element> envs = element.selectNodes("environment");
        List<Element> params;
        Map<String, String> configMap;
        for(Element env : envs){
            params = env.selectNodes("params/param");
            if(params != null){
                configMap = Maps.newHashMap();
                for(Element param : params){
                    configMap.put(param.attributeValue("name"),
                            param.attributeValue("value"));
                }
                environments.put(env.attributeValue("id"), configMap);
            }
        }
    }


    public Map<String, String> getEnvironment(String id){
        Map<String, String> environment = environments.get(id);
        if(environment == null) environment = Maps.newHashMap();
        return environment;
    }

    public static void main(String[] args) throws Exception {
        EnvironmentConfig environmentConfig = new EnvironmentConfig("devmaster", 50070);
    }

}
