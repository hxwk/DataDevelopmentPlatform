package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.codec;

import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.HandlersManger;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.config.TcpChannelConfig;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.BadFormattedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.*;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.tcp.TcpChannel;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.server.TCPSource;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.ProtoUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

@ChannelHandler.Sharable
public class ProtoMsgDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(ProtoMsgDecoder.class);

    private Set<String> skipPackMergeProtos;
    private ConcurrentMap<SplitPackKey, SplitPackItem> packCache = Maps.newConcurrentMap();
    private int receiveTimeoutMillisPerPack = 20000; //接收每包数据的超时毫秒数
    private TcpChannel tcpChannel;


    public ProtoMsgDecoder(TcpChannel tcpChannel) {
        this.skipPackMergeProtos = HandlersManger.SKIPPACKMERGEPROTOS;
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
                                resendMissingPack(missingPacks, String.valueOf(spk.sim), spk.msgId, spk.sn);
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

    private void resendMissingPack(List<Integer> missingPacks, String sim, short msgId, int sn) {
        if (missingPacks.size() <= 0) {
            return;
        }

        int i = 0;
        int count = Math.min(255, missingPacks.size());
        while (count > 0) {
            ProtoMsg msg = new ProtoMsg();
            msg.msgId = ProtoConstants.RESEND_PACK_REQ;
            msg.sim = sim;
            msg.dataBuf = Unpooled.buffer(3 + 2 * i);
            msg.dataBuf.writeShort(sn);
            msg.dataBuf.writeByte(count);
            int j = 0;
            while (j < count) {
                msg.dataBuf.writeShort(missingPacks.indexOf(i++));
                j++;
            }
            tcpChannel.sendMessage(msg);
            count = Math.min(255, missingPacks.size() - i);
        }
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf packBuf, List<Object> msgs) throws Exception {
        try {
            ProtoPackHeader header = readProtoPackHeader(packBuf);
            if (header.splitFlag == 1 && !skipPackMergeProtos.contains(header.msgId)) {
                ByteBuf bb = mergeProtoPack(ctx, header, packBuf);
                if (bb != null) {
                    header.packCount = 0;
                    header.packIndex = 0;
                    msgs.add(buildProtoMsg(header, bb));
                } else {
                    sendCenterGeneralRes(ctx.channel(), header.sim, header.msgId, header.sn, ProtoConstants.RC_OK);
                }

            } else {
                packBuf.retain();
                msgs.add(buildProtoMsg(header, packBuf));
            }
        } catch (Exception e) {
            logger.warn("协议分包与消息转换失败", e);
        } /*finally {
            packBuf.release(); //父类中会释放
        }*/
    }

    private ProtoMsg buildProtoMsg(ProtoPackHeader header, ByteBuf dataBuf) {
        ProtoMsg protoMsg = new ProtoMsg();
        protoMsg.msgId = header.msgId;
        protoMsg.sim = header.sim;
        protoMsg.vid = header.vid;
        protoMsg.packCount = header.packCount;
        protoMsg.packIndex = header.packIndex;
        protoMsg.sn = header.sn;
        protoMsg.dataBuf = dataBuf;
        return protoMsg;
    }

    private String getHost(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getHostName();
    }

    private ProtoPackHeader readProtoPackHeader(ByteBuf pack) {
        if (pack.readableBytes() < ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT) {
            throw new BadFormattedProtocolException("协议数据包的长度小于包头长度(" + ProtoPackHeader.PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT + ")");
        }

        ProtoPackHeader packHeader = null;
        try {
            packHeader = new ProtoPackHeader();
            packHeader.msgId = pack.readShort();
            short prop = pack.readShort();
            packHeader.dataLen = (short) (prop & 0x03FF);
            packHeader.cryptFlag = (byte) ((prop >> 10) & 0x07);
            packHeader.splitFlag = (byte) ((prop >> 13) & 0x01);
            byte[] bcd = new byte[6];
            pack.readBytes(bcd);
            packHeader.sim = String.valueOf(ProtoUtil.bcd2Phone(bcd));
            packHeader.sn = pack.readShort();
            if (packHeader.splitFlag == 1) {
                packHeader.packCount = pack.readUnsignedShort();
                packHeader.packIndex = pack.readUnsignedShort();
                if (packHeader.packCount <= 0 || packHeader.packIndex > packHeader.packCount || packHeader.packIndex <= 0) {
                    throw new BadFormattedProtocolException("分包索引号异常: packCount=" + packHeader.packCount
                            + ",index=" + packHeader.packIndex);
                }
            }
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

    /**
     * 拼包处理
     *
     * @param ctx
     * @param header
     * @param packBuf
     * @return
     */
    private ByteBuf mergeProtoPack(ChannelHandlerContext ctx, ProtoPackHeader header, ByteBuf packBuf) {
        //部标规定，同一个消息的分包数据的流水号必须是连续的
        int sn = (header.sn & 0xFFFF);
        if (header.packIndex != 1) {
            sn -= (header.packIndex - 1); //按照第一包的序列号作为整包的序列号
            sn = (sn & 0xFFFF); //大于65535时，从0开始计算
        }
        SplitPackKey spk = new SplitPackKey(header.sim, header.msgId, sn);
        SplitPackItem spi = packCache.get(spk);
        if (spi == null) {
            spi = new SplitPackItem(header.packCount, System.currentTimeMillis() + header.packCount * receiveTimeoutMillisPerPack);
            SplitPackItem v = packCache.putIfAbsent(spk, spi);
            if (v != null) {
                spi = v;
            }
        }

        byte[] data = new byte[packBuf.readableBytes()];
        packBuf.readBytes(data);

        byte[] b = spi.packs.putIfAbsent(header.packIndex, data);
        if (b != null) { //之前，已经接收到相同的数据包了
            logger.info("接收到重复的协议包: sim={},msgId={},sn={},idx={}",
                    header.sim, header.msgId, header.sn & 0xFFFF, header.packIndex);
        }

        ByteBuf msgBuf = null;

        //所有数据包都收到后的处理
        if (spi.packCount == spi.packs.size()) {
            try {
                byte[][] ds = new byte[spi.packCount][];
                for (int i = 1; i <= spi.packCount; i++) {
                    ds[i - 1] = spi.packs.get(i);
                }
                msgBuf = Unpooled.wrappedBuffer(ds.length, ds);
            } catch (Exception e) {
                throw new BadFormattedProtocolException("拼接分包数据时发生异常: sim=" + spk.sim + ",msgId=" + spk.msgId
                        + ",sn=" + (spk.sn & 0xFFFF), e);
            } finally { //释放分包数据
                packCache.remove(spk);
            }
        }
        return msgBuf;
    }

    private void sendCenterGeneralRes(Channel channel, final String sim,
                                      short reqMsgId, short reqSn, byte retCode) {
        ProtoMsg msg = new ProtoMsg();
        msg.sim = sim;
        msg.msgId = ProtoConstants.CENTER_GENERAL_RES;
        msg.dataBuf = Unpooled.buffer(5);
        msg.dataBuf.writeShort(reqSn);
        msg.dataBuf.writeShort(reqMsgId);
        msg.dataBuf.writeByte(retCode);

        channel.writeAndFlush(msg);
    }


}
