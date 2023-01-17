package com.dfssi.dataplatform.datasync.model.road.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

public class Req_9201 extends JtsReqMsg {
    private String sim; //sim卡下发的时候根据这个来路由到车辆

    private byte serverIPAddressLength; //服务器IP地址长度 20
    private String serverIPAddress; //服务器IP地址
    private short serverAudioVideoMonitorPortTCP; //服务器音视频通道监听端口号TCP
    private short serverAudioVideoMonitorPortUDP; //服务器音视频通道监听端口号UDP
    private byte logicalChannelNum; //逻辑通道号
    private byte audioVideoResourceType; //音视频类型
    private byte bitStreamType; //码流类型
    private byte storageType; //存储器类型
    private byte refluxMode; //回流方式
    private byte fastForwardOrBackMultiple; //快进或快退倍数
    private String startTime; //开始时间 byte[6]
    private String endTime; //结束时间 byte[6]

    @Override
    public String id() { return "jts.9201"; }

    @Override
    public String toString() {

        return "Res_9202{sim:"+sim+"，serverIPAddressLength:"+serverIPAddressLength+"，serverIPAddress:"+serverIPAddress
                +"，serverAudioVideoMonitorPortTCP:"+serverAudioVideoMonitorPortTCP
                +"，serverAudioVideoMonitorPortUDP:"+serverAudioVideoMonitorPortUDP
                +"，audioVideoResourceType:"+audioVideoResourceType
                +",logicalChannelNum:"+logicalChannelNum
                +"，bitStreamType:"+bitStreamType
                +"，storageType:"+storageType
                +"，refluxMode:"+refluxMode
                +"，fastForwardOrBackMultiple:"+fastForwardOrBackMultiple
                +"，startTime:"+startTime
                +"，endTime:"+endTime+"}";
    }


    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public byte getServerIPAddressLength() {
        return serverIPAddressLength;
    }

    public void setServerIPAddressLength(byte serverIPAddressLength) {
        this.serverIPAddressLength = serverIPAddressLength;
    }

    public String getServerIPAddress() {
        return serverIPAddress;
    }

    public void setServerIPAddress(String serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
    }

    public short getServerAudioVideoMonitorPortTCP() {
        return serverAudioVideoMonitorPortTCP;
    }

    public void setServerAudioVideoMonitorPortTCP(short serverAudioVideoMonitorPortTCP) {
        this.serverAudioVideoMonitorPortTCP = serverAudioVideoMonitorPortTCP;
    }

    public short getServerAudioVideoMonitorPortUDP() {
        return serverAudioVideoMonitorPortUDP;
    }

    public void setServerAudioVideoMonitorPortUDP(short serverAudioVideoMonitorPortUDP) {
        this.serverAudioVideoMonitorPortUDP = serverAudioVideoMonitorPortUDP;
    }

    public byte getLogicalChannelNum() {
        return logicalChannelNum;
    }

    public void setLogicalChannelNum(byte logicalChannelNum) {
        this.logicalChannelNum = logicalChannelNum;
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

    public byte getRefluxMode() {
        return refluxMode;
    }

    public void setRefluxMode(byte refluxMode) {
        this.refluxMode = refluxMode;
    }

    public byte getFastForwardOrBackMultiple() {
        return fastForwardOrBackMultiple;
    }

    public void setFastForwardOrBackMultiple(byte fastForwardOrBackMultiple) {
        this.fastForwardOrBackMultiple  = fastForwardOrBackMultiple;
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
}
