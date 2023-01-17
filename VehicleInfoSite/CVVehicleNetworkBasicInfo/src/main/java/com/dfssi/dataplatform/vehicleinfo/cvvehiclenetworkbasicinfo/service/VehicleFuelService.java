package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleFuelCountByDaysEntity;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *    车辆油耗统计分析信息接口
 * @author LiXiaoCong
 * @version 2018/5/14 20:50
 */
public interface VehicleFuelService {

    Map<String, Object> countFuelByDay(String vid,
                                       long starttime,
                                       long endtime,
                                       int pagenum,
                                       int pagesize);

    Map<String, Object> countFuelByItem(String item,
                                        String vals,
                                        long starttime,
                                        long endtime);

    Map<String, Object> countFuelAnalysisInDays(String vid,
                                                long starttime,
                                                long endtime);

    List<VehicleFuelCountByDaysEntity> countFuelAnalysisByDays(String vid,
                                                               String startDay,
                                                               String endDay);
    List<Map<String, Object>> searchLatestStatusByVid(String vid);
}
