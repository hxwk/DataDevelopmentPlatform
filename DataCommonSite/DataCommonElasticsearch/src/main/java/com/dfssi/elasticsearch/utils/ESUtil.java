package com.dfssi.elasticsearch.utils;

import com.google.common.collect.HashMultimap;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.template.delete.DeleteIndexTemplateRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;


/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 1.0
 * @date 2016/11/21 9:30
 */
public class ESUtil {

    private ESUtil(){}

    public static TransportClient createClient(String clusterName,
                                               HashMultimap<String, Integer> hostAndPortMap) throws UnknownHostException {

        Settings.Builder builder = Settings.builder().put("client.transport.sniff", false);

        builder.put("cluster.name", clusterName);//配置集群名称
        Settings settings = builder.build();
        TransportClient client =  new PreBuiltTransportClient(settings);

        for (Map.Entry<String, Integer> hostAndPort : hostAndPortMap.entries()) {
            client.addTransportAddress(
                    new TransportAddress(
                            InetAddress.getByName(hostAndPort.getKey()), hostAndPort.getValue()));
        }

        client.admin().indices();

        return client;
    }

    public static boolean deleteIndex(TransportClient client,  String ... indexs) throws Exception {


        if(indexs.length == 0) return true;

        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexs);
        client.admin().indices().delete(deleteIndexRequest).actionGet();

        return  true;
    }


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
                                       String indexName,String indexType){

        if(StringUtils.isNotBlank(indexName) && StringUtils.isNotBlank(indexType)) {
            TypesExistsResponse response =
                    client.admin().indices()
                            .typesExists(new TypesExistsRequest(
                                    new String[]{indexName}, indexType)).actionGet();
            return response.isExists();
        }
        return false;
    }

    public static void main(String[] args) throws Exception {


        HashMultimap<String, Integer> map = HashMultimap.create();
        map.put("hadoop-2", 9302);
        map.put("hadoop-2", 9301);
        map.put("hadoop-2", 9300);

        TransportClient client = createClient("es", map);
        DeleteIndexTemplateRequestBuilder logstash = client.admin().indices().prepareDeleteTemplate("logstash");
        logstash.get();


        DeleteIndexRequestBuilder deleteIndexRequestBuilder = client.admin().indices().prepareDelete("logstash-2018.01.08");
        DeleteIndexResponse deleteIndexResponse = deleteIndexRequestBuilder.get();

        System.out.println(deleteIndexResponse);

    }

}
