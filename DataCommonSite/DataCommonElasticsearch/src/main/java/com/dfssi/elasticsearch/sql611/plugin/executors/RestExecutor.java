package com.dfssi.elasticsearch.sql611.plugin.executors;

import com.dfssi.elasticsearch.sql611.query.QueryAction;
import org.elasticsearch.client.Client;
import org.elasticsearch.rest.RestChannel;

import java.util.Map;

/**
 * Created by Eliran on 26/12/2015.
 */
public interface RestExecutor {
    public void execute(Client client, Map<String, String> params, QueryAction queryAction, RestChannel channel) throws Exception;

    public String execute(Client client, Map<String, String> params, QueryAction queryAction) throws Exception;
}
