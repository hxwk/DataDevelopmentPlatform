package com.dfssi.dataplatform.util.msg;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class msgDecoder extends ByteToMessageDecoder {
    private static Logger logger = LoggerFactory.getLogger(msgDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes()<4){
            return;
        }
//        byteBuf.isReadable();

//        int offset = 0;
//        byteBuf.getUnsignedByte(offset);
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if(dataLength<0){
            channelHandlerContext.close();
        }
        if(byteBuf.readableBytes()<dataLength){
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        String str = new String(data);
        logger.info("获得对象："+byteBuf.toString()+",并将其转成Str:"+str);
        list.add(str);
    }
}
