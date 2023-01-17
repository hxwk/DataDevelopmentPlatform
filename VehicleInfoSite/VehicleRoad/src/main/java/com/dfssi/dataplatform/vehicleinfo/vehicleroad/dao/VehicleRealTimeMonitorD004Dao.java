package com.dfssi.dataplatform.vehicleinfo.vehicleroad.dao;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ElasticSearchUtil;
import com.google.common.collect.Lists;
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
public class VehicleRealTimeMonitorD004Dao extends ElasticSearchDao{
    private final Logger logger = LoggerFactory.getLogger(VehicleRealTimeMonitorD004Dao.class);


    private String[] excludes = new String[]{"location",
            "expend_time", "expend_idle_time", "expend_idle", "expend_mile", "expend_gpsMile", "expend_fuel", "speed"};

    /**
     * 查询最新信息
     * @param vid
     * @param columns
     * @return
     */
    public Map<String, Object> getLatestDataByVid(String vid, Set<String> columns){
        GetRequestBuilder getRequestBuilder
                = transportClient.prepareGet(String.format("%s_latest", elasticsearchConfig.getTypeName()),
                elasticsearchConfig.getTypeName(), vid);

        if(columns != null && columns.size() > 0){
            getRequestBuilder.setFetchSource(columns.toArray(new String[columns.size()]), excludes);
        }else{
            getRequestBuilder.setFetchSource(null, excludes);
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
            prepareSearch.setFetchSource(columns.toArray(new String[columns.size()]), excludes);
        }else{
            prepareSearch.setFetchSource(null, excludes);
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
                .addSort("receiveTime", SortOrder.DESC)
                .get();

        return searchResponse.getHits();
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
        RangeQueryBuilder rangeQueryBuilder = ElasticSearchUtil.createRangeQueryBuilder("receiveTime", startTime, endTime);
        if(rangeQueryBuilder != null){
            queryBuilder.must(rangeQueryBuilder);
        }

        //中国经纬度
        rangeQueryBuilder = QueryBuilders.rangeQuery("longitude").gte(73).lt(135);
        queryBuilder.must(rangeQueryBuilder);
        rangeQueryBuilder = QueryBuilders.rangeQuery("latitude").gte(4).lt(53);
        queryBuilder.must(rangeQueryBuilder);
        queryBuilder.must(QueryBuilders.termQuery("vid", vid));

        requestBuilder.setQuery(queryBuilder);
        requestBuilder.addSort("receiveTime", SortOrder.ASC);

        requestBuilder.setScroll(TimeValue.timeValueMinutes(5));
        requestBuilder.setSize(500);
        List<Map<String, Object>> res = Lists.newLinkedList();

        SearchResponse searchResponse = requestBuilder.setFetchSource(null, excludes).get();
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
                    ElasticSearchUtil.locationTypeChange(sourceAsMap, posType, "longitude", "latitude");
                    res.add(sourceAsMap);
                }
            }
            searchResponse = transportClient.prepareSearchScroll(searchResponse.getScrollId())
                    .setScroll(TimeValue.timeValueMinutes(5)).get();
        } while(searchResponse.getHits().getHits().length != 0);


        return res;
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
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("receiveTime").lte(currentTime - l));
                    break;
                case 1:
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("receiveTime").gt(currentTime - l));
                    break;
                case 2:
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("receiveTime").gt(currentTime - l));

                    //仪表盘速度为0  或者 不存在仪表盘速度这个列
                    BoolQueryBuilder bq = QueryBuilders.boolQuery();
                    bq.should(QueryBuilders.rangeQuery("TachographVehicleSpeed").lte(0))
                            .should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("TachographVehicleSpeed")));

                    boolQueryBuilder.must(bq);
                    break;
                case 3:
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("receiveTime").gt(currentTime - l));
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("TachographVehicleSpeed").gt(0));
                    break;
            }
        }

        if(vid != null){
            String[] vids = vid.split(",");
            boolQueryBuilder.must(QueryBuilders.termsQuery("vid", vids));
        }


        prepareSearch.setFetchSource(new String[]{"vid", "receiveTime", "TachographVehicleSpeed"}, null);

        SearchResponse searchResponse = prepareSearch.setQuery(boolQueryBuilder).setFrom(from).setSize(size).get();
        return searchResponse.getHits();
    }
}
