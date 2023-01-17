package com.dfssi.dataplatform.service;

import com.dfssi.dataplatform.entity.database.Mileage;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/28 20:39
 */
public interface EvsMileageVerifyService {

    List<Mileage> searchMileageVerify(String vin,
                                      long starttime,
                                      long endtime);

    Map<String, Object> countVerifyMileage(String vin);
}
