package com.dfssi.dataplatform.datasnc.plugin.sink.elasticsearch;

import com.dfssi.dataplatform.datasnc.plugin.sink.elasticsearch.configuration.ElasticSearchClient;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ElasticSearchWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger("ElasticSearchWriter");

    public static void write(TransportClient client, List<? extends IndexRequest> indexRequestBuilders){
        try {
            int len = indexRequestBuilders.size();
            BulkResponse bulkResponse = null;
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for(int i = 1; i<= len; i++){
                bulkRequest.add(indexRequestBuilders.get(i - 1));
            }
            bulkResponse = bulkRequest.get();
            if(bulkResponse.hasFailures()){
                LOGGER.info("write to elasticsearch-sink fail: {}", bulkResponse.buildFailureMessage());
            }
        } catch (Exception e) {
            LOGGER.info("write events to elasticsearch-sink fail: {}", e);
        }
    }

    public static void write(TransportClient client, List<IndexRequestBuilder> indexRequestBuilders, int batchSize){
        int len = indexRequestBuilders.size();
        try {
            BulkResponse bulkResponse = null;
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for(int i = 1; i<= len; i++){
                bulkRequest.add(indexRequestBuilders.get(i - 1));
                if(i % batchSize == 0){
                    bulkResponse = bulkRequest.get();
                    if(bulkResponse.hasFailures()){
                        LOGGER.error("write events to elasticsearch-sink fail: {}", bulkResponse.buildFailureMessage());
                    }
                }
            }
            if(len % batchSize > 0){
                bulkResponse = bulkRequest.get();
                if(bulkResponse.hasFailures()){
                    LOGGER.error("write to elasticsearch-sink fail: {}", bulkResponse.buildFailureMessage());
                }
            }
            LOGGER.info("write to elasticsearch-sink total: {}", len);
        } catch (Exception e) {
            LOGGER.error("write events to elasticsearch-sink fail: {}", e);
        }
    }

    public void write(TransportClient client, String index, String type, List<Map<String, Object>> listMap, int batchSize){
        int len = listMap.size();
        try {
            BulkResponse bulkResponse;
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            for(int i = 1; i <= len; i++){
                bulkRequest.add(client.prepareIndex(index, type).setSource(listMap.get(i-1)));
                if(i % batchSize == 0){
                    bulkResponse = bulkRequest.get();
                    if(bulkResponse.hasFailures()){
                        LOGGER.info("write to elasticsearch-sink fail: {}", bulkResponse.buildFailureMessage());
                    }
                }
            }
            if(len % batchSize > 0){
                bulkResponse = bulkRequest.get();
                if(bulkResponse.hasFailures()){
                    LOGGER.info("write to elasticsearch-sink fail: {}", bulkResponse.buildFailureMessage());
                }
            }
            LOGGER.info("write to elasticsearch-sink success total: {}", len);
        } catch (Exception e) {
            LOGGER.error("write to elasticsearch-sink fail: {}", e.getMessage());
        }
    }

}
