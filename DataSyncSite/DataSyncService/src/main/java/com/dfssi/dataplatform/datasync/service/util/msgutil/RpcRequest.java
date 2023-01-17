package com.dfssi.dataplatform.datasync.service.util.msgutil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cxq on 2017/11/16.
 */
public class RpcRequest {
    private String requestId;
    private Map requestMap;

    public RpcRequest() {
        this(new HashMap<>());
    }

    public RpcRequest(Map requestMap) {
        this.requestMap = requestMap;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Map getRequestMap() {
        return requestMap;
    }

    public void setRequestMap(Map requestMap) {
        this.requestMap = requestMap;
    }

    public void setParam(String name,Object value){
        requestMap.put(name,value);
    }

    public Object getParam(String name){
        return requestMap.get(name);
    }
}
