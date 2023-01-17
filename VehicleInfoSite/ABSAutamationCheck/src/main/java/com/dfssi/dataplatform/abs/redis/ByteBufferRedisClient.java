package com.dfssi.dataplatform.abs.redis;

import redis.clients.jedis.Jedis;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/16 20:41
 */
public class ByteBufferRedisClient {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private Jedis jedis;

    public ByteBufferRedisClient(Jedis jedis){
        this.jedis = jedis;
    }

    public void set(String key, ByteBuffer value){
        jedis.set(key.getBytes(UTF_8), value.array());
    }

    public void set(String key, byte[] value){
        jedis.set(key.getBytes(UTF_8), value);
    }

    public byte[] get(String key){
        return get(key.getBytes(UTF_8));
    }

    public boolean exists(String key) {
        return jedis.exists(key);
    }

    public byte[] get(byte[] key){
        return jedis.get(key);
    }

    public Set<byte[]> keys(String keyPattern){
       return jedis.keys(keyPattern.getBytes(UTF_8));
    }

    public void del(String key){
        jedis.del(key.getBytes(UTF_8));
    }

    public void del(Collection<String> keys){
        List<byte[]> collect = keys.stream().map(key -> key.getBytes(UTF_8)).collect(Collectors.toList());
        jedis.del( collect.toArray(new byte[collect.size()][]));
    }

    public void close(){
        if(jedis != null){
            jedis.close();
        }
    }
}
