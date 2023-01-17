package com.dfssi.dataplatform.plugin.tcpnesource.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtoPackEncoder extends MessageToByteEncoder<ByteBuf> {

    private static Logger logger = LoggerFactory.getLogger(ProtoPackEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int len = msg.readableBytes();
        if (len == 0) {
            out.writeBytes(msg);
            return;
        }
        byte crc = 0;  //CRC校验和
        for (int i = 0; i < len + 1; i++) {
            byte b = 0;
            if (i < len) {
                b = msg.readByte();
                crc ^= b;
            } else {
                b = crc;
            }

            out.writeByte(b);
        }

        logger.debug(" ProtoMsgEncoder 添加校验码完毕");
    }
}
