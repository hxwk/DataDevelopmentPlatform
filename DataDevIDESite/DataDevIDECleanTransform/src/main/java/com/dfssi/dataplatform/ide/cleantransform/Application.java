package com.dfssi.dataplatform.ide.cleantransform;

import com.dfssi.dataplatform.cloud.common.annotation.EnableGlobalCors;
import com.dfssi.dataplatform.cloud.common.annotation.EnableWebLog;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by hongs on 2018/5/5.
 */
@SpringBootApplication
@EnableSwagger2
@EnableGlobalCors
@EnableWebLog

public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }
}
