package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.app;

import org.apache.commons.lang.StringUtils;
import org.apache.geode.cache.client.*;
import org.apache.geode.cache.control.ResourceManager;
import org.apache.geode.cache.query.QueryService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Geode 自动配置与初始化启动
 *
 * @author yanghs
 * @since 2018-4-3 13:34:46
 */
@Configuration
@Order(value = 2)
@EnableConfigurationProperties(GeodeProperties.class)
public class GeodeAutoConfiguration {

    private GeodeProperties geodeProperties;

    public GeodeAutoConfiguration(GeodeProperties geodeProperties) {
        this.geodeProperties = geodeProperties;
    }


    @Bean
    ClientRegionFactory ClientRegionFactory() {
        ClientRegionFactory factory = clientCache().createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);
        factory.setPoolName(geodeProperties.getPoolName());
//        factory.addCacheListener(new LoggingCacheListener());

        return factory;
    }

    @Bean
    QueryService queryService() {
        Pool pool = poolFactory().create(geodeProperties.getPoolName());
        QueryService queryService = pool.getQueryService();
        return queryService;
    }

    PoolFactory poolFactory() {
        PoolFactory poolFactory = PoolManager.createFactory();
        if (StringUtils.isNotEmpty(geodeProperties.getUrl())) {
            poolFactory.addLocator(geodeProperties.getUrl(), geodeProperties.getPort());
        }
        if (StringUtils.isNotEmpty(geodeProperties.getUrls())) {
            List list = getAllUrls();
            for (int i = 0; i < list.size(); i++) {
                poolFactory.addLocator(((String[]) list.get(i))[0], Integer.parseInt(((String[]) list.get(i))[1]));
            }
        }
        poolFactory.setReadTimeout(geodeProperties.getPoolTimeout());
        return poolFactory;
    }

    ClientCache clientCache() {
        ClientCacheFactory factory = new ClientCacheFactory();
        if (StringUtils.isNotEmpty(geodeProperties.getUrl())) {
            factory.addPoolLocator(geodeProperties.getUrl(), geodeProperties.getPort());
        }
        List list = getAllUrls();
        for (int i = 0; i < list.size(); i++) {
            factory.addPoolLocator(((String[]) list.get(i))[0], Integer.parseInt(((String[]) list.get(i))[1]));
        }
        ClientCache cache = factory.create();
        ResourceManager rm = cache.getResourceManager();
        rm.setCriticalHeapPercentage(85);
        rm.setEvictionHeapPercentage(75);
        return cache;
    }

    private List getAllUrls() {
        List urlList = new ArrayList();
        String[] url = geodeProperties.getUrls().split(",");
        for (int i = 0; i < url.length; i++) {
            String[] urlPort = url[i].split(":");
            if (urlPort.length == 2) {
                urlList.add(urlPort);

            } else {
                throw new IllegalArgumentException("geode 集群配置urls不符合规范");
            }
        }
        return urlList;
    }
}
