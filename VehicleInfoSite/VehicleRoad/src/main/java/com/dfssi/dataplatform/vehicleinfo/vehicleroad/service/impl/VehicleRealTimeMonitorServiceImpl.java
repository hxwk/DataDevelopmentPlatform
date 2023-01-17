package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.impl;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.config.RoadVehicleConfig;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.dao.VehicleRealTimeMonitorDao;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ElasticSearchUtil;
import com.google.common.collect.Maps;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/21 11:30
 */
//@Service
public class VehicleRealTimeMonitorServiceImpl  {
    private final Logger logger = LoggerFactory.getLogger(VehicleRealTimeMonitorServiceImpl.class);

    @Autowired
    private VehicleRealTimeMonitorDao vehicleRealTimeMonitorDao;

    @Autowired
    private RoadVehicleConfig roadVehicleConfig;


    //@Override
    public Map<String, Object> getLatestDataByVid(String vid,
                                                  Set<String> columns,
                                                  String posType) {
        Map<String, Object> map = vehicleRealTimeMonitorDao.getLatestDataByVid(vid, columns);
        ElasticSearchUtil.locationTypeChange(map, posType);
        return map;
    }

    //@Override
    public Map<String, Object> getLatestData(String vid,
                                             Set<String> columns,
                                             String posType,
                                             int pageNum,
                                             int pageSize) {
        int from = (pageNum - 1) * pageSize;
        SearchHits searchHits = vehicleRealTimeMonitorDao.getLatestData(vid, columns,  from, pageSize);

        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = ElasticSearchUtil.selectItemWithLocationTypeChange(searchHits, posType);

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }

   // @Override
    public List<Map<String, Object>> searchSamplingTrackLocations(String vid,
                                                                  Long startTime,
                                                                  Long endTime,
                                                                  Integer stepLength,
                                                                  String posType,
                                                                  int maxRows) {
        if(stepLength == null) {
            stepLength = 5;
        }
        return vehicleRealTimeMonitorDao.searchSamplingTrackLocationCans(vid, startTime, endTime, stepLength, posType, maxRows);
    }


   // @Override
    public Map<String, Object> getLatestStatus(String vid,
                                               Integer status,
                                               int pageNum,
                                               int pageSize) {

        int from = (pageNum - 1) * pageSize;
        long currentTime = System.currentTimeMillis();

        SearchHits searchHits = vehicleRealTimeMonitorDao.getLatestStatus(vid, status, currentTime, from, pageSize);
        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = Arrays.stream(searchHits.getHits()).map(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            if (status == null) {
                long gpsTime = (long) sourceAsMap.get("receiveMsgTime");
                double speed = (double) sourceAsMap.get("speed");
                double speed1 = (double) sourceAsMap.get("speed1");

                sourceAsMap.put("status", getStatus(currentTime, gpsTime, speed, speed1));
            } else {
                sourceAsMap.put("status", status);
            }

            sourceAsMap.remove("speed");
            sourceAsMap.remove("speed1");

            return sourceAsMap;
        }).collect(Collectors.toList());

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }

    private int getStatus(long currentTime, long gpsTime, double speed, double speed1){
        int status;
        if(currentTime - gpsTime < roadVehicleConfig.getOnlineThreshold()  * 60 * 1000L){
            if(speed == 0 && speed1 == 0){
                status = 2;
            }else{
                status = 3;
            }
        }else{
            status = 0;
        }
        return status;
    }

}
