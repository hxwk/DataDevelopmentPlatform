package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.entity.database.EvsVehicleTypeDetect;
import com.dfssi.dataplatform.mapper.EvsVehicleTypeCheckMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/29 13:21
 */
@Component
public class EvsVehicleTypeCheckModel {

    @Autowired
    private EvsVehicleTypeCheckMapper evsVehicleTypeCheckMapper;


    public List<EvsVehicleTypeDetect> countDectectResult(String enterprise,
                                                         String hatchback,
                                                         String vin,
                                                         String startDay,
                                                         String endDay){
        return evsVehicleTypeCheckMapper.countDectectResult(enterprise, hatchback, vin, startDay, endDay);
    }
}
