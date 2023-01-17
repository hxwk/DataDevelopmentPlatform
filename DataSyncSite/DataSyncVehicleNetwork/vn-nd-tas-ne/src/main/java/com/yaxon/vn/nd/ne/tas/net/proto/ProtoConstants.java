package com.yaxon.vn.nd.ne.tas.net.proto;

/**
 * Author: 程行荣
 * Time: 2013-11-13 10:22
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class ProtoConstants {

    //协议消息ID定义
    public static final short TERMINAL_GENERAL_RES = (short) 0x0001; //终端通用应答
    public static final short CENTER_GENERAL_RES = (short) 0x8001; //平台通用应答
    public static final short TERMINAL_HEART_BEAT_REQ = (short) 0x0002; //终端心跳请求
    public static final short TERMINAL_REGISTER_REQ = (short) 0x01; //终端注册请求
    public static final short TERMINAL_REGISTER_RES = (short) 0x81; //终端注册应答
    public static final short TERMINAL_UNREGISTER_REQ = (short) 0x0003; //终端注销请求
    public static final short TERMINAL_AUTH_REQ = (short) 0x0102; //终端鉴权请求
    public static final short RESEND_PACK_REQ = (short) 0x8003; //补传分包请求


    //通用应答码
    public static final byte RC_OK = 0x01;  //成功/确认
    public static final byte RC_FAIL = 0x02;  //失败

    public static final byte RC_BAD_REQUEST = 0x04; //请求消息有误
    public static final byte RC_NOT_SUPPORT = 0x05; //不支持
}
