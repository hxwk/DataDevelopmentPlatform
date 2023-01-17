package com.dfssi.dataplatform.service.impl;

import com.dfssi.dataplatform.config.EvsDataSearchConfig;
import com.dfssi.dataplatform.entity.database.EvsVehicleTypeDetect;
import com.dfssi.dataplatform.model.EvsVehicleTypeCheckModel;
import com.dfssi.dataplatform.service.EvsVehicleTypeCheckService;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/29 13:24
 */
@Service
public class EvsVehicleTypeCheckServiceImpl implements EvsVehicleTypeCheckService {
    private final Logger logger = LoggerFactory.getLogger(EvsVehicleTypeCheckServiceImpl.class);

    @Autowired
    private EvsDataSearchConfig evsDataSearchConfig;

    @Autowired
    private EvsVehicleTypeCheckModel evsVehicleTypeCheckModel;

    @Override
    public Map<String, Object> countDectectResult(String enterprise,
                                                         String hatchback,
                                                         String vin,
                                                         long starttime,
                                                         long endtime) {
        String startDay = new DateTime(starttime).toString("yyyyMMdd");
        String endDay = new DateTime(endtime).toString("yyyyMMdd");

        List<EvsVehicleTypeDetect> evsVehicleTypeDetects =
                evsVehicleTypeCheckModel.countDectectResult(enterprise, hatchback, vin, startDay, endDay);

        return vehicleTypeCheck(evsVehicleTypeDetects, vin);
    }

    /**
     *    错误原因： 0 无数据，1 错误率过高，2 数据缺失， 3 错误率过高和数据缺失均存在
     *
     */
    private Map<String, Object> vehicleTypeCheck(List<EvsVehicleTypeDetect> evsVehicleTypeDetects, String vin){

        Map<String, Object> res = Maps.newHashMap();
        if(evsVehicleTypeDetects != null && !evsVehicleTypeDetects.isEmpty()){

            Map<String, Map<String, EvsVehicleTypeDetect>> all = Maps.newHashMap();
            Map<String, Map<String, EvsVehicleTypeDetect>> err = Maps.newHashMap();

            //按天分类并检查车辆数据是否满足条件
            double standardRate = evsDataSearchConfig.getErrorRate();
            double errorRate;
            for(EvsVehicleTypeDetect evsVehicleTypeDetect: evsVehicleTypeDetects){
                errorRate = (evsVehicleTypeDetect.getErrorCount() * 1.0 / (evsVehicleTypeDetect.getTotalCount() * 1.0));
                evsVehicleTypeDetect.setErrorRate(errorRate);
                evsVehicleTypeDetect.setStandardRate(standardRate);
                if(errorRate > standardRate){
                    evsVehicleTypeDetect.setOverRate(true);
                    Map<String, EvsVehicleTypeDetect> errMap = err.get(evsVehicleTypeDetect.getDay());
                    if(errMap == null){
                        errMap = Maps.newHashMap();
                        err.put(evsVehicleTypeDetect.getDay(), errMap);
                    }
                    errMap.put(evsVehicleTypeDetect.getVin(), evsVehicleTypeDetect);
                }

                Map<String, EvsVehicleTypeDetect> map = all.get(evsVehicleTypeDetect.getDay());
                if(map == null){
                    map = Maps.newHashMap();
                    all.put(evsVehicleTypeDetect.getDay(), map);
                }
                map.put(evsVehicleTypeDetect.getVin(), evsVehicleTypeDetect);
            }

            //检查是否存在缺失车辆
            Set<String> vins = Sets.newHashSet(vin.split(","));
            Map<String, Map<String, EvsVehicleTypeDetect>> missing = Maps.newHashMap();
            all.forEach((day, entry) ->{
                Set<String> strings = entry.keySet();
                for(String v : vins){
                    if(!Strings.isNullOrEmpty(v) && !strings.contains(v)){
                        EvsVehicleTypeDetect evsVehicleTypeDetect = new EvsVehicleTypeDetect();
                        evsVehicleTypeDetect.setDay(Long.parseLong(day));
                        evsVehicleTypeDetect.setVin(v);
                        evsVehicleTypeDetect.setMissing(true);

                        Map<String, EvsVehicleTypeDetect> map = missing.get(day);
                        if(map == null){
                            map = Maps.newHashMap();
                            missing.put(day, map);
                        }
                        map.put(v, evsVehicleTypeDetect);
                    }
                }
            });

            int errSize = err.size();
            int missSize = missing.size();
            if(errSize > 0 || missSize > 0){
                res.put("access", false);
                res.put("all", all);
                if(errSize > 0 && missSize == 0){
                    res.put("reason", 1);
                    res.put("overRate", err);
                }else if(errSize == 0 && missSize > 0){
                    res.put("reason", 2);
                    res.put("missing", missing);
                }else{
                    res.put("reason", 3);
                    res.put("overRate", err);
                    res.put("missing", missing);
               }
            }else{
                res.put("access", true);
                res.put("all", all);
            }
        }else{
            res.put("access", false);
            res.put("reason", 0);
        }

        return res;
    }

}
