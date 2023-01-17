package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.cache.elasticsearch.ElasticSearchIndexNameCache;
import com.dfssi.dataplatform.utils.ElasticSearchUtil;
import com.google.common.collect.Lists;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *   es全量数据查询
 * @author LiXiaoCong
 * @version 2018/5/2 19:03
 */
@Component
public class EvsAllDataElasticSearchModel {
    private final Logger logger = LoggerFactory.getLogger(EvsAllDataElasticSearchModel.class);

    @Autowired
    private TransportClient transportClient;

    @Autowired
    private ElasticSearchIndexNameCache elasticSearchIndexNameCache;

    public SearchHits searchData(String[] enterprise,
                                 String[] hatchback,
                                 String[] vin,
                                 Set<String> columns,
                                 Long startTime,
                                 Long endTime,
                                 int from,
                                 int size){

        SearchRequestBuilder searchRequestBuilder = newSearchRequestBuilder(startTime, endTime);
        if(columns != null && columns.size() > 0){
            columns.add("index");
            columns.add("id");
            searchRequestBuilder.setFetchSource(columns.toArray(new String[columns.size()]), null);
        }

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        RangeQueryBuilder rangeQueryBuilder = ElasticSearchUtil.createRangeQueryBuilder("collectTime", startTime, endTime);
        if(rangeQueryBuilder != null){
            queryBuilder.must(rangeQueryBuilder);
        }

        if(enterprise != null){
            queryBuilder.must(QueryBuilders.termsQuery("vehicleCompany", enterprise));
        }
        if(hatchback != null){
            queryBuilder.must(QueryBuilders.termsQuery("vehicleType", hatchback));
        }
        if(vin != null){
            queryBuilder.must(QueryBuilders.termsQuery("vin", vin));
        }

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder)
                .addSort("collectTime", SortOrder.DESC)
                .setFrom(from)
                .setSize(size)
                .get();

        return searchResponse.getHits();
    }

    public Map<String, Object> getDataByIndexId(String index,
                                            String id,
                                            Set<String> columns){

        GetRequestBuilder getRequestBuilder = transportClient.prepareGet(index, "new_vehicle_data", id);
        if(columns != null && columns.size() > 0){
            getRequestBuilder.setFetchSource(columns.toArray(new String[columns.size()]), null);
        }

        GetResponse response = getRequestBuilder.get();

        return response.getSourceAsMap();
    }


    //查询车辆自身告警数据细节
    public SearchHits searchVehicleAlarmDetailWithCondition(String[] enterprise,
                                                            String[] hatchback,
                                                            String[] vin,
                                                            Integer alarmLevel,
                                                            String alarmContent,
                                                            Long startTime,
                                                            Long endTime,
                                                            int from,
                                                            int size){

        SearchRequestBuilder searchRequestBuilder = newSearchRequestBuilder(startTime, endTime);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if(enterprise != null){
            queryBuilder.must(QueryBuilders.termsQuery("vehicleCompany", enterprise));
        }
        if(hatchback != null){
            queryBuilder.must(QueryBuilders.termsQuery("vehicleType", hatchback));
        }
        if(vin != null){
            queryBuilder.must(QueryBuilders.termsQuery("vin", vin));
        }

        if(alarmLevel != null){
            queryBuilder.must(QueryBuilders.termQuery("maxAlarmRating", alarmLevel.intValue()));
        }else {
            queryBuilder.must(QueryBuilders.rangeQuery("maxAlarmRating").gt(0));
        }

        if(alarmContent != null){
            queryBuilder.must(QueryBuilders.prefixQuery("alarmIdentificationList.keyword", alarmContent));
        }else{
            queryBuilder.must(QueryBuilders.existsQuery("alarmIdentificationList"));
        }

        RangeQueryBuilder rangeQueryBuilder = ElasticSearchUtil.createRangeQueryBuilder("collectTime", startTime, endTime);
        if(rangeQueryBuilder != null){
            queryBuilder.must(rangeQueryBuilder);
        }

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder)
                .addSort("collectTime", SortOrder.DESC)
                .setFrom(from)
                .setFetchSource(new String[]{"speed", "location", "collectTime", "alarmIdentificationList",
                                "maxAlarmRating", "vin", "vehicleType", "vehicleCompany"},
                        null)
                .setSize(size)
                .get();

        return searchResponse.getHits();

    }

    public List<Map<String, Object>> searchSamplingTrackLocations(String vin,
                                                                  Long startTime,
                                                                  Long endTime,
                                                                  int stepLength,
                                                                  String posType){

        SearchRequestBuilder requestBuilder = newSearchRequestBuilder(startTime, endTime);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = ElasticSearchUtil.createRangeQueryBuilder("collectTime", startTime, endTime);
        if(rangeQueryBuilder != null){
            queryBuilder.must(rangeQueryBuilder);
        }

        //中国经纬度
        rangeQueryBuilder = QueryBuilders.rangeQuery("longitude").gte(73).lt(135);
        queryBuilder.must(rangeQueryBuilder);
        rangeQueryBuilder = QueryBuilders.rangeQuery("latitude").gte(4).lt(53);
        queryBuilder.must(rangeQueryBuilder);
        queryBuilder.must(QueryBuilders.termQuery("vin", vin));

        requestBuilder.setQuery(queryBuilder);
        requestBuilder.setFetchSource(new String[]{"collectTime", "longitude", "latitude"}, null);
        requestBuilder.addSort("collectTime", SortOrder.ASC);

        requestBuilder.setScroll(TimeValue.timeValueMinutes(5));
        requestBuilder.setSize(500);
        List<Map<String, Object>> res = Lists.newLinkedList();

        SearchResponse searchResponse = requestBuilder.get();
        long total = searchResponse.getHits().getTotalHits();
        if(total / stepLength > 10000){
            stepLength = (int)(total / 10001);
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

    private SearchRequestBuilder newSearchRequestBuilder(Long startTime, Long endTime){
        SearchRequestBuilder requestBuilder;

        if(startTime == null && endTime == null){
            requestBuilder = transportClient.prepareSearch();
        }else{
            requestBuilder = transportClient.prepareSearch(getIndexs(startTime, endTime));
        }

        return requestBuilder.setTypes("new_vehicle_data");
    }

    private String[] getIndexs(Long startTime, Long endTime){

        DateTime endDate;
        if(endTime == null){
            endDate = DateTime.now();
        }else{
            endDate = new DateTime(endTime);
        }

        DateTime startDate;
        if(startTime == null){
            startDate = endDate.minus(Period.weeks(1));
        }else{
            startDate = new DateTime(startTime);
        }

        int days = Days.daysBetween(startDate, endDate).getDays();
        String[] indexs = new String[days + 1];
        for (int i = 0; i <= days; i++) {
            indexs[i] = String.format("new_vehicle_data_%s",
                    startDate.plusDays(i).toString("yyyyMMdd"));
        }

        return elasticSearchIndexNameCache.selectExist(indexs);
    }

}
