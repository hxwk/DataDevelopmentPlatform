package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.HistoryDataQueryEntity;

import java.util.List;
import java.util.Map;

/**
 * 历史数据查询
 * Created by yanghs on 2018/9/12.
 */
public interface IDataStatisticsService {

    /**
     * 历史数据查询
     * @param historyDataQueryEntity
     * @return
     */
    Map<String, Object> queryHistoryData(HistoryDataQueryEntity historyDataQueryEntity);


    Map<String, Object> exportHistoryData(HistoryDataQueryEntity historyDataQueryEntity);

    List<Map<String, Object>> findAllFields(String label);


    Map<String, Object> queryStatisticsByDay(String vid,
                                             Long startTime,
                                             Long stopTime,
                                             int pageNow,
                                             int pageSize);

    Map<String, Object> queryStatisticsByMonth(String vid,
                                               Long startTime,
                                               Long stopTime,
                                               int pageNow,
                                               int pageSize);

    Map<String, Object> queryStatisticsTrip(String vid,
                                            Long startTime,
                                            Long stopTime,
                                            int pageNow,
                                            int pageSize);
}
