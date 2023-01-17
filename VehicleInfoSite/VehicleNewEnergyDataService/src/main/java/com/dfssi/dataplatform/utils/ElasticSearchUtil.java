package com.dfssi.dataplatform.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/27 8:30
 */
public class ElasticSearchUtil {
    private ElasticSearchUtil(){}

    public static boolean isExistsIndex(TransportClient client,
                                        String indexName){
        if(StringUtils.isNotBlank(indexName)) {
            IndicesExistsResponse response =
                    client.admin().indices().exists(
                            new IndicesExistsRequest().indices(new String[]{indexName})).actionGet();
            return response.isExists();
        }
        return false;
    }


    public static boolean isExistsType(TransportClient client,
                                       String indexName,
                                       String indexType){

        if(StringUtils.isNotBlank(indexName) && StringUtils.isNotBlank(indexType)) {
            TypesExistsResponse response =
                    client.admin().indices()
                            .typesExists(new TypesExistsRequest(
                                    new String[]{indexName}, indexType)).actionGet();
            return response.isExists();
        }
        return false;
    }

    public static List<String> getIndices(TransportClient client) {
        ImmutableOpenMap<String, IndexMetaData> indexMap = client.admin()
                .cluster().prepareState().execute().actionGet().getState().getMetaData().getIndices();
        Iterator<String> keysIt = indexMap.keysIt();
        return Lists.newArrayList(keysIt);
    }

    public static List<Map<String, Object>> selectItem(SearchHits searchHits){
        SearchHit[] hits = searchHits.getHits();
        List<Map<String, Object>> resList = Lists.newLinkedList();
        for(SearchHit hit : hits){
            resList.add(hit.getSourceAsMap());
        }
        return resList;
    }

    public static List<Map<String, Object>> selectItemWithLocationTypeChange(SearchHits searchHits, String toType){
        SearchHit[] hits = searchHits.getHits();
        List<Map<String, Object>> resList = Lists.newLinkedList();
        Map<String, Object> sourceAsMap;
        for(SearchHit hit : hits){
            sourceAsMap = hit.getSourceAsMap();
            locationTypeChange(sourceAsMap, toType);
            resList.add(sourceAsMap);
        }
        return resList;
    }

    public static RangeQueryBuilder createRangeQueryBuilder(String field,
                                                            Long startTime,
                                                            Long endTime){
        RangeQueryBuilder rangeQueryBuilder = null;
        if(startTime != null || endTime != null){
            rangeQueryBuilder = QueryBuilders.rangeQuery(field);
            if(startTime != null){
                rangeQueryBuilder.gte(startTime);
            }
            if(endTime != null) {
                rangeQueryBuilder.lte(endTime);
            }

        }
        return rangeQueryBuilder;
    }


    /**
     *  将map中的经纬度 进行高德地图纠偏
     * @param sourceAsMap
     */
    public static void locationTypeChange(Map<String, Object> sourceAsMap, String toType){
        if(sourceAsMap != null && toType != null) {
            Object longitudeObj = sourceAsMap.get("longitude");
            Object latitudeObj = sourceAsMap.get("latitude");
            if (latitudeObj != null && longitudeObj != null) {

                double longitude = (double) longitudeObj;
                double latitude = (double) latitudeObj;

                double[] doubles = null;
                switch (toType){
                    //高德
                    case "gcj02":
                        doubles = GeoPosTransformUtil.wgs84togcj02(longitude, latitude);
                        break;
                    //百度
                    case "bd09":
                        doubles = GeoPosTransformUtil.wgs84tobd09(longitude, latitude);
                        break;
                }

                if(doubles != null) {
                    sourceAsMap.put("longitude", doubles[0]);
                    sourceAsMap.put("latitude", doubles[1]);
                }
            }
        }
    }
}
