package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.common;

import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.constants.PropertiUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;


public class KafkaPro {
    private final static int MAX_VALUE = 3;
    private final static Logger LOGGER = LoggerFactory.getLogger(KafkaPro.class);

    private static Properties props_pro = new Properties();
    private static Properties props_con = new Properties();
    private static Producer producer =null;
    private static String broker64 =null;
    private KafkaPro() {

    }

    /**
     * 生产者，注意kafka生产者不能够从代码上生成主题，只有在服务器上用命令生成
     */
    static {

        try {
            String conf = "../../config/client.properties";
            File configurationFile = new File(conf);
            LOGGER.info("获得配置文件路径："+configurationFile.getCanonicalPath());
            System.setProperty("conf", configurationFile.getCanonicalPath());
        }  catch (Exception e) {
            e.printStackTrace();
        }
        try{
            broker64 = PropertiUtil.getStr("kafka.bootstrap.servers");
        }catch(Exception e){
            LOGGER.error("PropertiUtil由is_trans_new_energy_data获取配置文件中的值失败，请检查配置文件是否存在,异常信息:{}",e);
        }
        //服务器ip:端口号，集群用逗号分隔
        props_pro.put("bootstrap.servers", broker64);
        props_pro.put("acks", "all");
        props_pro.put("retries", MAX_VALUE);
        props_pro.put("batch.size", 16384);
        props_pro.put("linger.ms", 1);
        props_pro.put("buffer.memory", 33554432);
        props_pro.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props_pro.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props_pro.put("max.in.flight.requests.per.connection",1);
        props_pro.put("unclean.leader.election.enable",false);
        props_pro.put("replication.factor",3);
        props_pro.put("min.insync.replicas",2);
        props_pro.put("replication.factor",3);

        producer=getKafkaProducer();
        flushData();

    }

    /**
     * 消费者
     */
    static {
        //服务器ip:端口号，集群用逗号分隔
        props_con.put("bootstrap.servers", broker64);
        props_con.put("group.id", "road");
        //props_con.put("group.id", Config.GROUP_ID);
        //初始化消费offset位置
        props_con.put("auto.offset.reset", "earliest");
        props_con.put("enable.auto.commit", true);
        props_con.put("auto.commit.interval.ms", "1000");
        props_con.put("session.timeout.ms", "30000");
        props_con.put("max.poll.records", "1800");
        props_con.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props_con.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
       //props_con.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");



    }



    /**
     * 发送对象消息 至kafka上,调用json转化为json字符串，应为kafka存储的是String。
     * @param msg
     */
    public static void sendMsgToKafka(String topic,String msg) {
        ProducerRecord record = new ProducerRecord<String, String>(topic, String.valueOf(System.currentTimeMillis()),
                msg);
        Future<RecordMetadata> future = producer.send(record,new SendCallback(record,0));
    }
    public static void sendMsgToKafka(KafkaProducer producer,String topic,String msg) {
        ProducerRecord record = new ProducerRecord<String, String>(topic, String.valueOf(System.currentTimeMillis()),
                msg);
        Future<RecordMetadata> future = producer.send(record,new SendCallback(producer,record,0));
        //System.out.println(JSON.toJSONString(future));
    }





    public static Consumer<String, String> getKafkaConsumer(String topic) {
        KafkaConsumer consumer = new KafkaConsumer<String,String>(props_con);
        consumer.subscribe(Arrays.asList(topic));

        return consumer;
    }
    public static Consumer<String, String> getKafkaConsumer(String topic,String groupid) {
        props_con.put("group.id", groupid);
        KafkaConsumer consumer = new KafkaConsumer<String,String>(props_con);
        consumer.subscribe(Arrays.asList(topic));

        return consumer;
    }

    public static  Consumer getBeginConsumer(String topic,String groupid){
        props_con.put("group.id", groupid);

        KafkaConsumer consumer = new KafkaConsumer<String,String>(props_con);

        TopicPartition partition0 = new TopicPartition(topic, 0);

        TopicPartition partition1 = new TopicPartition(topic, 1);
        TopicPartition partition2 = new TopicPartition(topic, 2);

        List<TopicPartition> set =new ArrayList<>();

        set.add(partition0);
        set.add(partition1);
        set.add(partition2);

        consumer.assign( set);

        consumer.seekToBeginning(set.toArray(new TopicPartition[set.size()]));

        return consumer;
    }

    public static KafkaProducer getKafkaProducer(){
        KafkaProducer producer = new KafkaProducer(props_pro);
        return producer;
    }
    public static void flushData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000L);
                    producer.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * producer回调
     */
    static class SendCallback implements Callback {
        KafkaProducer producer;
        ProducerRecord<String, String> record;
        int sendSeq = 0;

        public SendCallback(ProducerRecord record, int sendSeq) {
            this.record = record;
            this.sendSeq = sendSeq;
        }
        public SendCallback(KafkaProducer producer,ProducerRecord record, int sendSeq) {
            this.producer = producer;
            this.record = record;
            this.sendSeq = sendSeq;
        }
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            //send success
            if (null == e) {
                String meta = "topic:" + recordMetadata.topic() + ", partition:"
                        + recordMetadata.topic() + ", offset:" + recordMetadata.offset();
                //LOGGER.info("send message success, record:" + record.toString() + ", meta:" + meta);
                return;
            }
            //send failed
                 LOGGER.error("send message failed, seq:" + sendSeq + ", record:" + record.toString() + ", errmsg:" + e.getMessage());
            if (sendSeq < 1) {
                producer.send(record, new SendCallback(record, ++sendSeq));
            }
        }
    }




}
