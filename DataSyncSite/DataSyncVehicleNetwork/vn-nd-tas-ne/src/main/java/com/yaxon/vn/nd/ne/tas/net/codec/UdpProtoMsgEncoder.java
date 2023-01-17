package com.yaxon.vn.nd.ne.tas.net.codec;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoPackHeader;
import com.yaxon.vn.nd.ne.tas.util.ProtoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Author: 程行荣
 * Time: 2014-02-25 15:50
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
@ChannelHandler.Sharable
public class UdpProtoMsgEncoder extends MessageToMessageEncoder<ProtoMsg> {
    private int maxBytesPerPack = 1000;


    public UdpProtoMsgEncoder(int maxBytesPerPack) {
        this.maxBytesPerPack = maxBytesPerPack;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtoMsg msg, List<Object> packs) throws Exception {
        //TODO: 特殊处理:终端向中心请求分包重传，分包通用应答等

        try {
            List<DatagramPacket> ps = convertMsgToPacks(msg);
            packs.addAll(ps);
        } finally {
            msg.release();
        }
    }

    private List<DatagramPacket> convertMsgToPacks(ProtoMsg msg) {
        List<DatagramPacket> packs = Lists.newLinkedList();
        int dataLen = msg.dataBuf.readableBytes();
        DatagramPacket packet = null;
        ByteBuf buf;
        try {
            if (msg.msgType == 1) { //透传消息
                if (dataLen >= 1024) {
                    throw new CorruptedFrameException("透传消息长度>=1024");
                }

                ProtoPackHeader packHeader = new ProtoPackHeader();
                packHeader.msgId = msg.msgId;
                packHeader.cryptFlag = 0;//暂不考虑处理加密问题

                packHeader.sim = msg.sim;
                packHeader.sn = msg.sn;
                if (msg.packCount > 0) {
                    packHeader.splitFlag = 1;
                    packHeader.packCount = msg.packCount;
                    packHeader.packIndex = msg.packIndex;
                }
                packHeader.dataLen = (short)dataLen;

                buf = Unpooled.buffer(ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT + dataLen);
                writePackHeader(packHeader, buf);
                buf.writeBytes(msg.dataBuf);
                packet = new DatagramPacket(buf,msg.sender);
                packs.add(packet);
            } else if (dataLen <= maxBytesPerPack) {
                ProtoPackHeader packHeader = new ProtoPackHeader();
                packHeader.msgId = msg.msgId;
                packHeader.cryptFlag = 0;//暂不考虑处理加密问题
                packHeader.splitFlag = 0;
                packHeader.dataLen = (short)dataLen;
                packHeader.sim = msg.sim;
                packHeader.sn = msg.sn;

                buf = Unpooled.buffer(ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT + dataLen);
                writePackHeader(packHeader, buf);
                buf.writeBytes(msg.dataBuf);
                packet = new DatagramPacket(buf,msg.sender);
                packs.add(packet);
            } else if (dataLen > maxBytesPerPack) { //分包处理
                int packSize = 0;
                int packCount = (dataLen + maxBytesPerPack - 1) / maxBytesPerPack;
                int sn = (msg.sn & 0xFFFF);
                int idx = 0;
                while (dataLen > 0) {
                    packSize = Ints.min(dataLen, maxBytesPerPack);

                    ProtoPackHeader packHeader = new ProtoPackHeader();
                    packHeader.msgId = msg.msgId;
                    packHeader.cryptFlag = 0;//暂不考虑处理加密问题
                    packHeader.splitFlag = 1;
                    packHeader.dataLen = (short) packSize;
                    packHeader.sim = msg.sim;
                    packHeader.sn = (short) (sn & 0xFFFF);
                    packHeader.packCount = packCount;
                    packHeader.packIndex = ++idx;

                    buf = Unpooled.buffer(ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT + 4 + packSize);
                    writePackHeader(packHeader, buf);
                    buf.writeBytes(msg.dataBuf, packSize);
                    packet = new DatagramPacket(buf,msg.sender);
                    packs.add(packet);

                    dataLen -= packSize;
                    sn++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("封装协议消息异常", e);
        }
        return packs;
    }

    private void writePackHeader(ProtoPackHeader packHeader, ByteBuf outBuf) {
        outBuf.writeShort(packHeader.msgId);
        short prop = (short) (packHeader.dataLen & 0x03FF);
        prop = (short) (prop | ((packHeader.cryptFlag & 0x07) << 10));
        if (packHeader.splitFlag == 1) {
            prop = (short) (prop | 0x2000);
        }
        outBuf.writeShort(prop);
        byte[] bcd = ProtoUtil.phone2Bcd(Long.valueOf(packHeader.sim));
        outBuf.writeBytes(bcd);
        outBuf.writeShort(packHeader.sn);
        if (packHeader.splitFlag == 1) {
            outBuf.writeShort(packHeader.packCount);
            outBuf.writeShort(packHeader.packIndex);
        }
    }

}
