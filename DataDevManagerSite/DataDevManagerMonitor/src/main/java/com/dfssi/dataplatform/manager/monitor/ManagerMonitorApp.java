package com.dfssi.dataplatform.manager.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Spring boot启用类
 * Bean装配默认规则是根据Application类所在的包位置从上往下扫描
 */
//@Configuration
//@EnableAutoConfiguration
@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {"com.dfssi.dataplatform.manager.monitor"})
@EnableTransactionManagement
@EnableEurekaClient
@EnableSwagger2
public class ManagerMonitorApp {

    public static void main(String[] args) {
        SpringApplication.run(ManagerMonitorApp.class, args);
    }

}
