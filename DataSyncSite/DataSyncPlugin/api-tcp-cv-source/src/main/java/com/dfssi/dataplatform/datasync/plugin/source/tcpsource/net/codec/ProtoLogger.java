package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.codec;

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

@ChannelHandler.Sharable
public class ProtoLogger extends MessageToMessageCodec<ByteBuf, ByteBuf> {
    private static final Logger protoLogger = LoggerFactory.getLogger("protolog");
    private static BaseEncoding hex = BaseEncoding.base16().withSeparator(" ", 2);

    private static Logger logger = LoggerFactory.getLogger(ProtoLogger.class);
//    KafkaDataTrans kafka = new KafkaDataTrans();
//    String kafkaMsg;

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
//        if (protoLogger.isTraceEnabled()) {
            protoBuf.markReaderIndex();
            int count = protoBuf.readableBytes();
            byte[] out = new byte[count];
            protoBuf.readBytes(out);
            protoBuf.resetReaderIndex();
            InetSocketAddress isa = (InetSocketAddress) ctx.channel().remoteAddress();
            //kafkaMsg = hex.encode(out);
//            logger.debug("获得字节数组为：" + Arrays.toString(out));
//            logger.debug("将字节数组编码得到：" + hex.encode(out));
//            kafkaMsg = ByteBufUtil.hexDump(out).toUpperCase();
            protoLogger.warn("[{}/{}:{}] tcp:{} {}",
                    new LocalDateTime().toString("yy-MM-dd HH:mm:ss"),
                    isa.getAddress().getHostAddress(),
                    isa.getPort(),
                    up ? "->" : "<-",
                    hex.encode(out));
//            if(true==up){
//                kafka.sendMessageToKafka(kafkaMsg);
//            }
//        }
    }
}
