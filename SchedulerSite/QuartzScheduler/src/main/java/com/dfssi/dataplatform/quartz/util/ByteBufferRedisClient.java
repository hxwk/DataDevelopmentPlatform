package com.dfssi.dataplatform.quartz.util;

import io.netty.buffer.Unpooled;
import redis.clients.jedis.Jedis;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Set;

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

    public ByteBuffer get(String key){
        return get(key.getBytes(UTF_8));
    }

    public ByteBuffer get(byte[] key){
        byte[] bytes = jedis.get(key);
        if(bytes != null){
            return Unpooled.wrappedBuffer(bytes).nioBuffer();
        }
        return null;
    }

    public Set<byte[]> keys(String keyPattern){
       return jedis.keys(keyPattern.getBytes(UTF_8));
    }

    public void close(){
        if(jedis != null){
            jedis.close();
        }
    }
}
