package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 10:30
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 多媒体数据上传
 */
public class Req_0801 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.0801";
    }

    private Integer mediaDataId; //多媒体数据ID
    private Byte mediaType; //多媒体类型
    private Byte mediaFormatCode; //多媒体格式编码
    private Byte incidentCode; //事件项编码
    private Byte channelId; //通道ID

    private GpsVo gps;

    //多媒体数据包


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

    public GpsVo getGps() {
        return gps;
    }

    public void setGps(GpsVo gps) {
        this.gps = gps;
    }

    @Override
    public String toString() {
        return "Req_0801{" + super.toString() +
                ", mediaDataId=" + mediaDataId +
                ", mediaType=" + mediaType +
                ", mediaFormatCode=" + mediaFormatCode +
                ", incidentCode=" + incidentCode +
                ", channelId=" + channelId +
                ", gps=" + gps +
                '}';
    }
}
