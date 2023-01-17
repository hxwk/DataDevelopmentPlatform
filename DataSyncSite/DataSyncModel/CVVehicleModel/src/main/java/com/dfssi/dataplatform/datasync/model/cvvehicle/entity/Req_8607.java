package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-04 14:48
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.List;

/**
 * 删除路线
 */
public class Req_8607 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8607";
    }

    private List<Long> routeIds;

    public List<Long> getRouteIds() {
        return routeIds;
    }

    public void setRouteIds(List<Long> routeIds) {
        this.routeIds = routeIds;
    }

    @Override
    public String toString() {
        return "Req_8607{" + super.toString() +
                ", routeIds=" + routeIds +
                '}';
    }
}
