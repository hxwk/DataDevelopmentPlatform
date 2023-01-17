package com.yaxon.vn.nd.ne.tas.net.udp;

import org.apache.commons.lang3.Validate;

/**
 * Author: 程行荣
 * Time: 2013-11-13 19:16
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class UdpChannelConfig {
    private String nodeId;
    private String host;  //绑定主机ip
    private Integer port; //绑定端口
    private int ioThreads = Runtime.getRuntime().availableProcessors() * 2; //IO线程数
    private int requestTimeoutMillis = 60000; //下行请求超时时间
    private int receiveTimeoutMillisPerPack = 20000; //每个分包的接收超时时间
    private int terminalMaxIdleTimeMillis = 300000; //终端最大空闲时间（即不发任何数据的时间）
    private int maxBytesPerPack = 1023; //每个分包最大字节数
    private String configFile;
    // private Set<Short> skipPackMergeProtos = Sets.newHashSet(); //忽略分包合并的协议

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public int getMaxBytesPerPack() {
        return maxBytesPerPack;
    }

    public void setMaxBytesPerPack(int maxBytesPerPack) {
        this.maxBytesPerPack = maxBytesPerPack;
    }

    public int getTerminalMaxIdleTimeMillis() {
        return terminalMaxIdleTimeMillis;
    }

    public void setTerminalMaxIdleTimeMillis(int terminalMaxIdleTimeMillis) {
        this.terminalMaxIdleTimeMillis = terminalMaxIdleTimeMillis;
    }

    public int getReceiveTimeoutMillisPerPack() {
        return receiveTimeoutMillisPerPack;
    }

    public void setReceiveTimeoutMillisPerPack(int receiveTimeoutMillisPerPack) {
        this.receiveTimeoutMillisPerPack = receiveTimeoutMillisPerPack;
    }

    public int getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
    }

    public int getIoThreads() {
        return ioThreads;
    }

    public void setIoThreads(int ioThreads) {
        this.ioThreads = ioThreads;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public void checkConfig() throws IllegalArgumentException {
        try {
            Validate.isTrue(port > 0 && port <= 0xFFFF, "[port] 不在取值范围 (0,65535]: %d", port);
            Validate.isTrue(ioThreads > 0, "[ioThreads] 必须大于0: %d", ioThreads);
            Validate.isTrue(requestTimeoutMillis > 0, "[requestTimeoutMillis] 必须大于0: %d", requestTimeoutMillis);
            Validate.isTrue(receiveTimeoutMillisPerPack > 0, "[receiveTimeoutMillisPerPack] 必须大于0: %d", receiveTimeoutMillisPerPack);
            Validate.isTrue(terminalMaxIdleTimeMillis > 0, "[terminalMaxIdleTimeMillis] 必须大于0: %d", terminalMaxIdleTimeMillis);
            Validate.isTrue(maxBytesPerPack > 0 && maxBytesPerPack < 1024, "[maxBytesPerPack] 不在取值范围 (0,1024]: %d", maxBytesPerPack);
        } catch (Exception e) {
            throw new IllegalArgumentException("Udp接入服务器配置异常", e);
        }
    }

    @Override
    public String toString() {
        return "UdpChannelConfig{" +
                "nodeId='" + nodeId + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", ioThreads=" + ioThreads +
                ", requestTimeoutMillis=" + requestTimeoutMillis +
                ", receiveTimeoutMillisPerPack=" + receiveTimeoutMillisPerPack +
                ", terminalMaxIdleTimeMillis=" + terminalMaxIdleTimeMillis +
                ", maxBytesPerPack=" + maxBytesPerPack +
                ", configFile='" + configFile + '\'' +
                '}';
    }
}
