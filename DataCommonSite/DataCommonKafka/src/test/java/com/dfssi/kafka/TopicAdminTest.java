package com.dfssi.kafka;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/15 17:23
 */
public class TopicAdminTest {
    private final Logger logger = LogManager.getLogger(TopicAdminTest.class);

    private TopicAdmin admin;

    @Before
    public void init(){
        Map<String, Object> confs = Maps.newHashMap();
        confs.put("zk.connect", "172.16.1.210:2181,172.16.1.211:2181,172.16.1.212:2181");
        confs.put("zookeeper.session.timeout.ms", "12000");
        confs.put("zookeeper.connection.timeout.ms", "12000");
        this.admin = KafkaContext.get().newTopicAdmin(confs);
    }

    @Test
    public void testCreateTopic(){
        admin.createTopic("demo001", 4, 2);
    }


    @Test
    public void testDeleteTopic(){
        admin.deleteTopic("demo001");
    }
}
