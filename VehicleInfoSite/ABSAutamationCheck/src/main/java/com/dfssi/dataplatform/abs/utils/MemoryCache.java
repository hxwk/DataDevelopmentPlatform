package com.dfssi.dataplatform.abs.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/19 20:25
 */
public class MemoryCache<K, V> {

    private Cache<K,V> cache;

    public MemoryCache(int maxSize) {
        this(maxSize, 1, TimeUnit.DAYS);
    }

    public MemoryCache(int maxSize,
                       long removeDuration,
                       TimeUnit timeUtil) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(removeDuration , timeUtil)
                .maximumSize(maxSize).build();
    }

    public void put(K key, V value){
        if(key != null && value != null){
            cache.put(key, value);
        }
    }

    public void putAll(Map<K, V> kvs){
        if(kvs != null){
            cache.putAll(kvs);
        }
    }

    public V get(K key){
        return cache.getIfPresent(key);
    }

    public Map<K, V>  getAll(){
        return cache.asMap();
    }

    public static void main(String[] args) {
        MemoryCache<String, String> cache = new MemoryCache<>(3);
        cache.put("jechedo", "dsw");
        cache.put("jechedo", "eew");
        cache.put("dewdew", "dsw");
        cache.put("jechedewdewdo", "dsw");


        System.out.println(cache.getAll());
    }
}
