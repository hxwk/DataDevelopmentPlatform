package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleMileageDTO;

import java.util.List;

/**
 * 车辆里程油耗信息
 * Created by yanghs on 2018/5/10.
 */
public interface IVehicleMileageService {

    /**
     * 查询车辆里程油耗信息
     * @param vidList
     * @return
     */
    List<VehicleMileageDTO> findMileageInfo(List<String> vidList);
}
