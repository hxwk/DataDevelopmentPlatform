package com.dfssi.dataplatform.datasync.model.road.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-05 17:37
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.Arrays;

/**
 * 查询指定终端参数
 */
public class Req_8106 extends JtsReqMsg {
    @Override
    public String id() {return "jts.8106";}

    private long sim;

    private String timestamp;

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

    private int[] paramIds; //参数ID列表

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
