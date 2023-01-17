package com.dfssi.dataplatform.server;

import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtoEncoder extends MessageToByteEncoder<ByteBuf> {
    private static Logger logger = LoggerFactory.getLogger(ProtoEncoder.class);
    private static BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);
    private class InvalidTest {
    }

    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out)
            throws Exception {
        logProto(ctx, false, msg);
        msg.retain();
        out.writeBytes(msg);

    }
    private void logProto(ChannelHandlerContext ctx, boolean up, ByteBuf protoBuf) {
        protoBuf.markReaderIndex();
        int count = protoBuf.readableBytes();
        byte[] out = new byte[count];
        protoBuf.readBytes(out);
        protoBuf.resetReaderIndex();
        if(up){
            System.out.println("master->:"+hex.encode(out));
        }else{
            System.out.println("master<-:"+hex.encode(out));
        }

    }
}
