package com.dfssi.dataplatform.analysis.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan({"com.dfssi"})
@EnableEurekaClient
@EnableFeignClients
public class WorkflowApp {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowApp.class, args);
    }

}
