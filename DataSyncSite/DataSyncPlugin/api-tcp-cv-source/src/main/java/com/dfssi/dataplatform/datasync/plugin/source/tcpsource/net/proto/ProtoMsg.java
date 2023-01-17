package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto;

import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.CodecUtils;
import io.netty.buffer.ByteBuf;

import java.net.InetSocketAddress;


/**
 * 协议消息
 */
public class ProtoMsg {
    /* 消息ID */
    public short msgId;
    /* 终端手机号 */
    public String sim;
    /* 车辆ID */
    public String vid = "";
    /* 流水号 */
    public short sn;
    /* 0,完整消息（不分包或分包合并）;>0,分包，分包数 */
    public int packCount = 0;
    /* 分包索引号，从1开始计算 */
    public int packIndex = 0;
    //public int dataLen; //数据长度

    /* 消息类型：0,普通消息；1，透传消息 */
    public byte msgType = 0;

    /* 数据包缓存 */
    public ByteBuf dataBuf;

    public InetSocketAddress sender;

    public void release() {
        if (dataBuf != null && dataBuf.refCnt() > 0) {
            dataBuf.release();
        }
    }

    @Override
    public String toString() {
        return "ProtoMsg{" +
                "msgId=0x" + CodecUtils.shortToHex(msgId) +
                ", sim=" + sim +
                ", vid=" + vid +
                ", sn=" + (sn&0xFFFF) +
                ", msgType=" + msgType +
                ", packCount=" + packCount +
                ", packIndex=" + packIndex +
                ", sender=" + sender +
                '}';
    }
}
