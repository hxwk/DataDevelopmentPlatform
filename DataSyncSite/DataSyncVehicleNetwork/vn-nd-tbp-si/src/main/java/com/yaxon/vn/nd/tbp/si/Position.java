package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;

/**
 * Author: 杨俊辉
 * Time: 2014-09-03 18:44
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class Position implements Serializable {

    private Float lon;  //经度
    private Float lat;  //维度
    private Integer height; //海拔

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Position{" +
                "lon=" + lon +
                ", lat=" + lat +
                ", height=" + height +
                '}';
    }
}
