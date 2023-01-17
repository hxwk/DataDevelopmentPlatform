package com.dfssi.dataplatform.vehicleinfo.vehicleroad.dao;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.config.RoadVehicleConfig;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ElasticSearchUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/21 11:38
 */
@Component
public class VehicleRealTimeMonitorDao extends ElasticSearchDao{
    private final Logger logger = LoggerFactory.getLogger(VehicleRealTimeMonitorDao.class);

    @Autowired
    private RoadVehicleConfig roadVehicleConfig;

    /**
     * 查询最新信息
     * @param vid
     * @param columns
     * @return
     */
    public Map<String, Object> getLatestDataByVid(String vid, Set<String> columns){
        GetRequestBuilder getRequestBuilder
                = transportClient.prepareGet(String.format("%s_latest",
                elasticsearchConfig.getTypeName()), elasticsearchConfig.getTypeName(), vid);

        if(columns != null && columns.size() > 0){
            getRequestBuilder.setFetchSource(columns.toArray(new String[columns.size()]), null);
        }

        GetResponse response = getRequestBuilder.get();

        return response.getSourceAsMap();
    }


    /**
     * @param vid
     * @param columns
     * @param from
     * @param size
     * @return
     */
    public SearchHits getLatestData(String vid,
                                    Set<String> columns,
                                    int from,
                                    int size){

        SearchRequestBuilder prepareSearch = transportClient
                    .prepareSearch(String.format("%s_latest", elasticsearchConfig.getTypeName()));

        if(columns != null && columns.size() > 0){
            prepareSearch.setFetchSource(columns.toArray(new String[columns.size()]), null);
        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(vid != null){
            String[] vids = vid.split(",");
            boolQueryBuilder.must(QueryBuilders.termsQuery("vid", vids));
        }

        SearchResponse searchResponse = prepareSearch
                .setQuery(boolQueryBuilder)
                .setFrom(from)
                .setSize(size)
                .addSort("gpsTime", SortOrder.DESC)
                .get();

        return searchResponse.getHits();
    }

    public List<Map<String, Object>> searchSamplingTrackLocationCans(String vid,
                                                                     Long startTime,
                                                                     Long endTime,
                                                                     int stepLength,
                                                                     String posType,
                                                                     int maxRows){

        List<Map<String, Object>> maps = searchSamplingTrackLocations(vid, startTime, endTime, stepLength, posType, maxRows);
        addSamplingTrackCans(vid, startTime, endTime, maps);

        return maps;

    }



    /**
     * 查询轨迹
     * @param vid
     * @param startTime
     * @param endTime
     * @param stepLength
     * @param posType
     * @return
     */
    public List<Map<String, Object>> searchSamplingTrackLocations(String vid,
                                                                  Long startTime,
                                                                  Long endTime,
                                                                  int stepLength,
                                                                  String posType,
                                                                  int maxRows){

        SearchRequestBuilder requestBuilder = newSearchRequestBuilder(startTime, endTime);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = ElasticSearchUtil.createRangeQueryBuilder("gpsTime", startTime, endTime);
        if(rangeQueryBuilder != null){
            queryBuilder.must(rangeQueryBuilder);
        }

        //中国经纬度
        rangeQueryBuilder = QueryBuilders.rangeQuery("lon").gte(73).lt(135);
        queryBuilder.must(rangeQueryBuilder);
        rangeQueryBuilder = QueryBuilders.rangeQuery("lat").gte(4).lt(53);
        queryBuilder.must(rangeQueryBuilder);
        queryBuilder.must(QueryBuilders.termQuery("vid", vid));

        requestBuilder.setQuery(queryBuilder);
        requestBuilder.setFetchSource(
                new String[]{"vid", "gpsTime", "lon", "lat", "alt", "dir", "cumulativeOilConsumption", "fuel", "totalFuelConsumption",
                        "brakeStatus", "braking", "speed", "gear", "mile", "absStatus", "engineSpeed"}, null);
        requestBuilder.addSort("gpsTime", SortOrder.ASC);

        requestBuilder.setScroll(TimeValue.timeValueMinutes(5));
        requestBuilder.setSize(500);
        List<Map<String, Object>> res = Lists.newLinkedList();

        SearchResponse searchResponse = requestBuilder.get();
        long total = searchResponse.getHits().getTotalHits();
        if(total < maxRows){
            stepLength = 1;
        }else if(total / stepLength > maxRows){
            stepLength = (int)(total / (maxRows + 1));
        }

        do {
            SearchHit[] hits = searchResponse.getHits().getHits();
            int length = hits.length;
            Map<String, Object> sourceAsMap;
            for(int i = 0; i < length; i++){
                if(i % stepLength == 0){
                    sourceAsMap = hits[i].getSourceAsMap();
                    ElasticSearchUtil.locationTypeChange(sourceAsMap, posType);
                    res.add(sourceAsMap);
                }
            }
            searchResponse = transportClient.prepareSearchScroll(searchResponse.getScrollId())
                    .setScroll(TimeValue.timeValueMinutes(5)).get();
        } while(searchResponse.getHits().getHits().length != 0);


        return res;
    }

    private void addSamplingTrackCans(String vid,
                                      Long startTime,
                                      Long endTime,
                                      List<Map<String, Object>> locations){

        SearchRequestBuilder requestBuilder = transportClient.prepareSearch("road_terminal_0705_2018*");

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = ElasticSearchUtil.createRangeQueryBuilder("receiveTime", startTime, endTime);
        if(rangeQueryBuilder != null){
            queryBuilder.must(rangeQueryBuilder);
        }

        queryBuilder.must(QueryBuilders.termQuery("vid", vid));

        requestBuilder.setQuery(queryBuilder);
        requestBuilder.setFetchSource(
                new String[]{"receiveTime", "signal_name", "value"}, null);
        requestBuilder.addSort("receiveTime", SortOrder.ASC);

        requestBuilder.setScroll(TimeValue.timeValueMinutes(5));
        requestBuilder.setSize(500);

        Map<String, Integer> posMap = Maps.newHashMap();
        int pos = 0;

        SearchResponse searchResponse = requestBuilder.get();

        int size = locations.size();
        do {
            SearchHit[] hits = searchResponse.getHits().getHits();
            int length = hits.length;
            Map<String, Object> sourceAsMap;
            String tmpKey;
            Integer index;
            String signalName;
            for(int i = 0; i < length; i++){
                 sourceAsMap = hits[i].getSourceAsMap();

                 tmpKey = String.format("%s-%s", vid, sourceAsMap.get("receiveTime"));
                 index = posMap.get(tmpKey);
                 if(index == null){
                     index = pos;
                     posMap.put(tmpKey, index);
                     pos += 1;
                 }

                 if(index == size) {
                    return;
                 }

                signalName = sourceAsMap.get("signal_name").toString();
                String[] s = signalName.split("_");
                if(s.length == 3){
                    signalName = s[1];
                }else{
                    signalName = null;
                }

                if(signalName != null){
                    locations.get(index).put(signalName, sourceAsMap.get("value"));
                }
            }
            searchResponse = transportClient.prepareSearchScroll(searchResponse.getScrollId())
                    .setScroll(TimeValue.timeValueMinutes(5)).get();

        } while(searchResponse.getHits().getHits().length != 0 );

         if(pos < size && pos > 0){
             Map<String, Object> map = locations.get(pos - 1);
             Map<String, Object> t;
             for(int i = pos; i < size; i++){
                 t = Maps.newHashMap(map);
                 t.putAll(locations.get(i));
                 locations.set(i, t);
            }
        }
    }


    /**
     * 条件查询车辆状态
     * @param vid
     * @param status
     * @return
     */
    public SearchHits getLatestStatus(String vid,
                                      Integer status,
                                      long currentTime,
                                      int from,
                                      int size){

        SearchRequestBuilder prepareSearch = transportClient
                .prepareSearch(String.format("%s_latest", elasticsearchConfig.getTypeName()));

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(status != null){
            /**添加状态筛选*/
            long l = roadVehicleConfig.getOnlineThreshold() * 60 * 1000L;
            switch (status){
                case 0 :
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("receiveMsgTime").lte(currentTime - l));
                    break;
                case 1:
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("receiveMsgTime").gt(currentTime - l));
                    break;
                case 2:
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("receiveMsgTime").gt(currentTime - l));

                    BoolQueryBuilder speedBool = QueryBuilders.boolQuery();
                    speedBool.must(QueryBuilders.rangeQuery("speed1").lte(0))
                             .must(QueryBuilders.rangeQuery("speed").lte(0));

                    boolQueryBuilder.must(speedBool);
                    break;
                case 3:
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("receiveMsgTime").gt(currentTime - l));
                    speedBool = QueryBuilders.boolQuery();
                    speedBool.should(QueryBuilders.rangeQuery("speed1").gt(0))
                            .should(QueryBuilders.rangeQuery("speed").gt(0));

                    boolQueryBuilder.must(speedBool);
                    break;
            }
        }

        if(vid != null){
            String[] vids = vid.split(",");
            boolQueryBuilder.must(QueryBuilders.termsQuery("vid", vids));
        }


        prepareSearch.setFetchSource(new String[]{"vid", "receiveMsgTime", "speed", "speed1"}, null);

        SearchResponse searchResponse = prepareSearch.setQuery(boolQueryBuilder).setFrom(from).setSize(size).get();
        return searchResponse.getHits();
    }

}
