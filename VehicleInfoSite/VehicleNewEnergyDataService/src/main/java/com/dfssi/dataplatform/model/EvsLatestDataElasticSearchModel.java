package com.dfssi.dataplatform.model;

import com.dfssi.dataplatform.utils.ElasticSearchUtil;
import com.dfssi.dataplatform.utils.EsWhAeraGeoPoints;
import com.dfssi.dataplatform.utils.GeoPosTransformUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoPolygonQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.FiltersAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilters;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import org.elasticsearch.search.aggregations.metrics.valuecount.InternalValueCount;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Description:
 *    es最新数据查询模型
 * @author LiXiaoCong
 * @version 2018/5/2 11:31
 */
@Component
public class EvsLatestDataElasticSearchModel {
    private final Logger logger = LoggerFactory.getLogger(EvsLatestDataElasticSearchModel.class);

    @Autowired
    private TransportClient transportClient;

    @Autowired
    private ExecutorService executorService;


    public Map<String, Object> countLatestOnlineMsg(){
        SearchRequestBuilder requestBuilder = getCountLatestRequestBuilder();

        AggregationBuilder totalManufacturerCount = AggregationBuilders.cardinality("totalManufacturer")
                .field("vehicleCompany");

        AggregationBuilder totalVehicleTypeCount = AggregationBuilders.cardinality("totalVehicleType")
                .field("vehicleType");

        AggregationBuilder totalVehicleCount = AggregationBuilders.cardinality("totalVehicle")
                .field("vin");


        requestBuilder.addAggregation(totalVehicleCount);
        requestBuilder.addAggregation(totalVehicleTypeCount);
        requestBuilder.addAggregation(totalManufacturerCount);

        addTodayLimit(requestBuilder);
        SearchResponse searchResponse = requestBuilder.get();

        Map<String, Object> map = Maps.newHashMap();

        InternalCardinality totalVehicleAgg = searchResponse.getAggregations().get("totalVehicle");
        long totalVehicle = totalVehicleAgg.getValue();
        map.put("totalVehicle", totalVehicle);

        InternalCardinality totalVehicleTypeAgg = searchResponse.getAggregations().get("totalVehicleType");
        long totalVehicleType = totalVehicleTypeAgg.getValue();
        map.put("totalVehicleType", totalVehicleType);

        InternalCardinality totalManufacturerAgg = searchResponse.getAggregations().get("totalManufacturer");
        long totalManufacturer = totalManufacturerAgg.getValue();
        map.put("totalManufacturer", totalManufacturer);

        return map;
    }

    public Map<String, Object> countLatestWorkingstatus(){
        SearchRequestBuilder requestBuilder = getCountLatestRequestBuilder();

        AggregationBuilder totalVehicleCount = AggregationBuilders.count("totalOnline")
                .field("vin");

        AggregationBuilder totalRunningCount = AggregationBuilders.filter("totalRunning",
                QueryBuilders.termQuery("vehicleStatusCode", 1));

        AggregationBuilder totalChargingCount = AggregationBuilders.filter("totalCharging",
                QueryBuilders.termsQuery("chargingStatusCode", new int[]{1, 2}));


        requestBuilder.addAggregation(totalVehicleCount);
        requestBuilder.addAggregation(totalRunningCount);
        requestBuilder.addAggregation(totalChargingCount);

        addTodayLimit(requestBuilder);
        SearchResponse searchResponse = requestBuilder.get();

        Map<String, Object> map = Maps.newHashMap();

        InternalValueCount totalVehicleAgg = searchResponse.getAggregations().get("totalOnline");
        long totalVehicle = totalVehicleAgg.getValue();
        map.put("totalOnline", totalVehicle);

        InternalFilter totalRunningAgg = searchResponse.getAggregations().get("totalRunning");
        long totalRunning = totalRunningAgg.getDocCount();
        map.put("totalRunning", totalRunning);

        InternalFilter totalChargingCountAgg = searchResponse.getAggregations().get("totalCharging");
        long totalCharging = totalChargingCountAgg.getDocCount();
        map.put("totalCharging", totalCharging);

        map.put("total", searchResponse.getHits().getTotalHits());

        return map;
    }

    public List<Map<String, Object>> countOnlineTopNVehicleCompany(int n){
        SearchRequestBuilder requestBuilder = getCountLatestRequestBuilder();

        TermsAggregationBuilder topCount = AggregationBuilders.terms("VehicleCompanyOnlineTopN")
                .field("vehicleCompany")
                .order(BucketOrder.count(false))
                .size(n);

        requestBuilder.addAggregation(topCount);

        addTodayLimit(requestBuilder);
        SearchResponse searchResponse = requestBuilder.get();

        InternalTerms vehicleCompanyOnlineTopN = searchResponse.getAggregations().get("VehicleCompanyOnlineTopN");
        List<InternalTerms.Bucket> buckets = vehicleCompanyOnlineTopN.getBuckets();
        int size = buckets.size();
        List<Map<String, Object>> res = Lists.newArrayListWithCapacity(size);
        InternalTerms.Bucket bucket;
        Map<String, Object> map;
        for (int i = 0; i < size; i++) {
            bucket = buckets.get(i);

            map = Maps.newHashMap();
            map.put("seq", (i + 1));
            map.put("name", bucket.getKey());
            map.put("totalOnline", bucket.getDocCount());

            res.add(map);
        }
        return res;
    }


    public List<Map<String, Object>> countOnlineTopNArea(int n){

        SearchRequestBuilder requestBuilder = getCountLatestRequestBuilder();

        Map<String, List<GeoPoint>> whAreaPointsMap = EsWhAeraGeoPoints.getWhAreaPointsMap();
        whAreaPointsMap.forEach((areaName, points) ->{
            GeoPolygonQueryBuilder polygonFilterBuilder = QueryBuilders.geoPolygonQuery("location", points);
            FiltersAggregationBuilder filtersAggregationBuilder
                    = AggregationBuilders.filters(areaName, polygonFilterBuilder);
            requestBuilder.addAggregation(filtersAggregationBuilder);
        }) ;

        addTodayLimit(requestBuilder);
        SearchResponse searchResponse = requestBuilder.get();

        List<Map<String, Object>> res = Lists.newArrayList();
        Map<String, Object> map;

        Iterator<Aggregation> iterator = searchResponse.getAggregations().iterator();
        InternalFilters aggregation;
        String name;
        List<InternalFilters.InternalBucket> buckets;
        InternalFilters.InternalBucket bucket;
        while (iterator.hasNext()){
            aggregation = (InternalFilters) iterator.next();
            name = aggregation.getName();
            buckets = aggregation.getBuckets();
            if(buckets != null && !buckets.isEmpty()){
                bucket = buckets.get(0);

                map = Maps.newHashMap();
                map.put("name", name);
                map.put("total", bucket.getDocCount());

                res.add(map);
            }
        }

        res.sort((o1, o2) -> Long.compare((long)o2.get("total"), (long)o1.get("total")));

        int size = res.size();
        List<Map<String, Object>> lists = Lists.newArrayListWithCapacity(n);
        for (int i = 0; i < size && i < n; i++) {
            Map<String, Object> m = res.get(i);
            m.put("seq", i + 1);
            lists.add(m);
        }
        return lists;
    }


    public Map<String, Object> getLatestDataByVin(String vin, Set<String> columns){
        GetRequestBuilder getRequestBuilder = transportClient.prepareGet("new_vehicle_data_latest",
                "new_vehicle_data",
                vin);
        if(columns != null && columns.size() > 0){
            getRequestBuilder.setFetchSource(columns.toArray(new String[columns.size()]), null);
        }

        GetResponse response = getRequestBuilder.get();

        return response.getSourceAsMap();
    }

    public SearchHits getLatestVehiclePosData(String status, int from, int size){
        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch("new_vehicle_data_latest")
                .setTypes("new_vehicle_data")
                .setFetchSource(new String[]{"vin", "latitude", "longitude", "collectTime",
                        "vehicleStatus", "chargingStatus", "speed", "accumulativeMile", "soc"}, null)
                .addSort("collectTime", SortOrder.ASC)
                .setFrom(from)
                .setSize(size);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //经纬度方位限定
        RangeQueryBuilder  rangeQueryBuilder = QueryBuilders.rangeQuery("longitude").gte(73).lt(135);
        boolQueryBuilder.must(rangeQueryBuilder);
        rangeQueryBuilder = QueryBuilders.rangeQuery("latitude").gte(4).lt(53);
        boolQueryBuilder.must(rangeQueryBuilder);

        if(status != null){
            switch (status){
                case "online":
                    addTodayLimit(boolQueryBuilder);
                    break;
                case "running":
                    addTodayLimit(boolQueryBuilder);
                    boolQueryBuilder.must(QueryBuilders.termQuery("vehicleStatusCode", 1));
                    break;
                case "charging":
                    addTodayLimit(boolQueryBuilder);
                    boolQueryBuilder.must(QueryBuilders.termsQuery("chargingStatusCode", new int[]{1, 2}));
                    break;
                default:
                    break;
            }
        }

        searchRequestBuilder.setQuery(boolQueryBuilder);

        SearchResponse searchResponse = searchRequestBuilder.get();

        return searchResponse.getHits();
    }


    public SearchHits searchLatestData(String[] enterprise,
                                       String[] hatchback,
                                       String[] vin,
                                       Set<String> columns,
                                       Long startTime,
                                       Long endTime,
                                       int from,
                                       int size){

        SearchRequestBuilder searchRequestBuilder = transportClient.prepareSearch("new_vehicle_data_latest")
                .setTypes("new_vehicle_data");

        if(columns != null && columns.size() > 0){
            columns.add("index");
            columns.add("id");
            searchRequestBuilder.setFetchSource(columns.toArray(new String[columns.size()]), null);
        }

        BoolQueryBuilder queryBuilder = createQuery(enterprise, hatchback, vin, startTime, endTime);

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder)
                .addSort("collectTime", SortOrder.DESC)
                .setFrom(from)
                .setSize(size)
                .get();

        return searchResponse.getHits();
    }

    private BoolQueryBuilder createQuery(String[] enterprise,
                                         String[] hatchback,
                                         String[] vin,
                                         Long startTime,
                                         Long endTime) {
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

        RangeQueryBuilder rangeQueryBuilder = ElasticSearchUtil.createRangeQueryBuilder("collectTime", startTime, endTime);
        if(rangeQueryBuilder != null){
            queryBuilder.must(rangeQueryBuilder);
        }
        return queryBuilder;
    }

    public List<Map<String, Object>> countOnlineAndRunningByField(String field,
                                                                  String[] enterprise,
                                                                  String[] hatchback,
                                                                  String[] vin){

        Future<List<Map<String, Object>>> countOnlineFuture =
                executorService.submit(() -> countOnlineByField(field, enterprise, hatchback, vin));

        Future<Map<Object, Long>> countOnRunningFuture =
                executorService.submit(() -> countOnRunningByField(field, enterprise, hatchback, vin));

        List<Map<String, Object>> countOnline;
        try {
           countOnline = countOnlineFuture.get();
            Map<Object, Long> countOnRunning = countOnRunningFuture.get();

            countOnline.forEach(r ->{
                Long running = countOnRunning.get(r.get(field));
                if(running == null)running = 0L;
                r.put("totalRunning", running);

                Object totalOnlineObj = r.get("totalOnline");
                long totalOnline = (totalOnlineObj == null) ? 0L : (long)totalOnlineObj;

                r.put("totalStopping", totalOnline - running);
            });

        } catch (Exception e) {
            logger.error(String.format("以字段%s统计查询在线以及运行车辆数目失败。", field), e);
            countOnline = Lists.newArrayList();
        }

        return countOnline;
    }



    private List<Map<String, Object>> countOnlineByField(String field,
                                                         String[] enterprise,
                                                         String[] hatchback,
                                                         String[] vin){

        SearchRequestBuilder requestBuilder = getCountLatestRequestBuilder();
        BoolQueryBuilder queryBuilder = createQuery(enterprise, hatchback, vin, null, null);


        //查询按指定字段统计的 在线车辆
        TermsAggregationBuilder aggregationBuilder
                = AggregationBuilders.terms(field)
                .field(field)
                .order(BucketOrder.count(false));

        requestBuilder.addAggregation(aggregationBuilder);
        addTodayLimit(requestBuilder);

        SearchResponse searchResponse = requestBuilder.setQuery(queryBuilder).get();

        InternalTerms vehicleCompanyOnlineTopN = searchResponse.getAggregations().get(field);
        List<InternalTerms.Bucket> buckets = vehicleCompanyOnlineTopN.getBuckets();
        int size = buckets.size();
        List<Map<String, Object>> res = Lists.newArrayListWithCapacity(size);
        InternalTerms.Bucket bucket;
        Map<String, Object> map;
        for (int i = 0; i < size; i++) {
            bucket = buckets.get(i);

            map = Maps.newHashMap();
            map.put("seq", (i + 1));
            map.put(field, bucket.getKey());
            map.put("totalOnline", bucket.getDocCount());

            res.add(map);
        }

        return res;
    }

    private Map<Object, Long> countOnRunningByField(String field,
                                                    String[] enterprise,
                                                    String[] hatchback,
                                                    String[] vin){

        SearchRequestBuilder requestBuilder = getCountLatestRequestBuilder();
        BoolQueryBuilder queryBuilder = createQuery(enterprise, hatchback, vin, null, null);
        queryBuilder.must(QueryBuilders.rangeQuery("speed").gt(0.0));

        //查询按指定字段统计的 在线车辆
        TermsAggregationBuilder aggregationBuilder
                = AggregationBuilders.terms(field)
                .field(field);

        requestBuilder.addAggregation(aggregationBuilder);
        addTodayLimit(requestBuilder);

        SearchResponse searchResponse = requestBuilder.setQuery(queryBuilder).get();

        InternalTerms vehicleCompanyOnlineTopN = searchResponse.getAggregations().get(field);
        List<InternalTerms.Bucket> buckets = vehicleCompanyOnlineTopN.getBuckets();
        int size = buckets.size();
        Map<Object, Long> res = Maps.newHashMapWithExpectedSize(size);
        InternalTerms.Bucket bucket;
        for (int i = 0; i < size; i++) {
            bucket = buckets.get(i);
            res.put(bucket.getKey(), bucket.getDocCount());
        }
        return res;
    }

    private void addTodayLimit(SearchRequestBuilder requestBuilder){
        // requestBuilder.setQuery(QueryBuilders.rangeQuery("collectTime").gte(DateUtil.getTodayStartForLong()));
        //测试  20180727
        requestBuilder.setQuery(QueryBuilders.rangeQuery("collectTime").gte(1532620800000L));
    }

    private void addTodayLimit(BoolQueryBuilder boolQueryBuilder){
        // requestBuilder.setQuery(QueryBuilders.rangeQuery("collectTime").gte(DateUtil.getTodayStartForLong()));
        //测试  20180727
        boolQueryBuilder.must(QueryBuilders.rangeQuery("collectTime").gte(1532620800000L));
    }

    private SearchRequestBuilder getCountLatestRequestBuilder(){
        return transportClient.prepareSearch("new_vehicle_data_latest")
                .setTypes("new_vehicle_data")
                .setSize(0);
    }

    private GeoPoint newGeoPoint(double lat, double lon){
        double[] doubles = GeoPosTransformUtil.gcj02towgs84(lon, lat);
        return new GeoPoint(doubles[1],  doubles[0]);
    }
}
