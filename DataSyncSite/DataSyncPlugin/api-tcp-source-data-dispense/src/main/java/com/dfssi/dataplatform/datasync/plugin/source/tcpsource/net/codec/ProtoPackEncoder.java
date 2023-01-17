package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.codec;

import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler.CanInformationPH;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtoPackEncoder extends MessageToByteEncoder<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(ProtoPackEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
//        try {
        int len = msg.readableBytes();
        if (len == 0) {
            out.writeBytes(msg);
            return;
        }
        byte crc = 0;  //CRC校验和
        out.writeByte(0x7e);
        for (int i = 0; i < len + 1; i++) {
            byte b = 0;
            if (i < len) {
                b = msg.readByte();
                crc ^= b;
            } else {
                b = crc;
            }

            switch (b) {
                case 0x7d:
                    out.writeByte(0x7d);
                    out.writeByte(0x01);
                    break;
                case 0x7e:
                    out.writeByte(0x7d);
                    out.writeByte(0x02);
                    break;
                default:
                    out.writeByte(b);
                    break;
            }
        }

        out.writeByte(0x7e);
        //logger.info("client向车机终端发送的报文协议:{}",ByteBufUtil.toHexString(out.array()));
//        } finally {
//            msg.release();
//        }
    }
}
