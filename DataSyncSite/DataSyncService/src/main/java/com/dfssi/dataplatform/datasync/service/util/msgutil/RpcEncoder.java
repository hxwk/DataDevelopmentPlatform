package com.dfssi.dataplatform.datasync.service.util.msgutil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by cxq on 2017/11/17.
 */
public class RpcEncoder extends MessageToByteEncoder {
    private  Class<?> clazz;
    public RpcEncoder(Class<?> rpcRequstClass) {
        this.clazz = rpcRequstClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (clazz.isInstance(o)){
            byte[] data = SerializationUtil.serialize(o);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }
}
