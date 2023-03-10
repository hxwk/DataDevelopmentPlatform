package com.dfssi.elasticsearch.sql611.plugin;


import com.dfssi.elasticsearch.sql611.exception.SqlParseException;
import com.dfssi.elasticsearch.sql611.query.*;
import com.dfssi.elasticsearch.sql611.query.join.ESJoinQueryAction;
import com.dfssi.elasticsearch.sql611.query.multi.MultiQueryAction;
import com.dfssi.elasticsearch.sql611.query.multi.MultiQueryRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregations;

import java.io.IOException;

/**
 * Created by Eliran on 3/10/2015.
 */
public class QueryActionElasticExecutor {
    public static SearchHits executeSearchAction(DefaultQueryAction searchQueryAction) throws SqlParseException {
        SqlElasticSearchRequestBuilder builder  =  searchQueryAction.explain();
        return ((SearchResponse) builder.get()).getHits();
    }

    public static SearchHits executeJoinSearchAction(Client client , ESJoinQueryAction joinQueryAction) throws IOException, SqlParseException {
        SqlElasticRequestBuilder joinRequestBuilder = joinQueryAction.explain();
        ElasticJoinExecutor executor = ElasticJoinExecutor.createJoinExecutor(client,joinRequestBuilder);
        executor.run();
        return executor.getHits();
    }

    public static Aggregations executeAggregationAction(AggregationQueryAction aggregationQueryAction) throws SqlParseException {
        SqlElasticSearchRequestBuilder select =  aggregationQueryAction.explain();
        return ((SearchResponse)select.get()).getAggregations();
    }

    public static ActionResponse executeDeleteAction(DeleteQueryAction deleteQueryAction) throws SqlParseException {
        return deleteQueryAction.explain().get();
    }

    public static SearchHits executeMultiQueryAction(Client client, MultiQueryAction queryAction) throws SqlParseException, IOException {
        SqlElasticRequestBuilder multiRequestBuilder = queryAction.explain();
        ElasticHitsExecutor executor = MultiRequestExecutorFactory.createExecutor(client, (MultiQueryRequestBuilder) multiRequestBuilder);
        executor.run();
        return executor.getHits();
    }

    public static Object executeAnyAction(Client client , QueryAction queryAction) throws SqlParseException, IOException {
        if(queryAction instanceof DefaultQueryAction)
            return executeSearchAction((DefaultQueryAction) queryAction);
        if(queryAction instanceof AggregationQueryAction)
            return executeAggregationAction((AggregationQueryAction) queryAction);
        if(queryAction instanceof ESJoinQueryAction)
            return executeJoinSearchAction(client, (ESJoinQueryAction) queryAction);
        if(queryAction instanceof MultiQueryAction)
            return executeMultiQueryAction(client, (MultiQueryAction) queryAction);
        if(queryAction instanceof DeleteQueryAction)
            return executeDeleteAction((DeleteQueryAction) queryAction);
        return null;
    }


}
