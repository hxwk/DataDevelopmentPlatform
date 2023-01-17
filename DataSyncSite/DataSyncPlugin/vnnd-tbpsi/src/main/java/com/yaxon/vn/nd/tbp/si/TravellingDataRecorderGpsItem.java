package com.yaxon.vn.nd.tbp.si;

/**
 * Author: Sun Zhen
 * Time: 2013-12-30 11:53
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.io.Serializable;

/**
 * 行驶记录仪位置数据项
 */

public class TravellingDataRecorderGpsItem implements Serializable {
    private Integer lon;
    private Integer lat;
    private Short height; //海拔

    public TravellingDataRecorderGpsItem() {

    }

    public TravellingDataRecorderGpsItem(Integer lon, Integer lat, Short height) {
        this.lon = lon;
        this.lat = lat;
        this.height = height;
    }

    public Integer getLon() {
        return lon;
    }

    public void setLon(Integer lon) {
        this.lon = lon;
    }

    public Integer getLat() {
        return lat;
    }

    public void setLat(Integer lat) {
        this.lat = lat;
    }

    public Short getHeight() {
        return height;
    }

    public void setHeight(Short height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "TravellingDataRecorderGpsItem{" +
                "lon=" + lon +
                ", lat=" + lat +
                ", height=" + height +
                '}';
    }
}
