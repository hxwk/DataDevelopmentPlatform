package com.dfssi.dataplatform.service;

import java.util.List;
import java.util.Map;

/**
 * 新能源平台可充电储能系统编码长度检测
 * Created by yanghs on 2018/5/30.
 */
public interface EvsCodeCheckService {


    /**
     * 新能源平台可充电储能系统编码长度检测查询
     * @param vinList
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String,Object> findCodeCheckInfo(List<String> vinList, String startTime, String endTime);
}
