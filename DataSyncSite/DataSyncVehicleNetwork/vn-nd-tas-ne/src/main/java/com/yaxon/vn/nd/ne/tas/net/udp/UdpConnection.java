package com.yaxon.vn.nd.ne.tas.net.udp;

import com.google.common.base.Throwables;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoConstants;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoMsg;
import com.yaxon.vndp.common.util.CodecUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.CodecException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author: 程行荣
 * Time: 2013-11-07 11:50
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * UDP服务处理器.
 * 1.忽略上行/下行重传机制.
 * 2.
 */
//TODO: 优化内存池处理
//TODO: 优化建议：1、可以利用MapDB来缓存分包数据以及下行数据缓存以便重传
public class UdpConnection extends SimpleChannelInboundHandler {
    private static final Logger logger = LoggerFactory.getLogger(UdpConnection.class);

    private UdpChannel udpChannel;
    private UdpConnectionManager connectionManager;
    private static AtomicLong idGen = new AtomicLong();
    protected AtomicLong lastCommTime = new AtomicLong();
    private int terminalMaxIdleTimeMillis = 90000; //终端最大的空闲时间

    protected long connId;
    public static final int CS_DISCONNECTED = 0;
    public static final int CS_CONNECTED = 1;
    public static final int CS_AUTHENCATED = 2;
    public static final int CS_CLOSING = 3;

    protected volatile int state = 0;

    private Channel channel;

   /* private long sim = 0;
    private long vid = 0;*/

    public UdpConnection() {
        this.connId = idGen.incrementAndGet();
    }

    public long id() {
        return this.connId;
    }

   /* public long sim() {
        return this.sim;
    }

    public long vid() {
        return this.vid;
    }*/

    public Channel getChannel() {
        return channel;
    }

    public int getState() {
        return state;
    }

    public long getLastCommTime() {
        return lastCommTime.get();
    }

    public UdpConnection(UdpChannel udpChannel) {
        this.connId = idGen.incrementAndGet();
        this.udpChannel = udpChannel;
        this.connectionManager = udpChannel.getConnectionManager();
        this.terminalMaxIdleTimeMillis = udpChannel.getConfig().getTerminalMaxIdleTimeMillis();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof CodecException) {
            logger.warn("[{}]协议编码异常:\n{}", ctx.channel().remoteAddress(), Throwables.getStackTraceAsString(cause));
        } else {
            logger.warn("[{}]网络异常, 将关闭连接: {}", ctx.channel().remoteAddress(), cause.getMessage());
            ctx.close();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /*public void close() {
        if (channel != null && channel.isActive()) {
            channel.close();
            state = CS_CLOSING;
        } else {
            connectionManager.removeConnection(this);
        }
    }*/

    /*private String remoteAddress() {
        return sim + "/" + remoteHost;
    }*/


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object protoMsg) throws Exception {
        this.channel = ctx.channel();
        connectionManager.setUdpConnection(this);
        ProtoMsg msg = (ProtoMsg) protoMsg;

        try {
            if (state != CS_AUTHENCATED
                    && msg.msgId != ProtoConstants.TERMINAL_REGISTER_REQ
                    && msg.msgId != ProtoConstants.TERMINAL_AUTH_REQ
                    && msg.msgId != ProtoConstants.TERMINAL_UNREGISTER_REQ
                    && msg.msgId != ProtoConstants.TERMINAL_HEART_BEAT_REQ) {
                throw new RuntimeException("终端未鉴权");
            }
            connectionManager.bingSimToAddress(msg.sim, msg.sender);
            lastCommTime.set(System.currentTimeMillis());

            switch (msg.msgId) {
                case ProtoConstants.TERMINAL_REGISTER_REQ:
                    doTerminalRegisterReq(msg);
                    break;
                case ProtoConstants.TERMINAL_UNREGISTER_REQ:
                    doTerminalUnregisterReq(msg);
                    break;
                case ProtoConstants.TERMINAL_AUTH_REQ:
                    doTerminalAuthReq(msg);
                    break;
                case ProtoConstants.TERMINAL_HEART_BEAT_REQ:
                    doTerminalHeartBeat(msg);
                    break;
                case ProtoConstants.RESEND_PACK_REQ:
                    logger.info("[{}] 接收到补传分包请求，丢弃之:sn={}", msg.sim, msg.sn);
                    break;
                default:
                    msg.vin = connectionManager.getVidBySim(msg.sim);
                    connectionManager.bingVidToAddress(msg.vin,msg.sender);
                    udpChannel.receiveMessage(msg);
                    break;
            }
        } catch (Throwable t) {
            logger.warn("[{}] 协议处理异常(0x{}):{}", msg.sim, CodecUtils.shortToHex(msg.msgId), t.getMessage());
        } finally {
            //msg.release();
        }
    }



   /* protected void checkState() {
        long tm = System.currentTimeMillis();
        if (tm - getLastCommTime() > terminalMaxIdleTimeMillis) {
            logger.info("[{}] 连接空闲超时，将关闭连接", remoteAddress());
            close();
        }
    }*/

    public ChannelFuture send(ProtoMsg msg) {
        if (msg.msgId == ProtoConstants.TERMINAL_REGISTER_RES) {
            doTerminalRegisterRes(msg);
        } else if (msg.msgId == ProtoConstants.CENTER_GENERAL_RES) {
            doCenterGeneralRes(msg);
        }

        ChannelFuture cf = channel.writeAndFlush(msg);
        lastCommTime.set(System.currentTimeMillis());
        return cf;
    }


    private void doTerminalRegisterReq(ProtoMsg msg) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] 收到终端注册请求", msg.sim);
        }

        if (state != CS_AUTHENCATED) {
            connectionManager.bingSimToAddress(msg.sim, msg.sender);
        }

        udpChannel.receiveMessage(msg);
    }

    private void doTerminalRegisterRes(ProtoMsg msg) {
        ByteBuf dataBuf = msg.dataBuf;
        int readerIndex = dataBuf.readerIndex();
        byte rc = dataBuf.getByte(readerIndex + 2);
        if (rc == ProtoConstants.RC_OK) { //注册成功
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] 终端注册成功", msg.sim);
            }
            connectionManager.bingVidToAddress(msg.vin, msg.sender);
            connectionManager.bingVidToSim(msg.vin, msg.sim);

        } else {
            logger.info("[{}] 终端注册失败({})", msg.sim, rc);
        }
    }

    /**
     * 平台通用应答
     *
     * @param msg
     */
    private void doCenterGeneralRes(ProtoMsg msg) {
        ByteBuf dataBuf = msg.dataBuf;
        int readerIndex = dataBuf.readerIndex();
        short reqId = dataBuf.getShort(readerIndex + 2);
        if (reqId == ProtoConstants.TERMINAL_AUTH_REQ) {
            doTerminalAuthRes(msg);
        } else if (reqId == ProtoConstants.TERMINAL_UNREGISTER_REQ) {
            doTerminalUnregisterRes(msg);
        }
    }

    private void doTerminalAuthReq(ProtoMsg msg) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] 收到终端鉴权请求", msg.sim);
        }
        udpChannel.receiveMessage(msg);
    }

    private void doTerminalAuthRes(ProtoMsg msg) {
        ByteBuf dataBuf = msg.dataBuf;
        int readerIndex = dataBuf.readerIndex();
        short reqId = dataBuf.getShort(readerIndex + 2);
        byte rc = dataBuf.getByte(readerIndex + 4);
        if (rc == ProtoConstants.RC_OK) { //鉴权成功
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] 终端鉴权成功", msg.sim);
            }
            connectionManager.bingVidToAddress(msg.vin, msg.sender);
            connectionManager.bingVidToSim(msg.vin, msg.sim);
            state = CS_AUTHENCATED;
        } else {
            logger.info("[{}] 终端鉴权失败({})", msg.sim, rc);
        }
    }


    private void doTerminalUnregisterReq(ProtoMsg msg) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] 收到终端注销请求", msg.sim);
        }

        if (state != CS_AUTHENCATED) {
            connectionManager.bingSimToAddress(msg.sim, msg.sender);
        }

        udpChannel.receiveMessage(msg);
    }


    private void doTerminalUnregisterRes(ProtoMsg msg) {
        //不做处理
    }


    private void doTerminalHeartBeat(ProtoMsg msg) {
        if (logger.isTraceEnabled()) {
            logger.trace("[{}] 接收到终端心跳", msg.sim);
        }
        if (connectionManager.getInetSocketAddressBySim(msg.sim) != null) {
            connectionManager.bingSimToAddress(msg.sim, msg.sender);
        }
        sendCenterGeneralRes(msg.sim, msg.msgId, msg.sn, ProtoConstants.RC_OK);
        udpChannel.receiveMessage(msg);
    }

    private void sendCenterGeneralRes(String sim, short reqMsgId, short reqSn, byte retCode) {
        ProtoMsg msg = new ProtoMsg();
        msg.sim = sim;
        msg.msgId = ProtoConstants.CENTER_GENERAL_RES;
        msg.dataBuf = Unpooled.buffer(5);
        msg.dataBuf.writeShort(reqSn);
        msg.dataBuf.writeShort(reqMsgId);
        msg.dataBuf.writeByte(retCode);
        msg.sn = udpChannel.nextSn(sim);
        InetSocketAddress sender = connectionManager.getInetSocketAddressBySim(sim);
        msg.sender = sender;
        channel.writeAndFlush(msg);
    }

    private void doTerminalLogout() {
        if (state != CS_AUTHENCATED) {
            return;
        }

        ProtoMsg msg = new ProtoMsg();
        msg.msgId = 0x0000;
/*        msg.sim = sim;
        msg.vid = vid;*/
        msg.sn = 0;
        msg.dataBuf = Unpooled.buffer(2);
        msg.dataBuf.writeShort(0x0102);

        udpChannel.receiveMessage(msg);
    }
}
