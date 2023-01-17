package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.impl;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.service.IVehicleTrackService;
import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DateFormatUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class VehicleTrackService implements IVehicleTrackService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleTrackService.class);

    private final static String LOCATION_INDEXS="terminal_0200_latest";//位置信息

    private final static String TABLE_TYPE = "terminal_0200";//类型

    @Autowired
    TransportClient client;

    @Override
    public Map getCurrentLocationsByVid(String vid) {
        SearchResponse searchResponse = client.prepareSearch(LOCATION_INDEXS)
                .setTypes(TABLE_TYPE)
                .setQuery(QueryBuilders.termQuery("vid", vid)).get();
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        if(searchHits.length == 0){
            return null;
        }
        Map<String, Object> map = searchHits[0].getSourceAsMap();
        HashMap tempMap=new HashMap();
        tempMap.put("vid",map.get("vid"));
        tempMap.put("gpsTime",map.get("gpsTime"));
        tempMap.put("lon",map.get("lon"));
        tempMap.put("lat",map.get("lat"));
        return tempMap;
    }

    @Override
    public List<Map> getTrackByVid(String vid, long starttime, long endtime, int interval) {
        List<SearchResponse> searchResponses = getSearchResponses(client, vid, starttime, endtime, interval, 1);
        List<Map> resultMap = Lists.newArrayList();

        searchResponses.stream().forEach(searchResponse -> {
            if(searchResponse!=null){
                Iterator<SearchHit> iter = searchResponse.getHits().iterator();
                Map<String, Object> map = new HashMap<>();
                while(iter.hasNext()){
                    SearchHit searchHit = iter.next();
                    map = searchHit.getSourceAsMap();
                    HashMap tempMap=new HashMap();
                    tempMap.put("vid",map.get("vid"));
                    tempMap.put("gpsTime",map.get("gpsTime"));
                    tempMap.put("lon",map.get("lon"));
                    tempMap.put("lat",map.get("lat"));
                    tempMap.put("speed",map.get("speed"));
//                    System.out.println("======="+map.get("vid") + "-----" + map.get("gpsTime") + "-- [ " + map.get("lon") + "," + map.get("lat") + " ]");
                    resultMap.add(tempMap);
                }
            }

        });

        return resultMap;
    }

    @Override
    public List<Map> getCurrentLocationList(List<String> vids) {
        SearchRequestBuilder srb = client.prepareSearch(LOCATION_INDEXS)
                .setTypes(TABLE_TYPE)
                .setQuery(QueryBuilders.termsQuery("vid", vids));
        List<Map> resultMap = new ArrayList<>();
        SearchResponse searchResponse = null;
        try {
            long size = srb.get().getHits().getTotalHits();
            if (size >= 10000) {
                searchResponse = srb.setScroll(new TimeValue(30000)).get();
            } else {
                searchResponse = srb.setSize((int) size).get();
            }
            SearchHit[] searchHits = searchResponse.getHits().getHits();
            if(searchHits.length == 0){
                return Collections.EMPTY_LIST;
            }

            for(SearchHit sh : searchHits){
                Map map = sh.getSourceAsMap();
                HashMap tempMap=new HashMap();
                tempMap.put("vid",map.get("vid"));
                tempMap.put("gpsTime",map.get("gpsTime"));
                tempMap.put("lon",map.get("lon"));
                tempMap.put("lat",map.get("lat"));
                resultMap.add(tempMap);
            }
        }catch (IndexNotFoundException e1) {
            logger.error("索引不存在");
        }catch (Exception e2){
            e2.printStackTrace();
        }
        return resultMap;
    }

    private List<SearchResponse> getSearchResponses(TransportClient client, String vid, long starttime, long endtime, int interval, int size){
//        String lonMin = esConfig.getLon().split(",")[0];
//        String lonMax = esConfig.getLon().split(",")[1];
//        String latMin = esConfig.getLat().split(",")[0];
//        String latMax = esConfig.getLat().split(",")[1];
        String lonMin = "73.33";
        String lonMax = "135.05";
        String latMin = "3.51";
        String latMax = "53.33";
        List<SearchResponse> responses = Lists.newArrayList();
        BigDecimal start = new BigDecimal(starttime);
        BigDecimal end = new BigDecimal(endtime);
        int step = end.subtract(start).divide(new BigDecimal(interval), BigDecimal.ROUND_CEILING).intValue();
        long s = 0;
        long e = 0;
//        System.out.println("starttime: "+starttime+"---endtime: "+endtime+"---step=======" + step);
        for(int i = 0; i< step; i++){
            s = starttime + interval * i;
            e = starttime + interval * (1 + i);
            if(e > endtime){
                e = endtime;
            }
            String date = DateFormatUtils.format(s + 1, "yyyyMMdd");
//            System.out.println("s======= " + s + "----e=======" + e + "date====" + date);
            QueryBuilder qb = QueryBuilders.boolQuery()
                    .must(QueryBuilders.rangeQuery("gpsTime").gt(s).lte(e))
                    .must(QueryBuilders.rangeQuery("lon").gte(lonMin).lte(lonMax))
                    .must(QueryBuilders.rangeQuery("lat").gte(latMin).lte(latMax));
            SearchRequestBuilder srb = client.prepareSearch("terminal_0200_" + date).setTypes(TABLE_TYPE)
                    .setQuery(QueryBuilders.termQuery("vid", vid))
                    .setPostFilter(qb);
            SearchResponse searchResponse = null;
            try {
                if (size == -1) {
                    size = (int) srb.get().getHits().getTotalHits();
                    if (size >= 10000) {
                        searchResponse = srb.setScroll(new TimeValue(30000)).addSort("gpsTime", SortOrder.ASC).get();
                    } else {
                        searchResponse = srb.setSize(size).addSort("gpsTime", SortOrder.ASC).get();
                    }
                } else {
                    if (size >= 10000) {
                        searchResponse = srb.setScroll(new TimeValue(30000)).addSort("gpsTime", SortOrder.ASC).get();
                    } else {
                        searchResponse = srb.setSize(size).addSort("gpsTime", SortOrder.ASC).get();
                    }
                }
            }catch (IndexNotFoundException e1) {
                logger.error("索引不存在");
            }catch (Exception e2){
                e2.printStackTrace();
            }
            responses.add(searchResponse);
//            searchResponse.getHits().forEach(hit->{
//                Map<String, Object> map = hit.getSourceAsMap();
//                System.out.println("=everyone==="+map.get("vid") + "-----" + map.get("gpsTime"));
//            });
        }
        return responses;
    }
}
