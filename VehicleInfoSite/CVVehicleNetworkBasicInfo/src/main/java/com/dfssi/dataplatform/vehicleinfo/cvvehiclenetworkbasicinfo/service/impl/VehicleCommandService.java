package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.RedisService;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehicleCommandService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yanghs on 2018/5/24.
 */
@Service
public class VehicleCommandService implements IVehicleCommandService{

    private static final String PREFIX_COMMAND="command:";

    @Autowired
    private RedisService redisService;

    @Override
    public List findCommandInfo(String vid, String msgId) {
        List resultList=new ArrayList<>();
        String querykey=PREFIX_COMMAND+"*";
        if (StringUtils.isNotEmpty(vid)&&StringUtils.isEmpty(msgId)){
            querykey=PREFIX_COMMAND+vid+":*";
        }
        if (StringUtils.isEmpty(vid)&&StringUtils.isNotEmpty(msgId)){
            querykey=PREFIX_COMMAND+"*"+msgId+"*";
        }
        if (StringUtils.isNotEmpty(vid)&&StringUtils.isNotEmpty(msgId)){
            querykey=PREFIX_COMMAND+vid+":"+msgId+":*";
        }
       List list=redisService.getValueList(querykey);
        for (int i = 0; i <list.size() ; i++) {
            resultList.add(JSONObject.parseObject(String.valueOf(list.get(i)), Map.class));
        }
        return resultList;
    }
}
