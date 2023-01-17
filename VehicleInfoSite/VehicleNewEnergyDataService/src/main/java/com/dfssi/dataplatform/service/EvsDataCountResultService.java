package com.dfssi.dataplatform.service;

import com.dfssi.dataplatform.entity.count.MaxOnlineAndRun;
import com.dfssi.dataplatform.entity.count.MileDetail;
import com.dfssi.dataplatform.entity.count.TotalMileAndTime;
import com.dfssi.dataplatform.entity.count.TotalRunningMsg;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *    greenplum 车辆行驶统计信息统计查询
 * @author LiXiaoCong
 * @version 2018/4/21 12:53
 */
public interface EvsDataCountResultService {

    TotalMileAndTime queryTotalMileAndTime();

    TotalRunningMsg queryTotalMileAndTimeByVin(String vin);

    Map<String, Double> queryTotalEnergy();

    MileDetail queryMileDetail(String vin,
                               Long startTime,
                               Long endTime);

    List<MaxOnlineAndRun> queryCurrentMonthMaxOnlineAndRunByDay();

    Map<String, Object> countMonthTotalOnlineAndRun(String month);

    Map<String, Object> countVehicleMileAndTime(String enterprise,
                                                      String hatchback,
                                                      String vin,
                                                      Long startTime,
                                                      Long endTime,
                                                      int pageNow,
                                                      int pageSize);

    List<Map<String, Object>> queryWarningCountByDay(String enterprise,
                                                     String hatchback,
                                                     String vin,
                                                     Long startTime,
                                                     Long endTime);

    Map<String, Object> queryWarningCount(String enterprise,
                                          String hatchback,
                                          String vin,
                                          Long startTime,
                                          Long endTime);

    Map<String, Object> queryWarningCountTotal(String enterprise,
                                               String hatchback,
                                               String vin);



    Map<String, Object> queryOnlineCountByDay(String enterprise,
                                              String hatchback,
                                              Long startTime,
                                              Long endTime,
                                              int pageNow,
                                              int pageSize);

    Map<String, Object> queryOnlineCountInEnterpriseByDay(String enterprise,
                                                          String hatchback,
                                                          Long startTime,
                                                          Long endTime,
                                                          int pageNow,
                                                          int pageSize);
}
