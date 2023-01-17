package com.dfssi.dataplatform.abs.service;

import com.dfssi.dataplatform.abs.check.ABSCheckRecord;
import com.dfssi.dataplatform.abs.check.ABSCheckRule;
import com.dfssi.dataplatform.abs.check.AbsCheckTask;
import com.dfssi.dataplatform.abs.config.KafkaConfig;
import com.dfssi.dataplatform.abs.config.RedisConfig;
import com.dfssi.dataplatform.abs.mapper.AbsCheckRecordMapper;
import com.dfssi.dataplatform.abs.mapper.AbsCheckResultMapper;
import com.dfssi.dataplatform.abs.mapper.AbsVehicleStatusMapper;
import com.dfssi.dataplatform.abs.redis.ByteBufferRedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/9/27 9:30
 */
@Service
@Slf4j
public class AbsCheckService {

    @Autowired
    private KafkaConfig config;
    @Autowired
    private RedisConfig redisConfig;
    @Autowired
    private AbsCheckResultMapper resultMapper;
    @Autowired
    private AbsCheckRecordMapper recordMapper;
    @Autowired
    private AbsVehicleStatusMapper statusMapper;

    @Async("asyncServiceExecutor")
    public void absCheckTask(String vid,
                               int vehicleType,
                               int vehicleStatus,
                               double minStartBreakSpeed,
                               double minBreakLessSpeed,
                               double maxBreakDistance) {
        try {
            ABSCheckRule rule = new ABSCheckRule(vehicleType, vehicleStatus, minStartBreakSpeed, minBreakLessSpeed, maxBreakDistance);
            AbsCheckTask absCheckTask = new AbsCheckTask(vid, config, redisConfig, rule);
            absCheckTask.setResultMapper(resultMapper);
            absCheckTask.setRecordMapper(recordMapper);
            absCheckTask.setStatusMapper(statusMapper);
            absCheckTask.run();
        } catch (Exception e) {
            log.error("ABS自动检测分析失败。", e);
        }
    }
    

}
