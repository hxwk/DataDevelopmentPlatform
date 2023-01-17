package com.dfssi.dataplatform.service;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/29 13:23
 */
public interface EvsVehicleTypeCheckService {

    Map<String, Object> countDectectResult(String enterprise,
                                           String hatchback,
                                           String vin,
                                           long starttime,
                                           long endtime);
}
