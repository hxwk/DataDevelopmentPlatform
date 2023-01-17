package com.dfssi.dataplatform.analysis.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Description:
 *
 * @author PengWuKai
 * @version 2018/9/11 15:16
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.dfssi.dataplatform.analysis.service")
public class ServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApp.class, args);
    }

}
