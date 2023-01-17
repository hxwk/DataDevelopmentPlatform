package com.dfssi.dataplatform.plugin.tcpnesource.net.codec;

import com.dfssi.dataplatform.datasync.common.ne.ProtoMsg;
import com.dfssi.dataplatform.plugin.tcpnesource.net.proto.ProtoPackHeader;
import com.dfssi.dataplatform.plugin.tcpnesource.util.ProtoUtil;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ChannelHandler.Sharable
public class ProtoMsgEncoder extends MessageToMessageEncoder<ProtoMsg> {
    private static Logger logger = LoggerFactory.getLogger(ProtoMsgEncoder.class);

    private int maxBytesPerPack = 1000;


    public ProtoMsgEncoder(int maxBytesPerPack) {
        this.maxBytesPerPack = maxBytesPerPack;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtoMsg msg, List<Object> packs) throws Exception {
//        logger.info("wx 开始明文转换为十六进制");
        try {
            List<ByteBuf> ps = convertMsgToPacks(msg);
            packs.addAll(ps);
        } finally {
            msg.release();
        }
        logger.debug(" ProtoMsgEncoder 明文转换十六进制完毕");
    }

    private List<ByteBuf> convertMsgToPacks(ProtoMsg msg) {
        logger.debug("wx msg:{}",msg);
        List<ByteBuf> packs = Lists.newLinkedList();
        int dataLen = msg.dataBuf.readableBytes();
        ByteBuf buf = null;
        try {
            ProtoPackHeader packHeader = new ProtoPackHeader();
            packHeader.msgId = msg.msgId;
            packHeader.cryptFlag = 0x01;//暂不考虑处理加密问题
            packHeader.dataLen = (short)dataLen;
            packHeader.vin = msg.vin;
            packHeader.commandSign = msg.commandSign;
            packHeader.answerSign = msg.answerSign;

            buf = Unpooled.buffer(ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT + dataLen);
            writePackHeader(packHeader, buf);
            buf.writeBytes(msg.dataBuf);

            packs.add(buf);
//            logger.info("wx packs:{}",packs);
        } catch (Exception e) {
            throw new RuntimeException("封装协议消息异常", e);
        }
        return packs;
    }

    private void writePackHeader(ProtoPackHeader packHeader, ByteBuf outBuf) {
        outBuf.writeByte(0x23);
        outBuf.writeByte(0x23);
        outBuf.writeByte(packHeader.commandSign);
        outBuf.writeByte(packHeader.answerSign);
        outBuf.writeBytes(packHeader.vin.getBytes(ProtoUtil.CHARSET_GBK));
        outBuf.writeByte(packHeader.cryptFlag);
    }

}
