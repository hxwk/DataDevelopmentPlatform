package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleStatusDTO;

import java.util.List;

/**
 * 车辆状态服务
 * Created by yanghs on 2018/5/8.
 */
public interface IVehicleStatusService {

    /**
     * 通过vid查询车辆状态信息
     * @param vidList
     * @return
     */
    List<VehicleStatusDTO> findStatusInfo(List<String> vidList);
}
