package com.yaxon.vn.nd.ne.tas.net.proto;

/**
 * Author: 程行荣
 * Time: 2013-11-07 10:27
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 协议分包
 */
public class ProtoPackHeader {
    public static final int PROTO_PACK_HEADER_LEN_WITHOUT_SPLIT = 22;

    public short msgId;
    public short dataLen = 0;
    public byte cryptFlag = 0; //加密标志:0-不加密；1-RSA加密
    public byte splitFlag = 0; //分包标志:0-不分包；1-分包
    public byte commandSign;
    public byte answerSign;
    public String sim = ""; //终端手机号
    public String vin = ""; //车架号
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
                ", commandSign=" + commandSign +
                ", answerSign=" + answerSign +
                ", sim=" + sim +
                ", vin=" + vin +
                ", sn=" + sn +
                ", packCount=" + packCount +
                ", packIndex=" + packIndex +
                ", vid=" + vid +
                '}';
    }
}

