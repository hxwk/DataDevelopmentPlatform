package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import java.io.Serializable;

/**
 * Author: <孙震>
 * Time: 2013-11-02 18:00
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 多边形顶点项
 */

public class PeakParamItem implements Serializable {
    private int lat; //顶点纬度
    private int lon; //顶点经度

    public PeakParamItem() {

    }

    public PeakParamItem(int lat, int lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public int getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "PeakParamItem{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
