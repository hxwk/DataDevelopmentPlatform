package com.dfssi.common;

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** 
* An LRU cache, based on <code>LinkedHashMap</code>. 
* 
* <p> 
* This cache has a fixed maximum number of elements (<code>cacheSize</code>). 
* If the cache is full and another entry is added, the LRU (least recently used) entry is dropped. 
* 
* <p> 
* This class is thread-safe. All methods of this class are synchronized. 
* 
* <p> 
* Author: Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland<br> 
* Multi-licensed: EPL / LGPL / GPL / AL / BSD.
 * 超时时间可以理解为发呆时间  每次 获取缓存 会更新时间
*/  
public class LRUCache<K,V> {

   private static final float   hashTableLoadFactor = 0.75f;

   private LinkedHashMap<K, TimeVaue<V>>   map;
   private int                  cacheSize;
   private long cacheOutTime = -1L;


   public LRUCache(final int cacheSize){
      this(cacheSize, -1L);
   }

   /**
    * Creates a new LRU cache.
    * @param cacheSize the maximum number of entries that will be kept in this cache.
    *@param cacheOutTime 缓存超时时间  单位：毫秒
    */
   public LRUCache(final int cacheSize, final long cacheOutTime) {
      this.cacheSize = cacheSize;
      int hashTableCapacity = (int)Math.ceil(cacheSize / hashTableLoadFactor) + 1;

      //当参数accessOrder为true时，即会按照访问顺序排序，最近访问的放在最前，最早访问的放在后面
      this.map = new LinkedHashMap<K, TimeVaue<V>>(hashTableCapacity, hashTableLoadFactor, true) {

         private static final long serialVersionUID = 1;
         @Override
         protected boolean removeEldestEntry (Map.Entry<K, TimeVaue<V>> eldest) {
            boolean status = false;
            if(cacheOutTime > 0) {
               long timestamp = eldest.getValue().timestamp;
               status = ((System.currentTimeMillis() - timestamp) >= cacheOutTime);
            }
            return (status || size() > LRUCache.this.cacheSize);
         }
      };

      this.cacheOutTime = cacheOutTime;
   }


   /**
    * Retrieves an entry from the cache.<br>
    * The retrieved entry becomes the MRU (most recently used) entry.
    * @param key the key whose associated value is to be returned.
    * @return    the value associated to this key, or null if no value with this key exists in the cache.
    */
   public synchronized V get (K key) {
      return get(key, false);
   }

   public synchronized V getAndUpdateTime (K key) {
      return get(key, true);
   }

   private V get(K key, boolean uodateTime) {

      TimeVaue<V> timeVaue = this.map.get(key);
      V value = null;
      if(timeVaue != null){
         if((System.currentTimeMillis() - timeVaue.timestamp) < cacheOutTime){
            value = timeVaue.value;
            if(uodateTime)timeVaue.timestamp = System.currentTimeMillis();
         }else {
            this.map.remove(key);
         }
      }
      return value;
   }

   /**
    * Adds an entry to this cache.
    * The new entry becomes the MRU (most recently used) entry.
    * If an entry with the specified key already exists in the cache, it is replaced by the new entry.
    * If the cache is full, the LRU (least recently used) entry is removed from the cache.
    * @param key    the key with which the specified value is to be associated.
    * @param value  a value to be associated with the specified key.
    */
   public synchronized void put (K key, V value) {
      this.map.put (key, new TimeVaue<>(value));
   }

   /**
    * Clears the cache.
    */
   public synchronized void clear() {
      this.map.clear();
   }


   public synchronized int usedEntries() {
      return this.map.size();
   }


   public synchronized Map<K, V> All() {
      Map<K, V> all = Maps.newHashMap();
      Iterator<Map.Entry<K, TimeVaue<V>>> iterator = this.map.entrySet().iterator();
      Map.Entry<K, TimeVaue<V>> next;
      while (iterator.hasNext()){
         next = iterator.next();
         if(System.currentTimeMillis() - next.getValue().timestamp < cacheOutTime){
            all.put(next.getKey(), next.getValue().value);
         }else {
            iterator.remove();
         }

      }
      return all;
   }

   public synchronized void remove(K key){
      this.map.remove(key);
   }

   private class TimeVaue<V>{
      private V value;
      private Long timestamp;

      public TimeVaue(V value){
         this(value, System.currentTimeMillis());
      }

      public TimeVaue(V value, long timestamp){
         this.value = value;
         this.timestamp = timestamp;
      }
   }


   public static void main(String[] args) throws InterruptedException {
      LRUCache<String, String> fuelSpeedCache = new LRUCache<>(4, 1 * 30 * 1000L);
      fuelSpeedCache.put("test", "231321");
      fuelSpeedCache.put("ddewewd1", "free");
      fuelSpeedCache.put("ddewewd2", "free");
      fuelSpeedCache.put("ddewewd3", "free");
      fuelSpeedCache.put("ddewewd4", "free");
      System.out.println("1. " + fuelSpeedCache.cacheSize + " : " + fuelSpeedCache.All());

      fuelSpeedCache.put("ddewewd5", "free");
      System.out.println("2. " + fuelSpeedCache.cacheSize + " : " + fuelSpeedCache.All());

      Thread.sleep(1 * 60 * 1000L);

      System.out.println("3. " + fuelSpeedCache.cacheSize + " : " + fuelSpeedCache.All());

      fuelSpeedCache.put("4", "dssdef");
      System.out.println("4. " + fuelSpeedCache.cacheSize + " : " + fuelSpeedCache.All());
   }

}