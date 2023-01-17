package com.dfssi.dataplatform.ide.access;

import com.dfssi.dataplatform.cloud.common.annotation.EnableGlobalCors;
import com.dfssi.dataplatform.cloud.common.annotation.EnableWebLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by hongs on 2018/5/5.
 */
@SpringBootApplication
@EnableSwagger2
@EnableFeignClients
@EnableGlobalCors
@EnableWebLog
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }

    
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }
}



