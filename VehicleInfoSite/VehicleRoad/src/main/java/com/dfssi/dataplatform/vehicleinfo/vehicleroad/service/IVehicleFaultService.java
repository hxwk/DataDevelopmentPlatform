package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service;

import java.util.Map;

/**
 * 车辆故障管理
 * Created by yanghs on 2018/9/12.
 */
public interface IVehicleFaultService {

    /**
     * 车辆故障查询
     * @return
     */
    Map<String, Object> queryFault(String vid,
                                   Long startTime,
                                   Long stopTime,
                                   String alarmType,
                                   Integer spn,
                                   Integer fmi,
                                   Integer sa,
                                   String posType,
                                   int pageNow,
                                   int pageSize) ;

    Map<String, Object> queryRealTimeFault(String vid,
                                           String alarmType,
                                           Integer spn,
                                           Integer fmi,
                                           Integer sa,
                                           String posType,
                                           int pageNow,
                                           int pageSize);
}
