package com.dfssi.dataplatform.service;

import java.util.List;
import java.util.Map;

/**
 * 登出报文检测
 * Created by yanghs on 2018/5/30.
 */
public interface EvsLogoutCheckService {


    /**
     * 新能源平台登出报文检测查询
     * @param vinList
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String,Object> findLogoutCheckInfo(List<String> vinList, String startTime, String endTime);
}
