package com.dfssi.dataplatform.ide.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/11/14 16:46
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.dfssi"})
//@MapperScan("com.dfssi.dataplatform.analysis")
@EnableEurekaClient
@EnableSwagger2
public class SecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }

}
