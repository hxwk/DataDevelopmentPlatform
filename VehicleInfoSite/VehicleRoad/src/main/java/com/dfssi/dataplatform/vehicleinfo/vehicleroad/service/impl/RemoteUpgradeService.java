package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.impl;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.common.RedisService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.*;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.IRemoteUpgradeService;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RemoteUpgradeService implements IRemoteUpgradeService {

    @Autowired
    private RedisService redisService;

    protected Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public Object query0108Response(String queryKey) {
        Object result=null;
        try {
            result=redisService.getValue(queryKey);
            return result;
        }catch (Exception e){
            logger.error("查询远程升级响应失败",e.getMessage());
        }
        return result;
    }

    @Override
    public Req_8108 getReq_8108(Req_8108_Entity entity) {
        Req_8108 req_8108= new Req_8108();
        try {
            req_8108.setId("jts.8108");
            req_8108.setSim(entity.getSim());
            req_8108.setVid(entity.getVid());
            req_8108.setVin(entity.getVin());
            req_8108.setUpdateType(entity.getUpdateType());
            req_8108.setManufacturerId(entity.getManufacturerId());
            req_8108.setVersion(entity.getVersion());
            req_8108.setFilePath(entity.getFilePath());
        } catch (NumberFormatException e) {
            logger.error("入参校验失败："+entity.toString(),e);
            e.printStackTrace();
        }
        return  req_8108;
    }

    @Override
    public Req_E103 getReq_E103(Req_E103_Entity entity) {
        Req_E103 req_E103= new Req_E103();
        try {
            req_E103.setId("jts.E103");
            String times = String.valueOf(System.currentTimeMillis());
            req_E103.setTimestamp(times);
            req_E103.setSim(entity.getSim());
            req_E103.setVid(entity.getVid());
            req_E103.setVin(entity.getVin());
            req_E103.setParamId(entity.getParamId());
            req_E103.setParamValue(entity.getParamValue());
        } catch (NumberFormatException e) {
            logger.error("入参校验失败："+entity.toString(),e);
            e.printStackTrace();
        }
        return  req_E103;
    }

    @Override
    public Object queryE003Response(String queryKey) {

        Object result=null;
        try {
            result=redisService.getValue(queryKey);
            return result;
        }catch (Exception e){
            logger.error("查询远程升级响应失败",e.getMessage());
        }
        return result;
    }


}
