package com.dfssi.elasticsearch.sql611.query.multi;

import com.dfssi.elasticsearch.sql611.exception.SqlParseException;
import com.dfssi.elasticsearch.sql611.query.QueryAction;
import org.elasticsearch.client.Client;

/**
 * Created by Eliran on 19/8/2016.
 */
public class ESMultiQueryActionFactory {

    public static QueryAction createMultiQueryAction(Client client, MultiQuerySelect multiSelect) throws SqlParseException {
        switch (multiSelect.getOperation()){
            case UNION_ALL:
            case UNION:
                return new MultiQueryAction(client,multiSelect);
            default:
                throw new SqlParseException("only supports union and union all");
        }
    }
}
