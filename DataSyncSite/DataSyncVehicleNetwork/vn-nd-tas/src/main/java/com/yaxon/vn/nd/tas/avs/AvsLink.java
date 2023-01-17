package com.yaxon.vn.nd.tas.avs;

/**
 * Author: 程行荣
 * Time: 2014-05-05 19:34
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 视频服务器
 */
//TODO: 1、注销请求后，不再发生连接请求；
public class AvsLink {
    protected static Logger logger = LoggerFactory.getLogger(AvsLink.class);

    private static final int CONNECT_TIMEOUT_MILLIS = 20000;

    private String Host;
    private int port;

    private Bootstrap bootstrap;
    private EventLoopGroup ioThreadPool;
    private Channel channel;
    private AvsLinkHandler avsLinkHandler;

    private volatile int linkState = 0; //链路状态
    private static final int LS_STARTED = 1;//已启动
    private static final int LS_STOPPED = 2;//已停止

    private volatile int connectState = CS_CLOSED; //连接状态
    private static final int CS_CLOSED = 0;
    private static final int CS_CONNECTING = 1;
    private static final int CS_CONNECTED = 2;
    private static final int CS_CLOSING = 3;

    private AtomicLong lastContactTime = new AtomicLong(0);
    private final Lock connectLock = new ReentrantLock(false);


    private Thread loopThread;

    class LoopThread extends Thread {
        LoopThread() {
            setName("AvsLink-loop");
            setDaemon(true);
        }

        @Override
        public void run() {
            int ticks = 0;
            long t = 0;
            while (true) {
                ++ticks;
                t = System.currentTimeMillis();
                try {
                    if (ticks % 30 == 0) { //发送心跳
                        if (connectState == CS_CONNECTED) {
                            avsLinkHandler.sendLinkTestReq(channel);
                        }
                    }

                    if (ticks % 5 == 0) { //连接状态检查
                        if (linkState != LS_STOPPED) {
                            if (connectState == CS_CLOSED) {//发起连接请求
                                openConnect();
                            } else if (connectState == CS_CONNECTING) {
                                if (t - lastContactTime.get() > CONNECT_TIMEOUT_MILLIS) {
                                    logger.warn("连接超时，即将关闭连接");
                                    closeConnect();
                                }
                            } else if (connectState == CS_CONNECTED) {
                                if (t - lastContactTime.get() > 180000) {
                                    logger.warn("链路保持超时，即将关闭连接");
                                    closeConnect();
                                }
                            }
                        }
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    logger.warn("后台线程异常", e);
                }
            }
            logger.info("线程({})已停止", getName());
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return Host;
    }

    public void setHost(String host) {
        Host = host;
    }

    /**
     * 开启通道
     *
     * @throws Exception
     */
    public void start() throws Exception {
        if (linkState == LS_STARTED) {
            return;
        }


        try {
            ioThreadPool = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            avsLinkHandler = new AvsLinkHandler(this);

            bootstrap.group(ioThreadPool)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new ProtoDecoder());
                            p.addLast(new ProtoEncoder());
                            p.addLast(avsLinkHandler);
                        }
                    });
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.SO_REUSEADDR, true);

            openConnect();

            loopThread = new LoopThread();
            loopThread.start();

            linkState = LS_STARTED;

            logger.info("视频服务器客户端已启动.");
        } catch (Exception e) {
            logger.warn("启动视频服务器客户端失败", e);
        }
    }

    /**
     * 关闭视频服务器服务
     */
    public void stop() {
        if (linkState == LS_STOPPED) {
            return;
        }

        try {
            closeConnect();

            if (loopThread != null) {
                loopThread.interrupt();
                loopThread = null;
            }

            if (ioThreadPool != null) {
                ioThreadPool.shutdownGracefully();
                ioThreadPool = null;
            }

            if (bootstrap != null) {
                bootstrap = null;
            }

            linkState = LS_STOPPED;
            logger.info("视频服务器客户端已关闭.");
        } catch (Exception e) {
            logger.warn("关闭视频服务器客户端失败", e);
        }
    }

    protected void openConnect() {
        logger.info("正在发起视频服务器连接({}:{})..", getHost(), getPort());
        connectLock.lock();
        try {
            if (connectState != CS_CLOSED) {
                return;
            }

            ChannelFuture cf = bootstrap.connect(getHost(), getPort());
            connectState = CS_CONNECTING;
            lastContactTime.set(System.currentTimeMillis());
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        logger.warn("开启视频服务器连接异常", future.cause());
                        connectState = CS_CLOSED;
                    }
                }
            });
        } finally {
            connectLock.unlock();
        }
    }

    protected void closeConnect() {
        connectLock.lock();
        try {
            if (connectState == CS_CLOSED) {
                return;
            }

            if (channel != null) {
                channel.close();
                connectState = CS_CLOSING;
                lastContactTime.set(System.currentTimeMillis());
            }
        } finally {
            connectLock.unlock();
        }
    }

    protected void connnected(Channel ch) {
        connectLock.lock();
        connectState = CS_CONNECTED;
        this.channel = ch;
        lastContactTime.set(System.currentTimeMillis());
        connectLock.unlock();
    }


    protected void disconnected() {
        connectLock.lock();
        connectState = CS_CLOSED;
        channel = null;
        lastContactTime.set(0);
        connectLock.unlock();
    }

    protected void checkAlive() {
        lastContactTime.set(System.currentTimeMillis());
    }


    public void sendMessage(ProtoMsg msg) throws RuntimeException {
        try {
            if (msg == null) {
                throw new RuntimeException("待发送的消息不能为null");
            }

            if (!isActive()) {
                throw new RuntimeException("视频服务器连接未建立");
            }

            avsLinkHandler.sendMessage(channel, msg).sync();
        } catch (Exception e) {
            throw new RuntimeException("向视频服务器发送消息失败", e);
        }
    }

    public boolean isStopped() {
        return linkState == LS_STOPPED;
    }

    public boolean isActive() {
        if (linkState == LS_STARTED && connectState == CS_CONNECTED) {
            return true;
        } else {
            return false;
        }
    }
}
