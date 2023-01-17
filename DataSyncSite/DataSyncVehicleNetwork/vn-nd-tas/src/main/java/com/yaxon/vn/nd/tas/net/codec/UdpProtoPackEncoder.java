package com.yaxon.vn.nd.tas.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Author: 程行荣
 * Time: 2013-07-29 10:49
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class UdpProtoPackEncoder extends MessageToMessageEncoder<DatagramPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        //        try {
        DatagramPacket packet;
        ByteBuf buf = msg.content();
        ByteBuf msgData = Unpooled.buffer();
        int len = buf.readableBytes();
        if (len == 0) {
            msgData.writeBytes(buf);
            return;
        }
        byte crc = 0;  //CRC校验和
        msgData.writeByte(0x7e);
        for (int i = 0; i < len + 1; i++) {
            byte b = 0;
            if (i < len) {
                b = buf.readByte();
                crc ^= b;
            } else {
                b = crc;
            }

            switch (b) {
                case 0x7d:
                    msgData.writeByte(0x7d);
                    msgData.writeByte(0x01);
                    break;
                case 0x7e:
                    msgData.writeByte(0x7d);
                    msgData.writeByte(0x02);
                    break;
                default:
                    msgData.writeByte(b);
                    break;
            }
        }

        msgData.writeByte(0x7e);
        packet = new DatagramPacket(msgData, msg.recipient());
        out.add(packet);
//        } finally {
//            msg.release();
//        }
    }

}
