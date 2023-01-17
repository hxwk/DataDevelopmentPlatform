package com.yaxon.vn.nd.tas.net.codec;

import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Author: 程行荣
 * Time: 2014-01-23 17:37
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
@ChannelHandler.Sharable
public class UdpProtoLogger extends MessageToMessageCodec<DatagramPacket, DatagramPacket> {

    private static final Logger protoLogger = LoggerFactory.getLogger("protolog");
    private static BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);

    @Override
    protected void encode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> list) throws Exception {
          logProto(ctx, false, datagramPacket);
          datagramPacket.retain();
          list.add(datagramPacket);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> list) throws Exception {
        logProto(ctx, true, datagramPacket);
        datagramPacket.retain();
        list.add(datagramPacket);
    }

    private void logProto(ChannelHandlerContext ctx, boolean up, DatagramPacket datagramPacket) {
        ByteBuf protoBuf = datagramPacket.content();
        if (protoLogger.isTraceEnabled()) {
            protoBuf.markReaderIndex();
            int count = protoBuf.readableBytes();
            byte[] out = new byte[count];
            protoBuf.readBytes(out);
            protoBuf.resetReaderIndex();

            protoLogger.trace("[{}{}] udp:{} {}",
                    new LocalDateTime().toString("yy-MM-dd HH:mm:ss"),
                    up ? datagramPacket.sender().getAddress() : datagramPacket.recipient().getAddress(),
                    up ? "->" : "<-",
                    hex.encode(out));
        }
    }


}
