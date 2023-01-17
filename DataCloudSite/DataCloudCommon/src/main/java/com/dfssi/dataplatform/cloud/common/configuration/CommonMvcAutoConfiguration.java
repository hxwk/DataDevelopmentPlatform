package com.dfssi.dataplatform.cloud.common.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.annotation.Order;

/**
 * 全局配置
 */
@Configuration
@PropertySources({@PropertySource("classpath:application.properties")})
@Order(1)
public class CommonMvcAutoConfiguration{

}