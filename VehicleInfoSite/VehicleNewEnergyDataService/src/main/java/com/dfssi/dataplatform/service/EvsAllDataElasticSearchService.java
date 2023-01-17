package com.dfssi.dataplatform.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *    es全量数据统计查询
 * @author LiXiaoCong
 * @version 2018/5/2 19:17
 */
public interface EvsAllDataElasticSearchService {

    Map<String, Object> searchData(String[] enterprise,
                                   String[] hatchback,
                                   String[] vin,
                                   Set<String> columns,
                                   Long startTime,
                                   Long endTime,
                                   int from,
                                   int size);

    Map<String, Object> getDataByIndexId(String index,
                                         String id,
                                         Set<String> columns);

    List<Map<String, Object>> searchSamplingTrackLocations(String vin,
                                                           Long startTime,
                                                           Long endTime,
                                                           Integer stepLength,
                                                           String posType);
}

