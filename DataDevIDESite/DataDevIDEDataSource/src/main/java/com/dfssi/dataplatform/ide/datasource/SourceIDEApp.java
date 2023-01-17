package com.dfssi.dataplatform.ide.datasource;

import com.dfssi.dataplatform.cloud.common.annotation.EnableGlobalCors;
import com.dfssi.dataplatform.cloud.common.annotation.EnableWebLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Spring boot启用类
 * 数据源管理
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableFeignClients
@EnableSwagger2
@EnableWebLog
@EnableGlobalCors
public class SourceIDEApp {

    public static void main(String[] args) {
        SpringApplication.run(SourceIDEApp.class, args);
    }

}
