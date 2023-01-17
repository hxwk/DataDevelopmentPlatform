package com.dfssi.scheduling;

import com.dfssi.dataplatform.cloud.common.annotation.EnableDruid;
import com.dfssi.dataplatform.cloud.common.annotation.EnableGlobalCors;
import com.dfssi.dataplatform.cloud.common.annotation.EnableWebLog;
import com.dfssi.dataplatform.cloud.common.annotation.EnableXDiamond;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 任务调度中心
 */
@SpringBootApplication
@EnableEurekaClient
@EnableGlobalCors
@EnableSwagger2
@EnableWebLog
@EnableXDiamond
@EnableDruid
public class SchedulingApplication {

	public static void main(String[] args) {
	    SpringApplication.run(SchedulingApplication.class, args);
	}

}
