package com.dfssi.dataplatform.plugin.tcpnesource.net.proto;

import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;


public class SplitPackItem {
    public int packCount = 0; //总包数
    public long timeout; //接收到第一包数据的时间
    public byte resend = 0; //发起重传次数

    public SplitPackItem(int total, long timeout) {
        this.packCount = total;
        this.timeout = timeout;
    }

    final public ConcurrentMap<Integer, byte[]> packs = Maps.newConcurrentMap();
}
