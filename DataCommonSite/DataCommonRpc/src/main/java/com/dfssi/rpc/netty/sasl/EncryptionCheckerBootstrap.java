package com.dfssi.rpc.netty.sasl;

import com.dfssi.rpc.netty.server.RpcHandler;
import com.dfssi.rpc.netty.server.TransportServerBootstrap;
import io.netty.channel.*;

@ChannelHandler.Sharable
public class EncryptionCheckerBootstrap extends ChannelOutboundHandlerAdapter
    implements TransportServerBootstrap {

    boolean foundEncryptionHandler;
    String encryptHandlerName;

    public EncryptionCheckerBootstrap(String encryptHandlerName) {
      this.encryptHandlerName = encryptHandlerName;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
      if (!foundEncryptionHandler) {
        foundEncryptionHandler =
          ctx.channel().pipeline().get(encryptHandlerName) != null;
      }
      ctx.write(msg, promise);
    }

    @Override
    public RpcHandler doBootstrap(Channel channel, RpcHandler rpcHandler) {
        channel.pipeline().addFirst("encryptionChecker", this);
        return rpcHandler;
    }

  }