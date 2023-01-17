package com.yaxon.vn.nd.tas.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Author: 程行荣
 * Time: 2013-07-29 10:49
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class ProtoPackEncoder extends MessageToByteEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
//        try {
        int len = msg.readableBytes();
        if (len == 0) {
            out.writeBytes(msg);
            return;
        }
        byte crc = 0;  //CRC校验和
        out.writeByte(0x7e);
        for (int i = 0; i < len + 1; i++) {
            byte b = 0;
            if (i < len) {
                b = msg.readByte();
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
//        } finally {
//            msg.release();
//        }
    }
}
