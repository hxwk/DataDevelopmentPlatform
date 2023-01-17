package com.yaxon.vn.nd.tas.net.udp;

import org.springframework.context.ApplicationContext;

/**
 * Author: 程行荣
 * Time: 2013-09-04 10:04
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class UdpChannelFactory {
    private static UdpChannel udpChannel = null;

    public static UdpChannel createUdpChannel(ApplicationContext applicationContext, UdpChannelConfig config) throws Exception {
        if (udpChannel == null) {
            udpChannel = new UdpChannel();
            udpChannel.init(applicationContext, config);
        }
        return udpChannel;
    }

    public static UdpChannel getUdpChannel() {
        return udpChannel;
    }

}
