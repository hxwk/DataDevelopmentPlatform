package com.dfssi.elasticsearch.sql611.plugin;

import com.dfssi.elasticsearch.sql611.exception.SqlParseException;
import com.dfssi.elasticsearch.sql611.query.multi.MultiQueryRequestBuilder;
import org.elasticsearch.client.Client;


/**
 * Created by Eliran on 21/8/2016.
 */
public class MultiRequestExecutorFactory {
     public static ElasticHitsExecutor createExecutor(Client client, MultiQueryRequestBuilder builder) throws SqlParseException {
         switch (builder.getRelation()){
             case UNION_ALL:
             case UNION:
                 return new UnionExecutor(client,builder);
             case MINUS:
                 return new MinusExecutor(client,builder);
             default:
                 throw new SqlParseException("only supports union, and minus operations");
         }
     }
}
