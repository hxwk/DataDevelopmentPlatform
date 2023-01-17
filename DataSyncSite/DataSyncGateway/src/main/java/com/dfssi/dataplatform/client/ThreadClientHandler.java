package com.dfssi.dataplatform.client;

import com.dfssi.dataplatform.model.ChannelManager;
import com.dfssi.dataplatform.server.ServerHandler;
import com.dfssi.dataplatform.util.PropertiesUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Map;
import java.util.Properties;

/**
 * Description
 *
 * @author bin.Y
 * @version 2018/10/6 20:07
 */
public class ThreadClientHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(ThreadClientHandler.class);
    private Channel channel;
    private String ipPort;//目的平台的IP，port
    private SocketAddress socketAddress;

    public void setMasterSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }
    public SocketAddress getMasterSocketAddress(){
        return socketAddress;
    }
    public String getIpPort(){
        return this.ipPort;
    }
    public ThreadClientHandler(String ipPort) {
        this.ipPort = ipPort;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
//        this.channel = ctx.channel();
        ChannelManager manager = new ChannelManager();
        manager.putClientListChannelFree(ipPort, ctx.channel());
        manager.putClientIpMap(ctx.channel().remoteAddress(),ipPort);
    }

    public void channelInactive(ChannelHandlerContext ctx)
            throws Exception {
        this.channel = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object protoMsg) throws Exception {
        System.out.println("进入client:" + ctx.channel().localAddress());
        ChannelManager manager = new ChannelManager();
        SocketAddress socketAddress = manager.getClientMasterRelationByKey(ctx.channel().remoteAddress());
        System.out.println("关系中master的地址:"+socketAddress);
        ChannelHandlerContext masterChannelMap = manager.getMasterChannelMapBykey(socketAddress);
        masterChannelMap.writeAndFlush((ByteBuf) protoMsg);
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
