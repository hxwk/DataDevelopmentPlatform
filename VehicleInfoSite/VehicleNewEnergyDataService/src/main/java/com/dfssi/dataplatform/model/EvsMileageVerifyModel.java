package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.entity.database.Mileage;
import com.dfssi.dataplatform.mapper.EvsMileageVerifyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/28 20:37
 */
@Component
public class EvsMileageVerifyModel {

    @Autowired
    private EvsMileageVerifyMapper evsMileageVerifyMapper;

    public List<Mileage> searchMileageVerify(String vin,
                                             String startDay,
                                             String endDay){
        return evsMileageVerifyMapper.searchMileageVerify(vin, startDay, endDay);
    }

    public Map<String, Object> countVerifyMileage(String vin) {
        return evsMileageVerifyMapper.countVerifyMileage(vin);
    }

}
