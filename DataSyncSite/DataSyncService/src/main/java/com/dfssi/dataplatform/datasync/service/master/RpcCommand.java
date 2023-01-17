package com.dfssi.dataplatform.datasync.service.master;

import java.util.Map;

/**
 * Created by cxq on 2017/11/16.
 */
public class RpcCommand {
    private String action;
    private Map params;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public String toString() {
        return "{action:" + action + "params:" + params + "}";
    }



    public static void main(String[] args) {
        String hadoop = System.getProperty("hadoop.home.dir");
        System.out.println("hadoop = " + hadoop);
    }
}
