package com.dfssi.dataplatform.datasync.plugin.sink.hbase.conf;

import com.dfssi.dataplatform.datasync.flume.agent.Context;
import com.dfssi.dataplatform.datasync.plugin.sink.hbase.HBase2SinkConfigurationConstants;
import com.google.common.base.Charsets;
import org.apache.commons.lang.StringUtils;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HbaseConfiguration {
    private static HashMap<String,EventTopicConfig> topicConfigMap = new HashMap<>();
    private byte[] columnFamily;
    private String zookeeperQuorum ;
    private String batchSize;
    private int flushSize=6;
    private List<EventTopicConfig> topicList;

    public static EventTopicConfig getTopicConfig(String topic){
        return topicConfigMap.get(topic);
    }

    public HbaseConfiguration(){

    }
    public HbaseConfiguration(Context context){
        String cf = context.getString(
                HBase2SinkConfigurationConstants.CONFIG_COLUMN_FAMILY);
        if(StringUtils.isBlank(cf)){
            cf= context.getString(
                    ParameterConstant.SERICES,"f");
        }
        columnFamily = cf.getBytes(Charsets.UTF_8);
        //String quorum =
        throw  new InvalidParameterException();
    }
    public String getZookeeperQuorum() {
        return zookeeperQuorum;
    }

    public void setZookeeperQuorum(String zookeeperQuorum) {
        this.zookeeperQuorum = zookeeperQuorum;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
    }

    public int getFlushSize() {
        return flushSize;
    }

    public void setFlushSize(int flushSize) {
        this.flushSize = flushSize;
    }

    public List<EventTopicConfig> getTopicList() {
        return topicList;
    }

    public void setTopicList(List<EventTopicConfig> topicList) {
        this.topicList = topicList;
    }
}
