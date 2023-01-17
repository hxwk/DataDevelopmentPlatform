package com.dfssi.dataplatform.datasync.common.common;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * 读取表对应的存储es集群的配置文件初始化相关配置
 */
public class FastDFSConfigReader {

    private static Logger LOG = Logger.getLogger(FastDFSConfigReader.class);

    private static final String CONFIG_FILE = "fastdfs-client.properties";

    private static final String CUSTOM_CONFIG_FILE = "/usr/conf/common/fastdfs-client.properties";

    private CompositeConfiguration config = new CompositeConfiguration();

    private static FastDFSConfigReader instance = null;


    private FastDFSConfigReader() {
        readConfig();
    }

    public static FastDFSConfigReader getInstance() {
        if (null == instance) {
            instance = new FastDFSConfigReader();
        }

        return instance;
    }

    private void readConfig() {
        String defaultFilename = CONFIG_FILE;

        try {
            config.addConfiguration(new PropertiesConfiguration(defaultFilename));
            LOG.info(" 读取app配置使用默认配置文件： " + defaultFilename);

        } catch (Exception e) {
            LOG.error(null, e);
        }
    }

    public CompositeConfiguration getConfig() {
        return config;
    }
}
