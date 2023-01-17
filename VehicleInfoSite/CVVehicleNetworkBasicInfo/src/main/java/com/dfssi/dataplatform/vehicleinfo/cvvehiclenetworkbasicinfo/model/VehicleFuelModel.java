package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model;

import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleFuelAnalysisInDaysEntity;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity.VehicleFuelCountByDaysEntity;
import com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.mapper.VehicleFuelMapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/14 20:55
 */
@Component
public class VehicleFuelModel {
    private final Logger logger = LoggerFactory.getLogger(VehicleFuelModel.class);

    @Autowired
    private VehicleFuelMapper vehicleFuelMapper;

    @Autowired
    TransportClient client;


    @DataSource(DataSource.GPDATA)
    public List<VehicleFuelAnalysisInDaysEntity> countFuelByDay(String vid,
                                                            String startDay,
                                                            String endDay){

        return vehicleFuelMapper.countFuelByDay(vid, startDay, endDay);
    }

    @DataSource(DataSource.GPDATA)
    public List<VehicleFuelAnalysisInDaysEntity> countFuelAnalysisInDays(String vid,
                                                                         String startDay,
                                                                         String endDay){

        return vehicleFuelMapper.countFuelAnalysisInDays(vid, startDay, endDay);
    }

    @DataSource(DataSource.GPDATA)
    public List<VehicleFuelCountByDaysEntity> countFuelAnalysisByDays(String vid,
                                                                      String startDay,
                                                                      String endDay){

        return vehicleFuelMapper.countFuelAnalysisByDays(vid, startDay, endDay);
    }


    public SearchHits searchLatestStatusByVid(String vid){

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("terminal_0200_latest");

        int size = 10;
        if(vid != null) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            String[] vids = vid.split(",");
            boolQueryBuilder.must(QueryBuilders.termsQuery("vid", vids));
            //boolQueryBuilder.must(todayLimit());

            searchRequestBuilder.setQuery(boolQueryBuilder);
            size = vids.length;
        }

        searchRequestBuilder.setFetchSource(new String[]{"vid", "speed1", "mile", "cumulativeOilConsumption", "gpsTime"}, null)
                .setSize(size)
                .addSort("gpsTime", SortOrder.DESC);
        SearchResponse searchResponse = searchRequestBuilder.get();

        return searchResponse.getHits();
    }

    private RangeQueryBuilder todayLimit(){
        return QueryBuilders.rangeQuery("gpsTime").gte(System.currentTimeMillis());
    }
}
