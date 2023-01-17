package com.dfssi.dataplatform.datasync.plugin.interceptor.bean;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 * 消息结构
 * @author jianKang
 * @date 2017/12/14
 */
public class ProtoMsg implements Serializable{
    /**
     * 消息ID
     */
    public String msgId;
    /**
     * 终端手机号
     */
    public long sim;
    /**
     * 车辆ID
     */
    public long vid = 0;
    /**
     * 流水号
     */
    public short sn;
    /**
     * 0,完整消息（不分包或分包合并）;>0,分包，分包数
     */
    public int packCount = 0;
    /**
     * 分包索引号，从1开始计算
     */
    public int packIndex = 0;

    //public int dataLen; //数据长度

    /**
     * 消息类型：0,普通消息；1，透传消息
     */
    public byte msgType = 0;

    /**
     * 数据包缓存
     */
    public byte[] dataBuf;

    public InetSocketAddress sender;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public byte[] getDataBuf() {
        return dataBuf;
    }

    public void setDataBuf(byte[] dataBuf) {
        this.dataBuf = dataBuf;
    }

    public long getSim() {
        return sim;
    }

    public void setSim(long sim) {
        this.sim = sim;
    }

    @Override
    public String toString() {
        /*return "msg[" +
                "ID=" + msgId +
                ", sim=" + sim +
                ", VehicleID=" + vid +
                ", flowID=" + (sn&0xFFFF) +
                ", msgType=" + msgType +
                ", packCount=" + packCount +
                ", packIndex=" + packIndex +
                ", sender=" + sender +
                ']';*/
        return JSON.toJSONString(this);
    }
}
