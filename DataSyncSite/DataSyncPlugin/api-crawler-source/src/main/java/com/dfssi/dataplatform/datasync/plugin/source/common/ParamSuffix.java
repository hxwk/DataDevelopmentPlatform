package com.dfssi.dataplatform.datasync.plugin.source.common;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/**
 * url list district code as url param suffix
 * @author jianKang
 * @date 2017/11/8
 */
public class ParamSuffix {
    static final Logger logger = LoggerFactory.getLogger(ParamSuffix.class);

    private static List<String> urls = Lists.newArrayList();
    private final InputStream configurationPath;


    public List<String> getUrls() {
        return urls;
    }

    public ParamSuffix(){
        configurationPath = ParamSuffix.class.getClassLoader().getResourceAsStream("weaRule.properties");
        Properties prop = new Properties();
        try {
            prop.load(configurationPath);
            String urlPrefix = prop.getProperty("urlPrefix");
            String cityCodes = prop.getProperty("cityCodes");
            String[] cityCodeArr = cityCodes.split(",");
            logger.info("params load over .......");
            for(String cityCode:cityCodeArr){
                urls.add(urlPrefix+cityCode);
            }

        } catch (IOException e) {
            logger.error("ParamSuffix load properties error, please check.",e.getMessage());
        }
    }
}
