package com.dfssi.elasticsearch.sql611.plugin.executors;

import com.dfssi.elasticsearch.sql611.plugin.QueryActionElasticExecutor;
import com.dfssi.elasticsearch.sql611.query.QueryAction;
import com.google.common.base.Joiner;
import org.elasticsearch.client.Client;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestStatus;

import java.util.Map;

/**
 * Created by Eliran on 26/12/2015.
 */
public class CSVResultRestExecutor implements RestExecutor {

    @Override
    public void execute(Client client, Map<String, String> params, QueryAction queryAction, RestChannel channel) throws Exception {
        Object queryResult = QueryActionElasticExecutor.executeAnyAction(client, queryAction);

        boolean flat = getBooleanOrDefault(params,"flat",false);
        String separator = ",";
        if(params.containsKey("separator")){
            separator = params.get("separator");
        }
        boolean includeScore = getBooleanOrDefault(params,"_score",false);
        boolean includeType = getBooleanOrDefault(params,"_type",false);
        boolean includeId = getBooleanOrDefault(params,"_id",false);
        CSVResult result  = new CSVResultsExtractor(includeScore,includeType,includeId).extractResults(queryResult,flat,separator);
        String newLine = "\n";
        if(params.containsKey("newLine")){
            newLine = params.get("newLine");
        }
        String csvString = buildString(separator, result, newLine);
        BytesRestResponse bytesRestResponse = new BytesRestResponse(RestStatus.OK, csvString);
        channel.sendResponse(bytesRestResponse);
    }

    @Override
    public String execute(Client client, Map<String, String> params, QueryAction queryAction) throws Exception {
        Object queryResult = QueryActionElasticExecutor.executeAnyAction(client, queryAction);

        boolean flat = getBooleanOrDefault(params,"flat",false);
        String separator = ",";
        if(params.containsKey("separator")){
            separator = params.get("separator");
        }
        boolean includeScore = getBooleanOrDefault(params,"_score",false);
        boolean includeType = getBooleanOrDefault(params,"_type",false);
        boolean includeId = getBooleanOrDefault(params,"_id",false);
        CSVResult result  = new CSVResultsExtractor(includeScore,includeType,includeId).extractResults(queryResult,flat,separator);
        String newLine = "\n";
        if(params.containsKey("newLine")){
            newLine = params.get("newLine");
        }
        String csvString = buildString(separator, result, newLine);
        return csvString;
    }

    private boolean getBooleanOrDefault(Map<String, String> params, String param, boolean defaultValue) {
        boolean flat = defaultValue;
        if(params.containsKey(param)){
            flat = Boolean.parseBoolean(params.get(param));
        }
        return flat;
    }

    private String buildString(String separator, CSVResult result, String newLine) {
        StringBuilder csv = new StringBuilder();
        csv.append(Joiner.on(separator).join(result.getHeaders()));
        csv.append(newLine);
        csv.append(Joiner.on(newLine).join(result.getLines()));
        return csv.toString();
    }

}
