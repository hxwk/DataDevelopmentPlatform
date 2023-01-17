package com.dfssi.dataplatform.ide.logmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2017/11/14 16:46
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.dfssi.dataplatform.ide.logmanager", "com.dfssi.ibase"})
@EnableTransactionManagement
//@EnableEurekaClient
@EnableSwagger2
public class LoggerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoggerApplication.class, args);
    }

}
