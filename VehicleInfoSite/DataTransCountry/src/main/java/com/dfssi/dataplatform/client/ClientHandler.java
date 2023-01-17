package com.dfssi.dataplatform.client;

import com.dfssi.dataplatform.server.NettyServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * descripiton: 客户端逻辑处理
 *
 * @author: www.iknowba.cn
 * @date: 2018/3/23
 * @time: 16:50
 * @modifier:
 * @since:
 */
//因为设计上传输过去只用了一个ClientHandler 通过该ClientHandler向对应的ip port发送数据 必须实时去校验该ClientHandler是否建立连接 因此重启机制上必须加上ChannelHandler.Sharable
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private Map<String, Channel> channelMap = new HashMap<>();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        //打印服务端的发送数据
        logger.info("(模拟返回，实际上不需要国家平台返回数据)服务端返回的数据："+s);
    }

    public  Map<String, Channel> getChannelMap(){
        logger.info("获取到client端的channelMap:"+channelMap.toString());
        return channelMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //以Netty 4.0.32.Final为例，在Channel注册EventLoop、绑定SocketAddress和连接ChannelFuture的时候都有可能会触发ChannelInboundHandler的channelActive方法的调用。
        super.channelActive(ctx);
        logger.debug("ClientHandler.channelActive");
        InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
        //维护channelMap信息
        logger.info("向channelMap中添加key:"+address.getAddress().getHostAddress()+"，value:{channel}");
        channelMap.put(address.getAddress().getHostAddress(),ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
        logger.debug("ClientHandler.channelInactive");
        ctx.channel().close();
        logger.info("向channelMap中删除key:"+address.getAddress().getHostAddress()+"，value:{channel}");
        channelMap.remove(address.getAddress().getHostAddress());
    }
}