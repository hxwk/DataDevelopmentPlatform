package com.dfssi.dataplatform.datasync.service.util.msgutil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by cxq on 2017/11/17.
 */
public class RpcDecoder<T> extends ByteToMessageDecoder {
    private  Class<T> clazz;

    public RpcDecoder(Class<T> clazz) {
        this.clazz = clazz;
    }

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
        T obj = SerializationUtil.deserialize(data,clazz);




        /*
        channelHandlerContext.pipeline().addLast("second",new RpcDecoder<T>((Class<T>) RpcCommand.class));
        if(byteBuf.isReadable()){
            list.add(obj);
            list.add(byteBuf.readBytes(super.actualReadableBytes()));
        }else{
            list.add(obj);
        }

        channelHandlerContext.pipeline().remove(this);*/


/*
        // Decode the first message
        Object firstMessage = ...;

        // Add the second decoder
        ctx.pipeline().addLast("second", new SecondDecoder());

        if (buf.isReadable()) {
            // Hand off the remaining data to the second decoder
            out.add(firstMessage);
            out.add(buf.readBytes(super.actualReadableBytes()));
        } else {
            // Nothing to hand off
            out.add(firstMessage);
        }
      // Remove the first decoder (me)
         ctx.pipeline().remove(this);*/



        list.add(obj);
    }
}
