package com.yaxon.vn.nd.tas.avs;

import com.yaxon.vn.nd.tas.net.proto.ProtoMsg;
import com.yaxon.vn.nd.tas.net.tcp.TcpChannel;
import com.yaxon.vn.nd.tas.net.tcp.TcpChannelFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: 程行荣
 * Time: 2014-05-08 10:13
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

@ChannelHandler.Sharable
public class AvsLinkHandler extends ChannelInboundHandlerAdapter {
    protected static Logger logger = LoggerFactory.getLogger(AvsLinkHandler.class);

    private AvsLink avsLink;
    private TcpChannel tcpChannel;

    public AvsLinkHandler(AvsLink avsLink) {
        this.avsLink = avsLink;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("[{}]视频服务器连接已建立", ctx.channel().remoteAddress());
        avsLink.connnected(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("[{}]视频服务器连接已断开", ctx.channel().remoteAddress());
        avsLink.disconnected();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warn("视频服务器连接异常，即将关闭视频服务器连接", cause);
        avsLink.closeConnect();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ProtoMsg m = (ProtoMsg) msg;
            avsLink.checkAlive();

            if (m.sim == "") {
                return;
            }

            m.msgType = 1;
            TcpChannel tc = tcpChannel();
            if (tc != null) {
                tc.sendMessage(m);
            } else {
                throw new ChannelException("Tcp通道获取失败");
            }
         } catch (Exception e) {
            logger.info("处理视频服务器的下行消息异常", e);
        }
    }

    private TcpChannel tcpChannel() {
        if (tcpChannel == null) {
            tcpChannel = TcpChannelFactory.getTcpChannel();
        }
        return tcpChannel;
    }


    /**
     * 发送视频服务器连接保持请求
     */
    protected ChannelFuture sendLinkTestReq(Channel channel) {
        if (logger.isDebugEnabled()) {
            logger.debug("<- 发送视频服务器连接保持请求..");
        }

        ProtoMsg msg = new ProtoMsg();
        msg.msgId = 0x0002;
        msg.sim = "";
        msg.sn = 0;
        msg.dataBuf = Unpooled.buffer(0);

        return sendMessage(channel, msg);
    }

    protected ChannelFuture sendMessage(Channel channel, ProtoMsg msg) {
        return channel.writeAndFlush(msg);
    }


}
