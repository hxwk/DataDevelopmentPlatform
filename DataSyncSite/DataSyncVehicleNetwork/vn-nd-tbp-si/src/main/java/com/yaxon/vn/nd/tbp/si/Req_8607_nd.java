package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <赖贵明>
 * Time: 2015-05-21 14:48
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.List;

/**
 * 南斗版删除路线
 */
public class Req_8607_nd extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8607.nd";
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
