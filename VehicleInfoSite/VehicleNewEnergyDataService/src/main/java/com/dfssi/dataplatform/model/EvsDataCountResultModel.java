package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.entity.count.*;
import com.dfssi.dataplatform.mapper.EvsDataCountResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *    新能源数据统计结果查询Model
 * @author LiXiaoCong
 * @version 2018/4/21 12:52
 */
@Component
public class EvsDataCountResultModel {

    @Autowired
    private EvsDataCountResultMapper evsDataCountResultMapper;

    public TotalMileAndTime queryTotalMileAndTime(){
        return evsDataCountResultMapper.queryTotalMileAndTime();
    }

    public TotalRunningMsg queryTotalMileAndTimeByVin(String vin){
        return evsDataCountResultMapper.queryTotalMileAndTimeByVin(vin);
    }

    public Map<String, Double> queryTotalEnergy(){
        return evsDataCountResultMapper.queryTotalEnergy();
    }

    public MileDetail queryMileDetail(String vin,
                                      String startDay,
                                      String endDay){
        return evsDataCountResultMapper.queryMileDetail(vin, startDay, endDay);
    }

    public List<MaxOnlineAndRun> queryMaxOnlineAndRunByDay(String startDay, String endDay){
        return evsDataCountResultMapper.queryMaxOnlineAndRunByDay(startDay, endDay);
    }

    public Map<String, Object> countMonthTotalOnlineAndRun(String startDay, String endDay){
        return evsDataCountResultMapper.countMonthTotalOnlineAndRun(startDay, endDay);
    }

    public List<VehicleMileAndTime> countVehicleMileAndTime(String enterprise,
                                                            String hatchback,
                                                            String vin,
                                                            String startDay,
                                                            String endDay){
        return evsDataCountResultMapper.countVehicleMileAndTime(enterprise, hatchback, vin, startDay, endDay);
    }

    public List<Map<String, Object>> queryWarningCountByDay(String enterprise,
                                                            String hatchback,
                                                            String vin,
                                                            String startDay,
                                                            String endDay){
        return evsDataCountResultMapper.queryWarningCountByDay(enterprise, hatchback, vin, startDay, endDay);
    }

    public Map<String, Object> queryWarningCount(String enterprise,
                                                       String hatchback,
                                                       String vin,
                                                       String startDay,
                                                       String endDay){
        return evsDataCountResultMapper.queryWarningCount(enterprise, hatchback, vin, startDay, endDay);
    }

    public Map<String, Object> queryWarningCountTotal(String enterprise,
                                                            String hatchback,
                                                            String vin){
        return evsDataCountResultMapper.queryWarningCountTotal(enterprise, hatchback, vin);
    }

    public List<VehicleOnlineCount> queryOnlineCountByDay(String enterprise,
                                                          String hatchback,
                                                          String startDay,
                                                          String endDay){
        return evsDataCountResultMapper.queryOnlineCountByDay(enterprise, hatchback, startDay, endDay);
    }

    public List<VehicleOnlineCount> queryOnlineCountInEnterpriseByDay(String enterprise,
                                                                      String hatchback,
                                                                      String startDay,
                                                                      String endDay){
        return evsDataCountResultMapper.queryOnlineCountInEnterpriseByDay(enterprise, hatchback, startDay, endDay);
    }

}
