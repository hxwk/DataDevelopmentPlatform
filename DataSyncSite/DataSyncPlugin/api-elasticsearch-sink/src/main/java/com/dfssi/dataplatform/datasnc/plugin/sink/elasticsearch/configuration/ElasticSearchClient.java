package com.dfssi.dataplatform.datasnc.plugin.sink.elasticsearch.configuration;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ElasticSearchClient {
    private static TransportClient client = null;
    private static ElasticSearchClient instance = null;
    private static int port;
    private static String ip;
    private static String name;

    private ElasticSearchClient(){
        createClient();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchClient.class);

    public static TransportClient getClient(String clusterName, String esServiceIp, int esPort ) {
        if(instance == null){
            name = clusterName;
            ip = esServiceIp;
            port = esPort;
            instance = new ElasticSearchClient();
        }
        return client;
    }

    private void createClient(){
        Settings settings = Settings.builder()
                .put("cluster.name", name)
                .build();
        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(ip), port));
            LOGGER.info("elasticsearch client init success");
        } catch (UnknownHostException e) {
            LOGGER.error("elasticsearch host error: {}",ip);
        }
    }
}
