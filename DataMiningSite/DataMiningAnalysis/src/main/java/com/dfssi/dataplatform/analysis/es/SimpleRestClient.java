package com.dfssi.dataplatform.analysis.es;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.hadoop.EsHadoopIllegalStateException;
import org.elasticsearch.hadoop.cfg.Settings;
import org.elasticsearch.hadoop.rest.Response;
import org.elasticsearch.hadoop.rest.RestClient;
import org.elasticsearch.hadoop.util.BytesArray;
import org.elasticsearch.hadoop.util.StringUtils;

import static org.elasticsearch.hadoop.rest.Request.Method.PUT;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/2/6 21:18
 */
public class SimpleRestClient extends RestClient {


    public SimpleRestClient(Settings settings) {
        super(settings);
    }


    public void putMapping(String index, byte[] indexSettings, String mapping, byte[] mappingSettrings) {
        // create index first (if needed) - it might return 403/404
        if(touch(index, indexSettings)) {
            execute(PUT, getMappingPath(index, mapping), new BytesArray(mappingSettrings));
        }
    }

    public boolean touch(String index, byte[] settings){
        if (!indexExists(index)) {
            Response response;
            if(settings != null) {
               response = execute(PUT, index, new BytesArray(settings), false);
            }else {
                response = execute(PUT, index,false);
            }

            if (response.hasFailed()) {
                String msg = null;
                // try to parse the answer
                try {
                    msg = IOUtils.toString(response.body());
                } catch (Exception ex) {
                    // can't parse message, move on
                }
                if (StringUtils.hasText(msg) && !msg.contains("IndexAlreadyExistsException")) {
                    throw new EsHadoopIllegalStateException(msg);
                }
            }
            return response.hasSucceeded();
        }
        return false;

    }


    private String getMappingPath(String index, String mapping){
        return String.format("/%s/_mapping/%s", index, mapping);
    }


}
