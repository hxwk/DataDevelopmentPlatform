package com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.impl;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.dao.VehicleAlarmDao;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.AreaAlarmEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.AreaLinkEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.service.IVehicleAlarmService;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ElasticSearchUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class VehicleAlarmService implements IVehicleAlarmService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private final static String TABLE_TYPE = "road_alarm_record";

    @Autowired
    private VehicleAlarmDao vehicleAlarmDao;


    @Override
    public Map<String, Object> queryalarm(String vid,
                                          Long startTime,
                                          Long stopTime,
                                          String alarmType,
                                          String posType,
                                          int pageNow,
                                          int pageSize) {

        int from = (pageNow - 1) * pageSize;

        SearchHits searchHits = vehicleAlarmDao.queryalarm(vid, startTime, stopTime, alarmType, from, pageSize);
        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = Arrays.stream(searchHits.getHits())
                .map(hit ->  {
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
    public Map<String, Object> queryRealTimeAlarm(String vid,
                                                  String alarmType,
                                                  String posType,
                                                  int pageNow,
                                                  int pageSize) {

        int from = (pageNow - 1) * pageSize;

        SearchHits searchHits = vehicleAlarmDao.queryRealTimeAlarm(vid, alarmType, from, pageSize);
        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = Arrays.stream(searchHits.getHits())
                .map(hit ->  {
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


    /** 区域增删改查 */

    @Override
    public int addArea(AreaAlarmEntity alarmEntity){
        return vehicleAlarmDao.addAreas(Lists.newArrayList(alarmEntity));
    }

    @Override
    public int addAreas(List<AreaAlarmEntity> alarmEntities){
        return vehicleAlarmDao.addAreas(alarmEntities);
    }

    @Override
    public int deleteArea(String id){
        return vehicleAlarmDao.deleteArea(id);
    }

    @Override
    public int updatyeArea(AreaAlarmEntity alarmEntity){
        return vehicleAlarmDao.updateArea(alarmEntity);
    }

    @Override
    public Map<String, Object> queryAreas(String id,
                                          String name,
                                          Integer type,
                                          int pageNow,
                                          int pageSize){

        Page<Map<String, Object>> page = PageHelper.startPage(pageNow, pageSize);
        vehicleAlarmDao.queryAreas(id, name, type);
        long total = page.getTotal();

        List<Map<String, Object>> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

    /** 连接增删查 */
    @Override
    public int addLink(AreaLinkEntity linkEntity){
        return vehicleAlarmDao.addLinks(Lists.newArrayList(linkEntity));
    }

    @Override
    public int addLinks(List<AreaLinkEntity> linkEntities){
        return vehicleAlarmDao.addLinks(linkEntities);
    }

    @Override
    public int deleteLink(String vid, String id){
        return vehicleAlarmDao.deleteLink(vid, id);
    }


    @Override
    public Map<String, Object> queryLinks(String id,
                                          String vid,
                                          String areaId,
                                          Integer areaType,
                                          int pageNow,
                                          int pageSize){

        Page<Map<String, Object>> page = PageHelper.startPage(pageNow, pageSize);
        vehicleAlarmDao.queryLinks(id, vid, areaId, areaType);
        long total = page.getTotal();

        List<Map<String, Object>> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

    @Override
    public Map<String, Object> queryLinkAreas(String vid,
                                              Integer type,
                                              int pageNow,
                                              int pageSize){

        Page<Map<String, Object>> page = PageHelper.startPage(pageNow, pageSize);
        vehicleAlarmDao.queryLinkAreas(vid, type);
        long total = page.getTotal();

        List<Map<String, Object>> result = page.getResult();
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", result.size());
        res.put("records", result);

        return res;
    }

}
