package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.impl;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.PeriodicReportDTO;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model.CommonUtils;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehiclePeriodicReportService;
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
 * Created by yanghs on 2018/5/30.
 */
@Service
public class VehiclePeriodicReportService implements IVehiclePeriodicReportService{


    private final static String TABLE_PREFIX = "terminal_d004_latest";//商用车实时数据周期上报记录前缀

    private final static String TABLE_TYPE = "terminal_d004";//商用车实时数据周期上报类型


    private final static String ALARM_TABLE_PREFIX = "terminal_0200_latest";//商用车告警信息记录前缀

    private final static String ALARM_TABLE_TYPE = "terminal_0200";//商用车告警类型

    @Autowired
    private TransportClient client;

    @Override
    public PeriodicReportDTO findPeriodicReportInfo(String vid) {
        PeriodicReportDTO periodicReportDTO = getSearchResponse(vid);
        return periodicReportDTO;
    }


    /**
     * vid查询最新数据
     * @param vid
     * @return
     */
    private PeriodicReportDTO getSearchResponse(String vid) {
        SearchResponse searchResponse = client.prepareSearch(TABLE_PREFIX)
                .setTypes(TABLE_TYPE)
                .setQuery(QueryBuilders.termQuery("vid", vid)).get();
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        if(searchHits.length == 0){
            return null;
        }
        Map<String, Object> map = searchHits[0].getSourceAsMap();
        PeriodicReportDTO periodicReportDTO = new PeriodicReportDTO();
        periodicReportDTO.setVid(CommonUtils.object2String(map.get("vid")));
        periodicReportDTO.setRpm(CommonUtils.object2String(map.get("rpm")));
        periodicReportDTO.setInstrumentSpeed(CommonUtils.object2String(map.get("instrumentSpeed")));
        periodicReportDTO.setWheelSpeed(CommonUtils.object2String(map.get("wheelSpeed")));
        periodicReportDTO.setGpsSpeed(CommonUtils.object2String(map.get("gpsSpeed")));
        periodicReportDTO.setThrottleOpening(CommonUtils.object2String(map.get("throttleOpening")));
        periodicReportDTO.setPercentagetorque(CommonUtils.object2String(map.get("percentagetorque")));
        periodicReportDTO.setSwitchs(CommonUtils.object2String(map.get("switchsCode")));
        periodicReportDTO.setEngineFuelRate(CommonUtils.object2String(map.get("engineFuelRate")));
        periodicReportDTO.setFuelLevel(CommonUtils.object2String(map.get("fuelLevel")));
        periodicReportDTO.setWaterTemp(CommonUtils.object2String(map.get("waterTemp")));
        periodicReportDTO.setEngineTorqueMode(CommonUtils.object2String(map.get("engineTorqueMode")));
        periodicReportDTO.setOilPressure(CommonUtils.object2String(map.get("oilPressure")));
        periodicReportDTO.setGpsDir(CommonUtils.object2String(map.get("gpsDir")));
        periodicReportDTO.setAirCompressorStatus(CommonUtils.object2String(map.get("airCompressorStatus")));
        periodicReportDTO.setTransmissionOutputSpeed(CommonUtils.object2String(map.get("transmissionOutputSpeed")));
        periodicReportDTO.setDataTime(CommonUtils.object2String(map.get("dataTime")));
        periodicReportDTO.setAlarmInfo(CommonUtils.object2String(getAlarmSearchResponses(vid)));
        return periodicReportDTO;
    }

    private String getAlarmSearchResponses(String vid) {
        SearchResponse searchResponse = client.prepareSearch(ALARM_TABLE_PREFIX)
                .setTypes(ALARM_TABLE_TYPE)
                .setQuery(QueryBuilders.termQuery("vid", vid)).get();
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        if(searchHits.length == 0){
            return null;
        }
        Map<String, Object> map = searchHits[0].getSourceAsMap();

        if(map.get("alarmLists")!=null){
            List list=(List)map.get("alarmLists");
            return String.join(",",list );
        }else{
            return "";
        }
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














    /**
     * 查询时间段数据
     * @param vids
     * @param starttime
     * @param endtime
     * @return
     */
    private List<PeriodicReportDTO> getSearchResponses(List<String> vids, String starttime, String endtime) {
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

        List<PeriodicReportDTO> list = new ArrayList<>();
        responses.stream().forEach(searchResponse -> {
            Iterator<SearchHit> iter = searchResponse.getHits().iterator();
            Map<String, Object> map = new HashMap<>();
            while (iter.hasNext()) {
                SearchHit searchHit = iter.next();
                map = searchHit.getSourceAsMap();
                PeriodicReportDTO periodicReportDTO = new PeriodicReportDTO();
                periodicReportDTO.setVid(CommonUtils.object2String(map.get("vid")));
                periodicReportDTO.setRpm(CommonUtils.object2String(map.get("rpm")));
                periodicReportDTO.setInstrumentSpeed(CommonUtils.object2String(map.get("instrumentSpeed")));
                periodicReportDTO.setWheelSpeed(CommonUtils.object2String(map.get("wheelSpeed")));
                periodicReportDTO.setGpsSpeed(CommonUtils.object2String(map.get("gpsSpeed")));
                periodicReportDTO.setThrottleOpening(CommonUtils.object2String(map.get("throttleOpening")));
                periodicReportDTO.setPercentagetorque(CommonUtils.object2String(map.get("percentagetorque")));
                periodicReportDTO.setSwitchs(CommonUtils.object2String(map.get("switchs")));
                periodicReportDTO.setEngineFuelRate(CommonUtils.object2String(map.get("engineFuelRate")));
                periodicReportDTO.setFuelLevel(CommonUtils.object2String(map.get("fuelLevel")));
                periodicReportDTO.setWaterTemp(CommonUtils.object2String(map.get("waterTemp")));
                periodicReportDTO.setEngineTorqueMode(CommonUtils.object2String(map.get("engineTorqueMode")));
                periodicReportDTO.setOilPressure(CommonUtils.object2String(map.get("oilPressure")));
                periodicReportDTO.setGpsDir(CommonUtils.object2String(map.get("gpsDir")));
                periodicReportDTO.setAirCompressorStatus(CommonUtils.object2String(map.get("airCompressorStatus")));
                periodicReportDTO.setTransmissionOutputSpeed(CommonUtils.object2String(map.get("transmissionOutputSpeed")));
                list.add(periodicReportDTO);
            }
        });
        return list;
    }

}
