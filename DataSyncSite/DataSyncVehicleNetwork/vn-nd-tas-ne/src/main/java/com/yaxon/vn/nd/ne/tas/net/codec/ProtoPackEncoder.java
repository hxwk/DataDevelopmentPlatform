package com.yaxon.vn.nd.ne.tas.net.codec;

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
        int len = msg.readableBytes();
        if (len == 0) {
            out.writeBytes(msg);
            return;
        }
        byte crc = 0;  //CRC校验和
        for (int i = 0; i < len + 1; i++) {
            byte b = 0;
            if (i < len) {
                b = msg.readByte();
                crc ^= b;
            } else {
                b = crc;
            }

            out.writeByte(b);
        }
    }
}
