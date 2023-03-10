package com.dfssi.kafka;


import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kafka.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Description:
 *  读取配置 以及创建  Producer 和  ConsumerConnector
 * @author LiXiaoCong
 * @version 2017/6/8 13:16
 */
public class KafkaContext {

    private static volatile KafkaContext kafkaContext;
    private KafkaBaseConf kafkaBaseConf;

    //private TopicAdmin topicAdmin;

    private KafkaContext(boolean loadEnv) {
        this.kafkaBaseConf = new KafkaBaseConf(loadEnv);
        //this.topicAdmin = new TopicAdmin(prop);
    }

    public static KafkaContext get(){
        return get(true);
    }

    public static KafkaContext get(boolean loadEnv) {
        if(kafkaContext == null){
            synchronized (KafkaContext.class){
                if(kafkaContext == null){
                    kafkaContext = new KafkaContext(loadEnv);
                }
            }
        }
        return kafkaContext;
    }

    /**
     *
     *  Map<String, Object> confs = Maps.newHashMap();
        confs.put("zk.connect", "172.16.1.210:2181,172.16.1.211:2181,172.16.1.212:2181");
        confs.put("zookeeper.session.timeout.ms", "12000");
        confs.put("zookeeper.connection.timeout.ms", "12000");

     zk.connect 为必填项
     *
     * @param confs
     * @return
     */
    public TopicAdmin newTopicAdmin(Map<String, Object> confs){
        Preconditions.checkNotNull(confs, "confs 配置不能为null");
        Properties properties = new Properties();
        properties.putAll(confs);
        return new TopicAdmin(properties);

    }

    public <K,V> KafkaProducer<K, V> newProducer(Map<String, Object> confs){
        return newProducer(confs, null, null);
    }

    public <K,V> KafkaProducer<K, V> newProducer(Map<String, Object> confs,
                                                 Serializer<K> keySerializer,
                                                 Serializer<V> valueSerializer){
        Properties properties = kafkaBaseConf.newBaseProperties();
        if(confs != null)properties.putAll(confs);
        return new KafkaProducer<>(properties, keySerializer, valueSerializer);
    }


    public <K, V> KafkaConsumer<K, V> newConsumer(Map<String, Object> confs){
        return newConsumer(confs, null, null);
    }

    public <K, V> KafkaConsumer<K, V> newConsumer(Map<String, Object> confs,
                                                  Deserializer<K> keyDeserializer,
                                                  Deserializer<V> valueDeserializer){
        Properties properties = kafkaBaseConf.newBaseProperties();
        if(confs != null)properties.putAll(confs);
        return new KafkaConsumer<>(properties, keyDeserializer, valueDeserializer);
    }

    public AdminClient newAdminClient(Map<String, Object> confs){
        Properties properties = kafkaBaseConf.newBaseProperties();
        if(confs != null)properties.putAll(confs);
        return AdminClient.create(properties);
    }


    public static void close(Producer producer){
        if(producer != null)producer.close();
    }

    public static void close(Consumer consumer){
        if(consumer != null)consumer.close();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        KafkaContext kafkaContext = KafkaContext.get();
        HashMap<String, Object> confs = Maps.newHashMap();
        confs.put("group.id", "test");

        AdminClient adminClient = kafkaContext.newAdminClient(confs);

        KafkaConsumer<String, String> consumer = kafkaContext.newConsumer(confs);
        consumer.subscribe(Lists.newArrayList("streams-wordcount-processor-output"));
        ConsumerRecords<String, String> poll;
        while ((poll = consumer.poll(1000)) != null){
            poll.forEach(record ->{
                System.out.println("key = " + record.key() + ", value = " + record.value());
            });
        }

    }
}
