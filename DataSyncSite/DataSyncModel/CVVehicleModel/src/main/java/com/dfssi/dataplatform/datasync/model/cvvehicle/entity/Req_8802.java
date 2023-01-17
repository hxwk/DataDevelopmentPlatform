package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-06 11:38
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.Date;

/**
 * 存储多媒体数据检索
 */
public class Req_8802 extends JtsReqMsg {

    public static final String _id = "jts.8802";

    @Override
    public String id() {
        return "jts.8802";
    }

    private Byte mediaType; //多媒体类型
    private Byte incidentCode; //事件项编码
    private Byte channelId; //通道ID
    private Date beginTime; //起始时间
    private Date endTime; //结束时间

    public Byte getMediaType() {
        return mediaType;
    }

    public void setMediaType(Byte mediaType) {
        this.mediaType = mediaType;
    }

    public Byte getIncidentCode() {
        return incidentCode;
    }

    public void setIncidentCode(Byte incidentCode) {
        this.incidentCode = incidentCode;
    }

    public Byte getChannelId() {
        return channelId;
    }

    public void setChannelId(Byte channelId) {
        this.channelId = channelId;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Req_8802{" + super.toString() +
                ", mediaType=" + mediaType +
                ", incidentCode=" + incidentCode +
                ", channelId=" + channelId +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                '}';
    }
}

