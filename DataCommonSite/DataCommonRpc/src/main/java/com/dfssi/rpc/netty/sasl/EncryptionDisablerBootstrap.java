package com.dfssi.rpc.netty.sasl;

import com.dfssi.rpc.netty.client.TransportClient;
import com.dfssi.rpc.netty.client.TransportClientBootstrap;
import io.netty.channel.Channel;

public class EncryptionDisablerBootstrap implements TransportClientBootstrap {

    @Override
    public void doBootstrap(TransportClient client, Channel channel) {
      channel.pipeline().remove(SaslEncryption.ENCRYPTION_HANDLER_NAME);
    }

  }