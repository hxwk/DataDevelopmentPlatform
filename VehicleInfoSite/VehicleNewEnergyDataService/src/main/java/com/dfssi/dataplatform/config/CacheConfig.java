package com.dfssi.dataplatform.config;

import com.dfssi.dataplatform.cache.database.EvsDetectDetailCache;
import com.dfssi.dataplatform.cache.elasticsearch.ElasticSearchIndexNameCache;
import com.dfssi.dataplatform.cache.elasticsearch.ElasticSearchResultCache;
import com.dfssi.dataplatform.cache.result.EvsDataCountResultCache;
import com.dfssi.dataplatform.cache.result.FeatureAnalysisResultCache;
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
public class CacheConfig {

    @Autowired
    private ExecutorService executorService;

    @Bean
    public EvsDetectDetailCache evsDetectDetailCache(){
        return new EvsDetectDetailCache(executorService);
    }

    @Bean
    public ElasticSearchIndexNameCache elasticSearchIndexNameCache(){
        return new ElasticSearchIndexNameCache(executorService);
    }

    @Bean
    public EvsDataCountResultCache evsDataCountResultCache(){
        return new EvsDataCountResultCache(executorService);
    }

    @Bean
    public ElasticSearchResultCache elasticSearchResultCache(){
        return new ElasticSearchResultCache(executorService);
    }

    @Bean
    public FeatureAnalysisResultCache featureAnalysisResultCache(){
        return new FeatureAnalysisResultCache(executorService);
    }
}
