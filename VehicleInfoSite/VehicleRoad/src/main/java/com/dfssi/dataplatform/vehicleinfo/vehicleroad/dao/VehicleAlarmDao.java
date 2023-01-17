package com.dfssi.dataplatform.vehicleinfo.vehicleroad.dao;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.AreaAlarmEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity.AreaLinkEntity;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.mapper.AreaAlarmMapper;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.util.ElasticSearchUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/10/9 16:35
 */
@Component
@Slf4j
public class VehicleAlarmDao extends ElasticSearchDao {

    protected String typeName = "road_alarm_record";

    @Autowired
    private AreaAlarmMapper areaAlarmMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private AtomicBoolean inited = new AtomicBoolean(false);

    public SearchHits queryalarm(String vid,
                                 Long startTime,
                                 Long stopTime,
                                 String alarmType,
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

        queryBuilder.must(QueryBuilders.termQuery("alarmTag", "0"));

        requestBuilder.setFetchSource(new String[]{"vid", "sim", "id", "alarmType", "time", "alarmDesc",
                "startTime", "stopTime", "count", "latitude", "longitude"}, null);
        requestBuilder.addSort("startTime", SortOrder.DESC);
        requestBuilder.setQuery(queryBuilder);

        SearchResponse searchResponse = requestBuilder.setFrom(from).setSize(size).get();

       return searchResponse.getHits();
    }


    public SearchHits queryRealTimeAlarm(String vid,
                                         String alarmType,
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

        queryBuilder.must(QueryBuilders.termQuery("alarmTag", "0"));

        requestBuilder.setFetchSource(new String[]{"vid", "sim", "id", "alarmType",
                "time", "alarmDesc", "startTime", "stopTime", "count",  "latitude", "longitude"}, null);

        requestBuilder.addSort("startTime", SortOrder.DESC);
        requestBuilder.setQuery(queryBuilder);

        SearchResponse searchResponse = requestBuilder.setFrom(from).setSize(size).get();

        return searchResponse.getHits();
    }

    public int addAreas(List<AreaAlarmEntity> alarmEntities){
        int i = areaAlarmMapper.addAreas(alarmEntities);
        //数据写到redis
        try {
            alarmEntities.stream().forEach(this ::addArea2Redis);
        } catch (Exception e) {
            log.error("添加区域到redis中失败。", e);
        }
        return i;
    }

    public int addLinks(List<AreaLinkEntity> linkEntities){
        //数据写到redis
        int i = areaAlarmMapper.addLinks(linkEntities);
        try {
            linkEntities.stream().forEach(this :: addLink2Redis);
        } catch (Exception e) {
            log.error("添加连接到redis中失败。", e);
        }
        return i;
    }

    public int deleteArea(String id){
        int i = areaAlarmMapper.deleteArea(id);
        try {
            initRedisTemplate();
            redisTemplate.delete(String.format("road:vehicle:areas:%s", id));
        } catch (Exception e) {
            log.error(String.format("删除redis中的数据%s失败。", id), e);
        }
        return i;
    }

    public int deleteLink(String vid, String id){
        int i = areaAlarmMapper.deleteLink(id);
        try {
            initRedisTemplate();
            redisTemplate.delete(String.format("road:vehicle:link:%s:%s", vid, id));
        } catch (Exception e) {
            log.error(String.format("删除redis中的数据%s失败。", id), e);
        }
        return i;
    }

    public int updateArea(AreaAlarmEntity alarmEntity){

        int i = areaAlarmMapper.updateArea(alarmEntity);
        //数据写到redis
        try {
            addArea2Redis(alarmEntity);
        } catch (Exception e) {
            log.error("添加区域到redis中失败。", e);
        }

        return i;
    }

    private void addArea2Redis(AreaAlarmEntity alarmEntity) {

        initRedisTemplate();

        Map<String, String> map = Maps.newHashMap();
        map.put("id", alarmEntity.getId());
        map.put("type", String.valueOf(alarmEntity.getType()));
        map.put("name", alarmEntity.getName());
        map.put("locations", alarmEntity.getLocations());

        redisTemplate.opsForHash()
                .putAll(String.format("road:vehicle:areas:%s", alarmEntity.getId()), map);
    }

    private void addLink2Redis(AreaLinkEntity linkEntity) {
        initRedisTemplate();

        Map<String, String> map = Maps.newHashMap();
        map.put("areaId", linkEntity.getAreaId());
        map.put("startTime", String.valueOf(linkEntity.getStartTime()));
        map.put("stopTime", String.valueOf(linkEntity.getStopTime()));
        map.put("stayTime", String.valueOf(linkEntity.getStayTime()));

        switch (linkEntity.getAreaType()){
            case 0:
                map.put("alarmType", "违规停车报警");
                break;
            case 1:
                map.put("alarmType", "偏航报警");
                break;
        }
        map.put("alarmDesc", linkEntity.getAlarmDesc());

        redisTemplate.opsForHash()
                .putAll(String.format("road:vehicle:link:%s:%s", linkEntity.getVid(), linkEntity.getId()), map);
    }

    public List<Map<String, Object>> queryAreas(String id, String name, Integer type){
        return areaAlarmMapper.queryArea(id, name, type);
    }

    public List<Map<String, Object>> queryLinks(String id, String vid, String areaId, Integer areaType){
        return areaAlarmMapper.queryLink(id, vid, areaId, areaType);
    }

    public List<Map<String, Object>> queryLinkAreas(String vid, Integer type){
        return areaAlarmMapper.queryLinkArea(vid, type);
    }

    @Override
    protected SearchRequestBuilder newSearchRequestBuilder(Long startTime, Long endTime){
        SearchRequestBuilder requestBuilder;

        if(startTime == null && endTime == null){
            requestBuilder = transportClient.prepareSearch(String.format("%s_2*", typeName));
        }else{
            requestBuilder = transportClient.prepareSearch(getIndexs(startTime, endTime));
        }

        return requestBuilder.setTypes(typeName);
    }

    @Override
    protected String[] getIndexs(Long startTime, Long endTime){

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
            indexs[i] = String.format("%s_%s", typeName, startDate.plusDays(i).toString("yyyy"));
        }

        return elasticSearchIndexNameCache.selectExist(indexs);
    }

    private void initRedisTemplate(){
        if(!inited.getAndSet(true)){
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new StringRedisSerializer());
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());
            redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        }
    }


}
