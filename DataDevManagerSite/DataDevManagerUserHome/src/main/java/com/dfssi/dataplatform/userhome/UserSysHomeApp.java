package com.dfssi.dataplatform.userhome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * Spring boot启用类
 * 系统管理
 */


@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
@ComponentScan("com.dfssi")
public class UserSysHomeApp {

    public static void main(String[] args) {
        SpringApplication.run(UserSysHomeApp.class, args);
    }

}
