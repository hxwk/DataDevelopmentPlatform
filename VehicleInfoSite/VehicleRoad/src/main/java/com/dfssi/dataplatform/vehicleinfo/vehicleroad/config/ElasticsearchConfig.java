package com.dfssi.dataplatform.vehicleinfo.vehicleroad.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import lombok.Getter;
import lombok.ToString;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.util.Map;

/**
 * Description:
 *   elasticsearch查询依赖
 * @author LiXiaoCong
 * @version 2018/4/25 15:09
 */
@Configuration
@Getter
@ToString
public class ElasticsearchConfig {

    private final Logger logger = LoggerFactory.getLogger(ElasticsearchConfig.class);

    @Value("${es.client.clustername}")
    private String clustername;

    @Value("${es.client.servers}")
    private String servers;

    @Value("${es.client.search.pool}")
    private String poolSize;

    @Value("${es.client.search.type}")
    private String typeName;

    @Bean
    public TransportClient init() {
        logger.info("Elasticsearch客户端初始化开始...");
        System.setProperty("es.set.netty.runtime.available.processors","false");
        TransportClient transportClient = null;
        try {
            // 配置信息
            Settings.Builder builder = Settings.builder();
            if(clustername == null) {
                builder.put("client.transport.ignore_cluster_name", true);
            }else {
                builder.put("cluster.name", clustername);
            }

            builder.put("client.transport.sniff", false);
            builder.put("client.transport.ping_timeout", "180s");
            builder.put("thread_pool.search.size", Integer.parseInt(poolSize));

            Settings esSetting = builder.build();
            //配置信息Settings自定义,下面设置为EMPTY
            transportClient = new PreBuiltTransportClient(esSetting);

            logger.info(String.format("es.client.servers = %s", servers));
            HashMultimap<String, Integer> hostAndPort = HashMultimap.create();
            String[] hps = servers.split(",");
            String[] kv;
            for(String hp : hps){
                kv = hp.split(":");
                if(kv.length == 2){
                    hostAndPort.put(kv[0], Integer.parseInt(kv[1]));
                }else{
                    logger.error(String.format("es.client.servers: %s 配置有误， 示例： host:port", hp));
                }
            }

            Preconditions.checkArgument(!hostAndPort.isEmpty(), "es.client.servers 中无可用的服务节点配置。");

            for(Map.Entry<String, Integer> hp : hostAndPort.entries()) {
                transportClient.addTransportAddress(new TransportAddress(InetAddress.getByName(hp.getKey()), hp.getValue()));
            }

            logger.info(String.format("Elasticsearch客户端初始化成功，参数为：%s", toString()));

        } catch (Exception e) {
            logger.error("elasticsearch TransportClient create error!!!", e);
        }

        return transportClient;
    }

}
