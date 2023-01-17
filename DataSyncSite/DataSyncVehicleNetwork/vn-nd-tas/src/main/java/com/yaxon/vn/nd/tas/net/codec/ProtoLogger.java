package com.yaxon.vn.nd.tas.net.codec;

import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Author: 程行荣
 * Time: 2014-01-23 17:37
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
@ChannelHandler.Sharable
public class ProtoLogger extends MessageToMessageCodec<ByteBuf, ByteBuf> {
    private static final Logger protoLogger = LoggerFactory.getLogger("protolog");
    private static BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        logProto(ctx, false, msg);
        msg.retain();
        out.add(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        logProto(ctx, true, msg);
        msg.retain();
        out.add(msg);
    }

    private void logProto(ChannelHandlerContext ctx, boolean up, ByteBuf protoBuf) {
        if (protoLogger.isTraceEnabled()) {
            protoBuf.markReaderIndex();
            int count = protoBuf.readableBytes();
            byte[] out = new byte[count];
            protoBuf.readBytes(out);
            protoBuf.resetReaderIndex();
            InetSocketAddress isa = (InetSocketAddress) ctx.channel().remoteAddress();
            protoLogger.trace("[{}/{}:{}] tcp:{} {}",
                    new LocalDateTime().toString("yy-MM-dd HH:mm:ss"),
                    isa.getAddress().getHostAddress(),
                    isa.getPort(),
                    up ? "->" : "<-",
                    hex.encode(out));
        }
    }
}
