package com.dfssi.dataplatform.service;

import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/21 10:22
 */

public interface DemoService {

    Map<String, Object> getVehicle(int pagenum, int pageSize);

    Map<String, Object> getDetecteRules(int pagenum, int pageSize);

}
