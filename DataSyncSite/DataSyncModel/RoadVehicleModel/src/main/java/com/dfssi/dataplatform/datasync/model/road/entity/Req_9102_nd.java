package com.dfssi.dataplatform.datasync.model.road.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * @author JianKang
 * 实时音视频传输控制指令
 */
public class Req_9102_nd extends JtsReqMsg {

    public static final String _id = "jts.9102.nd";

    @Override
    public String id() {
        return "jts.9102.nd";
    }

    private String sim;

    private byte channelNo;//channel号

    private byte controlCommand;//控制指令

    private byte shutDownAVType;//关闭音视频类型

    private byte streamType;//码流类型

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public byte getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(byte channelNo) {
        this.channelNo = channelNo;
    }

    public byte getControlCommand() {
        return controlCommand;
    }

    public void setControlCommand(byte controlCommand) {
        this.controlCommand = controlCommand;
    }

    public byte getShutDownAVType() {
        return shutDownAVType;
    }

    public void setShutDownAVType(byte shutDownAVType) {
        this.shutDownAVType = shutDownAVType;
    }

    public byte getStreamType() {
        return streamType;
    }

    public void setStreamType(byte streamType) {
        this.streamType = streamType;
    }

    @Override
    public String toString() {
        return "Req_9102_nd{" +
                "sim='" + sim + '\'' +
                ", channelNo=" + channelNo +
                ", controlCommand=" + controlCommand +
                ", shutDownAVType=" + shutDownAVType +
                ", streamType=" + streamType +
                '}';
    }
}
