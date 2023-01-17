package com.dfssi.dataplatform.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * descripiton: 服务器的处理逻辑
 *
 * @author: www.iknowba.cn
 * @date: 2018/3/23
 * @time: 15:50
 * @modifier:
 * @since:
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    /**
     * 所有的活动用户
     */
    public static final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 读取消息通道
     *
     * @param context
     * @param s
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext context, String s)
            throws Exception {
        Channel channel = context.channel();
        //当有用户发送消息的时候，对其他的用户发送消息
        for (Channel ch : group) {
            /*if (ch == channel) {
                System.out.println("我是国家平台，我现在通过channle"+"[" + channel.remoteAddress() + "]向你发送消息--->" + s + "\n");
                ch.writeAndFlush("我是国家平台，我现在通过channle"+"[" + channel.remoteAddress() + "]向你发送消息--->" + s + "\n");
            } else {
                System.out.println("我是国家平台，我现在通过channle"+"[" + channel.remoteAddress() + "]向你发送消息--->" + s + "\n");
                ch.writeAndFlush("我是国家平台，我现在通过channle"+"[" + channel.remoteAddress() + "]向你发送消息--->" + s + "\n");
            }*/
            logger.info("我是国家平台，我现在通过channle"+"[" + channel.remoteAddress() + "]向你发送消息--->" + s );
            ch.writeAndFlush("我是国家平台，我现在通过channle"+"[" + channel.remoteAddress() + "]向你发送消息--->" + s + "\n");

        }
        //System.out.println("[" + channel.remoteAddress() + "]: 我是国家平台" + s + "\n");
    }

    /**
     * 处理新加的消息通道
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        for (Channel ch : group) {
            if (ch == channel) {
                ch.writeAndFlush("[" + channel.remoteAddress() + "] coming");
            }
        }
        group.add(channel);
    }

    /**
     * 处理退出消息通道
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        for (Channel ch : group) {
            if (ch == channel) {
                ch.writeAndFlush("[" + channel.remoteAddress() + "] leaving");
            }
        }
        group.remove(channel);
    }

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
        if (active) {
            logger.info("[" + channel.remoteAddress() + "] is online");
        } else {
            logger.info("[" + channel.remoteAddress() + "] is offline");
        }
        ctx.writeAndFlush("[server]: welcome");
    }

    /**
     * 退出时发送消息
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if (!channel.isActive()) {
            logger.info("[" + channel.remoteAddress() + "] is offline");
        } else {
            logger.info("[" + channel.remoteAddress() + "] is online");
        }
    }

    /**
     * 异常捕获
     *
     * @param ctx
     * @param e
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        Channel channel = ctx.channel();
        logger.info("[" + channel.remoteAddress() + "] leave the room");
        ctx.close().sync();
    }

}