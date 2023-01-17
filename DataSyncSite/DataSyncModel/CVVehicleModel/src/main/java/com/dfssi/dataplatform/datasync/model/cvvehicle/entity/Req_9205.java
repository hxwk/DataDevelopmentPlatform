package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 平台向终端请求资源列表
 */
public class Req_9205 extends JtsReqMsg {

    private String sim; //sim卡下发的时候根据这个来路由到车辆

    private byte logicalChannelNum; //逻辑通道号
    private String startTime; //开始时间 byte[6]
    private String endTime; //结束时间 byte[6]
    private Long alarmSign; //报警标志 传递给终端的类型为64bits 应用那边用16进制的字符串来表示 如0FFF0FFF0FFF0FFF 来表示查询资源的条件
    private byte audioVideoResourceType; //音视频资源类型
    private byte bitStreamType; //码流类型
    private byte storageType; //存储器类型
    @Override
    public String id() { return "jts.9205"; }

    @Override
    public String toString() {
        return "Res_9205{Sim:"+sim
                +",logicalChannelNum:"+logicalChannelNum
                +",startTime:"+startTime
                +",endTime:"+endTime
                +",alarmSign:"+alarmSign
                +",audioVideoResourceType:"+audioVideoResourceType
                +",bitStreamType:"+bitStreamType+",storageType:"+storageType+"}";
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public byte getLogicalChannelNum() {
        return logicalChannelNum;
    }

    public void setLogicalChannelNum(byte logicalChannelNum) {
        this.logicalChannelNum = logicalChannelNum;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getAlarmSign() {
        return alarmSign;
    }

    public void setAlarmSign(Long alarmSign) {
        this.alarmSign = alarmSign;
    }

    public byte getAudioVideoResourceType() {
        return audioVideoResourceType;
    }

    public void setAudioVideoResourceType(byte audioVideoResourceType) {
        this.audioVideoResourceType = audioVideoResourceType;
    }

    public byte getBitStreamType() {
        return bitStreamType;
    }

    public void setBitStreamType(byte bitStreamType) {
        this.bitStreamType = bitStreamType;
    }

    public byte getStorageType() {
        return storageType;
    }

    public void setStorageType(byte storageType) {
        this.storageType = storageType;
    }

}
