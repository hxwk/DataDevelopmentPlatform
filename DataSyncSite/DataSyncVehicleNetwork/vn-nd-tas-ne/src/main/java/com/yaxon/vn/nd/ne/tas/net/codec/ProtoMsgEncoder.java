package com.yaxon.vn.nd.ne.tas.net.codec;

import com.google.common.collect.Lists;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoPackHeader;
import com.yaxon.vn.nd.ne.tas.util.ProtoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Author: 程行荣
 * Time: 2014-02-25 15:50
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
@ChannelHandler.Sharable
public class ProtoMsgEncoder extends MessageToMessageEncoder<ProtoMsg> {
    private int maxBytesPerPack = 1000;


    public ProtoMsgEncoder(int maxBytesPerPack) {
        this.maxBytesPerPack = maxBytesPerPack;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtoMsg msg, List<Object> packs) throws Exception {
        //TODO: 特殊处理:终端向中心请求分包重传，分包通用应答等

        try {
            List<ByteBuf> ps = convertMsgToPacks(msg);
            packs.addAll(ps);
        } finally {
            msg.release();
        }
    }

    private List<ByteBuf> convertMsgToPacks(ProtoMsg msg) {

        List<ByteBuf> packs = Lists.newLinkedList();
        int dataLen = msg.dataBuf.readableBytes();
        ByteBuf buf = null;
        try {
            ProtoPackHeader packHeader = new ProtoPackHeader();
            packHeader.msgId = msg.msgId;
            packHeader.cryptFlag = 0;//暂不考虑处理加密问题
            packHeader.dataLen = (short)dataLen;
            packHeader.vin = msg.vin;
            packHeader.commandSign = msg.commandSign;
            packHeader.answerSign = msg.answerSign;

            buf = Unpooled.buffer(ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT + dataLen);
            writePackHeader(packHeader, buf);
            buf.writeBytes(msg.dataBuf);

            packs.add(buf);
        } catch (Exception e) {
            throw new RuntimeException("封装协议消息异常", e);
        }
        return packs;
    }

    private void writePackHeader(ProtoPackHeader packHeader, ByteBuf outBuf) {
        outBuf.writeByte(0x23);
        outBuf.writeByte(0x23);
        outBuf.writeByte(packHeader.commandSign);
        outBuf.writeByte(packHeader.answerSign);
        outBuf.writeByte(packHeader.cryptFlag);
        outBuf.writeBytes(packHeader.vin.getBytes(ProtoUtil.CHARSET_GBK));
    }

}

