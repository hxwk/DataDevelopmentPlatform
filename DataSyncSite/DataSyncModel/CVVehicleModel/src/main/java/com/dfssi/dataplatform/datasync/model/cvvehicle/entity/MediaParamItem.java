package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-06 11:48
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.io.Serializable;

/**
 * 多媒体检索项
 */

public class MediaParamItem implements Serializable {
    private Integer mediaId; //多媒体ID
    private Byte mediaType; //多媒体类型
    private Byte incidentCode; //事件项编码
    private Byte channelId; //通道ID

    private GpsVo gps;

    public MediaParamItem() {
    }

    public MediaParamItem(Integer mediaId, Byte mediaType, Byte incidentCode, Byte channelId, GpsVo gps) {
        this.mediaId = mediaId;
        this.mediaType = mediaType;
        this.incidentCode = incidentCode;
        this.channelId = channelId;
        this.gps = gps;
    }

    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }

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

    public GpsVo getGps() {
        return gps;
    }

    public void setGps(GpsVo gps) {
        this.gps = gps;
    }

    @Override
    public String toString() {
        return "MediaParamItem{" +
                "mediaId=" + mediaId +
                ", mediaType=" + mediaType +
                ", incidentCode=" + incidentCode +
                ", channelId=" + channelId +
                ", gps=" + gps +
                '}';
    }
}
