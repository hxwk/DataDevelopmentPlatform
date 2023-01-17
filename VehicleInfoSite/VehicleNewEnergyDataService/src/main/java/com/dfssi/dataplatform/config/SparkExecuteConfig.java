package com.dfssi.dataplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/8/16 20:39
 */
@Configuration
public class SparkExecuteConfig {
    @Value("${spark.task.url}")
    private String sparkTaskUrl;

    @Value("${spark.task.name}")
    private String sparkTaskName;

    @Value("${spark.task.password}")
    private String sparkTaskPassword;

    @Value("${spark.task.port}")
    private int sparkTaskPort;

    public String getSparkTaskUrl() {
        return sparkTaskUrl;
    }

    public String getSparkTaskName() {
        return sparkTaskName;
    }

    public String getSparkTaskPassword() {
        return sparkTaskPassword;
    }

    public int getSparkTaskPort() {
        return sparkTaskPort;
    }
}
