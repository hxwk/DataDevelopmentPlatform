package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.tcp;

import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.common.HandlersManger;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.config.TcpChannelConfig;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.BadFormattedProtocolException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.exception.TcpException;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.handler.BaseProtoHandler;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.codec.*;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoConstants;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto.ProtoMsg;
import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.util.CodecUtils;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
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
                    if (ticks % 20 == 0) { //连接管理器*/
                        connectionManager.checkLoop();
                    }

                    if (ticks % 10 == 0) {
                        for (Map.Entry<String, InvokeStubItem> entry : invokeStubs.entrySet()) {
                            InvokeStubItem item = entry.getValue();
                            if (tm - item.timestamp > config.getRequestTimeoutMillis()) {
                                String key = entry.getKey();
                                item = invokeStubs.remove(key);
                                if (item != null) {
                                    logger.info("等待上行应答超时:{}", key);
                                }
                            }
                        }
                    }

                    if (ticks % 60 == 0) {
                        terminalconnLogger.info("TCP的ConnId终端连接数: " + connectionManager.numOfConnections());
                        terminalconnLogger.info("TCP的sim连接数: " + connectionManager.simOfConnections());
                        terminalconnLogger.info("TCP的vid连接数: " + connectionManager.vid2Connection());
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    logger.warn("线程异常", e);
                }
            }
            logger.info("线程({})已停止", getName());
        }
    }


    public void init(TcpChannelConfig config) throws Exception {
//        config.checkConfig();
        this.config = config;
        this.maxBytesPerPack = config.getMaxBytesPerPack();
        this.requestTimeoutMillis = config.getRequestTimeoutMillis();
    }


    public void start() throws Exception {
        if (state == ST_STARTED) {
            logger.info("Tcp通道已启动");
            return;
        }

        logger.info("Tcp通道配置: {}", config);

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
                                    protoLogger,
                                    new ProtoPackDecoder(),
                                    new ProtoPackEncoder(),
                                    protoMsgDecoder,
                                    protoMsgEncoder
                            ).addLast(
                                    businessPool,
                                    new TcpConnection(TcpChannel.this));
                        }
                    });

         /*   bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.SO_REUSEADDR, false);*/
            //
            //bootstrap.option(ChannelOption.SO_RCVBUF, 128);
            //bootstrap.option(ChannelOption.SO_SNDBUF, 128);

            bootstrap.childOption(ChannelOption.SO_REUSEADDR, true); //是让端口释放后立即就可以被再次使用,一般来说,一个端口释放后会等待两分钟之后才能再被使用
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);  //禁止nagle算法,不配置会有40毫秒的延迟
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

            if (StringUtils.isNotBlank(config.getHost())) {
                bootstrap.bind(new InetSocketAddress(config.getHost(), config.getPort())).sync();
            } else {
                bootstrap.bind(new InetSocketAddress(config.getPort())).sync();
            }

//            loopThread.setDaemon(true);
            loopThread = new LoopThread();
            loopThread.exit = true;
            loopThread.start();

            state = ST_STARTED;
            logger.info("Tcp通道已启动(host={} port={}).", config.getHost(), config.getPort());
        } catch (Throwable t) {
            logger.error("Tcp通道启动失败,请检查端口是否被占用: {}\n{}", config, Throwables.getStackTraceAsString(t));
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
            logger.info("Tcp通道已停止");
        } catch (Exception e) {
            logger.warn("关闭 Tcp通道失败", e);
        }
    }

    public TcpChannelConfig getConfig() {
        return config;
    }

    public TcpConnectionManager getConnectionManager() {
        return connectionManager;
    }

    protected void receiveMessage(final ProtoMsg msg) {
        try {
            logger.debug("2.9 TcpChannel 终端回传数据 msg = " + msg + ", state = " + state);
            logger.info("终端通过TcpChannel回传数据 msg = " + msg + ", state = " + state);
            boolean done = false;
            if (msg.msgId == ProtoConstants.TERMINAL_GENERAL_RES) {
                done = doUpGeneralRes(msg);
            } else {
                if (resMsgIds.contains(msg.msgId)) {
                    done = doUpRes(msg);
                }
            }

            if (!done) {
                dispatchMessage(msg);
            }
        } catch (Exception e) {
            //sendCenterGeneralRes(msg, ProtoConstants.RC_FAIL);
            logger.warn("处理上行消息失败:msgId=0x" + CodecUtils.shortToHex(msg.msgId));
        }
    }

    private void dispatchMessage(ProtoMsg msg) {
        BaseProtoHandler handler = HandlersManger.getUpHandlers(msg.msgId);
        if (handler == null) {
            logger.warn("未找到消息处理器:msgId=0x" + CodecUtils.shortToHex(msg.msgId));
        } else {
            try {
                logger.info("msg:{},taskId:{}",msg.msgId,config.getTaskId());
                handler.handle(msg, config.getTaskId(), config.getChannelProcessor());
            } catch (Exception e) {
                logger.warn("处理消息失败:msgId=0x" + CodecUtils.shortToHex(msg.msgId));
            }
        }
    }

    private void sendCenterGeneralRes(ProtoMsg req, byte rc) throws TcpException {
        ProtoMsg msg = new ProtoMsg();
        msg.sim = req.sim;
        msg.vid = req.vid;
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
                throw new TcpException(TcpException.UNKNOWN_EXCEPTION, "通道未开启");
            }

            Validate.notNull(req, "待发送的消息不能未空");
            if (req.dataBuf == null) {
                logger.info(" 8104的dataBuf为空");
                req.dataBuf = Unpooled.buffer(0);
            }

            TcpConnection conn = getConnection(req);
            if (conn == null) {
                throw new TcpException(TcpException.TERMINAL_NO_LOGIN_EXCEPTION, "终端未登录");
            }
            req.sim = conn.sim();
            req.sn = getSn(req);

            logger.debug(" 2.1 TcpChannel 开始发送指令 , req = " + req);
            ChannelFuture cf = conn.send(req);
            logger.debug(" 2.2 TcpChannel 发送指令结束 , req = " + req);
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {

                    logger.debug(" 2.3 cf.addListener回调 , f.isSuccess() = " + f.isSuccess());

                    if (f.isSuccess()) {
                        setInvokeStub(req, resId, sf);
                    } else {
                        logger.warn(String.format("向终端发送消息失败: msg=%s", req), f.cause());
                        sf.setException(new TcpException(TcpException.UNKNOWN_EXCEPTION, "发送消息失败", f.cause()));
                    }
                }
            });
        } catch (TcpException e) {
            logger.warn(String.format("向终端发送消息失败: msg=%s", req), e);
            sf.setException(new TcpException(TcpException.UNKNOWN_EXCEPTION, "发送消息失败", e));
        } catch (Exception e) {
            logger.warn(String.format("向终端发送消息失败: msg=%s", req), e);
            sf.setException(new TcpException(TcpException.UNKNOWN_EXCEPTION, "发送消息失败", e));
        }

        logger.debug(" 2.6 cf.addListener回调 , sf = " + sf);
        return sf;
    }

    @Override
    public ListenableFuture<ProtoMsg> sendRequest(final ProtoMsg req, final short... resIds) {
        final SettableFuture<ProtoMsg> sf = SettableFuture.create();
        try {
            if (state != ST_STARTED) {
                throw new TcpException(TcpException.UNKNOWN_EXCEPTION, "通道未开启");
            }

            Validate.notNull(req, "待发送的消息不能未空");
            if (req.dataBuf == null) {
                req.dataBuf = Unpooled.buffer(0);
            }

            TcpConnection conn = getConnection(req);
            if (conn == null) {
                throw new TcpException(TcpException.TERMINAL_NO_LOGIN_EXCEPTION, "终端未登录");
            }
            req.sim = conn.sim();
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
                        logger.warn(String.format("向终端发送消息失败: msg=%s", req), f.cause());
                        sf.setException(new TcpException(TcpException.UNKNOWN_EXCEPTION, "发送消息失败", f.cause()));
                    }
                }
            });
        } catch (TcpException e) {
            logger.warn(String.format("向终端发送消息失败: msg=%s", req), e);
            sf.setException(new TcpException(TcpException.UNKNOWN_EXCEPTION, "发送消息失败", e));
        } catch (Exception e) {
            logger.warn(String.format("向终端发送消息失败: msg=%s", req), e);
            sf.setException(new TcpException(TcpException.UNKNOWN_EXCEPTION, "发送消息失败", e));
        }
        return sf;
    }

    @Override
    public void sendMessage(ProtoMsg msg) throws TcpException {
        try {
            if (state != ST_STARTED) {
                throw new TcpException(TcpException.UNKNOWN_EXCEPTION, "通道未开启");
            }

            Validate.notNull(msg, "待发送的消息不能未空");
            if (msg.dataBuf == null) {
                msg.dataBuf = Unpooled.buffer(0);
            }

            TcpConnection conn = getConnection(msg);
            if (conn == null) {
                throw new TcpException(TcpException.TERMINAL_NO_LOGIN_EXCEPTION, "终端未登录");
            }
            msg.sim = conn.sim();
            msg.sn = getSn(msg);

            ChannelFuture cf = conn.send(msg);
            //cf.sync(); //可能导致死锁
        } catch (TcpException e) {
            logger.warn(String.format("向终端发送消息失败: msg=%s", msg), e);
            throw e;
        } catch (Exception e) {
            logger.warn(String.format("向终端发送消息失败: msg=%s", msg), e);
            throw new TcpException(TcpException.UNKNOWN_EXCEPTION, "发送消息失败", e);
        }
    }

    private TcpConnection getConnection(ProtoMsg msg) {
        TcpConnection conn = null;
        if (msg.vid != "") {
            conn = connectionManager.getConnectionByVid(msg.vid);
        }

        if (conn == null && msg.sim != "") {
            conn = connectionManager.getConnectionBySim(msg.sim);
        }

        return conn;
    }

    private short getSn(ProtoMsg msg) {
        if (msg.msgType == 1) { //透传消息
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

        if (resId == ProtoConstants.TERMINAL_GENERAL_RES) { //通用应答
            invokeStubKey.msgId = req.msgId;
        } else { //专有应答
            invokeStubKey.msgId = resId;
            resMsgIds.add(resId);
        }

        logger.debug(" 2.5 setInvokeStub 回调 , req = " + req + ", resMsgIds = " + resMsgIds);
        InvokeStubItem invokeStubItem = new InvokeStubItem(System.currentTimeMillis(), sf);
        if (invokeStubs.putIfAbsent(invokeStubKey.getUniqueKey(), invokeStubItem) != null) {
            sf.setException(new TcpException("生成重复的存根数据：" + invokeStubKey));
        }
    }

    private boolean doUpGeneralRes(ProtoMsg msg) {
        ByteBuf dataBuf = msg.dataBuf;
        if (dataBuf.readableBytes() < 5) {
            throw new BadFormattedProtocolException("消息体长度异常(<5)(0x0001)");
        }
        int readerIndex = dataBuf.readerIndex();
        short reqSn = dataBuf.getShort(readerIndex);
        short reqId = dataBuf.getShort(readerIndex + 2);
        logger.debug("3.0 TcpChannel 终端回传数据 msg = " + msg + ", invokeStubs = " + invokeStubs );

        InvokeStubKey stubKeyT = new InvokeStubKey(msg.sim, reqId, reqSn);
        logger.debug("3.2 TcpChannel 终端回传数据 stubKeyT = " + stubKeyT + ", containsKey = " + invokeStubs.containsKey(stubKeyT));
        InvokeStubItem item = invokeStubs.remove(stubKeyT.getUniqueKey());
        logger.debug("3.3 TcpChannel 终端回传数据 item = " + item + ", invokeStubs = " + invokeStubs);
        if (item != null) {
            item.future.set(msg);
            return true;
        }
        return false;
    }

    private boolean doUpRes(ProtoMsg msg) {
        ByteBuf dataBuf = msg.dataBuf;
        if (dataBuf.readableBytes() < 2) {
            throw new BadFormattedProtocolException("消息体长度异常(<2):" + dataBuf.readableBytes());
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
