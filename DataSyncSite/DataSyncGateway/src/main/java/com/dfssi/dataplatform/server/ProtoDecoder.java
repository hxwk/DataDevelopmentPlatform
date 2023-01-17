package com.dfssi.dataplatform.server;

import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProtoDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);

    protected void handlerRemoved0(ChannelHandlerContext ctx)
            throws Exception {
    }

    protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {
        logProto(ctx, true, in);
        in.retain();
        out.add(in);
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
