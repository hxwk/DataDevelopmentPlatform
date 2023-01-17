package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.mapper.EvsDataDetectResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/26 8:48
 */
@Component
public class EvsDataDetectResultModel {

    @Autowired
    private EvsDataDetectResultMapper evsDataDetectResultMapper;

    public Map<String, Object> queryDataQualityInDay(String vin, String day){
        return evsDataDetectResultMapper.queryDataQualityInDay(vin, day);
    }


    /**
     *查询 整车数据 质量检测统计信息
     */
    public Map<String, Object>  queryVehicleDataQuality(String enterprise,
                                                        String hatchback,
                                                        String vin){
        return evsDataDetectResultMapper.queryVehicleDataQuality(enterprise, hatchback, vin);
    }

    /**
     * 查询 指定日期范围 整车数据 质量检测统计信息
     */
    public Map<String, Object>  queryVehicleDataQualityInDay(String enterprise,
                                                             String hatchback,
                                                             String vin,
                                                             String startDay,
                                                             String endDay){
        return evsDataDetectResultMapper.queryVehicleDataQualityInDay(enterprise, hatchback, vin, startDay, endDay);
    }

    /**
     *查询 驱动电机数据 质量检测统计信息
     */
    public Map<String, Object>  queryDriverMotorDataQuality(String enterprise,
                                                            String hatchback,
                                                            String vin){
        return evsDataDetectResultMapper.queryDriverMotorDataQuality(enterprise, hatchback, vin);
    }

    /**
     * 查询 指定日期范围 驱动电机数据 质量检测统计信息
     */
    public Map<String, Object>  queryDriverMotorDataQualityInDay(String enterprise,
                                                                 String hatchback,
                                                                 String vin,
                                                                 String startDay,
                                                                 String endDay){
        return evsDataDetectResultMapper.queryDriverMotorDataQualityInDay(enterprise, hatchback, vin, startDay, endDay);
    }

    /**
     *查询 燃料电池 质量检测统计信息
     */
    public Map<String, Object>  queryFuelCellDataQuality(String enterprise,
                                                         String hatchback,
                                                         String vin){
        return evsDataDetectResultMapper.queryFuelCellDataQuality(enterprise, hatchback, vin);
    }

    /**
     * 查询 指定日期范围 燃料电池数据 质量检测统计信息
     */
    public Map<String, Object>  queryFuelCellDataQualityInDay(String enterprise,
                                                              String hatchback,
                                                              String vin,
                                                              String startDay,
                                                              String endDay){
        return evsDataDetectResultMapper.queryFuelCellDataQualityInDay(enterprise, hatchback, vin, startDay, endDay);
    }

    /**
     *查询 发动机 质量检测统计信息
     */
    public Map<String, Object>  queryEngineDataQuality(String enterprise,
                                                       String hatchback,
                                                       String vin){
        return evsDataDetectResultMapper.queryEngineDataQuality(enterprise, hatchback, vin);
    }

    /**
     * 查询 指定日期范围 发动机 质量检测统计信息
     */
    public Map<String, Object>  queryEngineDataQualityInDay(String enterprise,
                                                            String hatchback,
                                                            String vin,
                                                            String startDay,
                                                            String endDay){
        return evsDataDetectResultMapper.queryEngineDataQualityInDay(enterprise, hatchback, vin, startDay, endDay);
    }

    /**
     *查询 位置数据 质量检测统计信息
     */
    public Map<String, Object>  queryGpsDataQuality(String enterprise,
                                                    String hatchback,
                                                    String vin){
        return evsDataDetectResultMapper.queryGpsDataQuality(enterprise, hatchback, vin);
    }

    /**
     * 查询 指定日期范围 位置数据 质量检测统计信息
     */
    public Map<String, Object>  queryGpsDataQualityInDay(String enterprise,
                                                         String hatchback,
                                                         String vin,
                                                         String startDay,
                                                         String endDay){
        return evsDataDetectResultMapper.queryGpsDataQualityInDay(enterprise, hatchback, vin, startDay, endDay);
    }

    /**
     *查询 极值数据 质量检测统计信息
     */
    public Map<String, Object>  queryExtremumDataQuality(String enterprise,
                                                         String hatchback,
                                                         String vin){
        return evsDataDetectResultMapper.queryExtremumDataQuality(enterprise, hatchback, vin);
    }

    /**
     * 查询 极值数据 位置数据 质量检测统计信息
     */
    public Map<String, Object>  queryExtremumDataQualityInDay(String enterprise,
                                                              String hatchback,
                                                              String vin,
                                                              String startDay,
                                                              String endDay){
        return evsDataDetectResultMapper.queryExtremumDataQualityInDay(enterprise, hatchback, vin, startDay, endDay);
    }

    /**
     *查询 告警数据 质量检测统计信息
     */
    public Map<String, Object>  queryAlarmDataQuality(String enterprise,
                                                      String hatchback,
                                                      String vin){
        return evsDataDetectResultMapper.queryAlarmDataQuality(enterprise, hatchback, vin);
    }

    /**
     * 查询 告警数据 位置数据 质量检测统计信息
     */
    public Map<String, Object>  queryAlarmDataQualityInDay(String enterprise,
                                                           String hatchback,
                                                           String vin,
                                                           String startDay,
                                                           String endDay){
        return evsDataDetectResultMapper.queryAlarmDataQualityInDay(enterprise, hatchback, vin, startDay, endDay);
    }

    /**
     * 查询 数据质量 逻辑检测统计信息
     */
    public Map<String, Object> queryDataLogicCheck(String enterprise, String hatchback, String vin) {
        return evsDataDetectResultMapper.queryDataLogicCheck(enterprise, hatchback, vin);
    }

    /**
     * 查询 数据质量 逻辑检测统计信息
     */
    public Map<String, Object> queryDataLogicCheckInDay(String enterprise,
                                                        String hatchback,
                                                        String vin,
                                                        String startDay,
                                                        String endDay) {
        return evsDataDetectResultMapper.queryDataLogicCheckInDay(enterprise, hatchback, vin, startDay, endDay);
    }

    /**
     * 按天聚合 指定条件下的数据质量检测信息
     */
    public List<Map<String, Object>> queryDetectDataQualityByDay(String enterprise,
                                                                 String hatchback,
                                                                 String vin,
                                                                 String startDay,
                                                                 String endDay) {
        return evsDataDetectResultMapper.queryDetectDataQualityByDay(enterprise, hatchback, vin, startDay, endDay);
    }
    /**
     * 按天聚合 指定条件下的数据质量检测统计信息
     */
    public List<Map<String, Object>> queryDetectQualityCountByDay(String enterprise,
                                                                 String hatchback,
                                                                 String vin,
                                                                 String startDay,
                                                                 String endDay) {
        return evsDataDetectResultMapper.queryDetectQualityCountByDay(enterprise, hatchback, vin, startDay, endDay);
    }
    /**
     * 固定时间范围下 指定条件下的数据质量检测统计信息
     */
    public List<Map<String, Object>> queryDetectQualityCount(String enterprise,
                                                                 String hatchback,
                                                                 String vin,
                                                                 String startDay,
                                                                 String endDay) {
        return evsDataDetectResultMapper.queryDetectQualityCount(enterprise, hatchback, vin, startDay, endDay);
    }

    /**
     * 固定时间范围下 指定条件下的数据质量检测统计信息 按vin分组
     */
    public List<Map<String, Object>> queryVinDetectQualityCount(String enterprise,
                                                                 String hatchback,
                                                                 String vin,
                                                                 String startDay,
                                                                 String endDay) {
        return evsDataDetectResultMapper.queryVinDetectQualityCount(enterprise, hatchback, vin, startDay, endDay);
    }

    /**
     * 固定时间范围下 汇总数据质量检测统计信息
     */
    public Map<String, Object> queryDetectQualityAllCount(String enterprise,
                                                                String hatchback,
                                                                String vin,
                                                                String startDay,
                                                                String endDay) {
        return evsDataDetectResultMapper.queryDetectQualityAllCount(enterprise, hatchback, vin, startDay, endDay);
    }

    /**
     * 按天聚合 指定条件下的数据逻辑质量检测信息
     */
    public List<Map<String, Object>> queryDetectDataLogicQualityByDay(String enterprise,
                                                                      String hatchback,
                                                                      String vin,
                                                                      String startDay,
                                                                      String endDay) {
        return evsDataDetectResultMapper.queryDetectDataLogicQualityByDay(enterprise, hatchback, vin, startDay, endDay);
    }

    /**
     * 按天聚合 指定条件下的报文质量检测信息
     */
    public List<Map<String, Object>> queryDetectMessageQualityByDay(String enterprise,
                                                                    String hatchback,
                                                                    String vin,
                                                                    String startDay,
                                                                    String endDay) {
        return evsDataDetectResultMapper.queryDetectMessageQualityByDay(enterprise, hatchback, vin, startDay, endDay);
    }

}
