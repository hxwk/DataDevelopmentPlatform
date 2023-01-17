package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-05 17:37
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 查询指定终端参数
 */
public class Req_8106 extends JtsReqMsg {
    @Override
    public String id() {return "jts.8106";}
    private String id;

    private long sim;

    private String timestamp;

    private int[] paramIds; //参数ID列表

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSim() {
        return sim;
    }

    public void setSim(long sim) {
        this.sim = sim;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }



    public int[] getParamIds() {
        return paramIds;
    }

    public void setParamIds(int[] paramIds) {
        this.paramIds = paramIds;
    }

    @Override
    public String toString() {
        return "Req_8106{" + super.toString() +
                ",sim="+sim+",timestamp="+timestamp+",paramIds=" + Arrays.toString(paramIds) +
                '}';
    }
}
