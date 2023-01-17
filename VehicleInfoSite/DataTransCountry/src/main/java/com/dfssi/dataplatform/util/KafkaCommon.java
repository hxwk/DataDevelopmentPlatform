package com.dfssi.dataplatform.util;


import kafka.admin.*;
import kafka.admin.AdminUtils;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;
import kafka.server.ConfigType;
import kafka.utils.ZkUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.security.JaasUtils;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaCommon {
    private static Logger logger = LoggerFactory.getLogger(KafkaCommon.class);

    Producer producer;
    /**
     * @Description: 生产者
     */
    public  void createProducer(String address) {
        Properties properties = new Properties();
        //properties.put("zookeeper.connect", "172.16.1.210:2181");//声明zk
        properties.put("serializer.class", StringEncoder.class.getName());
        //properties.put("metadata.broker.list", "172.16.1.121:9092");// 声明kafka broker
        properties.put("metadata.broker.list", address);// 声明kafka broker

        producer = new Producer<Integer, String>(new ProducerConfig(properties));
        /*for (int i=0;i<1000;i++){
            producer.send(new KeyedMessage<Integer, String>("zhx9", "模拟中文发送消息: " + i));
            System.out.println("向zhx9发送数据"+i);
        }*/
    }

    //kafkaAddress支持用,分隔的ip+端口地址   其中只要有一个可以连接上就不报错  如果全部都无法连接就报错
    public  void sendMessageToKafka(String topic,String msg){
        /*for (int i=0;i<1000;i++){
            producer.send(new KeyedMessage<Integer, String>("zhx9", "模拟中文发送消息: " + i));
            System.out.println("向zhx9发送数据"+i);
        }*/
        logger.info(" 向kafka发送数据："+msg);
        producer.send(new KeyedMessage<Integer, String>(topic, msg));
    }


    // 创建主题
    public static ResultVo createTopic(String zkConnect,String topic,int partitions,int replicationFactor) {

        ResultVo rs = new ResultVo();
        int sessionTimeOut = 3000;
        int connectionTimeOut = 3000;
        Properties properties = new Properties();
        ZkUtils zkUtils = null;
        try {
            zkUtils =  ZkUtils.apply(zkConnect, sessionTimeOut, connectionTimeOut, JaasUtils.isZkSecurityEnabled());
        }catch(Exception e){
            e.printStackTrace();
            rs.setErrorNo(-1);
            rs.setErrorMsg("创建kafkaTopic失败,请检查kafka能否连接成功！");
            return rs;
        }
        try {
            AdminUtils.createTopic(zkUtils, topic, partitions, replicationFactor, properties);
        }catch(Exception e){
            e.printStackTrace();
            rs.setErrorNo(-1);
            rs.setErrorMsg("创建kafkaTopic失败,请检查kafkaTopic是否已存在！");
            return rs;
        }finally {
            if(zkUtils != null){
                zkUtils.close();
            }
        }
        rs.setErrorNo(0);
        rs.setErrorMsg("创建kafkaTopic失败成功！");
        return rs;
    }


    // 查询topic   zkAddress中只有存在一个zk节点无法连上的情况即报错
    //查询成功返回0  不成功返回-1
    public  int queryTopic(String zkConnect,String topic) {
        ResultVo rs = new ResultVo();
        int sessionTimeOut = 3000;
        int connectionTimeOut = 3000;
        Properties properties = new Properties();
        try {
            ZkUtils zkUtils =  ZkUtils.apply(zkConnect, sessionTimeOut, connectionTimeOut, JaasUtils.isZkSecurityEnabled());
            Properties props = AdminUtils.fetchEntityConfig(zkUtils, ConfigType.Topic(), topic);
            zkUtils.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("连接测试topic失败，请检查zkAddress:"+zkConnect,e);
            return -1;
        }
        logger.info("连接测试topic成功");
        return 0;
    }

    public static void main(String[] args) throws  Exception{
        KafkaCommon test1 = new KafkaCommon();
        test1.createProducer("172.16.1.121:9092,172.16.1.122:9092,172.16.1.123:9092");
        test1.sendMessageToKafka("zhx9","vcccccc");
        //查看topic是否存在，如果不存在则创建一个topic
        /*int i = test1.queryTopic("172.16.1.210:2181,172.16.1.211:2181,172.16.1.212:2181","zhx8");
        System.out.println(i);*/
    }


}
