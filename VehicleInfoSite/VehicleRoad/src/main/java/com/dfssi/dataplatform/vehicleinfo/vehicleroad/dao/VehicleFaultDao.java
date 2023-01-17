package com.dfssi.dataplatform.vehicleinfo.vehicleroad.dao;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ElasticSearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/10/9 16:35
 */
@Component
@Slf4j
public class VehicleFaultDao extends VehicleAlarmDao {

    public SearchHits queryFault(String vid,
                                 Long startTime,
                                 Long stopTime,
                                 String alarmType,
                                 Integer spn,
                                 Integer fmi,
                                 Integer sa,
                                 int from,
                                 int size){

        SearchRequestBuilder requestBuilder = newSearchRequestBuilder(startTime, stopTime);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        RangeQueryBuilder rangeQueryBuilder = ElasticSearchUtil.createRangeQueryBuilder("startTime", startTime, stopTime);
        if(rangeQueryBuilder != null){
            queryBuilder.must(rangeQueryBuilder);
        }

        if(vid != null){
            String[] vids = vid.split(",");
            queryBuilder.must(QueryBuilders.termsQuery("vid", vids));
        }

        if(alarmType != null){
            String[] alarmTypes = alarmType.split(",");
            queryBuilder.must(QueryBuilders.termsQuery("alarmType", alarmTypes));
        }

        if(spn != null){
            queryBuilder.must(QueryBuilders.termQuery("spn", spn));
        }
        if(fmi != null){
            queryBuilder.must(QueryBuilders.termQuery("fmi", fmi));
        }
        if(sa != null){
            queryBuilder.must(QueryBuilders.termQuery("sa", sa));
        }

        queryBuilder.must(QueryBuilders.termQuery("alarmTag", "1"));

        requestBuilder.setFetchSource(new String[]{"vid", "sim", "id", "alarmType",
                "time", "alarmDesc", "startTime", "diagnosticCode",
                "stopTime", "count", "spn", "fmi", "sa", "ecuName", "latitude", "longitude"}, null);
        requestBuilder.addSort("startTime", SortOrder.DESC);
        requestBuilder.setQuery(queryBuilder);

        SearchResponse searchResponse = requestBuilder.setFrom(from).setSize(size).get();

        return searchResponse.getHits();
    }



    public SearchHits queryRealTimeFault(String vid,
                                         String alarmType,
                                         Integer spn,
                                         Integer fmi,
                                         Integer sa,
                                         int from,
                                         int size){

        SearchRequestBuilder requestBuilder = transportClient
                .prepareSearch(String.format("%s_latest", typeName));


        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        //筛选在线数据
        long l = roadVehicleConfig.getOnlineThreshold() * 60 * 1000L;
        queryBuilder.must(QueryBuilders.rangeQuery("startTime").gt(System.currentTimeMillis() - l));

        if(vid != null){
            String[] vids = vid.split(",");
            size = vids.length;
            queryBuilder.must(QueryBuilders.termsQuery("vid", vids));
        }

        if(alarmType != null){
            String[] alarmTypes = alarmType.split(",");
            queryBuilder.must(QueryBuilders.termsQuery("alarmType", alarmTypes));
        }

        if(spn != null){
            queryBuilder.must(QueryBuilders.termQuery("spn", spn));
        }
        if(fmi != null){
            queryBuilder.must(QueryBuilders.termQuery("fmi", fmi));
        }
        if(sa != null){
            queryBuilder.must(QueryBuilders.termQuery("sa", sa));
        }

        queryBuilder.must(QueryBuilders.termQuery("alarmTag", "1"));

        requestBuilder.setFetchSource(new String[]{"vid", "sim", "id", "alarmType",
                "time", "alarmDesc", "diagnosticCode", "startTime", "stopTime", "count", "spn", "fmi", "sa",
                "ecuName", "latitude", "longitude"}, null);

        requestBuilder.addSort("startTime", SortOrder.DESC);
        requestBuilder.setQuery(queryBuilder);

        SearchResponse searchResponse = requestBuilder.setFrom(from).setSize(size).get();

        return searchResponse.getHits();
    }

}
