package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.mapper.FeatureAnalysisMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/7/24 13:13
 */
@Component
public class FeatureAnalysisModel {
    private final Logger logger = LoggerFactory.getLogger(FeatureAnalysisModel.class);

    @Autowired
    private FeatureAnalysisMapper featureAnalysisMapper;

    public List<Map<String, Object>> travelAvgMileageByDay(String enterprise,
                                                           String hatchback,
                                                           String vin,
                                                           String beginDay,
                                                           String endDay){
        return featureAnalysisMapper.travelAvgMileageByDay(enterprise, hatchback, vin, beginDay, endDay);
    }

    public List<Map<String, Object>> travelTripAvgMileageByDay(String enterprise,
                                                               String hatchback,
                                                               String vin,
                                                               String beginDay,
                                                               String endDay){
        return featureAnalysisMapper.travelTripAvgMileageByDay(enterprise, hatchback, vin, beginDay, endDay);
    }

    public List<Map<String, Object>> avgTripsByDay(String enterprise,
                                                   String hatchback,
                                                   String vin,
                                                   String beginDay,
                                                   String endDay){
        return featureAnalysisMapper.avgTripsByDay(enterprise, hatchback, vin, beginDay, endDay);
    }

    public List<Map<String, Object>> tripTimeLevelCount(String enterprise,
                                                        String hatchback,
                                                        String vin,
                                                        String beginDay,
                                                        String endDay){
        return featureAnalysisMapper.tripTimeLevelCount(enterprise, hatchback, vin, beginDay, endDay);
    }

    public List<Map<String, Object>> dayTimeLevelCount(String enterprise,
                                                       String hatchback,
                                                       String vin,
                                                       String beginDay,
                                                       String endDay){
        return featureAnalysisMapper.dayTimeLevelCount(enterprise, hatchback, vin, beginDay, endDay);
    }

    public List<Map<String, Object>> tripStartSocCount(String enterprise,
                                                       String hatchback,
                                                       String vin,
                                                       String beginDay,
                                                       String endDay){
        return featureAnalysisMapper.tripStartSocCount(enterprise, hatchback, vin, beginDay, endDay);
    }

    public List<Map<String, Object>> tripStopSocCount(String enterprise,
                                                      String hatchback,
                                                      String vin,
                                                      String beginDay,
                                                      String endDay){
        return featureAnalysisMapper.tripStopSocCount(enterprise, hatchback, vin, beginDay, endDay);
    }

    public List<Map<String, Object>> drivingTimeDistributeCount(String enterprise,
                                                                String hatchback,
                                                                String vin,
                                                                String beginDay,
                                                                String endDay){
        return featureAnalysisMapper.drivingTimeDistributeCount(enterprise, hatchback, vin, beginDay, endDay);
    }

    public List<Map<String, Object>> outBoundsVehicles(long timePoint,  String day){
        return featureAnalysisMapper.outBoundsVehicles(timePoint, day);
    }

}
