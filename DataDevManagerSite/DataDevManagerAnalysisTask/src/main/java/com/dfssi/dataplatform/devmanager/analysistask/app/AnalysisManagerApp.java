package com.dfssi.dataplatform.devmanager.analysistask.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Spring boot启用类
 * Bean装配默认规则是根据Application类所在的包位置从上往下扫描
 */
//@MapperScan("com.dfssi.dataplatform.analysis")
//@Configuration
//@EnableAutoConfiguration
//@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = {"com.dfssi.dataplatform.devmanager.analysistask"})
@EnableTransactionManagement
@EnableEurekaClient
@EnableSwagger2
public class AnalysisManagerApp {

    public static void main(String[] args) {
        SpringApplication.run(AnalysisManagerApp.class, args);
    }

}
