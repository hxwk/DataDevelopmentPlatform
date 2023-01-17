package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.cache.elasticsearch.ElasticSearchIndexNameCache;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *   es中质量检测细节表查询
 * @author LiXiaoCong
 * @version 2018/4/26 8:48
 */
@Component
public class EvsElasticSearchDataModel {
    private final Logger logger = LoggerFactory.getLogger(EvsElasticSearchDataModel.class);

    @Autowired
    private TransportClient transportClient;

    @Autowired
    private ElasticSearchIndexNameCache elasticSearchIndexNameCache;

    /**
     * 0(车辆自身故障告警)，1（新能源国标数据逻辑异常条件），2（新能源61项数据质量检测），3（报文完整）,...
     * 查询检测错误数据细节
     */
    public SearchHits searchDetectErrDatailWithCondition(String[] enterprise,
                                                         String[] hatchback,
                                                         String[] vin,
                                                         String alarmTypeName,
                                                         String alarmContent,
                                                         Long startTime,
                                                         Long endTime,
                                                         int from,
                                                         int size){

        SearchRequestBuilder searchRequestBuilder = newSearchRequestBuilder(startTime, endTime);
        QueryBuilder queryBuilder = createQuery(enterprise, hatchback, vin,
                alarmTypeName, null, alarmContent, startTime, endTime, 2);

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder)
                .addSort("collectTime", SortOrder.DESC)
                .setFrom(from)
                .setFetchSource(null,
                        new String[]{"index", "table"})
                .setSize(size)
                //.setPreference("_primary_first")
                .get();

        return searchResponse.getHits();

    }

    /**
     * 0(车辆自身故障告警)，1（新能源国标数据逻辑异常条件），2（新能源61项数据质量检测），3（报文完整）,...
     * 查询逻辑检测错误数据细节
     */
    public SearchHits searchLogicDetectErrDatailWithCondition(String[] enterprise,
                                                              String[] hatchback,
                                                              String[] vin,
                                                              String alarmTypeName,
                                                              String alarmContent,
                                                              Long startTime,
                                                              Long endTime,
                                                              int from,
                                                              int size){

        SearchRequestBuilder searchRequestBuilder = newSearchRequestBuilder(startTime, endTime);
        QueryBuilder queryBuilder = createQuery(enterprise, hatchback, vin,
                alarmTypeName, null, alarmContent, startTime, endTime, 1);

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder)
                .addSort("collectTime", SortOrder.DESC)
                .setFrom(from)
                .setFetchSource(null,
                        new String[]{"index", "table"})
                .setSize(size)
                .get();

        return searchResponse.getHits();

    }

    /**
     * 0(车辆自身故障告警)，1（新能源国标数据逻辑异常条件），2（新能源61项数据质量检测），3（报文完整）,...
     * 查询报文完整性
     */
    public SearchHits searchMessageErrDatailWithCondition(String[] enterprise,
                                                          String[] hatchback,
                                                          String[] vin,
                                                          String alarmTypeName,
                                                          String alarmContent,
                                                          Long startTime,
                                                          Long endTime,
                                                          int from,
                                                          int size){

        SearchRequestBuilder searchRequestBuilder = newSearchRequestBuilder(startTime, endTime);
        QueryBuilder queryBuilder = createQuery(enterprise, hatchback, vin,
                alarmTypeName, null, alarmContent, startTime, endTime, 3);

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder)
                .addSort("collectTime", SortOrder.DESC)
                .setFrom(from)
                .setFetchSource(null,
                        new String[]{"index", "table"})
                .setSize(size)
                .get();

        return searchResponse.getHits();

    }

    //查询车辆自身告警数据细节
    public SearchHits searchVehicleAlarmsWithCondition(String[] enterprise,
                                                       String[] hatchback,
                                                       String[] vin,
                                                       Integer alarmLevel,
                                                       String alarmContent,
                                                       Integer handleStatus,
                                                       Long startTime,
                                                       Long endTime,
                                                       int from,
                                                       int size){

        SearchRequestBuilder searchRequestBuilder = newSearchRequestBuilder(startTime, endTime);
        BoolQueryBuilder queryBuilder = createQuery(enterprise, hatchback, vin,
                null, alarmLevel, null, startTime, endTime, 0);
        if(alarmContent != null){
            queryBuilder.must(QueryBuilders.prefixQuery("alarm_value", alarmContent));
        }

        if(alarmLevel == null){
            //告警级别必须大于等于1
            queryBuilder.must(QueryBuilders.rangeQuery("alarm_level").gte(1));
        }

        if(null != handleStatus){
            switch (handleStatus) {
                case 1:
                    queryBuilder.must(QueryBuilders.termQuery("handleStatus", 1));
                    break;
                case 0:
                    queryBuilder.mustNot(QueryBuilders.termQuery("handleStatus", 1));
                    break;
            }
        }

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder)
                .addSort("collectTime", SortOrder.DESC)
                .setFrom(from)
                .setSize(size)
                .get();

        return searchResponse.getHits();

    }

    public void handleStatus2Es(List<Map<String, Object>> handleStatus) {
        String index = (String)handleStatus.get(0).get("index");
        String table = (String)handleStatus.get(0).get("table");

//        BulkRequestBuilder builder1 = transportClient.prepareBulk();
//        BulkRequestBuilder builder2 = transportClient.prepareBulk();

        for (Map<String, Object> handleStatu : handleStatus) {
            String id = (String) handleStatu.get("id");

//            builder1.add(transportClient.prepareDelete(index, table, id));
//            builder2.add(transportClient.prepareIndex(index, table, id).setSource(handleStatu));
//            logger.info(handleStatu.toString());

            transportClient.prepareDelete(index, table, id).execute().actionGet();
            transportClient.prepareIndex(index, table, id).setSource(handleStatu).execute().actionGet();
        }

//        BulkResponse response1 = builder1.execute().actionGet();
//        logger.info("删除记录状态 " + response1.status().toString());
//
//        BulkResponse response2 = builder2.execute().actionGet();
//        logger.info("新增记录状态 " + response2.status().toString());
    }

    public Map<String, Object> countVehicleAlarmsWithCondition(String[] enterprise,
                                                               String[] hatchback,
                                                               String[] vin,
                                                               Long startTime,
                                                               Long endTime){

        SearchRequestBuilder searchRequestBuilder = newSearchRequestBuilder(startTime, endTime);
        BoolQueryBuilder queryBuilder = createQuery(enterprise, hatchback, vin,
                null, null, null, startTime, endTime, 0);

        //告警级别必须大于等于1
        queryBuilder.must(QueryBuilders.rangeQuery("alarm_level").gte(1));


        //根据告警字段进行分组统计
        TermsAggregationBuilder alarmCount = AggregationBuilders.terms("alarm_level")
                .field("alarm_level")
                .order(BucketOrder.count(false));
        searchRequestBuilder.addAggregation(alarmCount);

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder).get();
        InternalTerms alarmCountTerms = searchResponse.getAggregations().get("alarm_level");
        List<InternalTerms.Bucket> buckets = alarmCountTerms.getBuckets();
        int size = buckets.size();

        //初始化结果集
        Map<String, Object> res = Maps.newHashMapWithExpectedSize(size);
        res.put("alarm1Count", 0);
        res.put("alarm2Count", 0);
        res.put("alarm3Count", 0);

        InternalTerms.Bucket bucket;
        for (int i = 0; i < size; i++) {
            bucket = buckets.get(i);
            switch (bucket.getKeyAsNumber().intValue()){
                case 1: res.put("alarm1Count", bucket.getDocCount());
                case 2: res.put("alarm2Count", bucket.getDocCount());
                case 3: res.put("alarm3Count", bucket.getDocCount());
            }
        }

        return res;

    }

    private BoolQueryBuilder createQuery(String[] enterprise,
                                         String[] hatchback,
                                         String[] vin,
                                         String alarmTypeName,
                                         Integer alarmLevel,
                                         String alarmContent,
                                         Long startTime,
                                         Long endTime,
                                         int alarmType){

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("alarm_type", alarmType));

        if(alarmTypeName != null){
            boolQueryBuilder.must(QueryBuilders.prefixQuery("alarm_type_name", alarmTypeName));
        }

        if(alarmLevel != null){
            boolQueryBuilder.must(QueryBuilders.termQuery("alarm_level", alarmLevel.intValue()));
        }

        if(alarmContent != null){
            boolQueryBuilder.must(QueryBuilders.prefixQuery("alarm_content", alarmContent));
        }

        RangeQueryBuilder rangeQueryBuilder;
        if(startTime != null){
            rangeQueryBuilder = QueryBuilders.rangeQuery("alarm_first_time");
            rangeQueryBuilder.gte(startTime);
            boolQueryBuilder.must(rangeQueryBuilder);
        }

        if(endTime != null){
            rangeQueryBuilder = QueryBuilders.rangeQuery("collectTime");
            rangeQueryBuilder.lte(endTime);
            boolQueryBuilder.must(rangeQueryBuilder);
        }

        if(enterprise != null){
            boolQueryBuilder.must(QueryBuilders.termsQuery("enterprise", enterprise));
        }

        if(hatchback != null){
            boolQueryBuilder.must(QueryBuilders.termsQuery("hatchback", hatchback));
        }
        if(vin != null){
            boolQueryBuilder.must(QueryBuilders.termsQuery("vin", vin));
        }

        return boolQueryBuilder;
    }

    private SearchRequestBuilder newSearchRequestBuilder(Long startTime, Long endTime){
        SearchRequestBuilder requestBuilder;

        if(startTime == null && endTime == null){
            requestBuilder = transportClient.prepareSearch();
        }else{
            requestBuilder = transportClient.prepareSearch(getIndexs(startTime, endTime));
        }

        return requestBuilder.setTypes("evs_err_detail");
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
            indexs[i] = String.format("evs_err_detail_%s",
                    startDate.plusDays(i).toString("yyyyMMdd"));
        }

        return elasticSearchIndexNameCache.selectExist(indexs);
    }

}
