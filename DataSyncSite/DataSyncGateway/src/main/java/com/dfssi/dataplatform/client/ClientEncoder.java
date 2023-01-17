package com.dfssi.dataplatform.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientEncoder extends MessageToByteEncoder<ByteBuf> {
    private static Logger logger = LoggerFactory.getLogger(ClientEncoder.class);

    private class InvalidTest {
    }

    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out)
            throws Exception {
        int length = msg.readableBytes();
        for (int i = 0; i < length; i++) {
            out.writeByte(msg.readByte());
        }

    }
}
