package com.dfssi.dataplatform.plugin.tcpnesource.net.tcp;

import com.dfssi.dataplatform.plugin.tcpnesource.config.TcpChannelConfig;

public class TcpChannelFactory {
    private static TcpChannel tcpChannel = null;

    public static TcpChannel createTcpChannel( TcpChannelConfig config) throws Exception {
        if (tcpChannel == null) {
            tcpChannel = new TcpChannel();
        }
        tcpChannel.init(config);
        return tcpChannel;
    }

    public static TcpChannel getTcpChannel() {
        return tcpChannel;
    }

}
