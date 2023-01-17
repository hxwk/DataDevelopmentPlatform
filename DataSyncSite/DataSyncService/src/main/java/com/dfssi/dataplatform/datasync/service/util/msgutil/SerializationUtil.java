package com.dfssi.dataplatform.datasync.service.util.msgutil;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by cxq on 2017/11/17.
 */
public class SerializationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerializationUtil.class);

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();
    private static Objenesis objenesis = new ObjenesisStd(true);

    private  SerializationUtil() {
    }

    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            if (schema != null){
                cachedSchema.put(clazz, schema);
            }
        }
        return schema;
    }


    public static<T> byte[] serialize(T o) {
        Class<T> clazz = (Class<T>) o.getClass();
        LinkedBuffer buffer =  LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        byte[] result;

        Schema<T> schema = getSchema(clazz);
        try {
             result = ProtostuffIOUtil.toByteArray(o,schema,buffer);
        } catch (Exception e) {
            LOGGER.error("SerializationUtil.serialize error:{}",e);
            result = new byte[0];
        }finally {
            buffer.clear();
        }
        return result;
    }

    public static <T> T deserialize(byte[] data,Class<T> tClass){
        T messgae = (T) objenesis.newInstance(tClass);
        Schema<T> schema = getSchema(tClass);
        ProtostuffIOUtil.mergeFrom(data,messgae,schema);
        return messgae;
    }


}
