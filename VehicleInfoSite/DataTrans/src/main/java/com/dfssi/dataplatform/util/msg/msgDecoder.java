package com.dfssi.dataplatform.util.msg;

import com.dfssi.dataplatform.util.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class msgDecoder extends ByteToMessageDecoder {
    private static Logger logger = LoggerFactory.getLogger(msgDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        logger.info("Tcp Recv：" + ByteBufUtil.buf2Str(byteBuf));
    }
}
