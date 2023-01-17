package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 崔伶俐
 * Time: 2014-11-11 09:59
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class Res_8B10 extends JtsResMsg{
    public static final byte RE_OK = 0x01;  //成功
    public static final byte RE_FAIL = 0x02; //失败
    public static final byte RE_Error = 0x03; //数据有误
    public static final byte RE_EXCEPTION = 0x04; //车台和屏之间通信异常
    public static final byte RE_NO_ACCESS = 0x05; //车台和屏连接断开或者没有连接

    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String id() { return "jts.8B10.r"; }


    public String getRcMsg(byte rc) {
        String rcMsg = "";
        switch (rc) {
            case Res_8B10.RE_OK:
                rcMsg = "发送成功";
                break;
            case Res_8B10.RE_FAIL:
                rcMsg = "发送失败";
                break;
            case Res_8B10.RE_Error:
                rcMsg = "数据有误";
                break;
            case Res_8B10.RE_EXCEPTION:
                rcMsg = "车台和屏之间通信异常";
                break;
            case Res_8B10.RE_NO_ACCESS:
                rcMsg = "车台和屏连接断开或者没有连接";
                break;
            default:
                rcMsg = "未知异常";
                break;
        }
        return rcMsg;
    }

    @Override
    public String toString() {
        return "Res_8B10{" + super.toString() + "}";

    }
}
