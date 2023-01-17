package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.PeriodicReportDTO;

/**
 * 车辆实时信息周期上报
 * Created by yanghs on 2018/5/30.
 */
public interface IVehiclePeriodicReportService {

    /**
     * 查询车辆周期上报信息
     * @param vid
     * @return
     */
    PeriodicReportDTO findPeriodicReportInfo(String vid);
}
