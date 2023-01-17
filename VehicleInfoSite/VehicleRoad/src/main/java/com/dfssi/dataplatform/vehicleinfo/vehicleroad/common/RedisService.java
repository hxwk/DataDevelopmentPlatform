package com.dfssi.dataplatform.vehicleinfo.vehicleroad.common;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Repository
public class RedisService {

    public Logger logger = LoggerFactory.getLogger(RedisService.class);

	@Autowired
	private RedisTemplate redisTemplate;  //redisTemplate默认采用的是JDK的序列化策略，保存的key和value都是采用此策略序列化保存的

    @Autowired
    private StringRedisTemplate stringRedisTemplate;//StringRedisTemplate默认采用的是String的序列化策略，保存的key和value都是采用此策略序列化保存的

    /**
     * 保存数据
     * @param key
     * @param obj
     */
    public  void setValue(String key,Object obj){
    	ValueOperations<String, Object> ops =redisTemplate.opsForValue();
    	ops.set(key, obj);
    }

    /**
     * 根据key查询value
     * @param key
     * @return
     */
    public Object getValue(String key){
    	ValueOperations<String, Object> ops =redisTemplate.opsForValue();
        return ops.get(key);
    }



    /**
     * 根据key删除value
     * @param key
     * @return
     */
    public boolean removeValue(String key){
        return redisTemplate.delete(key);
    }

    /**
     * 保存字符串数据
     * @param key
     * @param value
     */
    public  void setStringValue(String key,String value){
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 根据key查询字符串value
     * @param key
     */
    public  String getStringValue(String key){
        String value =  stringRedisTemplate.opsForValue().get(key);
        return value;
    }



    public String getRedisValue(String key,int retryTimes){
        //设置超时时间
        retryTimes--;
        logger.info("开始从redis里取数据，key："+key+",剩余尝试次数:"+retryTimes);
        String jsonValue = getStringValue(key);
        if (null == jsonValue){
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(retryTimes <= 0){
                return "nothing";
            }
            return getRedisValue(key,retryTimes);
        }else{
            return jsonValue;
        }
    }
    public String getRedisValue(String key,String field,int retryTimes){
        //设置超时时间
        retryTimes--;
        logger.info("开始从redis里取数据，key："+key+",剩余尝试次数:"+retryTimes);
        String jsonValue = hgetValue(key,field);
        if (null == jsonValue){
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(retryTimes <= 0){
                return "nothing";
            }
            return getRedisValue(key,field,retryTimes);
        }else{
            return jsonValue;
        }
    }

    public String getRedisValue(String key,String field,int retryTimes,String... eq){
        //设置超时时间
        retryTimes--;
        logger.info("开始从redis里取数据，key："+key+",剩余尝试次数:"+retryTimes);
        String jsonValue = hgetValue(key,field);
        if (null == jsonValue|| !Arrays.asList(eq).contains(jsonValue)){
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(retryTimes <= 0){
                return StringUtils.isBlank(jsonValue)?"nothing":jsonValue;
            }
            return getRedisValue(key,field,retryTimes,eq);
        }else{
            return jsonValue;
        }
    }

    /**
     * 根据key删除字符串value
     * @param key
     * @return
     */
    public boolean removeStringValue(String key){
        return stringRedisTemplate.delete(key);
    }


    /**
     * 根据key模糊查询所有字符串数据
     * @param key
     * @return
     */
    public List getStringValueList(String key){
        Set<String> keyset =  stringRedisTemplate.keys(key);
        return stringRedisTemplate.opsForValue().multiGet(keyset);
    }

    /**
     *hset
     * @param key
     * @param field
     * @param value
     */
    public void hsetValue(String key,String field,String value){
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    /**
     *hget
     * @param key
     * @param field
     * @return
     */
    public String hgetValue(String key,String field){
       String value =  (String)stringRedisTemplate.opsForHash().get(key, field);
       return value;
    }

    /**
     * remove
     * @param key
     * @param field
     * @return
     */
    public boolean removeStringValue(String key,String... field){
       return stringRedisTemplate.opsForHash().delete(key,field)>0;
    }
}
