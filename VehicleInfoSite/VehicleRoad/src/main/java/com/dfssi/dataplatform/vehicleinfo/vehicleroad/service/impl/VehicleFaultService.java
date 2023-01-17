package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.impl;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.dao.VehicleFaultDao;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.IVehicleFaultService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ElasticSearchUtil;
import com.google.common.collect.Maps;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.elasticsearch.client.transport.TransportClient;

/**
 * Created by yanghs on 2018/9/12.
 */
@Service
public class VehicleFaultService implements IVehicleFaultService {


    @Autowired
    private VehicleFaultDao vehicleFaultDao;


    @Override
    public Map<String, Object> queryFault(String vid,
                                          Long startTime,
                                          Long stopTime,
                                          String alarmType,
                                          Integer spn,
                                          Integer fmi,
                                          Integer sa,
                                          String posType,
                                          int pageNow,
                                          int pageSize) {

        int from = (pageNow - 1) * pageSize;

        SearchHits searchHits = vehicleFaultDao.queryFault(vid, startTime, stopTime, alarmType, spn, fmi, sa, from, pageSize);
        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = Arrays.stream(searchHits.getHits())
                .map(hit -> {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    ElasticSearchUtil.locationTypeChange(sourceAsMap, posType, "longitude", "latitude");
                    return sourceAsMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }


    @Override
    public Map<String, Object> queryRealTimeFault(String vid,
                                                  String alarmType,
                                                  Integer spn,
                                                  Integer fmi,
                                                  Integer sa,
                                                  String posType,
                                                  int pageNow,
                                                  int pageSize) {

        int from = (pageNow - 1) * pageSize;

        SearchHits searchHits = vehicleFaultDao.queryRealTimeFault(vid, alarmType, spn, fmi, sa, from, pageSize);
        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = Arrays.stream(searchHits.getHits())
                .map(hit -> {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    ElasticSearchUtil.locationTypeChange(sourceAsMap, posType, "longitude", "latitude");
                    return sourceAsMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }
}
