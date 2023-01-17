package com.dfssi.dataplatform.vehicleinfo.vehicleroad.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.VehicleLoginInfoStatus;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 消费者
 * 使用@KafkaListener注解,可以指定:主题,分区,消费组
 */
@Component
@Slf4j
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    //@Autowired
    private StringRedisTemplate stringRedisTemplate;

    //@KafkaListener(topics = {"ROAD_VNND_LOGININFO_STATUS"})
    public void receive(String message){

        try {
            VehicleLoginInfoStatus vehicleLoginInfoStatus = JSON.parseObject(message,
                    new TypeReference<VehicleLoginInfoStatus>() {
                    });
            updateVehicleStatus(vehicleLoginInfoStatus);
        } catch (Exception e) {
            log.error(String.format("解析状态数据失败： %s", message), e);
        }
    }

    private void updateVehicleStatus(VehicleLoginInfoStatus vehicleLoginInfoStatus){
        stringRedisTemplate.opsForValue()
                .set(
                        String.format("roadVehicle:%s", vehicleLoginInfoStatus.getVid()),
                                String.format("%s:%s", vehicleLoginInfoStatus.getStatus(), vehicleLoginInfoStatus.getTs()),
                        10, TimeUnit.MINUTES);
    }
}
