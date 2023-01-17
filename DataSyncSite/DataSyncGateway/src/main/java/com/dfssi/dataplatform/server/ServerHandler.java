package com.dfssi.dataplatform.server;

import com.dfssi.dataplatform.client.NettyClient;
import com.dfssi.dataplatform.client.ThreadClientHandler;
import com.dfssi.dataplatform.model.ChannelManager;
import com.dfssi.dataplatform.model.ClientBuild;
import com.dfssi.dataplatform.util.PropertiesUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * descripiton: 服务器的处理逻辑
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    Properties properties = PropertiesUtil.getProperties(PropertiesUtil.CONSUMER_SYNC_PROPERTIES);
//    private ChannelHandlerContext channelHandlerContext;
//
//    public ChannelHandlerContext getChannelHandlerContext() {
//        return this.channelHandlerContext;
//    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        int clientNum = Integer.parseInt(properties.getProperty("clientInstanceCountUse"));
        int channelPosition = (int) (Math.random() * (clientNum));
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        String s = socketAddress.toString();
        ChannelManager manager = new ChannelManager();
        List<Channel> clientListHandlerUse = manager.getClientListHandlerUse(socketAddress);
        Channel clientChannel = clientListHandlerUse.get(channelPosition);
        System.out.println("master推送的client地址："+clientChannel.remoteAddress());
        clientChannel.writeAndFlush((ByteBuf) msg);
    }

//    @Override
//    protected void channelRead0(ChannelHandlerContext channelHandlerContext) throws Exception {
//        logger.info("开始读取到链路上的信息，收到rpcRequst时，调用此方法");
//        logger.info("我是netty的数据网关，我现在通过已实例化的client向接入平台发送数据");
//
//    }


//
//    /**
//     * 处理新加的消息通道
//     *
//     * @param ctx
//     * @throws Exception
//     */
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        Channel channel = ctx.channel();
//        for (Channel ch : group) {
//            if (ch == channel) {
//
//            }
//        }
//        group.add(channel);
//    }
//
//    /**
//     * 处理退出消息通道
//     *
//     * @param ctx
//     * @throws Exception
//     */
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        Channel channel = ctx.channel();
//        for (Channel ch : group) {
//            if (ch == channel) {
//                ch.writeAndFlush("[" + channel.remoteAddress() + "] leaving");
//            }
//        }
//        group.remove(channel);
//    }

    /**
     * 在建立连接时发送消息
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        boolean active = channel.isActive();
//        String remoteHost = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
//        int remotePort = ((InetSocketAddress)ctx.channel().remoteAddress()).getPort();
//        String ipPort = remoteHost + ":" + remotePort;
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        System.out.println("运营商建立连接--socketAddress:"+socketAddress);
        String[] clientList = properties.getProperty("clientList").split(",");
        ChannelManager channelManager=new ChannelManager();
        //存储master的通道
        channelManager.putMasterChannelMap(socketAddress,ctx);
        //循环每台机器
        for (int i= 0; i< clientList.length; i++) {
            //跟进配置的IP获取实例化的clienthandler集合
            List<Channel> channels = channelManager.getClientListHandlerFree(clientList[i]);
            int clientInstanceCountUse = Integer.parseInt(properties.getProperty("clientInstanceCountUse"));
            //分配clientInstanceCountUse个实例的client给网关master
            for(int j=0;j<clientInstanceCountUse;j++){
                Channel channel1 = channels.remove(0);
                channelManager.putClientListChannelUse(socketAddress,channel1);
                //绑定客户端地址和服务端地址，维护到关系缓存
                channelManager.putClientMasterRelation(channel1.remoteAddress(),socketAddress);
            }
            if(channelManager.getClientListHandlerFree(clientList[i]).isEmpty()){
                channelManager.removeFreeClient((clientList[i]));
            }
        }
//        ChannelHandlerContext masterChannelMap = channelManager.getMasterChannelMap(socketAddress);
        Map<String, List<Channel>> clientListHandlerFree = channelManager.getClientListHandlerFree();
        if(clientListHandlerFree.isEmpty()){
            Thread thread = new Thread(){
                public void run(){
                    ClientBuild build = new ClientBuild();
                    build.instanceClient();
                    this.stop();
                }
            };
            thread.start();
        }


//        ClientBuild build=new ClientBuild();
//        build.instanceClient();
        if (active) {
            logger.info("[" + channel.remoteAddress() + "]netty网关 is online");
        } else {
            logger.info("[" + channel.remoteAddress() + "]netty网关 is offline");
        }
    }

    /**
     * 退出时发送消息
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        ChannelManager channelManager=new ChannelManager();
        List<Channel> clientListHandlerUse = channelManager.getClientListHandlerUse(socketAddress);
        for(int i=0;i<clientListHandlerUse.size();i++){
            Channel clientChannel = clientListHandlerUse.remove(0);
            channelManager.putClientListChannelFree(channelManager.getClientIpByKey( clientChannel.remoteAddress()),clientChannel);
        }
        //移除掉该master通道使用的client通道
        channelManager.removeUseClient(socketAddress);
        Channel channel = ctx.channel();
        if (!channel.isActive()) {
            logger.info("[" + channel.remoteAddress() + "]netty网关 is offline");
        } else {
            logger.info("[" + channel.remoteAddress() + "]netty网关 is online");
        }
    }

//    /**
//     * 异常捕获
//     *
//     * @param ctx
//     * @param e
//     * @throws Exception
//     */
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
//        Channel channel = ctx.channel();
//        logger.info("[" + channel.remoteAddress() + "]netty网关发生异常");
//        ctx.close().sync();
//    }

}