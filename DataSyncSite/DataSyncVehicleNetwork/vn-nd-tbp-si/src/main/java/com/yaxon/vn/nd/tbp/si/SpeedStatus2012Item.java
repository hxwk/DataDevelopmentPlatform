package com.yaxon.vn.nd.tbp.si;

/**
 * Author: Sun Zhen
 * Time: 2014-01-02 14:08
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 单位速度状态日志
 */

public class SpeedStatus2012Item implements Serializable {

    private long vid; //车辆id
    private String lpn;//车牌号
    private Byte status; //速度状态 1:正常;2:异常
    private Date startTime;//开始时间
    private Date endTime;//结束时间
    private Float recordSpeed;//开始时间对应的记录速度
    private Float referSpeed ;//开始时间对应的参考速度
    private List<SpeedStatus>  speedStatus; //记录、参考速度
    private Integer speednumber;//速度个数

    public Float getReferSpeed() {
        return referSpeed;
    }

    public void setReferSpeed(Float referSpeed) {
        this.referSpeed = referSpeed;
    }

    public Float getRecordSpeed() {
        return recordSpeed;
    }

    public void setRecordSpeed(Float recordSpeed) {
        this.recordSpeed = recordSpeed;
    }

    public Integer getSpeednumber() {
        return speednumber;
    }

    public void setSpeednumber(Integer speednumber) {
        this.speednumber = speednumber;
    }

    public long getVid() {
        return vid;
    }

    public void setVid(long vid) {
        this.vid = vid;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<SpeedStatus> getSpeedStatus() {
        return speedStatus;
    }

    public void setSpeedStatus(List<SpeedStatus> speedStatus) {
        this.speedStatus = speedStatus;
    }

    @Override
    public String toString() {
        return "SpeedStatus2012Item{" +
                "vid=" + vid +
                ", lpn='" + lpn + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", recordSpeed=" + recordSpeed +
                ", referSpeed=" + referSpeed +
                ", speedStatus=" + speedStatus +
                ", speednumber=" + speednumber +
                '}';
    }
}

