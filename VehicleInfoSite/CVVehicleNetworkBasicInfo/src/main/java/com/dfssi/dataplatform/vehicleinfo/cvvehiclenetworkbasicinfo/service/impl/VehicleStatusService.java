package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.impl;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleStatusDTO;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.Constants;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehicleStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanghs on 2018/5/8.
 */
@Service
public class VehicleStatusService implements IVehicleStatusService {

    private static final String KEYPRE = "gk:vs:";

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
   public List<VehicleStatusDTO> findStatusInfo(List<String> vidList) {
        List<VehicleStatusDTO> statusList = new ArrayList<>();
        for (int i = 0; i < vidList.size(); i++) {
            VehicleStatusDTO vehicleStatus = new VehicleStatusDTO();
            vehicleStatus.setVid(vidList.get(i));
            boolean flag = redisTemplate.hasKey(KEYPRE + vidList.get(i));
            if (flag) {
                vehicleStatus.setStatus(Constants.YES);
            } else {
                vehicleStatus.setStatus(Constants.NO);
            }
            statusList.add(vehicleStatus);
        }
        return statusList;
    }
}
