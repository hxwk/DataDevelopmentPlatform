package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 17:49
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.List;

/**
 * 删除矩形区域
 */
public class Req_8603 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8603"; }

    private List<Long> regionIds;

    public List<Long> getRegionIds() {
        return regionIds;
    }

    public void setRegionIds(List<Long> regionIds) {
        this.regionIds = regionIds;
    }

    @Override
    public String toString() {
        return "Req_8603{" + super.toString() +
                ", regionIds=" + regionIds +
                '}';
    }
}
