package com.dfssi.kafka;

import com.dfssi.kafka.serialization.JsonDeserializer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/15 16:48
 */
public class KafkaConsumerTest {
    private final Logger logger = LogManager.getLogger(KafkaConsumerTest.class);

    private KafkaConsumer<String, Map<String, Object>> consumer;

    @Before
    public void init(){
        Map<String, Object> confs = Maps.newHashMap();
        confs.put("group.id", "test808-consumer01");
        this.consumer = KafkaContext.get().newConsumer(confs, new StringDeserializer(), new JsonDeserializer());
    }

    @Test
    public void testConsumer(){


        try {
            consumer.subscribe(Lists.newArrayList("test808"));
            ConsumerRecords<String, Map<String, Object>> records = consumer.poll(500);
            System.out.println(records.count());

            records.iterator().forEachRemaining(record ->{
                //System.out.println(record.key() + " : " + record.value());
                Map<String, Object> value = record.value();
                System.out.println(value.keySet());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
