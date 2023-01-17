package com.dfssi.dataplatform.vehicleinfo.vehicleroad.config;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/4/13 11:32
 */
@Configuration
@Order(1)
public class BeanConfig {

    @Bean
    public ExecutorService executorService(){
        //io密集型  取 2 * Ncpu ； 计算密集型 取 1 + Ncpu
        return  new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2,
                new BasicThreadFactory.Builder().namingPattern("abs-event-schedule-pool-%d").daemon(true).build());
    }
}