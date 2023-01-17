package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service;

import java.util.List;
import java.util.Map;

/**
 * 车辆轨迹服务
 */
public interface IVehicleTrackService {

    Map getCurrentLocationsByVid(String vid);

    List<Map> getTrackByVid(String vid, long starttime, long endtime, int interval);

    List<Map> getCurrentLocationList(List<String> vids);
}
