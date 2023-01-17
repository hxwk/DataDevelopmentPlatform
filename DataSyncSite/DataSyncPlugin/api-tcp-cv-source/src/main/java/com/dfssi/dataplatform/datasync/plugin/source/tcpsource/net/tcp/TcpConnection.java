package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.tcp;

import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.RedisPoolManager;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.config.TcpChannelConfig;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.CodecUtils;
import com.google.common.base.Throwables;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.CodecException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;


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
    private TcpChannelConfig tcpChannelConfig;
    private TcpConnectionManager connectionManager;
    private TcpChannel tcpChannel;
    private int terminalMaxIdleTimeMillis = 90000; //终端最大的空闲时间
    private Channel channel;
    private String remoteHost;
    private int remotePort;
    private String sim = "";
        private String vid = "";

    public TcpConnection() {
        this.connId = idGen.incrementAndGet();
    }

    public TcpConnection(TcpChannel tcpChannel) {
        this.connId = idGen.incrementAndGet();
        this.tcpChannel = tcpChannel;
        this.tcpChannelConfig = tcpChannel.getConfig();
        this.connectionManager = tcpChannel.getConnectionManager();
        this.terminalMaxIdleTimeMillis = tcpChannel.getConfig().getTerminalMaxIdleTimeMillis();
    }

    public long id() {
        return this.connId;
    }

    public String sim() {
        return this.sim;
    }

    public String vid() {
        return this.vid;
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
        logger.info("[{}:{}/{}]连接已建立,connId:{}", remoteHost,remotePort, sim, connId);
        //flume采集连接状态
        //close();

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        doTerminalLogout();
        state = CS_DISCONNECTED;
        channel = null;
        Jedis jedis = new RedisPoolManager().getJedis();
        jedis.del("SimBinding:"+sim);
        jedis.close();
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
            connectionManager.removeConnection(connId,sim,vid);
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
                        logger.warn("sim卡号为：{0}的channel关闭失败，cause:{1}", sim, future.cause());
                    }
                }
            });

        } else {
            connectionManager.removeConnection(this);
        }
        //  logger.info("sim:{},执行关闭方法结束时间:{},connId:{}",sim,(System.currentTimeMillis() - startTime),connId);
    }

    private String remoteAddress() {
        return sim + "/" + remoteHost;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object protoMsg) throws Exception {
        ProtoMsg msg = (ProtoMsg) protoMsg;
        try {
            logger.debug("2.8 TcpConnection 终端回传数据 msg = " + msg + ", state = " + state);

            if (state != CS_AUTHENCATED
                    && msg.msgId != ProtoConstants.TERMINAL_REGISTER_REQ
                    && msg.msgId != ProtoConstants.TERMINAL_AUTH_REQ
                    && msg.msgId != ProtoConstants.TERMINAL_UNREGISTER_REQ
                    && msg.msgId != ProtoConstants.TERMINAL_HEART_BEAT_REQ) {
                checkRepeatLink(msg);
                if( msg.msgId != ProtoConstants.TERMINAL_AUTH_REQ){
                    sendCenterGeneralRes(msg.sim, msg.msgId, msg.sn, ProtoConstants.RC_FAIL);
                }
                throw new RuntimeException("终端未鉴权,connId:"+connId);
            }

            if (state != CS_AUTHENCATED) {
                this.sim = msg.sim;
                connectionManager.bingSimToConnection(this);
            }

            msg.vid = vid;
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
                    tcpChannel.receiveMessage(msg);
                    break;
            }
        } catch (Throwable t) {
            logger.warn("[{}] 协议处理异常(0x{}):{}", msg.sim, CodecUtils.shortToHex(msg.msgId), t.getMessage());
        } finally {
            //msg.release();
        }

    }

    private void checkRepeatLink(ProtoMsg msg) {
        TcpConnection sim2connection = connectionManager.getConnectionBySim(msg.sim);
        if (sim2connection != null) {
            if (sim2connection.connId != connId) {
                //close();
                sim2connection.close();
            }
            // logger.warn("[{}] 通过sim卡查找到Connection,connId:{}", msg.sim, sim2connection.connId);

        } else {
            // logger.warn("通过sim卡查找为空");
        }

        if(sim2connection!=null){
            TcpConnection vid2connection = connectionManager.getConnectionByVid(sim2connection.vid());
            if (vid2connection != null) {
                if (vid2connection.connId != connId) {
                    //close();
                    vid2connection.close();
                }
                // logger.warn("[{}] 通过connId卡查找到Connection,connId:{}", msg.sim, vid2connection.connId);
            } else {
                //  logger.warn("通connId查找为空");
            }
        }
//        TcpConnection connId2connection = connectionManager.getConnectionByConnId(connId);
//        if (connId2connection == null) {
//            close();
//        }
        TcpConnection connId2connection = connectionManager.getConnectionByConnId(connId);
        if (connId2connection == null) {
            close();
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
        if (msg.msgId == ProtoConstants.TERMINAL_REGISTER_RES) {
            doTerminalRegisterRes(msg);
        } else if (msg.msgId == ProtoConstants.CENTER_GENERAL_RES) {
            doCenterGeneralRes(msg);
        }

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
                this.vid = msg.vid;
                connectionManager.bingVidToConnection(this);
                state = CS_AUTHENCATED;
                //flume采集连接状态
            }
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
        tcpChannel.receiveMessage(msg);
    }

    private void doTerminalAuthRes(ProtoMsg msg) {
        ByteBuf dataBuf = msg.dataBuf;
        int readerIndex = dataBuf.readerIndex();
        short reqId = dataBuf.getShort(readerIndex + 2);
        byte rc = dataBuf.getByte(readerIndex + 4);
        if (rc == ProtoConstants.RC_OK) { //鉴权成功
            try{
                InetAddress localHost = InetAddress.getLocalHost();
                Jedis jedis = new RedisPoolManager().getJedis();
//                jedis.set("SimBinding:"+msg.sim,localHost.getHostAddress());
                jedis.set("SimBinding:"+msg.sim,getHostIp());
                jedis.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] 终端鉴权成功", msg.sim);
            }
            if (state != CS_AUTHENCATED) {
                this.vid = msg.vid;
                connectionManager.bingVidToConnection(this);
                state = CS_AUTHENCATED;
                //flume采集连接状态
            }
        } else {
            logger.info("[{}] 终端鉴权失败({})", msg.sim, rc);
        }
    }


    private void doTerminalUnregisterReq(ProtoMsg msg) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] 收到终端注销请求", msg.sim);
        }
        tcpChannel.receiveMessage(msg);
    }


    private void doTerminalUnregisterRes(ProtoMsg msg) {
        //不做处理
    }


    private void doTerminalHeartBeat(ProtoMsg msg) {
        if (logger.isTraceEnabled()) {
            logger.trace("[{}] 接收到终端心跳", msg.sim);
        }
        sendCenterGeneralRes(msg.sim, msg.msgId, msg.sn, ProtoConstants.RC_OK);

        tcpChannel.receiveMessage(msg);
    }

    private void sendCenterGeneralRes(String sim, short reqMsgId, short reqSn, byte retCode) {
        ProtoMsg msg = new ProtoMsg();
        msg.sim = sim;
        msg.msgId = ProtoConstants.CENTER_GENERAL_RES;
        msg.dataBuf = Unpooled.buffer(5);
        msg.dataBuf.writeShort(reqSn);
        msg.dataBuf.writeShort(reqMsgId);
        msg.dataBuf.writeByte(retCode);
        msg.sn = tcpChannel.nextSn(sim);

        channel.writeAndFlush(msg);
    }

    private void doTerminalLogout() {
        if (state != CS_AUTHENCATED) {
            //当超时的时候状态为CS_CLOSING,这种情况也要正常下线。这种情况会导致超时没有下线记录
            if(state!=CS_CLOSING)
                return;
        }

        ProtoMsg msg = new ProtoMsg();
        msg.msgId = 0x0000;
        msg.sim = sim;
        msg.vid = vid;
        msg.sn = 0;
        msg.dataBuf = Unpooled.buffer(2);
        msg.dataBuf.writeShort(0x0102);

        tcpChannel.receiveMessage(msg);
    }

    private static String getHostIp(){
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":")==-1){
                        return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
