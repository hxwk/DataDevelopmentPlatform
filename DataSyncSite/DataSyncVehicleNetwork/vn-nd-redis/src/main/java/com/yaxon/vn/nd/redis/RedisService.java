package com.yaxon.vn.nd.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Author: 程行荣
 * Time: 2014-04-08 17:25
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

@Service
public class RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    private static String PUSH_MSG_FOR_VEHICLE = "pmsg4v:";
    private static String PUSH_MSG_FOR_USER = "pmsg4u:";
    private static String PUSH_MSG_FOR_CHANNEL = "pmsg4c:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JsonRedisSerializer jsonRedisSerializer;

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public JsonRedisSerializer getJsonRedisSerializer() {
        return jsonRedisSerializer;
    }

    public void pushMsgForVehicle(String msgId, long vid, Object msg) throws Exception {
        Map<String, Object> pmsg = new HashMap<>();
        pmsg.put("id", msgId);
        pmsg.put("src", "v");
        pmsg.put("msg", msg);
        String destMsg = jsonRedisSerializer.seriazileAsString(pmsg);
        redisTemplate.convertAndSend(RedisConstants.PUSH_MSG_FOR_VEHICLE + vid, destMsg);
    }

    public void pushMsgForUser(String msgId, long uid, Object msg) throws Exception {
        Map<String, Object> pmsg = new HashMap<>();
        pmsg.put("id", msgId);
        pmsg.put("src", "u");
        pmsg.put("msg", msg);
        String destMsg = jsonRedisSerializer.seriazileAsString(pmsg);
        redisTemplate.convertAndSend(RedisConstants.PUSH_MSG_FOR_USER + uid, destMsg);
    }
    public void pushMsgForChannel(String msgId, String channelId, Object msg) throws Exception {
        Map<String, Object> pmsg = new HashMap<>();
        pmsg.put("id", msgId);
        pmsg.put("src", "u");
        pmsg.put("msg", msg);
        String destMsg = jsonRedisSerializer.seriazileAsString(pmsg);
        redisTemplate.convertAndSend(RedisConstants.PUSH_MSG_FOR_CHANNEL + channelId, destMsg);
    }
    public void postMsg(String topic, Object msg) {
        redisTemplate.convertAndSend(topic, jsonRedisSerializer.seriazileAsString(msg));
    }
    //当部署了多个相同模块，但是消息又需要单点执行的时候，用redis的队列实现生产者消费者模式
    //消息生产者产生消息，从左边放进队列
    public void leftPush(String K,Object V){
        redisTemplate.opsForList().leftPush(K,jsonRedisSerializer.seriazileAsString(V));
    }

    //消息消费者 从右边消费消息
    public String rightLockPop(String K, Long T, TimeUnit timeUnit){
        return redisTemplate.opsForList().rightPop(K,T,timeUnit);
    }
}
