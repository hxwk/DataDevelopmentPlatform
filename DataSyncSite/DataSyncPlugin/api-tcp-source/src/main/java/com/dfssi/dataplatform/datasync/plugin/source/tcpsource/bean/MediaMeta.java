package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.bean;


import com.dfssi.dataplatform.datasync.model.cvvehicle.entity.GpsVo;

/**
 * Author: 程行荣
 * Time: 2014-02-21 16:41
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class MediaMeta {
    public static final byte MT_IMAGE = 0;
    public static final byte MT_AUDIO = 1;
    public static final byte MT_VIDIO = 2;

    public static final byte MF_JPEG = 0;
    public static final byte MF_TIF = 1;
    public static final byte MF_MP3 = 2;
    public static final byte MF_WAV = 3;
    public static final byte MF_WMV = 4;


    private String vid;
    private String sim;
    private Integer mediaId;
    private Byte mediaType;
    private Byte mediaFormat;
    private Byte eventCode;
    private Byte ChannelId;
    private GpsVo gps;
    private String fileKey;
    private String fileKeyReal;//保存转码后的

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
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

    public Byte getMediaFormat() {
        return mediaFormat;
    }

    public void setMediaFormat(Byte mediaFormat) {
        this.mediaFormat = mediaFormat;
    }

    public Byte getEventCode() {
        return eventCode;
    }

    public void setEventCode(Byte eventCode) {
        this.eventCode = eventCode;
    }

    public Byte getChannelId() {
        return ChannelId;
    }

    public void setChannelId(Byte channelId) {
        ChannelId = channelId;
    }

    public GpsVo getGps() {
        return gps;
    }

    public void setGps(GpsVo gps) {
        this.gps = gps;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileKeyReal() {
        return fileKeyReal;
    }

    public void setFileKeyReal(String fileKeyReal) {
        this.fileKeyReal = fileKeyReal;
    }

    @Override
    public String toString() {
        return "MediaMeta{" +
                "vid=" + vid +
                ", sim=" + sim +
                ", mediaId=" + mediaId +
                ", mediaType=" + mediaType +
                ", mediaFormat=" + mediaFormat +
                ", eventCode=" + eventCode +
                ", ChannelId=" + ChannelId +
                ", gps=" + gps.toString() +
                ", fileKey='" + fileKey + '\'' +
                ", fileKeyReal='" + fileKeyReal + '\'' +
                '}';
    }
}
