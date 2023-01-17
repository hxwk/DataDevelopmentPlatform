package com.dfssi.dataplatform.ide.dataresource;

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
 * 数据资源管理
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableFeignClients
@EnableSwagger2
@EnableGlobalCors
@EnableWebLog
public class ResourceIDEApp {

    public static void main(String[] args) {
        SpringApplication.run(ResourceIDEApp.class, args);
    }

}
