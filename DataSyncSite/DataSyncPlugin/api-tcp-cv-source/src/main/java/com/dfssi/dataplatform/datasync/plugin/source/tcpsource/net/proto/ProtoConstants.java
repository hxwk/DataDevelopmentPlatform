package com.dfssi.dataplatform.datasync.plugin.source.tcpsource.net.proto;


public class ProtoConstants {

    //协议消息ID定义
    public static final short TERMINAL_GENERAL_RES = (short) 0x0001; //终端通用应答
    public static final short CENTER_GENERAL_RES = (short) 0x8001; //平台通用应答
    public static final short TERMINAL_HEART_BEAT_REQ = (short) 0x0002; //终端心跳请求
    public static final short TERMINAL_REGISTER_REQ = (short) 0x0100; //终端注册请求
    public static final short TERMINAL_REGISTER_RES = (short) 0x8100; //终端注册应答
    public static final short TERMINAL_UNREGISTER_REQ = (short) 0x0003; //终端注销请求
    public static final short TERMINAL_AUTH_REQ = (short) 0x0102; //终端鉴权请求
    public static final short RESEND_PACK_REQ = (short) 0x8003; //补传分包请求


    //通用应答码
    public static final byte RC_OK = 0x00;  //成功/确认
    public static final byte RC_FAIL = 0x01;  //失败
    public static final byte RC_BAD_REQUEST = 0x02; //请求消息有误
    public static final byte RC_NOT_SUPPORT = 0x03; //不支持
    public static final byte RC_ALARM_ACK = 0x04; //报警确认


    //终端注册返回失败信息
    public static final byte LOGIN_FAIL_1 = 0x01; //车辆已被注册
    public static final byte LOGIN_FAIL_2 = 0x02; //数据库中无该车辆
    public static final byte LOGIN_FAIL_3 = 0x03; //终端已被注册
    public static final byte LOGIN_FAIL_4 = 0x04; //数据库中无该终端

    //消息处理结果
    public static final Integer PROCESS_FAIL = 0; //处理失败
    public static final Integer PROCESS_SUCCESS = 1; //处理成功
}
