package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.RedisPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Map;

/**
 *
 * Created by yanghs on 2018/5/23.
 */
public class RedisUtils {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    private static final int EXPIRETIME=30 * 24 * 60  * 60;//数据失效时间一个月

    private static final String PREFIX_COMMAND="command:";//指令下发执行结果数据标志

    private static Jedis jedis = null;
    /**
     * 指令执行结果存redis
     * @param result
     */
    public static void commandStatus2Redis(Map result) {

        try {
            String key= PREFIX_COMMAND+result.get("vid")+":"+result.get("msgId")+":"+result.get("datetime") ;
            jedis = RedisPoolManager.getJedis();
            jedis.set(key, JSONObject.toJSONString(result));//存储数据
            jedis.expire(key, EXPIRETIME);
        } catch (Exception e) {
            logger.error("指令下发执行结果存储失败", e);
        } finally {
            if (null != jedis) {
                RedisPoolManager.returnResource(jedis);
            }
        }
    }

//    public static void main(String[] args) {
//        Map  map=new HashMap();
//        map.put("vid","vid123123213");
//        map.put("msgId","jts.0001");
//        map.put("instruction","123");
//        map.put("status","success");
//        map.put("datetime", Calendar.getInstance().getTimeInMillis());
//        commandStatus2Redis(map);
//        System.out.print("执行完成");
//    }
}
