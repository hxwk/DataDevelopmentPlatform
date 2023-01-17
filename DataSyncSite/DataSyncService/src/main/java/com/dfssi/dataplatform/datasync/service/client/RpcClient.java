package com.dfssi.dataplatform.datasync.service.client;

import com.dfssi.dataplatform.datasync.service.rpc.cluster.ServiceRegistry;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcDecoder;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcEncoder;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcRequest;
import com.dfssi.dataplatform.datasync.service.util.msgutil.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

//import com.dfssi.dataplatform.datasync.service.constants.PropertiUtil;


/**
 * Created by cxq on 2017/11/16.
 */
@ChannelHandler.Sharable
public class RpcClient{
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private String host;
    private  int port;
    private RpcResponse response;

    //做开启和关闭 需要保存关闭对象的引用
    private volatile  EventLoopGroup group =null;
    //    private volatile Channel channel = null;
    private volatile ChannelFuture channelFuture = null;
    private volatile Bootstrap bootstrap =null;
    private volatile boolean connected = false;
    //    private ClientHandler clientHandler = null;
    private  int retryTimes = 1000;
    private static int retryCount =0;

    private ServiceRegistry registor;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }



    public void close(){
        connected = false;
        if (null!=channelFuture){
            if (null!=channelFuture.channel() && channelFuture.channel().isOpen()){
                try {
                    channelFuture.channel().close();
                }catch (Exception e){
                    LOGGER.error(Thread.currentThread().getName()+"-closing");
                }
            }
            channelFuture = null;
        }


        if (group != null) {
            try {
                group.shutdownGracefully();
            } catch (Exception e) {
//                e.printStackTrace();
                LOGGER.error("RpcClient.close error :{}",e);
            }
            group = null;
            bootstrap = null;
        }
    }




    //启动服务
    public void start() throws InterruptedException {
        connected = false;
//        System.out.println("RpcClient.start");
        LOGGER.info("RpcClient开始启动服务,主动连接master");
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                             @Override
                             protected void initChannel(SocketChannel socketChannel) throws Exception {
                                 socketChannel.pipeline()
                                         .addFirst(new ChannelInboundHandlerAdapter() {
                                             @Override
                                             public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                                 super.channelInactive(ctx);
                                                 LOGGER.info("RpcClient channelInactive ... now try to reconnect "+ retryCount + " times...");
                                                 if (retryCount < retryTimes){
                                                     doConnect();
                                                 }  else {
                                                     doReconnect();
                                                 }
                                             }
//                              @Override
//                              public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//                                  LOGGER.error(Thread.currentThread().getName()+"client exceptionCaught cause:{}",cause);
//                                  ctx.channel().eventLoop().schedule(() -> doConnect(), 5, TimeUnit.SECONDS);
//                              }
                                         })
                                         .addLast(new IdleStateHandler(0,4,0, TimeUnit.SECONDS))
                                         .addLast(new RpcEncoder(RpcRequest.class))
                                         .addLast(new RpcDecoder<>(RpcResponse.class))
                                         .addLast(new ClientHandler());
                             }
                         }
                ).option(ChannelOption.TCP_NODELAY,true);
        doConnect();
    }

    public void doConnect() throws InterruptedException {
        if (connected)
            return;
        LOGGER.info("RpcClient开始连接master端,建立netty通信,host:{},port:{}",host,port);
        try {
            //在客户端启动时连接服务器ip和端口
            channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.channel().closeFuture().sync();
            /////////////////////////// 添加重连策略--added by HSF //////////////////////////////////////////////
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        LOGGER.info("connect success ...");
                        connected = true;
                    } else {
                        LOGGER.info("connect failed ...");
                        // future.channel().eventLoop().schedule(() -> doConnect(), 5, TimeUnit.SECONDS);
                        final EventLoop loop = future.channel().eventLoop();
                        loop.schedule(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    doConnect();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 5, TimeUnit.SECONDS);

                    }
                }
            });
            //这个地方即使不打印出日志，也不代表没有连接成功
            LOGGER.info("RpcClient连接master端成功，已建立netty通信");
        }catch (Exception e){
            //当master端死掉 client还活着的时候 会发生该异常
            LOGGER.error("RpcClient连接异常，尝试重连，请检查网络连接及master端是否存活" ,e);
            doReconnect();
        }
    }

    public void doReconnect() throws InterruptedException {
        close();
        LOGGER.info("RpcClient-连接master端异常，30s后尝试重连master端" );
        sleep(30 * 1000L);
        start();
    }


}
