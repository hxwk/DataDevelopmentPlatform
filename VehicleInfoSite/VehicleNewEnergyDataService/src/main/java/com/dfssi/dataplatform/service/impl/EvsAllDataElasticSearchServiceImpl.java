package com.dfssi.dataplatform.service.impl;

import com.dfssi.dataplatform.model.EvsAllDataElasticSearchModel;
import com.dfssi.dataplatform.service.EvsAllDataElasticSearchService;
import com.dfssi.dataplatform.utils.ElasticSearchUtil;
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
 * @version 2018/5/2 19:17
 */
@Service
public class EvsAllDataElasticSearchServiceImpl implements EvsAllDataElasticSearchService {
    private final Logger logger = LoggerFactory.getLogger(EvsAllDataElasticSearchServiceImpl.class);

    @Autowired
    private EvsAllDataElasticSearchModel evsAllDataElasticSearchModel;

    @Override
    public Map<String, Object> searchData(String[] enterprise,
                                          String[] hatchback,
                                          String[] vin,
                                          Set<String> columns,
                                          Long startTime,
                                          Long endTime,
                                          int from,
                                          int size) {

        SearchHits searchHits = evsAllDataElasticSearchModel.searchData(enterprise,
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
    public Map<String, Object> getDataByIndexId(String index, String id, Set<String> columns) {
        return evsAllDataElasticSearchModel.getDataByIndexId(index, id, columns);
    }

    @Override
    public List<Map<String, Object>> searchSamplingTrackLocations(String vin,
                                                                  Long startTime,
                                                                  Long endTime,
                                                                  Integer stepLength,
                                                                  String posType){
        if(stepLength == null) {
            stepLength = 5;
        }
        return evsAllDataElasticSearchModel.searchSamplingTrackLocations(vin, startTime, endTime, stepLength, posType);
    }

}
