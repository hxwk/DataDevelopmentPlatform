package com.dfssi.dataplatform.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.dfssi.dataplatform.model.EvsAllDataElasticSearchModel;
import com.dfssi.dataplatform.model.EvsElasticSearchDataModel;
import com.dfssi.dataplatform.service.EvsElasticSearchDataService;
import com.dfssi.dataplatform.utils.ElasticSearchUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
 * @version 2018/4/27 9:11
 */
@Service
public class EvsElasticSearchDataServiceImpl implements EvsElasticSearchDataService {
    private final Logger logger = LoggerFactory.getLogger(EvsElasticSearchDataServiceImpl.class);

    @Autowired
    private EvsElasticSearchDataModel evsElasticSearchDataModel;

    @Autowired
    private EvsAllDataElasticSearchModel evsAllDataElasticSearchModel;

    @Override
    public Map<String, Object> searchDetectErrDetail(String[] enterprise,
                                                     String[] hatchback,
                                                     String[] vin,
                                                     String alarmTypeName,
                                                     String alarmContent,
                                                     Long startTime,
                                                     Long endTime,
                                                     int from,
                                                     int size){
        SearchHits searchHits = evsElasticSearchDataModel.searchDetectErrDatailWithCondition(enterprise, hatchback, vin,alarmTypeName,
                 alarmContent, startTime, endTime, from, size);
        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = ElasticSearchUtil.selectItem(searchHits);
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }

    @Override
    public Map<String, Object> searchLogicDetectErrDetail(String[] enterprise,
                                                          String[] hatchback,
                                                          String[] vin,
                                                          String alarmTypeName,
                                                          String alarmContent,
                                                          Long startTime,
                                                          Long endTime,
                                                          int from,
                                                          int size){
        SearchHits searchHits = evsElasticSearchDataModel.searchLogicDetectErrDatailWithCondition(enterprise, hatchback, vin,alarmTypeName,
                 alarmContent, startTime, endTime, from, size);
        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = ElasticSearchUtil.selectItem(searchHits);
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }

    @Override
    public Map<String, Object> searchMessageDetectErrDetail(String[] enterprise,
                                                            String[] hatchback,
                                                            String[] vin,
                                                            String alarmTypeName,
                                                            String alarmContent,
                                                            Long startTime,
                                                            Long endTime,
                                                            int from,
                                                            int size){
        SearchHits searchHits = evsElasticSearchDataModel.searchMessageErrDatailWithCondition(enterprise, hatchback, vin,alarmTypeName,
                 alarmContent, startTime, endTime, from, size);
        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = ElasticSearchUtil.selectItem(searchHits);
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }



    @Override
    public Map<String, Object> searchVehicleAlarms(String[] enterprise,
                                                    String[] hatchback,
                                                    String[] vin,
                                                    Integer alarmLevel,
                                                    String alarmContent,
                                                    Integer handleStatus,
                                                    Long startTime,
                                                    Long endTime,
                                                    int from,
                                                    int size) {

        SearchHits searchHits = evsElasticSearchDataModel
                .searchVehicleAlarmsWithCondition(enterprise, hatchback, vin, alarmLevel,
                        alarmContent, handleStatus, startTime, endTime, from, size);
        long total = searchHits.getTotalHits();

        List<Map<String, Object>> list = ElasticSearchUtil.selectItem(searchHits);
        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }

    @Override
    public void handleStatus2Es(String vehcleAlarms) {
        List<Map<String,Object>> list = JSONArray.parseObject(vehcleAlarms,List.class);
        evsElasticSearchDataModel.handleStatus2Es(list);
    }

    @Override
    public Map<String, Object> countVehicleAlarmsWithCondition(String[] enterprise,
                                                               String[] hatchback,
                                                               String[] vin,
                                                               Long startTime,
                                                               Long endTime) {
        return evsElasticSearchDataModel
                .countVehicleAlarmsWithCondition(enterprise, hatchback, vin, startTime, endTime);
    }

    @Override
    public Map<String, Object> searchVehicleAlarmDetail(String[] enterprise,
                                                        String[] hatchback,
                                                        String[] vin,
                                                        Integer alarmLevel,
                                                        String alarmContent,
                                                        Long startTime,
                                                        Long endTime,
                                                        int from,
                                                        int size) {

        SearchHits searchHits = evsAllDataElasticSearchModel.searchVehicleAlarmDetailWithCondition(enterprise,
                hatchback, vin, alarmLevel, alarmContent, startTime, endTime, from, size);

        long total = searchHits.getTotalHits();

        SearchHit[] hits = searchHits.getHits();
        List<Map<String, Object>> list = Lists.newLinkedList();
        Map<String, Object> map;
        Object alarmIdentificationListObj;
        List<String> alarmIdentification;
        for(SearchHit hit : hits){
           map = hit.getSourceAsMap();
           map.put("alarm_level", map.remove("maxAlarmRating"));

           alarmIdentificationListObj = map.remove("alarmIdentificationList");
           if(alarmIdentificationListObj != null && alarmIdentificationListObj instanceof List){
               alarmIdentification = (List<String>) alarmIdentificationListObj;
               if(alarmContent == null && alarmIdentification.size() > 0){
                   map.put("alarm_content", alarmIdentification.get(0));
               }else {
                   for (String alarm : alarmIdentification) {
                       if (alarm.startsWith(alarmContent)) {
                           map.put("alarm_content", alarm);
                           break;
                       }
                   }
               }
           }
            list.add(map);
        }

        Map<String, Object> res = Maps.newHashMap();
        res.put("total", total);
        res.put("size", list.size());
        res.put("records", list);

        return res;
    }
}
