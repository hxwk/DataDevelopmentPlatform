package com.dfssi.dataplatform.datasync.service.util.msgutil;

/**
 * Created by cxq on 2017/11/16.
 */
public class RpcResponse {
    private String requestId;
    private String responseId;
    private Object msg;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public String toString(){
        return "{requstId:"+requestId+",responseId:"+responseId+",msg:"+msg+"}";
    }
}
