package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.impl;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleAlarmDTO;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.CommonUtils;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehicleAlarmService;
import com.google.common.collect.Lists;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 车辆报警信息服务
 * Created by yanghs on 2018/5/8.
 */
@Service
public class VehicleAlarmService implements IVehicleAlarmService {

    private final static String TABLE_PREFIX = "terminal_0200_";//商用车告警信息记录前缀

    private final static String TABLE_TYPE = "terminal_0200";//商用车告警类型

    @Autowired
    private TransportClient client;

    @Override
    public List<VehicleAlarmDTO> findAlarmInfo(List<String> vids, String startTime, String endTime) {
        List<VehicleAlarmDTO> list = getSearchResponses(vids, startTime, endTime);
        return list;
    }

    private List<VehicleAlarmDTO> getSearchResponses(List<String> vids, String starttime, String endtime) {
        List<SearchResponse> responses = Lists.newArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dBegin = null;
        Date dEnd = null;
        try {
            dBegin = sdf.parse(starttime);
            dEnd = sdf.parse(endtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<String> date = CommonUtils.findDates(dBegin, dEnd, null);
        String[] dates=new String[date.size()];
        for (int i=0;i<date.size();i++){
            dates[i]=TABLE_PREFIX+date.get(i);
        }
        SearchRequestBuilder srb = client.prepareSearch(dates).setTypes(TABLE_TYPE);
        if(vids.size()>0){
            srb .setQuery(QueryBuilders.termsQuery("vid", vids));
        }
        SearchResponse sr =   searchResult(srb);
        if(sr!=null){
            responses.add(sr);
        }

        List<VehicleAlarmDTO> list = new ArrayList<>();
        responses.stream().forEach(searchResponse -> {
            Iterator<SearchHit> iter = searchResponse.getHits().iterator();
            Map<String, Object> map = new HashMap<>();
            while (iter.hasNext()) {
                SearchHit searchHit = iter.next();
                map = searchHit.getSourceAsMap();
                VehicleAlarmDTO vehicleAlarmDTO = new VehicleAlarmDTO();
                vehicleAlarmDTO.setVid(String.valueOf( map.get("vid")));
                vehicleAlarmDTO.setAlarm(String.valueOf(map.get("alarm")));
                vehicleAlarmDTO.setAlarms(String.valueOf(map.get("alarms")));
                vehicleAlarmDTO.setGpsTime(String.valueOf(map.get("gpsTime")));
                list.add(vehicleAlarmDTO);
            }
        });
        return list;
    }

    private SearchResponse searchResult(SearchRequestBuilder srb){
        SearchResponse sr = null;
        try {
            int size = (int) srb.get().getHits().getTotalHits();
            if (size >= 10000) {
                sr = srb.setScroll(new TimeValue(30000)).addSort("gpsTime", SortOrder.ASC).get();
            } else {
                sr = srb.setSize(size).addSort("gpsTime", SortOrder.ASC).get();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
            return sr;
    }
}
