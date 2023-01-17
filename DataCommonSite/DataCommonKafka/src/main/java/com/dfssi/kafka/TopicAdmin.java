package com.dfssi.kafka;

import com.google.common.base.Preconditions;
import kafka.admin.AdminUtils;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;

import java.util.Iterator;
import java.util.Properties;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/6/21 11:28
 */
public class TopicAdmin {

    private Properties conf;
    private String zkConnect;

    private int sessionTimeOut;
    private int connectionTimeOut;

    public TopicAdmin(Properties conf){
        this.conf = conf;
        this.zkConnect = conf.getProperty("zk.connect");
        Preconditions.checkNotNull(zkConnect, "zk.connect 配置不能为空");

        String sessionTimeOut = conf.getProperty("zookeeper.session.timeout.ms", "6000");
        String connectionTimeOut = conf.getProperty("zookeeper.connection.timeout.ms", "6000");

        this.sessionTimeOut =  Integer.parseInt(sessionTimeOut);
        this.connectionTimeOut = Integer.parseInt(connectionTimeOut);
    }

    public void createTopic(String topic){
        String partitions = conf.getProperty("num.partitions", "1");
        String rep = conf.getProperty("default.replication.factor", "2");
        createTopic(topic, Integer.parseInt(partitions), Integer.parseInt(rep));
    }

    public void createTopic(String topic, int partitions, int replicationFactor){
        Properties properties = new Properties();
        //properties.putAll(conf);
        //properties.remove("num.partitions");
        //properties.remove("zk.connect");
        //properties.remove("default.replication.factor");
        createTopic(topic, partitions, replicationFactor, properties);
    }

    public void createTopic(String topic, int partitions, int replicationFactor, Properties topicConfig){

        ZkUtils zkUtils = getZkUtils();
        try {
            AdminUtils.createTopic(zkUtils, topic, partitions, replicationFactor, topicConfig);
        }finally {
            close(zkUtils);
        }
    }

    /**
     * 如果 没有配置 delete.topic.enable=true，那么只是标记删除
     * @param topic
     */
    public void deleteTopic(String topic){
        ZkUtils zkUtils = getZkUtils();
        try {
            AdminUtils.deleteTopic(zkUtils, topic);
        }finally {
            close(zkUtils);
        }
    }

    public void modifyTopic(String topic, Properties topicConfig, Iterable<String> conf2delete){
        ZkUtils zkUtils = getZkUtils();
        try {
            Properties props = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic(), topic);
            props.putAll(topicConfig);

            if(conf2delete != null){
                Iterator<String> iterator = conf2delete.iterator();
                while (iterator.hasNext()){
                    props.remove(iterator.next());
                }
            }
            AdminUtils.changeTopicConfig(zkUtils, topic, props);
        }finally {
            close(zkUtils);
        }
    }

    private ZkUtils getZkUtils(){
        return ZkUtils.apply(zkConnect, sessionTimeOut, connectionTimeOut, JaasUtils.isZkSecurityEnabled());
    }

    private void close(ZkUtils zkUtils){
        if(zkUtils != null)zkUtils.close();
    }




}
