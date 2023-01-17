package com.dfssi.dataplatform.service;

import com.dfssi.dataplatform.entity.FeatureAnalysisBasicInformation;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/7/24 13:11
 */
public interface FeatureAnalysisService {

    //基本信息查询
    Map<String, FeatureAnalysisBasicInformation> basicInformation(String enterprise,
                                                                  String hatchback,
                                                                  String vin,
                                                                  String beginDay,
                                                                  String endDay);
    //单次行驶时长统计
    Map<String, Map<Integer, Long>> tripTimeLevelCount(String enterprise,
                                                       String hatchback,
                                                       String vin,
                                                       String beginDay,
                                                       String endDay);

    //单日行驶时长统计
    Map<String, Map<Integer, Long>> dayTimeLevelCount(String enterprise,
                                                      String hatchback,
                                                      String vin,
                                                      String beginDay,
                                                      String endDay);

    //驾驶初始soc
    Map<String, Map<Integer, Long>> tripStartSocCount(String enterprise,
                                                      String hatchback,
                                                      String vin,
                                                      String beginDay,
                                                      String endDay);


    //驾驶结束soc
    Map<String, Map<Integer, Long>> tripStopSocCount(String enterprise,
                                                     String hatchback,
                                                     String vin,
                                                     String beginDay,
                                                     String endDay);

    //驾驶结束soc
    Map<String, Map<Integer, Long>> drivingTimeDistributeCount(String enterprise,
                                                               String hatchback,
                                                               String vin,
                                                               String beginDay,
                                                               String endDay);

    //车辆出省/市情况监控
    Map<String, Object> outBoundsVehicles(Long timePoint,
                                          int pageNow,
                                          int pageSize);
}
