package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.codec;

import com.google.common.io.BaseEncoding;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

@ChannelHandler.Sharable
public class ProtoLogger extends MessageToMessageCodec<ByteBuf, ByteBuf> {
    private static final Logger protoLogger = LoggerFactory.getLogger("protolog");
    private static BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);

    private static Logger logger = LoggerFactory.getLogger(ProtoLogger.class);


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        logger.trace("解码第一步：ProtoLogger的decode对进来的字节流进行译码成对象");
        logProto(ctx, true, msg);
        msg.retain();
        out.add(msg);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        logger.trace("编码第三步:ProtoLogger的encode将发送出去的对象编码成字节流;");
        logProto(ctx, false, msg);
        msg.retain();
        out.add(msg);
    }

    private void logProto(ChannelHandlerContext ctx, boolean up, ByteBuf protoBuf) {
//        if (protoLogger.isTraceEnabled()) {
            protoBuf.markReaderIndex();
            int count = protoBuf.readableBytes();
            byte[] out = new byte[count];
            protoBuf.readBytes(out);
            protoBuf.resetReaderIndex();
            InetSocketAddress isa = (InetSocketAddress) ctx.channel().remoteAddress();
            //kafkaMsg = hex.encode(out);
            //logger.debug("获得字节数组为：" + Arrays.toString(out));
            //logger.debug("将字节数组编码得到：" + hex.encode(out));
            protoLogger.warn("[{}:{}] tcp   {} {}",
                    isa.getAddress().getHostAddress(),
                    isa.getPort(),
                    up ? "recv:" : "send:",
                    hex.encode(out));
//        }
    }
}
