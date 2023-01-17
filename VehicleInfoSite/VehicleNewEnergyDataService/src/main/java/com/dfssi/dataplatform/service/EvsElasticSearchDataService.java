package com.dfssi.dataplatform.service;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *     es质量检测错误细节数据统计查询
 * @author LiXiaoCong
 * @version 2018/4/27 9:11
 */
public interface EvsElasticSearchDataService {

    Map<String, Object> searchDetectErrDetail(String[] enterprise,
                                              String[] hatchback,
                                              String[] vin,
                                              String alarmTypeName,
                                              String alarmContent,
                                              Long startTime,
                                              Long endTime,
                                              int from,
                                              int size);

    Map<String, Object> searchLogicDetectErrDetail(String[] enterprise,
                                              String[] hatchback,
                                              String[] vin,
                                              String alarmTypeName,
                                              String alarmContent,
                                              Long startTime,
                                              Long endTime,
                                              int from,
                                              int size);

    Map<String, Object> searchMessageDetectErrDetail(String[] enterprise,
                                                     String[] hatchback,
                                                     String[] vin,
                                                     String alarmTypeName,
                                                     String alarmContent,
                                                     Long startTime,
                                                     Long endTime,
                                                     int from,
                                                     int size);

    Map<String, Object> searchVehicleAlarms(String[] enterprise,
                                             String[] hatchback,
                                             String[] vin,
                                             Integer alarmLevel,
                                             String alarmContent,
                                             Integer handleStatus,
                                             Long startTime,
                                             Long endTime,
                                             int from,
                                             int size);

    void handleStatus2Es(String vehcleAlarms);

    Map<String, Object> countVehicleAlarmsWithCondition(String[] enterprise,
                                                       String[] hatchback,
                                                       String[] vin,
                                                       Long startTime,
                                                       Long endTime);

    Map<String, Object> searchVehicleAlarmDetail(String[] enterprise,
                                             String[] hatchback,
                                             String[] vin,
                                             Integer alarmLevel,
                                             String alarmContent,
                                             Long startTime,
                                             Long endTime,
                                             int from,
                                             int size);

}
