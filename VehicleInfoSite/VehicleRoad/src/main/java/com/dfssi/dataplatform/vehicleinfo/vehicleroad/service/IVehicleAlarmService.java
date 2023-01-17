package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.AreaAlarmEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.AreaLinkEntity;

import java.util.List;
import java.util.Map;

/**
 * 车辆报警管理
 * Created by yanghs on 2018/9/12.
 */
public interface IVehicleAlarmService {

    /**
     * 车辆报警查询
     * @return
     */
    Map<String, Object> queryalarm(String vid,
                                    Long startTime,
                                    Long stopTime,
                                    String alarmType,
                                    String posType,
                                    int pageNow,
                                    int pageSize);

    Map<String, Object> queryRealTimeAlarm(String vid,
                                           String alarmType,
                                           String posType,
                                           int pageNow,
                                           int pageSize);


    int addArea(AreaAlarmEntity alarmEntity);

    int addAreas(List<AreaAlarmEntity> alarmEntities);


    int deleteArea(String id);

    int updatyeArea(AreaAlarmEntity alarmEntity);

    Map<String, Object> queryAreas(String id,
                                   String name,
                                   Integer type,
                                   int pageNow,
                                   int pageSize);




    int addLink(AreaLinkEntity linkEntity);

    int addLinks(List<AreaLinkEntity> linkEntities);

    int deleteLink(String vid, String id);

    Map<String, Object> queryLinks(String id,
                                   String vid,
                                   String areaId,
                                   Integer areaType,
                                   int pageNow,
                                   int pageSize);

    Map<String, Object> queryLinkAreas(String vid,
                                       Integer type,
                                       int pageNow,
                                       int pageSize);

}
