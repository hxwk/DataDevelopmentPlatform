package com.dfssi.dataplatform.service;

import java.util.List;
import java.util.Map;

/**
 * 流水号检测
 * Created by yanghs on 2018/5/30.
 */
public interface EvsSnCheckService {


    /**
     * 新能源平台流水号检测查询
     * @param vinList
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String,Object> findSnCheckInfo(List<String> vinList, String startTime, String endTime);
}
