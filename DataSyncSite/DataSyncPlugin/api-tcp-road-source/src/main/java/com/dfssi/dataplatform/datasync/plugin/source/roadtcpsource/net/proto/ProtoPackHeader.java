package com.dfssi.dataplatform.datasync.plugin.source.roadtcpsource.net.proto;


/**
 * 协议分包
 */
public class ProtoPackHeader {
    public static final int PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT = 12;

    public short msgId;
    public short dataLen = 0;
    public byte cryptFlag = 0; //加密标志:0-不加密；1-RSA加密
    public byte splitFlag = 0; //分包标志:0-不分包；1-分包
    public String sim = ""; //终端手机号
    public short sn = 0; //流水号
    public int packCount = 0; //总分包数
    public int packIndex = 0; //分包索引，从1开始算
    public String vid = "";

    @Override
    public String toString() {
        return "ProtoPackHeader{" +
                "msgId=" + msgId +
                ", dataLen=" + dataLen +
                ", cryptFlag=" + cryptFlag +
                ", splitFlag=" + splitFlag +
                ", sim=" + sim +
                ", sn=" + sn +
                ", packCount=" + packCount +
                ", packIndex=" + packIndex +
                ", vid=" + vid +
                '}';
    }
}
