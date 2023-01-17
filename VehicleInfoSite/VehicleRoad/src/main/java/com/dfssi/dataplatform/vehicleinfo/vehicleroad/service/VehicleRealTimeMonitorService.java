package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/21 11:24
 */
public interface VehicleRealTimeMonitorService {


    /**
     *   根据vid 查询最新一条数据
     * @param vid
     * @param columns
     * @param posType
     * @return
     */
    Map<String, Object> getLatestDataByVid(String vid,
                                           Set<String> columns,
                                           String posType);

    Map<String, Object> getLatestData(String vid,
                                      Set<String> columns,
                                      String posType,
                                      int pageNum,
                                      int pageSize);

    List<Map<String, Object>> searchSamplingTrackLocations(String vid,
                                                           Long startTime,
                                                           Long endTime,
                                                           Integer stepLength,
                                                           String posType,
                                                           int maxRows);

    Map<String, Object> getLatestStatus(String vid,
                                        Integer status,
                                        int pageNum,
                                        int pageSize);



}
