package com.yaxon.vn.nd.tbp.si;

/**
 * @author JianKang
 * 实时音视频传输请求指令
 */
public class Req_9101_nd extends JtsReqMsg{

    public static final String _id = "jts.9101.nd";

    @Override
    public String id() {
        return "jts.9101.nd";
    }

    private String sim;

    private byte ipLength;//ip长度
    private String ip;//ip
    private short tcpPort;//tcp port
    private short udpPort;//udp port
    private byte channelNo;//channel号
    private byte dataType;//数据类型
    private byte streamType;//码流类型

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public byte getIpLength() {
        return ipLength;
    }

    public void setIpLength(byte ipLength) {
        this.ipLength = ipLength;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public short getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(short tcpPort) {
        this.tcpPort = tcpPort;
    }

    public short getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(short udpPort) {
        this.udpPort = udpPort;
    }

    public byte getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(byte channelNo) {
        this.channelNo = channelNo;
    }

    public byte getDataType() {
        return dataType;
    }

    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }

    public byte getStreamType() {
        return streamType;
    }

    public void setStreamType(byte streamType) {
        this.streamType = streamType;
    }

    @Override
    public String toString() {
        return "Req_9101_nd{" +
                "sim='" + sim + '\'' +
                ", ipLength=" + ipLength +
                ", ip='" + ip + '\'' +
                ", tcpPort=" + tcpPort +
                ", udpPort=" + udpPort +
                ", channelNo=" + channelNo +
                ", dataType=" + dataType +
                ", streamType=" + streamType +
                '}';
    }
}
