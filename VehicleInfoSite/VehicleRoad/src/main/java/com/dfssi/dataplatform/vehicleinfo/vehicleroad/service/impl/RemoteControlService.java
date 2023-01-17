package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.impl;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.RemoteControlEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.Req_8105;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.IRemoteControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by yanghs on 2018/9/12.
 */
@Service
public class RemoteControlService implements IRemoteControlService {

    protected Logger logger = LoggerFactory.getLogger(getClass());


    private RestTemplate restTemplate =new RestTemplate();

    @Value("http://192.168.80.31:8081")
    private String accessServiceUrl;


    @Override
    public Req_8105 getReq_8105(RemoteControlEntity remoteControlEntity) throws Exception{
        Req_8105 req_8105= new Req_8105();
        req_8105.setId("jts.8105");
        req_8105.setCommandParam(remoteControlEntity.getCommandParam());
        req_8105.setCommandType(remoteControlEntity.getCommandType());
        req_8105.setVid(remoteControlEntity.getVid());
        req_8105.setSim(remoteControlEntity.getSim());
        return  req_8105;
    }
}
