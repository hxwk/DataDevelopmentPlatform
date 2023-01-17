package com.dfssi.dataplatform.vehicleinfo.vehicleroad.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/26 9:16
 */
public abstract class AbstractCache<K, V> {
    private LoadingCache<K,V> cache;

    public AbstractCache() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K k) throws Exception
                    {
                        return loadData(k);
                    }
                });
    }

    /**
     * 超时缓存：数据写入缓存超过一定时间自动刷新
     * @param duration
     * @param timeUtil
     */
    public AbstractCache(long duration, TimeUnit timeUtil) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(duration, timeUtil)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K k) throws Exception
                    {
                        return loadData(k);
                    }
                });
    }

    public AbstractCache(long refreshDuration,
                         long removeDuration,
                         TimeUnit timeUtil,
                         ExecutorService executorService) {
        if(executorService == null){
            executorService = Executors.newFixedThreadPool(8);
        }
        ListeningExecutorService backgroundRefreshPools =
                MoreExecutors.listeningDecorator(executorService);

        cache = CacheBuilder.newBuilder()
                .refreshAfterWrite(refreshDuration, timeUtil)
                .expireAfterWrite(removeDuration , timeUtil)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K k) throws Exception {
                        return loadData(k);
                    }
                    @Override
                    public ListenableFuture<V> reload(K key, V oldValue) throws Exception {
                        return backgroundRefreshPools.submit(() -> loadData(key));
                    }
                });
    }

    /**
     * 限容缓存：缓存数据个数不能超过maxSize
     * @param maxSize
     */
    public AbstractCache(long maxSize) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K k) throws Exception
                    {
                        return loadData(k);
                    }
                });
    }

    /**
     * 权重缓存：缓存数据权重和不能超过maxWeight
     * @param maxWeight
     * @param weigher：权重函数类，需要实现计算元素权重的函数
     */
    public AbstractCache(long maxWeight, Weigher<K, V> weigher) {
        cache = CacheBuilder.newBuilder()
                .maximumWeight(maxWeight)
                .weigher(weigher)
                .build(new CacheLoader<K, V>() {
                    @Override
                    public V load(K k) throws Exception
                    {
                        return loadData(k);
                    }
                });
    }


    /**
     *
     * 缓存数据加载方法
     * @param k
     * @return
     */
    protected abstract V loadData(K k);

    /**
     *
     * 从缓存获取数据
     * @param param
     * @return
     */
    public V getCache(K param) {
        return cache.getUnchecked(param);
    }

    /**
     *
     * 清除缓存数据，缓存清除后，数据会重新调用load方法获取
     * @param k
     */
    public void refresh(K k) {
        cache.refresh(k);
    }

    /**
     *
     * 主动设置缓存数据
     */
    public void put(K k, V v) {
        cache.put(k, v);
    }
}