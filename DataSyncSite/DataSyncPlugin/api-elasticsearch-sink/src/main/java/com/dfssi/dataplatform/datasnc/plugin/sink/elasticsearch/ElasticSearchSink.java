package com.dfssi.dataplatform.datasnc.plugin.sink.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dfssi.dataplatform.datasnc.plugin.sink.elasticsearch.configuration.ElasticSearchClient;
import com.dfssi.dataplatform.datasnc.plugin.sink.elasticsearch.constant.ParameterConstant;
import com.dfssi.dataplatform.datasync.flume.agent.*;
import com.dfssi.dataplatform.datasync.flume.agent.conf.Configurable;
import com.dfssi.dataplatform.datasync.flume.agent.sink.AbstractSink;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.dfssi.dataplatform.datasnc.plugin.sink.elasticsearch.util.ProtoUtil.readTimeString;

public class ElasticSearchSink extends AbstractSink implements Configurable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchSink.class);
    private List<Map<String, String>> indexMap = new ArrayList<>();
    private int batchSize;
    private TransportClient client;

    @Override
    public Status process() throws EventDeliveryException {
        Status status = Status.READY;
        List<Event> listEvents = new ArrayList<>();
        Transaction transaction = null;
        Channel channel = getChannel();
        try {
            transaction = channel.getTransaction();
            transaction.begin();
            Event event;
            int processEvents = 0;
            //批量获取event
            for(; processEvents < batchSize; processEvents++){
                event = channel.take();
                if(event == null){
                    if(processEvents == 0){
                        status = Status.BACKOFF;
                    }
                    break;
                }
                listEvents.add(event);
            }
            //event写入相应的index中
            for(Map indMap : indexMap){
                String msgId = (String)indMap.get("msgId");
                String template = (String)indMap.get("indTemplate");
                String rule = (String)indMap.get("rule");
                String index = "";
                List<IndexRequestBuilder> indexRequestBuilders = new ArrayList<>();
                for(Event event1 : listEvents){
                    Map<String, String> header = event1.getHeaders();
                    byte[] body = event1.getBody();
                    JSONObject jsonObject = JSONObject.parseObject(header.get("key"));
                    String eventMsgId = jsonObject.getString("msgId");
                    Map map = (Map)JSON.parse(new String(body));
                    LOGGER.info("=======event header: {}", header);
                    LOGGER.info("=======event body: {}", map);
                    if(StringUtils.isNotEmpty(msgId) && msgId.equals(eventMsgId)){
                        if("DAY".equalsIgnoreCase(rule)){
                            index = template + "_" + readTimeString((Long) map.get("gpsTime"), "yyyyMMdd");
                        }else if("Month".equalsIgnoreCase(rule)){
                            index = template + "_" + readTimeString((Long) map.get("gpsTime"), "yyyyMM");
                        }else if("YEAR".equalsIgnoreCase(rule)){
                            index = template + "_" + readTimeString((Long) map.get("gpsTime"), "yyyy");
                        }else{
                            index = template;
                        }
                        IndexRequestBuilder indexRequest = client.prepareIndex(index, template).setSource(map);
                        indexRequestBuilders.add(indexRequest);
                    }
                }
                ElasticSearchWriter.write(client, indexRequestBuilders, batchSize);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            LOGGER.error("events to es-sink fail ", e);
        }finally {
            transaction.close();
        }
        return status;
    }

    @Override
    public void configure(Context context) {
        System.out.println("es-sink configure ......99");
        LOGGER.info("es-sink configure ......");
       String clusterName =  context.getString(ParameterConstant.ES_CLUSTER_NAME);
       int port = context.getInteger(ParameterConstant.ES_SERVER_PORT);
       String ip = context.getString(ParameterConstant.ES_SERVER_IP);
       batchSize = context.getInteger(ParameterConstant.EVENT_BATCH_SIZE);
       String indexMapping = context.getString(ParameterConstant.EVENT_INDEX_MAPPING);
       JSONArray array = JSONObject.parseArray(indexMapping);
       for(int i = 0; i<array.size(); i++){
           Map json = (Map)array.get(i);
           indexMap.add(json);
       }
       client = ElasticSearchClient.getClient(clusterName, ip, port);
    }
}
