package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service;


import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleAlarmDTO;

import java.util.List;

/**
 * 车辆报警服务
 * Created by yanghs on 2018/5/8.
 */
public interface IVehicleAlarmService {

    /**
     * 通过vid查询车辆报警信息
     * @param vidList
     * @param startTime
     * @param endTime
     * @return
     */
    List<VehicleAlarmDTO> findAlarmInfo(List<String> vidList, String startTime, String endTime);
}
