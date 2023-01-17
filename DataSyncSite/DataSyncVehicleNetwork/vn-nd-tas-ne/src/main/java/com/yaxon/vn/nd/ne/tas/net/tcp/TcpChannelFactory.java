package com.yaxon.vn.nd.ne.tas.net.tcp;

import org.springframework.context.ApplicationContext;

/**
 * Author: 程行荣
 * Time: 2013-09-04 10:04
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class TcpChannelFactory {
    private static TcpChannel tcpChannel = null;

    public static TcpChannel createTcpChannel(ApplicationContext applicationContext, TcpChannelConfig config) throws Exception {
        if (tcpChannel == null) {
            tcpChannel = new TcpChannel();
            tcpChannel.init(applicationContext, config);
        }
        return tcpChannel;
    }

    public static TcpChannel getTcpChannel() {
        return tcpChannel;
    }

}
