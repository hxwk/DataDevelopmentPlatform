package com.dfssi.dataplatform.ide.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Spring boot启用类
 * 视频管理
 */
@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
@ComponentScan("com.dfssi")
public class VideoIDEApp {
    public static void main(String[] args) {
        SpringApplication.run(VideoIDEApp.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        return builder.build();
    }

}
