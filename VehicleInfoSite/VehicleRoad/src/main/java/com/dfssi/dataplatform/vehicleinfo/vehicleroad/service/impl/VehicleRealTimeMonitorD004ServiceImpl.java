package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.impl;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.config.RoadVehicleConfig;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.dao.VehicleRealTimeMonitorD004Dao;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.VehicleRealTimeMonitorService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ElasticSearchUtil;
import com.google.common.collect.Maps;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class VehicleRealTimeMonitorD004ServiceImpl implements VehicleRealTimeMonitorService {
    private final Logger logger = LoggerFactory.getLogger(VehicleRealTimeMonitorD004ServiceImpl.class);

    @Autowired
    private VehicleRealTimeMonitorD004Dao vehicleRealTimeMonitorD004Dao;

    @Autowired
    private RoadVehicleConfig roadVehicleConfig;


    @Override
    public Map<String, Object> getLatestDataByVid(String vid,
                                                  Set<String> columns,
                                                  String posType) {
        Map<String, Object> map = vehicleRealTimeMonitorD004Dao.getLatestDataByVid(vid, columns);
        ElasticSearchUtil.locationTypeChange(map, posType, "longitude", "latitude");
        return map;
    }

    @Override
    public Map<String, Object> getLatestData(String vid,
                                             Set<String> columns,
                                             String posType,
                                             int pageNum,
                                             int pageSize) {
        int from = (pageNum - 1) * pageSize;
        if(columns != null){
            columns.add("receiveTime");
            columns.add("TachographVehicleSpeed");
        }
        long currentTime = System.currentTimeMillis();
        SearchHits searchHits = vehicleRealTimeMonitorD004Dao.getLatestData(vid, columns,  from, pageSize);

        long total = searchHits.getTotalHits();
        SearchHit[] hits = searchHits.getHits();

        List<Map<String, Object>> list = Arrays.stream(hits).map(hit ->{
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            ElasticSearchUtil.locationTypeChange(sourceAsMap, posType, "longitude", "latitude");

            //处理状态
            long gpsTime = (long) sourceAsMap.get("receiveTime");
            double gpsSpeed = (double) sourceAsMap.getOrDefault("TachographVehicleSpeed",  0.0);
            sourceAsMap.put("status", getStatus(currentTime, gpsTime, gpsSpeed));

            return sourceAsMap;
        }).collect(Collectors.toList());


        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }

    @Override
    public List<Map<String, Object>> searchSamplingTrackLocations(String vid,
                                                                  Long startTime,
                                                                  Long endTime,
                                                                  Integer stepLength,
                                                                  String posType,
                                                                  int maxRows) {
        if(stepLength == null) {
            stepLength = 5;
        }
        return vehicleRealTimeMonitorD004Dao.searchSamplingTrackLocations(vid, startTime, endTime, stepLength, posType, maxRows);
    }


    @Override
    public Map<String, Object> getLatestStatus(String vid,
                                               Integer status,
                                               int pageNum,
                                               int pageSize) {

        int from = (pageNum - 1) * pageSize;
        long currentTime = System.currentTimeMillis();

        SearchHits searchHits = vehicleRealTimeMonitorD004Dao.getLatestStatus(vid, status, currentTime, from, pageSize);
        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = Arrays.stream(searchHits.getHits()).map(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            if (status == null) {
                long gpsTime = (long) sourceAsMap.get("receiveTime");
                double gpsSpeed = (double) sourceAsMap.getOrDefault("TachographVehicleSpeed", 0.0);

                sourceAsMap.put("status", getStatus(currentTime, gpsTime, gpsSpeed));
            } else {
                sourceAsMap.put("status", status);
            }

            sourceAsMap.remove("TachographVehicleSpeed");

            return sourceAsMap;
        }).collect(Collectors.toList());

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }

    private int getStatus(long currentTime, long gpsTime, double speed){
        int status;
        if(currentTime - gpsTime < roadVehicleConfig.getOnlineThreshold()  * 60 * 1000L){
            if(speed == 0){
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
