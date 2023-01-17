package com.dfssi.dataplatform.vehicleinfo.vehicleroad.dao;

import com.dfssi.dataplatform.vehicleinfo.vehicleroad.cache.ElasticSearchIndexNameCache;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.config.ElasticsearchConfig;
import com.dfssi.dataplatform.vehicleinfo.vehicleroad.config.RoadVehicleConfig;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/9/30 11:46
 */
public abstract class ElasticSearchDao {
    private final Logger logger = LoggerFactory.getLogger(ElasticSearchDao.class);

    @Autowired
    protected ElasticsearchConfig elasticsearchConfig;

    @Autowired
    protected TransportClient transportClient;

    @Autowired
    protected RoadVehicleConfig roadVehicleConfig;

    @Autowired
    protected ElasticSearchIndexNameCache elasticSearchIndexNameCache;


    protected SearchRequestBuilder newSearchRequestBuilder(Long startTime, Long endTime){
        SearchRequestBuilder requestBuilder;

        if(startTime == null && endTime == null){
            requestBuilder = transportClient.prepareSearch(String.format("%s_2*", elasticsearchConfig.getTypeName()));
        }else{
            requestBuilder = transportClient.prepareSearch(getIndexs(startTime, endTime));
        }

        return requestBuilder.setTypes(elasticsearchConfig.getTypeName());
    }


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
            indexs[i] = String.format("%s_%s", elasticsearchConfig.getTypeName(), startDate.plusDays(i).toString("yyyyMMdd"));
        }

        return elasticSearchIndexNameCache.selectExist(indexs);
    }

}
