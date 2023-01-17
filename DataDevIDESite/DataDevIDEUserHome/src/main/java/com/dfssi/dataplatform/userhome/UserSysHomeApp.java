package com.dfssi.dataplatform.userhome;

import com.dfssi.dataplatform.cloud.common.annotation.EnableGlobalCors;
import com.dfssi.dataplatform.cloud.common.annotation.EnableWebLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * Spring boot启用类
 * 系统管理
 */
@SpringBootApplication
@EnableSwagger2
@EnableGlobalCors
@EnableWebLog
public class UserSysHomeApp {

    public static void main(String[] args) {
        SpringApplication.run(UserSysHomeApp.class, args);
    }

}
