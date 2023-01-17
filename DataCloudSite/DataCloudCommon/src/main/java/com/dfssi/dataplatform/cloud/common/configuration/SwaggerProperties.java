package com.dfssi.dataplatform.cloud.common.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by yanghs on 2018/6/5.
 */
@ConfigurationProperties(prefix = SwaggerProperties.SWAGGER_PREFIX)
public class SwaggerProperties {

    public static final String SWAGGER_PREFIX = "swagger";

    private String scanPackage="com.dfssi.dataplatform";

    public String getScanPackage() {
        return scanPackage;
    }

    public void setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
    }
}
