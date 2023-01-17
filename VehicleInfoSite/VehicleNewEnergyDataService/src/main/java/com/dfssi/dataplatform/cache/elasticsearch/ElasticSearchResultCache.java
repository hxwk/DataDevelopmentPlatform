package com.dfssi.dataplatform.cache.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dfssi.dataplatform.cache.AbstractCache;
import com.dfssi.dataplatform.model.EvsLatestDataElasticSearchModel;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/27 8:38
 */
public class ElasticSearchResultCache extends AbstractCache<String, Object> {

    private final Logger logger = LoggerFactory.getLogger(ElasticSearchResultCache.class);

    @Autowired
    private EvsLatestDataElasticSearchModel evsLatestDataElasticSearchModel;

    public ElasticSearchResultCache(ExecutorService executorService) {
        super(5, 30, TimeUnit.MINUTES, executorService);
    }

    @Override
    protected Object loadData(String key) {
        String[] items = key.split("<#>");
        switch (items[0]){
            case "latest" :
                return loadLatestData(items);
        }
        return null;
    }


    private Object loadLatestData(String[] params){
        switch (params[1]){
            case "countOnlineAndRunningByField":
               return executeCountOnlineAndRunningByField(params[2]);
            case "countOnlineTopNArea":
               return executeCountOnlineTopNArea(params[2]);
        }
        return null;
    }

    private Object executeCountOnlineAndRunningByField(String param){

        Map<String, String> paramMap = JSON.parseObject(param,
                new TypeReference<Map<String, String>>() {});
        String field = paramMap.get("field");
        String enterprises = paramMap.get("enterprises");
        String hatchbacks = paramMap.get("hatchbacks");
        String vins = paramMap.get("vin");

        String[] enterprise = (enterprises != null) ?
                enterprises.split(",") : null;
        String[] hatchback = (hatchbacks != null) ?
                hatchbacks.split(",") : null;
        String[] vin = (vins != null) ?
                vins.split(",") : null;

        logger.info(String.format("开始缓存按字段%s分组并且满足企业为：%s,车型为：%s, 车辆Vin为：%s的统计信息...",
                field, enterprises, hatchbacks, vins));

       return evsLatestDataElasticSearchModel.countOnlineAndRunningByField(field,
               enterprise, hatchback, vin);
    }

    private Object executeCountOnlineTopNArea(String param){
        int topN = (param == null)? 10 : Integer.parseInt(param);
        logger.info(String.format("开始缓存武汉市各区域在线数量Top%s信息...", topN));
        return evsLatestDataElasticSearchModel.countOnlineTopNArea(topN);
    }


    public String createKey(String ... params){
        return Joiner.on("<#>").join(params);
    }

}
