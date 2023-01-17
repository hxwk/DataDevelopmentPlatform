package com.dfssi.dataplatform.cloud.common.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by yanghs on 2018/6/5.
 */
@ConfigurationProperties(prefix = CorsProperties.CORS_PREFIX)
public class CorsProperties {

    public static final String CORS_PREFIX = "cors";

    private String pathPattern="/**";

    private String origins="*";

    private String allowedMethods;

    private String allowedHeaders;

    private String exposedHeaders;

    private boolean allowCredentials=true;

    private String maxAge="1800";

    public String getPathPattern() {
        return pathPattern;
    }

    public void setPathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
    }

    public String getOrigins() {
        return origins;
    }

    public void setOrigins(String origins) {
        this.origins = origins;
    }

    public String getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public String getExposedHeaders() {
        return exposedHeaders;
    }

    public void setExposedHeaders(String exposedHeaders) {
        this.exposedHeaders = exposedHeaders;
    }

    public boolean isAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public String getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }
}
