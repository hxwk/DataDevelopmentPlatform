package com.dfssi.dataplatform.server;

import com.dfssi.dataplatform.client.NettyClient;
import com.dfssi.dataplatform.model.ClientBuild;
import com.dfssi.dataplatform.util.PropertiesUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * netty数据网关，将一个链路变为多个链路
 *
 * @author: www.iknowba.cn
 * @date: 2018/3/23
 * @time: 15:37
 * @modifier:
 * @since:
 */
public class NettyServer {
    private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
    /**
     * 端口
     */
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void run() {

        //EventLoopGroup是用来处理IO操作的多线程事件循环器
        //负责接收客户端连接线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //负责处理客户端i/o事件、task任务、监听任务组
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //启动 NIO 服务的辅助启动类
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        //配置 Channel
        bootstrap.channel(NioServerSocketChannel.class);
        MasterLogger logger = new MasterLogger();
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline()
                        .addLast(new ProtoDecoder(), new ProtoEncoder())
                        .addLast(new ServerHandler()); // 处理 RPC 请求
            }
        });
        //BACKLOG用于构造服务端套接字ServerSocket对象，
        // 标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        //是否启用心跳保活机制
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            Channel channel = bootstrap.bind(port).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //关闭事件流组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        Properties properties = PropertiesUtil.getProperties(PropertiesUtil.CONSUMER_SYNC_PROPERTIES);
////        int clientNum = Integer.parseInt(properties.getProperty("clientNum"));
//        int clientInstanceCount = Integer.parseInt(properties.getProperty("clientInstanceCount"));
//        String[] clientList = properties.getProperty("clientList").split(",");
//        //循环每台机器
//        for (int j = 0; j < clientList.length; j++) {
//            String ip = clientList[j].split(":")[0];
//            int port = Integer.parseInt(clientList[j].split(":")[1]);
//            //每台机器创建clientInstanceCount个连接实例
//            for (int i = 0; i < clientInstanceCount; i++) {
//                NettyClient client = new NettyClient(ip, port);
//                client.start();
//            }
//        }
        ClientBuild build=new ClientBuild();
        build.instanceClient();
        new NettyServer(Integer.parseInt(properties.getProperty("serverPort"))).run();
    }
}