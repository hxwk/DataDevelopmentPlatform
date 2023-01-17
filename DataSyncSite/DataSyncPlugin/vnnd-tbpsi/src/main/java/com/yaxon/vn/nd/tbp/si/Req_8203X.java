package com.yaxon.vn.nd.tbp.si;

/**
 * Author: Sun Zhen
 * Time: 2013-12-31 16:14
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Date;

/**
 * 人工确认报警消息——扩展请求消息
 */
public class Req_8203X extends Req_8203 {
    @Override
    public String id() {return "jts.8203X";}

    private Long gpsId;
    private Date gpsTime; //报警时间
    private Integer lon; //经度
    private Integer lat; //纬度
    private Short speed; //速度
    private Short dir; //方向
    private String remark; //备注
    private Long optUid; //操作员id

    public Long getGpsId() {
        return gpsId;
    }

    public void setGpsId(Long gpsId) {
        this.gpsId = gpsId;
    }

    public Date getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(Date gpsTime) {
        this.gpsTime = gpsTime;
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

    public Short getSpeed() {
        return speed;
    }

    public void setSpeed(Short speed) {
        this.speed = speed;
    }

    public Short getDir() {
        return dir;
    }

    public void setDir(Short dir) {
        this.dir = dir;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getOptUid() {
        return optUid;
    }

    public void setOptUid(Long optUid) {
        this.optUid = optUid;
    }

    @Override
    public String toString() {
        return "Req_8203X{" + super.toString() +
                ", gpsId=" + gpsId +
                ", gpsTime=" + gpsTime +
                ", lon=" + lon +
                ", lat=" + lat +
                ", speed=" + speed +
                ", dir=" + dir +
                ", remark='" + remark + '\'' +
                ", optUid=" + optUid +
                '}';
    }
}
