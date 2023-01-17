package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-05 17:37
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.Arrays;

/**
 * 查询SD卡目录
 */
public class Req_E101 extends JtsReqMsg {
    @Override
    public String id() {return "jts.E101";}

    private long sim;

    public long getSim() {
        return sim;
    }

    public void setSim(long sim) {
        this.sim = sim;
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
        return "Req_E101{" + super.toString() +
                ",sim="+sim+" ,paramIds=" + Arrays.toString(paramIds) +
                '}';
    }
}
