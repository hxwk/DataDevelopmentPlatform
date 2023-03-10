package com.dfssi.dataplatform.plugin.tcpnesource.net.tcp;

import com.dfssi.dataplatform.datasync.common.ne.ProtoMsg;
import com.dfssi.dataplatform.datasync.common.utils.CodecUtils;
import com.dfssi.dataplatform.plugin.tcpnesource.common.HandlersManger;
import com.dfssi.dataplatform.plugin.tcpnesource.config.TcpChannelConfig;
import com.dfssi.dataplatform.plugin.tcpnesource.exception.BadFormattedProtocolException;
import com.dfssi.dataplatform.plugin.tcpnesource.exception.TcpException;
import com.dfssi.dataplatform.plugin.tcpnesource.handler.BaseProtoHandler;
import com.dfssi.dataplatform.plugin.tcpnesource.net.codec.*;
import com.dfssi.dataplatform.plugin.tcpnesource.net.proto.ProtoConstants;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicLongMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;


public class TcpChannel implements AccessChannel {

    protected static final Logger terminalconnLogger = LoggerFactory.getLogger("terminalconnLogger");
    private static final int ST_STOPPED = 0;
    private static final int ST_STARTED = 1;
    protected static Logger logger = LoggerFactory.getLogger(TcpChannel.class);
    private AtomicLongMap<String> snGen = AtomicLongMap.create();
    private volatile int state = ST_STOPPED;

    private TcpChannelConfig config;

    private EventLoopGroup bossThreadPool;
    private EventLoopGroup ioThreadPool;

    private TcpConnectionManager connectionManager = new TcpConnectionManager();
    private int maxBytesPerPack;
    private int requestTimeoutMillis;
    private ConcurrentMap<String, InvokeStubItem> invokeStubs = Maps.newConcurrentMap();
    private Set<Short> resMsgIds = Sets.newHashSet();
    private DefaultEventExecutorGroup businessPool;
    private String vin;
    private LoopThread loopThread = null;

    public class LoopThread extends Thread {
        public volatile boolean exit = false;
        @Override
        public void run() {
            int ticks = 0;
            while (exit) {
                ++ticks;
                long tm = System.currentTimeMillis();
                try {
                    if (ticks % 20 == 0) { //???????????????*/
                        connectionManager.checkLoop();
                    }

                    if (ticks % 10 == 0) {
                        for (Map.Entry<String, InvokeStubItem> entry : invokeStubs.entrySet()) {
                            InvokeStubItem item = entry.getValue();
                            if (tm - item.timestamp > config.getRequestTimeoutMillis()) {
                                String key = entry.getKey();
                                item = invokeStubs.remove(key);
                                if (item != null) {
                                    logger.info("????????????????????????:{}", key);
                                }
                            }
                        }
                    }

                    if (ticks % 60 == 0) {
                        terminalconnLogger.info("TCP???ConnId???????????????: " + connectionManager.numOfConnections());
                        terminalconnLogger.info("TCP???vin?????????: " + connectionManager.vin2Connection());
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    logger.warn("????????????", e);
                }
            }
            logger.info("??????({})?????????", getName());
        }
    };


    public void init(TcpChannelConfig config) throws Exception {
//        config.checkConfig();
        this.config = config;
        this.maxBytesPerPack = config.getMaxBytesPerPack();
        this.requestTimeoutMillis = config.getRequestTimeoutMillis();
    }


    @Override
    public void start() throws Exception {
        if (state == ST_STARTED) {
            logger.info("Tcp???????????????");
            return;
        }

        logger.info("Tcp????????????: {}", config);

        bossThreadPool = new NioEventLoopGroup(2);
        ioThreadPool = new NioEventLoopGroup(config.getIoThreads());
        businessPool = new DefaultEventExecutorGroup(config.getIoThreads());

        final ProtoLogger protoLogger = new ProtoLogger();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            final ProtoMsgEncoder protoMsgEncoder = new ProtoMsgEncoder(config.getMaxBytesPerPack());
            final ProtoMsgDecoder protoMsgDecoder = new ProtoMsgDecoder(this);
            bootstrap.group(bossThreadPool, ioThreadPool).
                    option(ChannelOption.SO_BACKLOG, 2048)
                    .channel(NioServerSocketChannel.class)
                    //.handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new ProtoDivDecoder(65556,Unpooled.copiedBuffer("##".getBytes())),//65531+25=65556
                                    protoLogger,
                                    new ProtoPackDecoder(),
                                    new ProtoPackEncoder(),
                                    protoMsgDecoder,
                                    protoMsgEncoder

                            ).addLast(
                                    new TcpConnection(TcpChannel.this));
                        }
                    });

            bootstrap.childOption(ChannelOption.SO_REUSEADDR, true); //???????????????????????????????????????????????????,????????????,???????????????????????????????????????????????????????????????
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);  //??????nagle??????,???????????????40???????????????
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            if (StringUtils.isNotBlank(config.getHost())) {
                bootstrap.bind(new InetSocketAddress(config.getHost(), config.getPort())).sync();
            } else {
                bootstrap.bind(new InetSocketAddress(config.getPort())).sync();
            }

            loopThread = new LoopThread();
            loopThread.exit = true;
            loopThread.start();

            state = ST_STARTED;
            logger.info("Tcp???????????????(host={} port={}).", config.getHost(), config.getPort());
        } catch (Throwable t) {
            logger.error("Tcp??????????????????,??????????????????????????????: {}\n{}", config, Throwables.getStackTraceAsString(t));
            stop();
        }
    }


    @Override
    public void stop() {
        if (state == ST_STOPPED) {
            return;
        }

        try {
            connectionManager.close();
            loopThread.exit=false;
            loopThread.join();

            if (bossThreadPool != null) {
                bossThreadPool.shutdownGracefully();
                bossThreadPool = null;
            }

            if (ioThreadPool != null) {
                ioThreadPool.shutdownGracefully();
                ioThreadPool = null;
            }

            state = ST_STOPPED;
            logger.info("Tcp???????????????(idea-neSource)");
        } catch (Exception e) {
            logger.warn("?????? Tcp????????????", e);
        }
    }

    public TcpChannelConfig getConfig() {
        return config;
    }

    @Override
    public TcpConnectionManager getConnectionManager() {
        return connectionManager;
    }

    protected void receiveMessage(final ProtoMsg msg,String vin) {
        try {
            this.vin = vin;
            logger.debug("2.9 TcpChannel ?????????????????? msg = " + msg + ", state = " + state);

            dispatchMessage(msg,vin);
        } catch (Exception e) {
            //sendCenterGeneralRes(msg, ProtoConstants.RC_FAIL);
            logger.warn("????????????????????????:msgId=0x" + CodecUtils.shortToHex(msg.commandSign));
        }
    }

    private void dispatchMessage(ProtoMsg msg,String vin) {
        BaseProtoHandler handler = HandlersManger.getUpHandlers((short)msg.commandSign);
        if (handler == null) {
            logger.warn("????????????????????????:msgId=0x" + CodecUtils.shortToHex(msg.commandSign));
        } else {
            try {
                handler.handle(msg, config.getTaskId(), config.getChannelProcessor(),vin);
            } catch (Exception e) {
                logger.warn("????????????????????????:msgId=0x" + CodecUtils.shortToHex(msg.commandSign));
            }
        }
    }

    private void sendCenterGeneralRes(ProtoMsg req, byte rc) throws TcpException {
        ProtoMsg msg = new ProtoMsg();
        msg.vin = req.vin;
        msg.msgId = ProtoConstants.CENTER_GENERAL_RES;
        msg.dataBuf = Unpooled.buffer(5);
        msg.dataBuf.writeShort(req.sn);
        msg.dataBuf.writeShort(req.msgId);
        msg.dataBuf.writeByte(rc);

        sendMessage(msg);
    }

    @Override
    public ListenableFuture<ProtoMsg> sendRequest(final ProtoMsg req, final short resId) {
        final SettableFuture<ProtoMsg> sf = SettableFuture.create();
        try {
            if (state != ST_STARTED) {
                throw new TcpException(TcpException.UNKNOWN_EXCEPTION, "???????????????");
            }

            Validate.notNull(req, "??????????????????????????????");
            if (req.dataBuf == null) {
                req.dataBuf = Unpooled.buffer(0);
            }

            TcpConnection conn = getConnection(req);
            if (conn == null) {
                throw new TcpException(TcpException.TERMINAL_NO_LOGIN_EXCEPTION, "???????????????");
            }
            req.vin = conn.vin();
            req.sn = getSn(req);

            logger.debug(" 2.1 TcpChannel ?????????????????? , req = " + req);
            ChannelFuture cf = conn.send(req);
            logger.debug(" 2.2 TcpChannel ?????????????????? , req = " + req);
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {

                    logger.debug(" 2.3 cf.addListener?????? , f.isSuccess() = " + f.isSuccess());

                    if (f.isSuccess()) {
                        setInvokeStub(req, resId, sf);
                    } else {
                        logger.warn(String.format("???????????????????????????: msg=%s", req), f.cause());
                        sf.setException(new TcpException(TcpException.UNKNOWN_EXCEPTION, "??????????????????", f.cause()));
                    }
                }
            });
        } catch (TcpException e) {
            logger.warn(String.format("???????????????????????????: msg=%s", req), e);
            sf.setException(new TcpException(TcpException.UNKNOWN_EXCEPTION, "??????????????????", e));
        } catch (Exception e) {
            logger.warn(String.format("???????????????????????????: msg=%s", req), e);
            sf.setException(new TcpException(TcpException.UNKNOWN_EXCEPTION, "??????????????????", e));
        }

        logger.debug(" 2.6 cf.addListener?????? , sf = " + sf);
        return sf;
    }

    @Override
    public ListenableFuture<ProtoMsg> sendRequest(final ProtoMsg req, final short... resIds) {
        final SettableFuture<ProtoMsg> sf = SettableFuture.create();
        try {
            if (state != ST_STARTED) {
                throw new TcpException(TcpException.UNKNOWN_EXCEPTION, "???????????????");
            }

            Validate.notNull(req, "??????????????????????????????");
            if (req.dataBuf == null) {
                req.dataBuf = Unpooled.buffer(0);
            }

            TcpConnection conn = getConnection(req);
            if (conn == null) {
                throw new TcpException(TcpException.TERMINAL_NO_LOGIN_EXCEPTION, "???????????????");
            }
            req.vin = conn.vin();
            req.sn = getSn(req);

            ChannelFuture cf = conn.send(req);
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {
                    if (f.isSuccess()) {
                        for (short resId : resIds) {
                            setInvokeStub(req, resId, sf);
                        }
                    } else {
                        logger.warn(String.format("???????????????????????????: msg=%s", req), f.cause());
                        sf.setException(new TcpException(TcpException.UNKNOWN_EXCEPTION, "??????????????????", f.cause()));
                    }
                }
            });
        } catch (TcpException e) {
            logger.warn(String.format("???????????????????????????: msg=%s", req), e);
            sf.setException(new TcpException(TcpException.UNKNOWN_EXCEPTION, "??????????????????", e));
        } catch (Exception e) {
            logger.warn(String.format("???????????????????????????: msg=%s", req), e);
            sf.setException(new TcpException(TcpException.UNKNOWN_EXCEPTION, "??????????????????", e));
        }
        return sf;
    }

    @Override
    public void sendMessage(ProtoMsg msg) throws TcpException {
        try {
            if (state != ST_STARTED) {
                throw new TcpException(TcpException.UNKNOWN_EXCEPTION, "???????????????");
            }

            Validate.notNull(msg, "??????????????????????????????");
            if (msg.dataBuf == null) {
                msg.dataBuf = Unpooled.buffer(0);
            }

            TcpConnection conn = getConnection(msg);
            if (conn == null) {
                throw new TcpException(TcpException.TERMINAL_NO_LOGIN_EXCEPTION, "???????????????");
            }
            logger.debug("wx conn:{}",conn.vin());
            ChannelFuture cf = conn.send(msg);
            //cf.sync(); //??????????????????
        } catch (TcpException e) {
            logger.warn(String.format("???????????????????????????: msg=%s", msg), e);
            throw e;
        } catch (Exception e) {
            logger.warn(String.format("???????????????????????????: msg=%s", msg), e);
            throw new TcpException(TcpException.UNKNOWN_EXCEPTION, "??????????????????", e);
        }
    }

    private TcpConnection getConnection(ProtoMsg msg) {
//        logger.info("wx ??????vin??????tcp?????????{}",this.vin);
        TcpConnection conn = null;
        if (this.vin != "") {
            conn = connectionManager.getConnectionByVin(this.vin);
        }

        return conn;
    }

    private short getSn(ProtoMsg msg) {
        if (msg.msgType == 1) { //????????????
            return msg.sn;
        }

        int dataLen = msg.dataBuf.readableBytes();
        //int packCount = (dataLen + maxBytesPerPack - 1) / maxBytesPerPack;
        int packCount = 1;
        if (dataLen > 0) {
            packCount = (dataLen + maxBytesPerPack - 1) / maxBytesPerPack;
        }
        long sn = snGen.getAndAdd(msg.sim, packCount);

        return (short)(sn & 0xFFFF);
    }

    protected short nextSn(String sim) {
        long sn = snGen.getAndIncrement(sim);
        return (short)(sn & 0xFFFF);
    }

    private void setInvokeStub(ProtoMsg req, short resId, SettableFuture<ProtoMsg> sf) {
        InvokeStubKey invokeStubKey = new InvokeStubKey();
        invokeStubKey.sim = req.sim;
        invokeStubKey.sn = req.sn;

        if (resId == ProtoConstants.TERMINAL_GENERAL_RES) { //????????????
            invokeStubKey.msgId = req.msgId;
        } else { //????????????
            invokeStubKey.msgId = resId;
            resMsgIds.add(resId);
        }

        logger.debug(" 2.5 setInvokeStub ?????? , req = " + req + ", resMsgIds = " + resMsgIds);
        InvokeStubItem invokeStubItem = new InvokeStubItem(System.currentTimeMillis(), sf);
        if (invokeStubs.putIfAbsent(invokeStubKey.getUniqueKey(), invokeStubItem) != null) {
            sf.setException(new TcpException("??????????????????????????????" + invokeStubKey));
        }
    }

    private boolean doUpGeneralRes(ProtoMsg msg) {
        ByteBuf dataBuf = msg.dataBuf;
        if (dataBuf.readableBytes() < 5) {
            throw new BadFormattedProtocolException("?????????????????????(<5)(0x0001)");
        }
        int readerIndex = dataBuf.readerIndex();
        short reqSn = dataBuf.getShort(readerIndex);
        short reqId = dataBuf.getShort(readerIndex + 2);
        logger.debug("3.0 TcpChannel ?????????????????? msg = " + msg + ", invokeStubs = " + invokeStubs );

        InvokeStubKey stubKeyT = new InvokeStubKey(msg.sim, reqId, reqSn);
        logger.debug("3.2 TcpChannel ?????????????????? stubKeyT = " + stubKeyT + ", containsKey = " + invokeStubs.containsKey(stubKeyT));
        InvokeStubItem item = invokeStubs.remove(stubKeyT.getUniqueKey());
        logger.debug("3.3 TcpChannel ?????????????????? item = " + item + ", invokeStubs = " + invokeStubs);
        if (item != null) {
            item.future.set(msg);
            return true;
        }
        return false;
    }

    private boolean doUpRes(ProtoMsg msg) {
        ByteBuf dataBuf = msg.dataBuf;
        if (dataBuf.readableBytes() < 2) {
            throw new BadFormattedProtocolException("?????????????????????(<2):" + dataBuf.readableBytes());
        }

        int readerIndex = dataBuf.readerIndex();
        short reqSn = dataBuf.getShort(readerIndex);
        InvokeStubKey stubKeyT = new InvokeStubKey(msg.sim, msg.msgId, reqSn);
        InvokeStubItem item = invokeStubs.remove(stubKeyT.getUniqueKey());
        if (item != null) {
            item.future.set(msg);
            return true;
        }
        return false;
    }

}
