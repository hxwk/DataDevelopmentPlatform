package com.dfssi.dataplatform.service.impl;

import com.dfssi.dataplatform.cache.elasticsearch.ElasticSearchResultCache;
import com.dfssi.dataplatform.model.EvsLatestDataElasticSearchModel;
import com.dfssi.dataplatform.service.EvsLatestDataElasticSearchService;
import com.dfssi.dataplatform.utils.ElasticSearchUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/2 11:30
 */
@Service
public class EvsLatestDataElasticSearchServiceImpl implements EvsLatestDataElasticSearchService {
    private final Logger logger = LoggerFactory.getLogger(EvsLatestDataElasticSearchServiceImpl.class);

    @Autowired
    private EvsLatestDataElasticSearchModel evsLatestDataElasticSearchModel;

    @Autowired
    private ElasticSearchResultCache elasticSearchResultCache;

    @Override
    public Map<String, Object> countLatestOnlineMsg() {
        return evsLatestDataElasticSearchModel.countLatestOnlineMsg();
    }

    @Override
    public Map<String, Object> countLatestWorkingstatus() {
        return evsLatestDataElasticSearchModel.countLatestWorkingstatus();
    }

    @Override
    public List<Map<String, Object>> countOnlineTopNVehicleCompany(int topN) {
        return evsLatestDataElasticSearchModel.countOnlineTopNVehicleCompany(topN);
    }

    @Override
    public List<Map<String, Object>> countOnlineTopNArea(int topN) {
        String cacheKey = elasticSearchResultCache.createKey("latest", "countOnlineTopNArea", String.valueOf(topN));
        return (List<Map<String, Object>>) elasticSearchResultCache.getCache(cacheKey);
    }

    @Override
    public Map<String, Object> getLatestDataByVin(String vin, Set<String> columns, String posType) {
        Map<String, Object> map = evsLatestDataElasticSearchModel.getLatestDataByVin(vin, columns);
        ElasticSearchUtil.locationTypeChange(map, posType);
        return map;
    }

    @Override
    public Map<String, Object> getLatestVehiclePosData(String status, int from, int size, String posType) {

        SearchHits searchHits = evsLatestDataElasticSearchModel.getLatestVehiclePosData(status, from, size);
        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = ElasticSearchUtil.selectItemWithLocationTypeChange(searchHits, posType);
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }

    @Override
    public Map<String, Object> searchLatestData(String[] enterprise,
                                                String[] hatchback,
                                                String[] vin,
                                                Set<String> columns,
                                                Long startTime,
                                                Long endTime,
                                                int from,
                                                int size) {

        SearchHits searchHits = evsLatestDataElasticSearchModel.searchLatestData(enterprise,
                hatchback, vin, columns, startTime, endTime, from, size);
        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = ElasticSearchUtil.selectItem(searchHits);
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }

    @Override
    public Map<String, Object> countOnlineAndRunningByField(String jsonParams,
                                                            int from,
                                                            int size){

        String cacheKey = elasticSearchResultCache.createKey("latest", "countOnlineAndRunningByField", jsonParams);
        List<Map<String, Object>> cache = (List<Map<String, Object>>) elasticSearchResultCache.getCache(cacheKey);

        List<Map<String, Object>>  list = Lists.newLinkedList();
        int cacheSize = cache.size();
        int n = 0;
        for(int i = from; i < cacheSize && n < size; i++, n++){
            list.add(cache.get(i));
        }

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", cacheSize);
        res.put("size", list.size());
        res.put("records", list);

        return res;

    }

}
