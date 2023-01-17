package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.tcp;

import com.dfssi.dataplatform.datasync.plugin.source.tcpsource.config.TcpChannelConfig;

/**
 * Author: 程行荣
 * Time: 2013-09-04 10:04
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

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
