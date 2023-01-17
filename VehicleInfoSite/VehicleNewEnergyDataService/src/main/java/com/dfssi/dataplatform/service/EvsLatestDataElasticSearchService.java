package com.dfssi.dataplatform.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *    es最新数据统计查询
 * @author LiXiaoCong
 * @version 2018/5/2 11:30
 */
public interface EvsLatestDataElasticSearchService {

    Map<String, Object> countLatestOnlineMsg();

    Map<String, Object> countLatestWorkingstatus();

    List<Map<String, Object>> countOnlineTopNVehicleCompany(int topN);

    List<Map<String, Object>> countOnlineTopNArea(int topN);

    Map<String, Object> getLatestDataByVin(String vin,
                                           Set<String> columns,
                                           String posType);

    Map<String, Object> getLatestVehiclePosData(String status,
                                                int from,
                                                int size,
                                                String posType);

    Map<String, Object> searchLatestData(String[] enterprise,
                                         String[] hatchback,
                                         String[] vin,
                                         Set<String> columns,
                                         Long startTime,
                                         Long endTime,
                                         int from,
                                         int size);

    Map<String, Object> countOnlineAndRunningByField(String jsonParams,
                                                     int from,
                                                     int size);
}
