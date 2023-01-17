package com.dfssi.dataplatform.datasync.model.road.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * 0704 附加报文，将每个gpsvo封装到一个实体类中
 * @author jianKang
 * @date 2018/01/22
 */
public class AddGpsVo implements Serializable {
    private String idx;
    private String msgId="0704";
    private byte positionDataType; //位置数据类型
    private String vid;
    private GpsVo gpsVo;
    private long receiveMsgTime; //消息接收时间

    public String getIdx() {
        return idx==null? UUID.randomUUID().toString():idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public GpsVo getGpsVo() {
        return gpsVo;
    }

    public void setGpsVo(GpsVo gpsVo) {
        this.gpsVo = gpsVo;
    }

    public byte getPositionDataType() {
        return positionDataType;
    }

    public void setPositionDataType(byte positionDataType) {
        this.positionDataType = positionDataType;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getReceiveMsgTime() {
        return receiveMsgTime;
    }

    public void setReceiveMsgTime(long receiveMsgTime) {
        this.receiveMsgTime = receiveMsgTime;
    }

    @Override
    public String toString() {
        return "补传GpsVo{" +
                "idx='" + idx + '\'' +
                ", msgId='" + msgId + '\'' +
                ", positionDataType=" + positionDataType +
                ", vid='" + vid + '\'' +
                ", gpsVo=" + gpsVo +
                ", receiveMsgTime=" + receiveMsgTime +
                '}';
    }
}
