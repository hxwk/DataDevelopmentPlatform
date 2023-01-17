package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-04 14:31
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.io.Serializable;

/**
 * 路线拐点项
 */

public class InflexionParamItem implements Serializable {
    private Long inflexionId; //拐点ID
    private Long sectionId; //路段ID
    private Integer lat; //拐点纬度
    private Integer lon; //拐点经度
    private Short sectionWidth; //路段宽度
    private Byte sectionAttr; //路段属性
    private Short sectionOverspeedLastTime; //路段超速持续时间
    private Integer sectionTooLongLimen; //路段行驶过长阈值
    private Integer sectionLackLimen; //路段行驶不足阈值
    private Integer sectionHighestSpeed; //路段最高速度

    public InflexionParamItem() {

    }

    public InflexionParamItem(Long inflexionId, Long sectionId, Integer lat, Integer lon, Short sectionWidth,
                              Byte sectionAttr, Short sectionOverspeedLastTime, Integer sectionTooLongLimen,
                              Integer sectionLackLimen, Integer sectionHighestSpeed) {
        this.inflexionId = inflexionId;
        this.sectionId = sectionId;
        this.lat = lat;
        this.lon = lon;
        this.sectionWidth = sectionWidth;
        this.sectionAttr = sectionAttr;
        this.sectionOverspeedLastTime = sectionOverspeedLastTime;
        this.sectionTooLongLimen = sectionTooLongLimen;
        this.sectionLackLimen = sectionLackLimen;
        this.sectionHighestSpeed = sectionHighestSpeed;
    }

    public Long getInflexionId() {
        return inflexionId;
    }

    public void setInflexionId(Long inflexionId) {
        this.inflexionId = inflexionId;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Integer getLat() {
        return lat;
    }

    public void setLat(Integer lat) {
        this.lat = lat;
    }

    public Integer getLon() {
        return lon;
    }

    public void setLon(Integer lon) {
        this.lon = lon;
    }

    public Short getSectionWidth() {
        return sectionWidth;
    }

    public void setSectionWidth(Short sectionWidth) {
        this.sectionWidth = sectionWidth;
    }

    public Byte getSectionAttr() {
        return sectionAttr;
    }

    public void setSectionAttr(Byte sectionAttr) {
        this.sectionAttr = sectionAttr;
    }

    public Short getSectionOverspeedLastTime() {
        return sectionOverspeedLastTime;
    }

    public void setSectionOverspeedLastTime(Short sectionOverspeedLastTime) {
        this.sectionOverspeedLastTime = sectionOverspeedLastTime;
    }

    public Integer getSectionTooLongLimen() {
        return sectionTooLongLimen;
    }

    public void setSectionTooLongLimen(Integer sectionTooLongLimen) {
        this.sectionTooLongLimen = sectionTooLongLimen;
    }

    public Integer getSectionLackLimen() {
        return sectionLackLimen;
    }

    public void setSectionLackLimen(Integer sectionLackLimen) {
        this.sectionLackLimen = sectionLackLimen;
    }

    public Integer getSectionHighestSpeed() {
        return sectionHighestSpeed;
    }

    public void setSectionHighestSpeed(Integer sectionHighestSpeed) {
        this.sectionHighestSpeed = sectionHighestSpeed;
    }

    @Override
    public String toString() {
        return "InflexionParamItem{" +
                "inflexionId=" + inflexionId +
                ", sectionId=" + sectionId +
                ", lat=" + lat +
                ", lon=" + lon +
                ", sectionWidth=" + sectionWidth +
                ", sectionAttr=" + sectionAttr +
                ", sectionOverspeedLastTime=" + sectionOverspeedLastTime +
                ", sectionTooLongLimen=" + sectionTooLongLimen +
                ", sectionLackLimen=" + sectionLackLimen +
                ", sectionHighestSpeed=" + sectionHighestSpeed +
                '}';
    }
}
