package com.dfssi.dataplatform.service.impl;

import com.dfssi.dataplatform.service.EvsSnCheckService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanghs on 2018/5/30.
 */
@Service
public class EvsSnCheckServiceImpl implements EvsSnCheckService {

    @Override
    public Map<String, Object> findSnCheckInfo(List<String> vinList, String startTime, String endTime) {
        //TODO
        HashMap map=new HashMap();
        map.put("total","11616");
        map.put("failCount","3");
        return map;
    }
}
