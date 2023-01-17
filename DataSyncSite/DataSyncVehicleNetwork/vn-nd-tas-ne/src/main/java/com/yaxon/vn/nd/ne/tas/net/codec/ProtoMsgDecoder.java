package com.yaxon.vn.nd.ne.tas.net.codec;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yaxon.vn.nd.ne.tas.exception.BadFormattedProtocolException;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoPackHeader;
import com.yaxon.vn.nd.ne.tas.net.proto.SplitPackItem;
import com.yaxon.vn.nd.ne.tas.net.proto.SplitPackKey;
import com.yaxon.vn.nd.ne.tas.net.tcp.TcpChannel;
import com.yaxon.vn.nd.ne.tas.util.ProtoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: 程行荣
 * Time: 2014-02-25 15:45
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
@ChannelHandler.Sharable
public class ProtoMsgDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(ProtoMsgDecoder.class);

    private TcpChannel tcpChannel;
    private ConcurrentMap<SplitPackKey, SplitPackItem> packCache = Maps.newConcurrentMap();
    private int receiveTimeoutMillisPerPack = 20000; //接收每包数据的超时毫秒数


    public ProtoMsgDecoder(TcpChannel tcpChannel) {
        this.tcpChannel = tcpChannel;
        this.receiveTimeoutMillisPerPack = tcpChannel.getConfig().getReceiveTimeoutMillisPerPack();

        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    private Thread cleanupThread = new Thread("pack-cache-cleanup-thread") {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3000);

                    for (SplitPackKey spk : packCache.keySet()) {
                        SplitPackItem spi = packCache.get(spk);
                        if (spi != null && System.currentTimeMillis() > spi.timeout) {
                            if (spi.resend > 0) {
                                spi = packCache.remove(spk);
                                if (logger.isDebugEnabled()) {
                                    logger.debug("[{}]接收多分包消息超时，清除已接收的数据:msgId={},sn={}", spk.sim, spk.msgId, spk.sn);
                                }
                            } else { //分包补传
                                List<Integer> missingPacks = Lists.newArrayList();
                                for (int i = 1; i <= spi.packCount; i++) {
                                    if (spi.packs.get(i) == null) {
                                        missingPacks.add(i);
                                    }
                                }
                                spi.timeout = System.currentTimeMillis() + missingPacks.size() * receiveTimeoutMillisPerPack;
                                spi.resend++;
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    break;
                } catch (Throwable t) {
                    logger.warn("", t);
                }
            }
        }
    };


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf packBuf, List<Object> msgs) throws Exception {
        try {
            ProtoPackHeader header = readProtoPackHeader(packBuf);

            packBuf.retain();
            msgs.add(buildProtoMsg(header, packBuf));
        } catch (Exception e) {
            logger.warn("协议分包与消息转换失败", e);
        } /*finally {
            packBuf.release(); //父类中会释放
        }*/
    }

    private ProtoMsg buildProtoMsg(ProtoPackHeader header, ByteBuf dataBuf) {
        ProtoMsg protoMsg = new ProtoMsg();
        protoMsg.msgId = header.msgId;
        protoMsg.vin = header.vin;
        protoMsg.commandSign = header.commandSign;
        protoMsg.answerSign = header.answerSign;
        protoMsg.dataBuf = dataBuf;
        return protoMsg;
    }

    private ProtoPackHeader readProtoPackHeader(ByteBuf pack) {
        if (pack.readableBytes() < ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT) {
            throw new BadFormattedProtocolException("协议数据包的长度小于包头长度(" + ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT + ")");
        }

        ProtoPackHeader packHeader = null;
        try {
            packHeader = new ProtoPackHeader();
            pack.readBytes(2);
            packHeader.msgId = 0x0707;
            packHeader.commandSign = pack.readByte();
            packHeader.answerSign = pack.readByte();
            packHeader.vin = ProtoUtil.readString(pack, 17);
            packHeader.cryptFlag = pack.readByte();
            packHeader.dataLen = pack.readShort();
        } catch (Exception e) {
            throw new BadFormattedProtocolException("解析协议数据包头失败", e);
        }

        //判断一下数据包完整性
        if (packHeader.dataLen != pack.readableBytes()) {
            throw new BadFormattedProtocolException("协议分包中的数据长度与消息体的字节数不一致: " + packHeader.dataLen
                    + "," + pack.readableBytes());
        }
        return packHeader;
    }

}
