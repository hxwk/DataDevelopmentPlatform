package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-02 17:37
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */


import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.List;

/**
 * 删除圆形区域
 */
public class Req_8601 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8601"; }

    private List<Long> regionIds;

    public List<Long> getRegionIds() {
        return regionIds;
    }

    public void setRegionIds(List<Long> regionIds) {
        this.regionIds = regionIds;
    }

    @Override
    public String toString() {
        return "Req_8601{" + super.toString() +
                "， regionIds=" + regionIds +
                '}';
    }
}

