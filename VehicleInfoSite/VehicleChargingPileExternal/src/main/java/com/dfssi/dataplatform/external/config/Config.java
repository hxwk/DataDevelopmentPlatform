package com.dfssi.dataplatform.external.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/4/13 11:32
 */
@Configuration
@Order(2)
public class Config {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ExecutorService executorService(){
        //io密集型  取 2 * Ncpu ； 计算密集型 取 1 + Ncpu
        return Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() * 2);
    }
}