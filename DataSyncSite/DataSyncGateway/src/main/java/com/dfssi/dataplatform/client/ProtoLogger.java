package com.dfssi.dataplatform.client;

import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

@ChannelHandler.Sharable
public class ProtoLogger extends MessageToMessageCodec<ByteBuf, ByteBuf> {
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
        protoBuf.markReaderIndex();
        int count = protoBuf.readableBytes();
        byte[] out = new byte[count];
        protoBuf.readBytes(out);
        protoBuf.resetReaderIndex();
        if(up){
            System.out.println("client->:"+hex.encode(out));
        }else{
            System.out.println("client<-:"+hex.encode(out));
        }
    }
}
