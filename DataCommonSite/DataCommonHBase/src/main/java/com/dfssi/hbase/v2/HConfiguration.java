package com.dfssi.hbase.v2;

import com.dfssi.resources.Resources;
import com.google.common.base.Preconditions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import java.io.Serializable;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/5/3 10:28
 */
public class HConfiguration implements Serializable{

    private static final String HBASESERVERCONFIG = "/hbase/hbase-server-config";
    private final Resources configs;

    private String logPath;
    private boolean needInitHbaseConn;
    private String zkPort;
    private String zkHosts;

    private Map<String, String> hbaseConfigMap;

    private transient Configuration configuration;

    public HConfiguration(Resources.Env env) throws Exception {
        this.configs = new Resources(env, HBASESERVERCONFIG);

        this.logPath = configs.getConfigItemValue("log.path", "/var/log/hbase");

        this.needInitHbaseConn = configs.getConfigItemBoolean("need.init.hbase", true);
        if(needInitHbaseConn) {

            this.zkPort = configs.getConfigItemValue("hbase.zookeeper.property.clientPort","2181");
            this.zkHosts = configs.getConfigItemValue("hbase.zookeeper.quorum", null);
            Preconditions.checkNotNull(zkHosts, "hbase.zookeeper.quorum的配置不能为空。");

            this.hbaseConfigMap = configs.getConfigMapWithPrefix("hbase.");
            this.hbaseConfigMap.remove("hbase.zookeeper.property.clientPort");
            this.hbaseConfigMap.remove("hbase.zookeeper.quorum");

            getConfiguration();
        }
    }

    public synchronized Configuration getConfiguration(){

        if(configuration == null && needInitHbaseConn){
            this.configuration = HBaseConfiguration.create();
            configuration.set("hbase.zookeeper.quorum", zkHosts);
            configuration.set("hbase.zookeeper.property.clientPort", zkPort);
            for(Map.Entry<String, String> entry : hbaseConfigMap.entrySet()){
                configuration.set(entry.getKey(), entry.getValue());
            }
        }
        return configuration;
    }

    public Resources getResources() {
        return configs;
    }

    public String getLogPath() {
        return logPath;
    }

    public boolean isNeedInitHbaseConn() {
        return needInitHbaseConn;
    }
}
