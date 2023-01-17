package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-04 17:33
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 多媒体事件信息上传
 */
public class Req_0800 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.0800";
    }

    private Integer mediaDataId; //多媒体数据ID
    private Byte mediaType; //多媒体类型
    private Byte mediaFormatCode; //多媒体格式编码
    private Byte incidentCode; //事件项编码
    private Byte channelId; //通道ID

    public Integer getMediaDataId() {
        return mediaDataId;
    }

    public void setMediaDataId(Integer mediaDataId) {
        this.mediaDataId = mediaDataId;
    }

    public Byte getMediaType() {
        return mediaType;
    }

    public void setMediaType(Byte mediaType) {
        this.mediaType = mediaType;
    }

    public Byte getMediaFormatCode() {
        return mediaFormatCode;
    }

    public void setMediaFormatCode(Byte mediaFormatCode) {
        this.mediaFormatCode = mediaFormatCode;
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

    @Override
    public String toString() {
        return "Req_0800{" + super.toString() +
                ", mediaDataId=" + mediaDataId +
                ", mediaType=" + mediaType +
                ", mediaFormatCode=" + mediaFormatCode +
                ", incidentCode=" + incidentCode +
                ", channelId=" + channelId +
                '}';
    }
}

