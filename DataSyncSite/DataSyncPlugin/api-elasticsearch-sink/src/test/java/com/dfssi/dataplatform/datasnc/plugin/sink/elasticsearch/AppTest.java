package com.dfssi.dataplatform.datasnc.plugin.sink.elasticsearch;

import static org.junit.Assert.assertTrue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasnc.plugin.sink.elasticsearch.configuration.ElasticSearchClient;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );

        TransportClient client = ElasticSearchClient.getClient("elk","172.16.1.221", 9300);
        Map<String, String> map = new HashMap<>();
        map.put("vid", "12sdfasdf123fxxxx");
        map.put("speed", "110.122");

        String json = "{\"abnormalDrivingBehaviorAlarmType\":[],\"alarm\":11,\"alarms\":[\"紧急报警\",\"超速报警\",\"危险预警\"],\"alt\":23,\"batteryVoltage\":5,\"canAndHydraulicTankStatus\":0,\"cumulativeOilConsumption\":1.331E8,\"dir\":34,\"extraInfoItems\":[],\"fromQly\":0,\"fuel\":0.3,\"gpsTime\":1526559010000,\"hydraulicTank\":0,\"lat\":30506903,\"lon\":114196893,\"mile\":1440123,\"msgId\":\"0200\",\"receiveMsgTime\":1526559174468,\"signalStates\":[],\"sim\":\"18888888888\",\"speed\":400,\"speed1\":40,\"state\":11,\"totalFuelConsumption\":0,\"vehicleStatus\":[\"ACC 开\",\"定位\",\"北纬\",\"西经\",\"运营状态\",\"经纬度未经保密插件加密\",\"空车\",\"车辆油路正常\",\"车辆电路正常\",\"车门解锁\",\"门1关\",\"门2关\",\"门3关\",\"门4关\",\"门5关\",\"未使用GPS卫星定位\",\"未使用北斗卫星定位\",\"未使用GLONASS卫星定位\",\"未使用Galileo卫星定位\"],\"vehicleWeight\":0.0,\"vid\":\"7e60fb61e28e46c3a9741a5477139a6e\"}";
//        Map jsonMap = (Map)JSONObject.parse(json);
        Map jsonMap = (Map) JSON.parse(json);
        IndexResponse indexResponse = client.prepareIndex("index_0200_20180518", "index_0200")
                .setSource(jsonMap)
                .get();

        SearchResponse searchResponse = client.prepareSearch("index_0200_20180518")
                .setTypes("index_0200")
                .get();
      long size = searchResponse.getHits().getTotalHits();
        System.out.println("size===" + size);
    }
}
