package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class RedisService {

	@Autowired
	private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 保存数据
     * @param key
     * @param obj
     */
    public  void setKey(String key,Object obj){
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
     * 根据key模糊查询所有数据
     * @param key
     * @return
     */
    public List getValueList(String key){
        Set<String> keyset =  stringRedisTemplate.keys(key);
        return stringRedisTemplate.opsForValue().multiGet(keyset);
    }
}
