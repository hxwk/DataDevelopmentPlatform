package com.dfssi.dataplatform.datasync.service.restful.entity;

import java.util.List;

/**
 * Created by cxq on 2017/11/28.
 */
public class RestfulResponse {
    private int returnCode;
    private String returnMsg;
    private List data;

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }
}
