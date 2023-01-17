package com.yaxon.vn.nd.tbp.si;

import com.yaxon.vndp.dms.Message;

/**
 * Author: 程行荣
 * Time: 2013-10-29 20:42
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * （上行/下行）应答消息基类
 */
public abstract class JtsResMsg implements Message {
    /* 通用应答码定义 */
    public static final byte RC_OK = 0x00;  //成功/确认
    public static final byte RC_FAIL = 0x01;  //失败
    public static final byte RC_BAD_REQUEST = 0x02; //请求消息有误
    public static final byte RC_NOT_SUPPORT = 0x03; //不支持
    public static final byte RC_ALARM_ACK = 0x04; //报警确认
    public static final byte RC_ONLINE_ACK = 0x05; //车辆不在线
    public static final byte NE_RC_OK = 0x01;
    public static final byte NE_RC_FAIL = 0X02;

    /* 应答码 */
    protected byte rc;
    /* 错误信息 */
    protected String errMsg;

    /* 车辆ID */
    protected String vid;

    /* 车辆VIN */
    protected String vin;

    public String getVid() {
        return vid;
    }

    public JtsResMsg setVid(String vid) {
        this.vid = vid;
        return this;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public byte getRc() {
        return rc;
    }

    public void setRc(byte rc) {
        this.rc = rc;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "vid=" + vid + ", rc=" + rc + ", errMsg='" + errMsg + "'";
    }
}
