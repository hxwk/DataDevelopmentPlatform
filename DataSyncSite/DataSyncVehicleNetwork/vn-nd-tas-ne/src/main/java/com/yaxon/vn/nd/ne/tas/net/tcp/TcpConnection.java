package com.yaxon.vn.nd.ne.tas.net.tcp;

import com.google.common.base.Throwables;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoConstants;
import com.yaxon.vn.nd.ne.tas.net.proto.ProtoMsg;
import com.yaxon.vndp.common.util.CodecUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
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
 * TCP服务处理器.
 * 1.忽略上行/下行重传机制.
 * 2.
 */
//TODO: 优化内存池处理
//TODO: 优化建议：1、可以利用MapDB来缓存分包数据以及下行数据缓存以便重传
public class TcpConnection extends ChannelInboundHandlerAdapter {
    public static final int CS_DISCONNECTED = 0;
    public static final int CS_CONNECTED = 1;
    public static final int CS_AUTHENCATED = 2;
    public static final int CS_CLOSING = 3;
    private static final Logger logger = LoggerFactory.getLogger(TcpConnection.class);
    private static AtomicLong idGen = new AtomicLong();
    protected AtomicLong lastCommTime = new AtomicLong();
    protected long connId;
    protected volatile int state = 0;
    private TcpChannel tcpChannel;
    private TcpConnectionManager connectionManager;
    private int terminalMaxIdleTimeMillis = 90000; //终端最大的空闲时间
    private Channel channel;
    private String remoteHost;
    private int remotePort;
    private String vin = "";

    public TcpConnection() {
        this.connId = idGen.incrementAndGet();
    }

    public TcpConnection(TcpChannel tcpChannel) {
        this.connId = idGen.incrementAndGet();
        this.tcpChannel = tcpChannel;
        this.connectionManager = tcpChannel.getConnectionManager();
        this.terminalMaxIdleTimeMillis = tcpChannel.getConfig().getTerminalMaxIdleTimeMillis();
    }

    public long id() {
        return this.connId;
    }

    public String vin() {
        return this.vin;
    }

    public Channel getChannel() {
        return channel;
    }

    public int getState() {
        return state;
    }

    public long getLastCommTime() {
        return lastCommTime.get();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        state = CS_CONNECTED;
        channel = ctx.channel();
        InetSocketAddress isa = (InetSocketAddress) channel.remoteAddress();
        remoteHost=isa.getAddress().getHostAddress();
        remotePort=isa.getPort();
        lastCommTime.set(System.currentTimeMillis());
        connectionManager.addConnection(this);
        logger.info("[{}:{}/{}]连接已建立,connId:{}", remoteHost,remotePort, vin, connId);
        //flume采集连接状态
        //close();

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        doTerminalLogout();
        state = CS_DISCONNECTED;
        channel = null;
        connectionManager.removeConnection(this);
        logger.info("[{}]连接已断开,connId:{}", remoteAddress(),connId);
        //flume采集连接状态
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof CodecException) {
            logger.warn("[{}]协议编码异常:\n{}", remoteAddress(), Throwables.getStackTraceAsString(cause));
        } else {
            logger.warn("[{}]网络异常, 将关闭连接: {},connId:{}", remoteAddress(), cause.getMessage(),connId);
            //flume采集连接状态
            ctx.close();
            connectionManager.removeConnection(connId, vin);
        }

    }

    public void close() {
        //final long startTime = System.currentTimeMillis();
        if (channel != null) {
            channel.flush();
            ChannelFuture future = channel.close();
            // ChannelFuture future = channel.close();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        // logger.warn("sim卡号为：{}的channel关闭成功,！·！！，cause:{},connId:{}", sim, future.cause(), connId);
                        // logger.info("执行链路关闭成功时间:" + (System.currentTimeMillis() - startTime) + ",connId:" + connId);
                        connectionManager.removeConnection(TcpConnection.this);
                        state = CS_CLOSING;

                    } else {
                        logger.warn("sim卡号为：{0}的channel关闭失败，cause:{1}", vin, future.cause());
                    }
                }
            });

        } else {
            connectionManager.removeConnection(this);
        }
        //  logger.info("sim:{},执行关闭方法结束时间:{},connId:{}",sim,(System.currentTimeMillis() - startTime),connId);


    }

    private String remoteAddress() {
        return vin + "/" + remoteHost;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object protoMsg) throws Exception {
        ProtoMsg msg = (ProtoMsg) protoMsg;
        try {

            System.out.println( " TcpConnection msg = " + msg);

            logger.debug("2.8 TcpConnection 终端回传数据 msg = " + msg + ", state = " + state);

            this.vin = msg.vin;
            if (null == connectionManager.getConnectionByVin(msg.vin)) {
                connectionManager.bingVinToConnection(this);
            }

            lastCommTime.set(System.currentTimeMillis());

            tcpChannel.receiveMessage(msg);
        } catch (Throwable t) {
            logger.warn("[{}] 协议处理异常(0x{}):{}", msg.vin, CodecUtils.shortToHex(msg.commandSign), t.getMessage());
        } finally {
            //msg.release();
        }

    }

    protected void checkState() {
        long tm = System.currentTimeMillis();
        if (tm - getLastCommTime() > terminalMaxIdleTimeMillis) {
            logger.info("[{}] 连接空闲超时，将关闭连接", remoteAddress());
            close();
        }
    }

    public ChannelFuture send(ProtoMsg msg) {
        logger.debug("2.7 往终端发送请求数据 msg = " + msg);
        ChannelFuture cf = channel.writeAndFlush(msg);
        lastCommTime.set(System.currentTimeMillis());
        return cf;
    }


    private void doTerminalRegisterReq(ProtoMsg msg) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] 收到终端注册请求", msg.sim);
        }
        tcpChannel.receiveMessage(msg);
    }

    private void doTerminalRegisterRes(ProtoMsg msg) {
        ByteBuf dataBuf = msg.dataBuf;
        int readerIndex = dataBuf.readerIndex();
        byte rc = dataBuf.getByte(readerIndex + 2);
        if (rc == ProtoConstants.RC_OK) { //注册成功
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] 终端注册成功", msg.sim);
            }

            if (state != CS_AUTHENCATED) {
                this.vin = msg.vin;
                connectionManager.bingVinToConnection(this);
                state = CS_AUTHENCATED;
                //flume采集连接状态
            }
        } else {
            logger.info("[{}] 终端注册失败({})", msg.sim, rc);
        }
    }


}
