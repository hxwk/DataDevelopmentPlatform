package com.dfssi.dataplatform.service;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * greenplum 车辆数据质量检测统计信息统计查询
 *
 * @author LiXiaoCong
 * @version 2018/4/26 13:19
 */
public interface EvsDataDetectResultService {

    Map<String, Object> queryDataQualityInDay(String vin, String day);

    /**
     * 查询 各项数据 质量检测统计信息
     */
    Map<String, Object> queryItemDataQuality(String item, String enterprise, String hatchback, String vin);

    /**
     * 查询 指定日期范围 各项数据 质量检测统计信息
     */
    Map<String, Object> queryItemDataQualityInDay(String item,
                                                  String enterprise,
                                                  String hatchback,
                                                  String vin,
                                                  Long startTime,
                                                  Long endTime);

    /**
     * 查询 各项数据 逻辑质量检测统计信息
     */
    Map<String, Object> queryDataLogicCheck(String enterprise, String hatchback, String vin);

    /**
     * 查询 指定日期范围 各项数据 逻辑质量检测统计信息
     */
    Map<String, Object> queryDataLogicCheckInDay(String enterprise,
                                                 String hatchback,
                                                 String vin,
                                                 Long startTime,
                                                 Long endTime);

    /**
     * 按天聚合 指定条件下的数据质量检测各项统计信息
     */
    List<Map<String, Object>> queryDetectDataQualityByDay(String enterprise,
                                                          String hatchback,
                                                          String vin,
                                                          Long startTime,
                                                          Long endTime);
    /**
     * 按天聚合 指定条件下的数据质量检测统计信息
     */
    Map<String, Object> queryDetectQualityCountByDay(String enterprise,
                                                     String hatchback,
                                                     String vin,
                                                     Long startTime,
                                                     Long endTime,
                                                     int pageNow,
                                                     int pageSize);
    /**
     * 固定时间范围下 指定条件下的数据质量检测统计信息
     */
    Map<String, Object> queryDetectQualityCount(String enterprise,
                                                String hatchback,
                                                String vin,
                                                Long startTime,
                                                Long endTime,
                                                int pageNow,
                                                int pageSize);

    /**
     * 固定时间范围下 指定条件下的数据质量检测统计信息 按车辆vin分组
     */
    Map<String, Object> queryVinDetectQualityCount(String enterprise,
                                                   String hatchback,
                                                   String vin,
                                                   Long startTime,
                                                   Long endTime,
                                                   int pageNow,
                                                   int pageSize);

    /**
     * 固定时间范围下 汇总数据质量检测统计信息
     */
    Map<String, Object> queryDetectQualityAllCount(String enterprise,
                                                   String hatchback,
                                                   String vin,
                                                   Long startTime,
                                                   Long endTime);

    /**
     * 按天聚合 指定条件下的数据逻辑质量检测信息
     */
    List<Map<String, Object>> queryDetectDataLogicQualityByDay(String enterprise,
                                                               String hatchback,
                                                               String vin,
                                                               Long startTime,
                                                               Long endTime);

    /**
     * 按天聚合 指定条件下的报文质量检测信息
     */
    List<Map<String, Object>> queryDetectMessageQualityByDay(String enterprise,
                                                             String hatchback,
                                                             String vin,
                                                             Long startTime,
                                                             Long endTime);

}
