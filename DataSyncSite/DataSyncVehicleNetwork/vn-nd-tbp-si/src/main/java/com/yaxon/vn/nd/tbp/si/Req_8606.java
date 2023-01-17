package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-04 14:13
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.List;

/**
 * 设置路线
 */
public class Req_8606 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8606";
    }

    private Long routeId; //路线ID
    private Short routeAttr; //路线属性
    private String beginTime; //起始时间
    private String endTime; //结束时间

    private List<InflexionParamItem> inflexionParamItems; //拐点项列表

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public Short getRouteAttr() {
        return routeAttr;
    }

    public void setRouteAttr(Short routeAttr) {
        this.routeAttr = routeAttr;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<InflexionParamItem> getInflexionParamItems() {
        return inflexionParamItems;
    }

    public void setInflexionParamItems(List<InflexionParamItem> inflexionParamItems) {
        this.inflexionParamItems = inflexionParamItems;
    }

    @Override
    public String toString() {
        return "Req_8606{" + super.toString() +
                ", routeId=" + routeId +
                ", routeAttr=" + routeAttr +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", inflexionParamItems=" + inflexionParamItems +
                '}';
    }
}
