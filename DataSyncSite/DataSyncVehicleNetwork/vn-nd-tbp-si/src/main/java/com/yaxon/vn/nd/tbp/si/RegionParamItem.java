package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 17:24
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.io.Serializable;
import java.util.List;

/**
 * 区域项
 */

public class RegionParamItem implements Serializable {
    private long areaId; //区域ID
    private short areaAttr; //区域属性
    private int lat; //中心点纬度
    private int lon; //中心点经度
    private int rad; //半径
    private String beginTime; //起始时间
    private String endTime; //结束时间
    private short speed; //最高速度
    private short time; //超速持续时间

    private int leftUpLat; //左上点纬度
    private int leftUpLon; //左上点经度
    private int rightDownLat; //右下点纬度
    private int rightDownLon; //右下点经度
    private byte alarmType; // 报警类型

    public byte getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(byte alarmType) {
        this.alarmType = alarmType;
    }

    private List<PeakParamItem> peakParamItems; //顶点项

    public RegionParamItem() {
    }

    public RegionParamItem(long areaId, short areaAttr, int lat, int lon, int rad,
                           String beginTime, String endTime, short speed, byte time,
                           int leftUpLat, int leftUpLon, int rightDownLat, int rightDownLon,
                           List<PeakParamItem> peakParamItems) {
        this.areaId = areaId;
        this.areaAttr = areaAttr;
        this.lat = lat;
        this.lon = lon;
        this.rad = rad;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.speed = speed;
        this.time = time;
        this.leftUpLat = leftUpLat;
        this.leftUpLon = leftUpLon;
        this.rightDownLat = rightDownLat;
        this.rightDownLon = rightDownLon;
        this.peakParamItems = peakParamItems;
    }

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

    public short getAreaAttr() {
        return areaAttr;
    }

    public void setAreaAttr(short areaAttr) {
        this.areaAttr = areaAttr;
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

    public int getRad() {
        return rad;
    }

    public void setRad(int rad) {
        this.rad = rad;
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

    public short getSpeed() {
        return speed;
    }

    public void setSpeed(short speed) {
        this.speed = speed;
    }

    public short getTime() {
        return time;
    }

    public void setTime(short time) {
        this.time = time;
    }

    public int getLeftUpLat() {
        return leftUpLat;
    }

    public void setLeftUpLat(int leftUpLat) {
        this.leftUpLat = leftUpLat;
    }

    public int getLeftUpLon() {
        return leftUpLon;
    }

    public void setLeftUpLon(int leftUpLon) {
        this.leftUpLon = leftUpLon;
    }

    public int getRightDownLat() {
        return rightDownLat;
    }

    public void setRightDownLat(int rightDownLat) {
        this.rightDownLat = rightDownLat;
    }

    public int getRightDownLon() {
        return rightDownLon;
    }

    public void setRightDownLon(int rightDownLon) {
        this.rightDownLon = rightDownLon;
    }

    public List<PeakParamItem> getPeakParamItems() {
        return peakParamItems;
    }

    public void setPeakParamItems(List<PeakParamItem> peakParamItems) {
        this.peakParamItems = peakParamItems;
    }

    @Override
    public String toString() {
        return "RegionParamItem{" +
                "areaId=" + areaId +
                ", areaAttr=" + areaAttr +
                ", lat=" + lat +
                ", lon=" + lon +
                ", rad=" + rad +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", speed=" + speed +
                ", time=" + time +
                ", leftUpLat=" + leftUpLat +
                ", leftUpLon=" + leftUpLon +
                ", rightDownLat=" + rightDownLat +
                ", rightDownLon=" + rightDownLon +
                ", peakParamItems=" + peakParamItems +
                '}';
    }
}
