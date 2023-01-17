package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.codec;

import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto.ProtoPackHeader;
import com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.util.ProtoUtil;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ChannelHandler.Sharable
public class ProtoMsgEncoder extends MessageToMessageEncoder<ProtoMsg> {
    private static final Logger logger = LoggerFactory.getLogger(ProtoMsgEncoder.class);
    private int maxBytesPerPack = 1000;


    public ProtoMsgEncoder(int maxBytesPerPack) {
        this.maxBytesPerPack = maxBytesPerPack;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtoMsg msg, List<Object> packs) throws Exception {
        //TODO: 特殊处理:终端向中心请求分包重传，分包通用应答等
        logger.trace("编码第一步：ProtoMsgEncoder的encode将发送出去的对象编码成字节流");
        try {
            List<ByteBuf> ps = convertMsgToPacks(msg);
            packs.addAll(ps);
        } finally {
            msg.release();
        }
    }

    private List<ByteBuf> convertMsgToPacks(ProtoMsg msg) {

        List<ByteBuf> packs = Lists.newLinkedList();
        int dataLen = msg.dataBuf.readableBytes();
        ByteBuf buf = null;
        try {
            if (msg.msgType == 1) { //透传消息
                if (dataLen >= 1024) {
                    throw new CorruptedFrameException("透传消息长度>=1024");
                }

                ProtoPackHeader packHeader = new ProtoPackHeader();
                packHeader.msgId = msg.msgId;
                packHeader.cryptFlag = 0;//暂不考虑处理加密问题

                packHeader.sim = msg.sim;
                packHeader.sn = msg.sn;
                if (msg.packCount > 0) {
                    packHeader.splitFlag = 1;
                    packHeader.packCount = msg.packCount;
                    packHeader.packIndex = msg.packIndex;
                }
                packHeader.dataLen = (short)dataLen;

                buf = Unpooled.buffer(ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT + dataLen);
                writePackHeader(packHeader, buf);
                buf.writeBytes(msg.dataBuf);

                packs.add(buf);
            } else if (dataLen <= maxBytesPerPack) {
                ProtoPackHeader packHeader = new ProtoPackHeader();
                packHeader.msgId = msg.msgId;
                packHeader.cryptFlag = 0;//暂不考虑处理加密问题
                packHeader.splitFlag = 0;
                packHeader.dataLen = (short)dataLen;
                packHeader.sim = msg.sim;
                packHeader.sn = msg.sn;

                buf = Unpooled.buffer(ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT + dataLen);
                writePackHeader(packHeader, buf);
                buf.writeBytes(msg.dataBuf);

                packs.add(buf);
            } else if (dataLen > maxBytesPerPack) { //分包处理
                int packSize = 0;
                int packCount = (dataLen + maxBytesPerPack - 1) / maxBytesPerPack;
                int sn = (msg.sn & 0xFFFF);
                int idx = 0;
                while (dataLen > 0) {
                    packSize = Ints.min(dataLen, maxBytesPerPack);

                    ProtoPackHeader packHeader = new ProtoPackHeader();
                    packHeader.msgId = msg.msgId;
                    packHeader.cryptFlag = 0;//暂不考虑处理加密问题
                    packHeader.splitFlag = 1;
                    packHeader.dataLen = (short) packSize;
                    packHeader.sim = msg.sim;
                    packHeader.sn = (short) (sn & 0xFFFF);
                    packHeader.packCount = packCount;
                    packHeader.packIndex = ++idx;

                    buf = Unpooled.buffer(ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT + 4 + packSize);
                    writePackHeader(packHeader, buf);
                    buf.writeBytes(msg.dataBuf, packSize);

                    packs.add(buf);

                    dataLen -= packSize;
                    sn++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("封装协议消息异常", e);
        }
        return packs;
    }

    private void writePackHeader(ProtoPackHeader packHeader, ByteBuf outBuf) {
        outBuf.writeShort(packHeader.msgId);
        short prop = (short) (packHeader.dataLen & 0x03FF);
        prop = (short) (prop | ((packHeader.cryptFlag & 0x07) << 10));
        if (packHeader.splitFlag == 1) {
            prop = (short) (prop | 0x2000);
        }
        outBuf.writeShort(prop);
        byte[] bcd = ProtoUtil.phone2Bcd(Long.valueOf(packHeader.sim));
        outBuf.writeBytes(bcd);
        outBuf.writeShort(packHeader.sn);
        if (packHeader.splitFlag == 1) {
            outBuf.writeShort(packHeader.packCount);
            outBuf.writeShort(packHeader.packIndex);
        }
    }

}
