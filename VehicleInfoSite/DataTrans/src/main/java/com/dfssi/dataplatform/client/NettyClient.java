package com.dfssi.dataplatform.client;

import com.dfssi.dataplatform.server.NettyServer;
import com.dfssi.dataplatform.util.IncreaseTimePeriod;
import com.dfssi.dataplatform.util.StringHelper;
import com.dfssi.dataplatform.util.msg.msgDecoder;
import com.dfssi.dataplatform.util.msg.msgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.apache.log4j.Logger;
//import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * descripiton: 客户端
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

    private boolean stop = false;


    //做开启和关闭 需要保存关闭对象的引用
    private volatile EventLoopGroup group = null;
    private volatile ChannelFuture channelFuture = null;
    private volatile Bootstrap bootstrap =null;
    private volatile boolean connected = false;
    //Channel channel = null;
    ClientHandler clientHandler = null;


    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        clientHandler = new ClientHandler();
    }

    public int start(){
        connected = false;
        logger.info("数据传输工程启动服务");
        //设置一个多线程循环器
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //启动附注类
        bootstrap = new Bootstrap();

        bootstrap.group(workerGroup);
        //指定所使用的NIO传输channel
        bootstrap.channel(NioSocketChannel.class);
        //指定客户端初始化处理
        //bootstrap.handler(new ClientIniterHandler(clientHandler));
        /*bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("decoder", new UserDecoder());
                pipeline.addLast("encoder", new UserEncoder());
                pipeline.addLast("handler", clientHandler);
            }
        });*/
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel socketChannel) throws Exception {
                         socketChannel.pipeline()
                                 .addFirst(new ChannelInboundHandlerAdapter() {
                                     @Override
                                     public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                         super.channelInactive(ctx);
                                     }
//                              @Override
//                              public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//                                  LOGGER.error(Thread.currentThread().getName()+"client exceptionCaught cause:{}",cause);
//                                  ctx.channel().eventLoop().schedule(() -> doConnect(), 5, TimeUnit.SECONDS);
//                              }
                                 })
                                 .addLast(new IdleStateHandler(0,4,0, TimeUnit.SECONDS))
                                 .addLast(new msgEncoder())
                                 .addLast(new msgDecoder())
                                 .addLast(clientHandler);
                     }
                 }
        ).option(ChannelOption.TCP_NODELAY,true);
       int res =  doConnect();
       return  res;
    }

    //停止服务，关闭连接 并且注销服务地址
    public void stop()  {
        logger.info("客户端首先停止，并关闭channel连接");
        connected = false;
        if (null!=channelFuture){
            if (null!=channelFuture.channel() && channelFuture.channel().isOpen()){
                try {
                    channelFuture.channel().close();
                }catch (Exception e){
                    logger.error(Thread.currentThread().getName()+"-closing");
                }
            }
            channelFuture = null;
        }

        /*Collection<Channel> channels = clientHandler.getChannelMap().values();
        try {
            channels.forEach((channel1 -> {
                if (channel1.isActive()){
                    channel1.close();
                }
            }));
        } catch (Exception e) {
            logger.error("客户端开始停止，关闭连接失败 error:{}",e);

        }*/
        if (group != null) {
            try {
                group.shutdownGracefully();
            } catch (Exception e) {
                logger.error("客户端开始停止，关闭连接失败 error:{}",e);

            }
        }
    }

    ////连接服务端
    public int doConnect(){
        if (connected){
            return 0;
        }
        logger.info("数据传输工程开始连接目的平台");
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();//服务端是bind  客户端是connect
            //channelFuture.channel().closeFuture().sync();
            //channel = future.channel();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        logger.info("数据传输工程连接目的平台成功，channelMap中添加channel成功！");
                        connected = true;
                    } else {
                        logger.info("数据传输工程连接目的平台失败，5S后尝试重连！");
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
        }catch (Exception e){
            logger.error("数据传输工程连接目的平台发送异常" ,e);
            //如果无法连接到对应的ip 端口会直接抛出异常
            int res = doReconnect();
            if(res == -1){
                logger.error("重新启动netty客户端向目的平台发送数据，仍无法与目的平台相连！现单次重连时间超过20min，关闭掉netty客户端，请检查网络通讯状况！");
                return  -1;
            }else{
                logger.info("重新启动netty客户端向目的平台发送数据与目的平台重连成功！");
                return 0;
            }
        }
        logger.info("数据传输工程连接目的平台未发现异常，需要在这里等待0.5S使channelMap中添加channel成功！");
        try {
            sleep(500);
        } catch (InterruptedException e) {
            logger.error("数据传输client客户端sleep异常，请检查系统环境 error :{}",e);
        }
        return 0;
    }


    /*  重连 -1代表重连失败 0点重连成功  重连还要保证channel的通道注册上即必须调用channelActive方法，因为后面向国家平台发送数据时必须依赖于这个判断*/
    public int doReconnect(){
        logger.info("数据传输client客户端开始重新连接目的平台");
        stop();
        int time = IncreaseTimePeriod.randomConnectPeriod();
        //int time = 5000;

        if(time == 20 * 60 * 1000){
            //logger.error("重新启动netty客户端向目的平台发送数据，仍无法与目的平台相连！现单次重连时间超过20min，关闭掉netty客户端，请检查网络通讯状况！");
            IncreaseTimePeriod.setNew();
            return -1;
        }
        logger.info("数据传输客户端进程休眠"+(time/1000)+"秒");
        try {
            sleep(time);
        } catch (InterruptedException e) {
            logger.error("数据传输client客户端sleep异常，请检查系统环境 error :{}",e);
        }
        start();
        //能够执行到这里就代表返回了0  即连接成功
        IncreaseTimePeriod.setNew();
        return 0;
    }




    //内部调用，故返回格式自定义
    public synchronized int sendMessageToServer(String msg){
        //客户端 ----》 目的平台
        //发送请求成功返回0
        //1、发送请求失败---单条消息发送失败返回-999(消息发送失败重试一次仍发送失败)
        //2、不能发送消息---不能与目的平台相连返回 -1
        //3、不能发送消息---无法根据目的平台的ip映射出连接目的平台的channel返回 -2
        //因为消息已经从kafka取出，取出的数据必须备份，另外无法发送的数据或者发送失败的数据必须有重发机制 如果某一条数据发送失败(网络传输，或目的平台异常)那么必须停下消费kafka的进程保证数据的完整性
        //取出kafkaTopic数据后透传数据过程中如果异常 首先重新启动netty客户端，重现尝试透传那一条失败的报文，重试1次(重启netty本身就是一个比较重的操作，基于netty的可靠性，如果两次都是传输异常则证明网络确实有问题)
        // 如果还是失败则保存下那条发送失败的报文，停掉netty客户端  返回-999  再停掉kafka消费者

        //更新 --所有的失败都重试  重试时间达到20min后就不再重试
       Map<String, Channel> map = clientHandler.getChannelMap();
        if(map.size() == 0){
            //发送失败情形1--没有channel连接
            logger.error("client端未发现与目的平台相连，无法传输数据，请检查目的平台的ip端口是否正确！");
            int time = IncreaseTimePeriod.randomConnectPeriod();
            //time = 5000;
            if(time == 20 * 60 * 1000){
                logger.error("重新启动netty客户端向目的平台发送数据，仍无法与目的平台相连！现单次重连时间超过20min，关闭掉netty客户端，请检查网络通讯状况！");
                //保存下来那条发送失败的报文数据，下次启动时先发送该条数据，如果成功再正常启动数据传输工程--todo
                stop();
                IncreaseTimePeriod.setNew();
                return -1;
            }
            logger.info("进程等待"+(time/1000)+"秒");
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int resConnect =  doReconnect();

            if(resConnect == 0) {
                logger.info("重连目的平台成功，因上次发送数据异常(未找到channel)，故重新发送数据！msg："+msg);
                int res = sendMessageToServer(msg);
                if(res == 0) {
                    logger.info("重新启动netty客户端向目的平台发送数据成功！msg："+msg);
                    return 0;
                }else{
                    sendMessageToServer(msg);
                }
            }else{
                logger.error("重新启动netty客户端多次尝试仍无法与目的平台相连！请检查网络通讯状况！");
            }
        }
        //发送失败情形2--有channel连接。但是无法根据目的平台的ip映射到对应的channel，仍无法发送数据
        Channel channel = map.get(host);
        //如果channel为null 那么则无法从clientIP映射出channel
        if (channel == null){
            logger.error("client端虽与服务器相连但无法根据目的平台的ip：{}映射出channel通道 ",host);
            int time = IncreaseTimePeriod.randomConnectPeriod();
            //time = 5000;
            if(time == 20 * 60 * 1000){
                logger.error("重新启动netty客户端向目的平台发送数据，仍无法根据目的平台的ip映射出channel通道！现单次重连时间超过20min，现关闭掉netty客户端，请检查网络通讯状况！");
                //保存下来那条发送失败的报文数据，下次启动时先发送该条数据，如果成功再正常启动数据传输工程--todo
                stop();
                IncreaseTimePeriod.setNew();
                return -2;
            }
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int resConnect =doReconnect();
            if(resConnect == 0) {
                logger.info("重连目的平台成功，因上次发送数据异常(无法根据目的平台的ip映射到对应的channel)，故重新发送数据！");
                int res = sendMessageToServer(msg);
                if(res == 0) {
                    logger.info("重新启动netty客户端向目的平台发送数据成功！");
                    return 0;
                }else{
                    sendMessageToServer(msg);
                }
            }else{
                logger.error("重新启动netty客户端多次尝试仍无法与目的平台相连！请检查网络通讯状况！");
            }
        }
        //正常发送数据情形
        try {
            //这个地方开始发送任务给client端
            logger.debug("开始向目的平台发送数据,msg:"+msg+"下一步：将16进制的表示的String转化成byte[]并发送");
            //channel.writeAndFlush(msg).sync();  会发生粘包拆包现象，多条消息一起发送(相隔几毫秒)会发生粘包，单条消息转成string后最大是260个字符 超过这个范围会发生拆包
            //改成非异步也还是会粘包，等待50毫秒测试一下  (100毫秒没问题，5毫秒会出现粘包现象)
            channel.writeAndFlush(msg);
            /*try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            //logger.info("等待50毫秒后，向目的平台发送数据成功");
        } catch (Exception e) {
            logger.error("client端开始发送数据到目的平台异常！",e);

            int time = IncreaseTimePeriod.randomConnectPeriod();
            //time = 5000;
            if(time == 20 * 60 * 1000){
                logger.error("重新启动netty客户端向目的平台发送数据异常！现单次重连时间超过20min，关闭掉netty客户端，请检查网络通讯状况！");
                //保存下来那条发送失败的报文数据，下次启动时先发送该条数据，如果成功再正常启动数据传输工程--todo
                stop();
                IncreaseTimePeriod.setNew();
                return -999;
            }
            try {
                Thread.sleep(time);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            doReconnect();
            int res = sendMessageToServer(msg);
            if(res == 0) {
                logger.info("重新启动netty客户端向目的平台发送数据成功！");
                return 0;
            }else{
                sendMessageToServer(msg);
            }
        }
        return 0;
    }

    /*private AtomicInteger connectPeriodTag = new AtomicInteger(0);

    private float connectTaskPeriod;

    private void randomConnectPeriod() {
        int start = (int) (Math.pow(2, connectPeriodTag.get()) * 1000);
        int end = (int) (Math.pow(2, connectPeriodTag.get() + 1) * 1000);
        connectTaskPeriod = RandomUtil.getRandom(start, end);
        if (connectTaskPeriod >= 20 * 60 * 1000) {
            connectTaskPeriod = 20 * 60 * 1000;
        } else {
            connectPeriodTag.incrementAndGet();
            SyncLogUtil.i("to increment the connectPeriodTag:" + connectPeriodTag.get() + ",connectTaskPeriod:" + connectTaskPeriod);
        }
        SyncLogUtil.i("connect task period is [" + TimeFormatUtil.format((long) connectTaskPeriod) + "]");
    }*/
}