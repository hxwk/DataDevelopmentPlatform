package com.dfssi.dataplatform.abs.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dfssi.dataplatform.abs.config.KafkaConfig;
import com.dfssi.dataplatform.abs.config.RedisConfig;
import com.dfssi.dataplatform.abs.utils.ECGeoCoordinateTransformUtil;
import com.dfssi.dataplatform.abs.utils.MathUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/10/29 20:41
 */
@Component
public class LatestKafkaRecordDao {

    private Logger logger = LoggerFactory.getLogger(LatestKafkaRecordDao.class);

    @Autowired
    private KafkaConfig config;
    @Autowired
    private RedisConfig redisConfig;

    private AtomicBoolean started = new AtomicBoolean(false);
    private Map<String, Map<String, Object>> latestMap = Maps.newConcurrentMap();


    public Map<String, Map<String, Object>> getLatestRecord(List<String> vids){

        if(!started.get()){
            try {
                KafkaConsumer kafkaConsumer = config.newKafkaConsumer(String.format("abs-%s", "latest-demo"));
                kafkaConsumer.subscribe(config.getTopicSet());

                Jedis jedis = redisConfig.getJedis();

                latest(kafkaConsumer, jedis);
                started.set(true);

                Thread.sleep(400);
            } catch (Exception e) {
                logger.error(null, e);
            }
        }

        Map<String, Map<String, Object>>  los = Maps.newHashMap();
        try {
            Jedis jedis = redisConfig.getJedis();
            if(vids == null || vids.isEmpty()){
                try {
                    vids = Lists.newArrayList(jedis.keys("abs:*"));
                } catch (Exception e) {
                    vids = Lists.newArrayList();
                }
            }else{
                vids = vids.stream().map(vid -> String.format("abs:%s", vid)).collect(Collectors.toList());
            }

            vids.forEach(vid ->{
                String json = jedis.get(vid);
                if(json != null) {

                    Map<String, Object> map = JSON.parseObject(json,
                            new TypeReference<Map<String, Object>>() {
                            });

                    Map<String, Object> res = Maps.newHashMap();
                    if (map != null) {
                        res.put("vid", map.get("vid"));
                        res.put("sim", map.get("sim"));
                        res.put("gpsTime", map.get("gpsTime"));

                        int speed = (int) map.getOrDefault("speed", 0);
                        res.put("speed", MathUtil.rounding(speed * 0.1, 1));

                        double lat = MathUtil.rounding((int) map.getOrDefault("lat", 0) * 0.000001, 6);
                        double lon =  MathUtil.rounding((int) map.getOrDefault("lon", 0)* 0.000001, 6);

                        double[] loc = ECGeoCoordinateTransformUtil.wgs84togcj02(lon, lat);
                        res.put("lon", loc[0]);
                        res.put("lat", loc[1]);

                        int dir = (int) map.getOrDefault("dir", 0);
                        res.put("dir", dir);
                    }
                    los.put(vid.replace("abs:", ""), res);
                }

            });

            jedis.close();

        } catch (Exception e) {
           logger.error("获取redis中的最新数据失败。", e);
        }

        return los;
    }

    private void latest(KafkaConsumer kafkaConsumer, Jedis jedis){
        new Thread(() -> {
            ConsumerRecords<String, String> poll;
            Iterator<ConsumerRecord<String, String>> iterator;
            while (true) {
                poll = kafkaConsumer.poll(200);
                if(!poll.isEmpty()){
                    iterator  = poll.iterator();
                    while (iterator.hasNext()){

                        String value = iterator.next().value();
                        Map<String, String> map = JSON.parseObject(value,
                                new TypeReference<Map<String, String>>() {
                                });
                        jedis.set(String.format("abs:%s",  map.get("vid")), value);
                   }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) { }
            }
        }).start();

    }



}
