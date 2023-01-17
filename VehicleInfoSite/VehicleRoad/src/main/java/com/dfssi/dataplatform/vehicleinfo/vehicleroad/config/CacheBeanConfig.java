package com.dfssi.dataplatform.vehicleinfo.vehicleroad.config;


import com.dfssi.dataplatform.vehicleinfo.vehicleroad.cache.ElasticSearchIndexNameCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.concurrent.ExecutorService;

/**
 * Description:
 *  缓存相关的 实例获取器
 * @author LiXiaoCong
 * @version 2018/4/26 9:31
 */
@Configuration
@Order(3)
public class CacheBeanConfig {

    @Autowired
    private ExecutorService executorService;

    @Bean
    public ElasticSearchIndexNameCache elasticSearchIndexNameCache(){
        return new ElasticSearchIndexNameCache(executorService);
    }


}
