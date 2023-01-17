package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.List;

/**
 * Author: 程行荣
 * Time: 2013-10-29 17:29
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 设置终端参数
 */
public class Req_8103 extends JtsReqMsg {

    public static final String _id = "jts.8103";

    @Override
    public String id() { return "jts.8103"; }
    private long sim;
    private List<ParamItem> paramItems;

    public long getSim() {
        return sim;
    }

    public void setSim(long sim) {
        this.sim = sim;
    }

    public List<ParamItem> getParamItems() {
        return paramItems;
    }

    public void setParamItems(List<ParamItem> paramItems) {
        this.paramItems = paramItems;
    }

    @Override
    public String toString() {
        return "Req_8103{" +
                "sim=" + sim +
                ", paramItems=" + paramItems +
                '}';
    }
}
