package com.dfssi.dataplatform.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * descripiton: netty网关的client客户端
 *
 * @author: www.iknowba.cn
 * @date: 2018/3/23
 * @time: 16:40
 * @modifier:
 * @since:
 */
public class NettyClient {
    private static Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private String host;//目的平台的ip

    private int port;//目的平台的port


    //做开启和关闭 需要保存关闭对象的引用
    private volatile EventLoopGroup group = null;
    private volatile ChannelFuture channelFuture = null;
    private volatile Bootstrap bootstrap = null;
    private volatile boolean connected = false;


    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public int start() {
        connected = false;
        logger.info("netty网关的client客户端启动服务");
        //设置一个多线程循环器
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //启动附注类
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        //指定所使用的NIO传输channel
        bootstrap.channel(NioSocketChannel.class);
        ThreadClientHandler clientHandler = new ThreadClientHandler(host+":"+port);
        //指定客户端初始化处理
        bootstrap.handler(new ChannelInitializer() {
                              @Override
                              protected void initChannel(Channel channel) throws Exception {
                                  ChannelPipeline p = channel.pipeline();
//                                  p.addLast(new ClientDecoder(), new ClientEncoder());
                                  p.addLast(clientHandler);
                              }
                          }
        ).option(ChannelOption.TCP_NODELAY, true);
        int res = doConnect();
        return res;
    }

    //停止服务，关闭连接 并且注销服务地址
    public void stop() {
        logger.info("客户端首先停止，并关闭channel连接");
        connected = false;
        if (null != channelFuture) {
            if (null != channelFuture.channel() && channelFuture.channel().isOpen()) {
                try {
                    channelFuture.channel().close();
                } catch (Exception e) {
                    logger.error(Thread.currentThread().getName() + "-closing");
                }
            }
            channelFuture = null;
        }
        if (group != null) {
            try {
                group.shutdownGracefully();
            } catch (Exception e) {
                logger.error("客户端开始停止，关闭连接失败 error:{}", e);
            }
        }
    }

    ////连接服务端
    public int doConnect() {
        if (connected) {
            return 0;
        }
        logger.info("netty网关的client客户端开始连接接入平台");
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();//服务端是bind  客户端是connect
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        logger.info("netty网关的client客户端连接目的平台成功，channelMap中添加channel成功！");
                        connected = true;
                    } else {
                        logger.info("netty网关的client客户端目的平台失败，5S后尝试重连！");
                        // future.channel().eventLoop().schedule(() -> doConnect(), 5, TimeUnit.SECONDS);
                        final EventLoop loop = future.channel().eventLoop();
                        loop.schedule(new Runnable() {
                            @Override
                            public void run() {
                                doReconnect();
                            }
                        }, 5, TimeUnit.SECONDS);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("netty网关的client客户端连接目的平台发送异常", e);
            //如果无法连接到对应的ip 端口会直接抛出异常
            int res = doReconnect();
            if (res == -1) {
                logger.error("重新启动netty客户端向目的平台发送数据，仍无法与目的平台相连！现单次重连时间超过20min，关闭掉netty客户端，请检查网络通讯状况！");
                return -1;
            } else {
                logger.info("重新启动netty客户端向目的平台发送数据与目的平台重连成功！");
                return 0;
            }
        }
        logger.info("数据传输工程连接目的平台未发现异常，需要在这里等待0.5S使channelMap中添加channel成功！");
        try {
            sleep(500);
        } catch (InterruptedException e) {
            logger.error("数据传输client客户端sleep异常，请检查系统环境 error :{}", e);
        }
        return 0;
    }


    /*  重连 -1代表重连失败 0点重连成功  重连还要保证channel的通道注册上即必须调用channelActive方法*/
    public int doReconnect() {
        logger.info("netty网关的client客户端开始重新连接目的平台");
        stop();
        start();
        return 0;
    }


}