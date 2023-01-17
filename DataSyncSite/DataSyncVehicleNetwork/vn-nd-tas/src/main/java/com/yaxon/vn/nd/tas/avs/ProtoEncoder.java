package com.yaxon.vn.nd.tas.avs;

import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tas.util.ProtoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Author: 程行荣
 * Time: 2013-07-29 10:49
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class ProtoEncoder extends MessageToByteEncoder<ProtoMsg> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ProtoMsg msg, ByteBuf out) throws Exception {
        int dataLen = msg.dataBuf.readableBytes();

        ByteBuf tmpBuf = Unpooled.buffer();  //存放消息头和消息体数据
        
        tmpBuf.writeShort(msg.msgId);
        short prop = (short) (dataLen & 0x03FF);
        if (msg.packCount != 0) {
            prop = (short) (prop | 0x2000);
        }
        tmpBuf.writeShort(prop);
        byte[] bcd = ProtoUtil.phone2Bcd(Long.valueOf(msg.sim));
        tmpBuf.writeBytes(bcd);
        tmpBuf.writeShort(msg.sn);
        if (msg.packCount != 0) {
            tmpBuf.writeShort(msg.packCount);
            tmpBuf.writeShort(msg.packIndex);
        }
        tmpBuf.writeBytes(msg.dataBuf);

        byte crc = 0;  //CRC校验和
        int len = tmpBuf.readableBytes();
        out.writeByte(0x7e);
        for (int i = 0; i < len + 1; i++) {
            byte b = 0;
            if (i < len) {
                b = tmpBuf.readByte();
                crc ^= b;
            } else {
                b = crc;
            }

            switch (b) {
                case 0x7d:
                    out.writeByte(0x7d);
                    out.writeByte(0x01);
                    break;
                case 0x7e:
                    out.writeByte(0x7d);
                    out.writeByte(0x02);
                    break;
                default:
                    out.writeByte(b);
                    break;
            }
        }
        out.writeByte(0x7e);

        tmpBuf.release();
    }

}
