package com.dfssi.dataplatform.service.impl;

import com.dfssi.dataplatform.entity.database.Mileage;
import com.dfssi.dataplatform.model.EvsMileageVerifyModel;
import com.dfssi.dataplatform.service.EvsMileageVerifyService;
import com.dfssi.dataplatform.utils.NumberUtil;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/28 20:41
 */
@Service
public class EvsMileageVerifyServiceImpl implements EvsMileageVerifyService {
    private final Logger logger = LoggerFactory.getLogger(EvsMileageVerifyServiceImpl.class);

    @Autowired
    private EvsMileageVerifyModel evsMileageVerifyModel;

    @Override
    public List<Mileage> searchMileageVerify(String vin,
                                             long starttime,
                                             long endtime) {

        String startDay = new DateTime(starttime).toString("yyyyMMdd");
        String endDay = new DateTime(endtime).toString("yyyyMMdd");

        return evsMileageVerifyModel.searchMileageVerify(vin, startDay, endDay);
    }

    /*
      （1）使用核查周期内的首次里程、末次里程计算总在线里程；
      （2）对核查周期内所有天的有效里程、核算里程进行比对；
      （3）有效里程、核算里程取较大值，用作新的参照里程；
      （4）计算总在线里程和参照里程偏差率，小于等于 7%选取总在线里程为最
           终核算里程，否则选取两个里程的较小值为最终核算里程。
     */
    private Map<String, Object> verify(List<Mileage> mileages){

        Map<String, Object> res = Maps.newHashMap();

        if(res != null){
            int size = mileages.size();
            if(size > 0){
                 double totalOnlieMile = mileages.get(size -1).getOnlineMile() - mileages.get(0).getOnlineMile();

                 double totalValidMile = 0.0;
                 double totalVerifyMile = 0.0;
                 double totaltime = 0.0;
                 Mileage mileage;
                 for (int i = 0; i < size; i++) {
                    mileage = mileages.get(i);
                    totalValidMile += mileage.getValidMile();
                    totalVerifyMile += mileage.getVerifyMile();
                    totaltime += mileage.getTotaltime();
                 }

                 double totalModelMile = Math.max(totalValidMile, totalVerifyMile);

                 double totalmile;
                 double abs = Math.abs((totalOnlieMile - totalModelMile) / totalOnlieMile);
                 if(abs <= 0.07){
                     totalmile = totalOnlieMile;
                 }else{
                     totalmile =  Math.min(totalOnlieMile, totalModelMile);
                 }

                res.put("totalmile", totalmile);
                res.put("totaltime", totaltime);
                res.put("totalday", size);
                res.put("avgdaytime", NumberUtil.rounding(totaltime/30.0, 1));
                res.put("avgdaymile", NumberUtil.rounding(totalmile/30.0, 1));
                res.put("avgspeed", NumberUtil.rounding(totalmile/totaltime, 1));
            }
        }

        return res;
    }


    @Override
    public Map<String, Object> countVerifyMileage(String vin) {
        Map<String, Object> map = evsMileageVerifyModel.countVerifyMileage(vin);
        if(map == null){
            map = Maps.newHashMap();
            map.put("verifyMile", 0.0);
        }
        return map;
    }
}
