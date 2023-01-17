package com.dfssi.dataplatform.ide.analysis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Spring boot启用类
 * Bean装配默认规则是根据Application类所在的包位置从上往下扫描
 */
@SpringBootApplication
@ComponentScan({"com.dfssi"})
@MapperScan("com.dfssi.dataplatform.analysis")
@EnableEurekaClient
@EnableSwagger2
public class AnalysisIDEApp {

    public static void main(String[] args) {
        SpringApplication.run(AnalysisIDEApp.class, args);
    }

}
