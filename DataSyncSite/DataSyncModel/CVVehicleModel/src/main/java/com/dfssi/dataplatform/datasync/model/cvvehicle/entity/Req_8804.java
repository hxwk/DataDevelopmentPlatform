package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-06 14:11
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 录音开始命令
 */
public class Req_8804 extends JtsReqMsg {

    public static final String _id = "jts.8804";

    @Override
    public String id() { return "jts.8804"; }

    private Byte recordOrder; //录音命令
    private Integer recordTime; //录音时间
    private Byte saveFlag; //保存标志
    private Byte audioSample; //音频采样率

    public Byte getRecordOrder() {
        return recordOrder;
    }

    public void setRecordOrder(Byte recordOrder) {
        this.recordOrder = recordOrder;
    }

    public Integer getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Integer recordTime) {
        this.recordTime = recordTime;
    }

    public Byte getSaveFlag() {
        return saveFlag;
    }

    public void setSaveFlag(Byte saveFlag) {
        this.saveFlag = saveFlag;
    }

    public Byte getAudioSample() {
        return audioSample;
    }

    public void setAudioSample(Byte audioSample) {
        this.audioSample = audioSample;
    }

    @Override
    public String toString() {
        return "Req_8804{" + super.toString() +
                ", recordOrder=" + recordOrder +
                ", recordTime=" + recordTime +
                ", saveFlag=" + saveFlag +
                ", audioSample=" + audioSample +
                '}';
    }
}
