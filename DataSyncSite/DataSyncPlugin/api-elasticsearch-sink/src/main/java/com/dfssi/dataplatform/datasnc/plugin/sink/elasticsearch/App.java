package com.dfssi.dataplatform.datasnc.plugin.sink.elasticsearch;

import com.dfssi.dataplatform.datasnc.plugin.sink.elasticsearch.configuration.ElasticSearchClient;
import org.elasticsearch.client.transport.TransportClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        System.out.println( "Hello World!" );
        ElasticSearchWriter elasticSearchWriter = new ElasticSearchWriter();
        TransportClient client = ElasticSearchClient.getClient("elk","172.16.1.221",9300);


    }
}
