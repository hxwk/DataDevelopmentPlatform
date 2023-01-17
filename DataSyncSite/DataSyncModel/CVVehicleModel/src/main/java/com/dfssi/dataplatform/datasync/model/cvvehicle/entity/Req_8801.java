package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-06 11:04
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 摄像头立即拍摄命令
 */
public class Req_8801 extends JtsReqMsg {
    private Byte channelId; //通道ID
    private Integer shotOrder; //拍摄命令
    private Integer shotSpace; //拍摄间隔/录像时间
    private Byte saveFlag; //保存标志
    private Byte resolution; //分辨率
    private Byte videoQuality; //视频、图片质量
    private Short brightness; //亮度
    private Byte contrast; //对比度
    private Byte saturation; //饱和度
    private Short chroma; //色度
    private String comeFrom;//来自平台 多种上级平台或中心系统

    public static final String _id = "jts.8801";

    @Override
    public String id() {
        return "jts.8801";
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    public Byte getChannelId() {
        return channelId;
    }

    public void setChannelId(Byte channelId) {
        this.channelId = channelId;
    }

    public Integer getShotOrder() {
        return shotOrder;
    }

    public void setShotOrder(Integer shotOrder) {
        this.shotOrder = shotOrder;
    }

    public Integer getShotSpace() {
        return shotSpace;
    }

    public void setShotSpace(Integer shotSpace) {
        this.shotSpace = shotSpace;
    }

    public Byte getSaveFlag() {
        return saveFlag;
    }

    public void setSaveFlag(Byte saveFlag) {
        this.saveFlag = saveFlag;
    }

    public Byte getResolution() {
        return resolution;
    }

    public void setResolution(Byte resolution) {
        this.resolution = resolution;
    }

    public Byte getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(Byte videoQuality) {
        this.videoQuality = videoQuality;
    }

    public Short getBrightness() {
        return brightness;
    }

    public void setBrightness(Short brightness) {
        this.brightness = brightness;
    }

    public Byte getContrast() {
        return contrast;
    }

    public void setContrast(Byte contrast) {
        this.contrast = contrast;
    }

    public Byte getSaturation() {
        return saturation;
    }

    public void setSaturation(Byte saturation) {
        this.saturation = saturation;
    }

    public Short getChroma() {
        return chroma;
    }

    public void setChroma(Short chroma) {
        this.chroma = chroma;
    }

    @Override
    public String toString() {
        return "Req_8801{" + super.toString() +
                ", channelId=" + channelId +
                ", shotOrder=" + shotOrder +
                ", shotSpace=" + shotSpace +
                ", saveFlag=" + saveFlag +
                ", resolution=" + resolution +
                ", videoQuality=" + videoQuality +
                ", brightness=" + brightness +
                ", contrast=" + contrast +
                ", saturation=" + saturation +
                ", chroma=" + chroma +
                ", comeFrom='" + comeFrom + '\'' +
                '}';
    }
}
