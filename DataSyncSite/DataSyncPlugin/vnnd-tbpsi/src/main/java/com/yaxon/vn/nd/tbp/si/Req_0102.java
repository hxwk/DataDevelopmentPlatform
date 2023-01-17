package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 程行荣
 * Time: 2013-10-29 17:29
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 终端上线/下线请求
 */
public class Req_0102 extends JtsReqMsg {

    public static final String _id = "jts.0102.x";

    @Override
    public String id() {return _id;}

    public static final byte LOG_IN = 1;
    public static final byte LOG_OUT = 2;

    private byte logFlag = 0; //1，上线;2，下线
    private String authCode; //鉴权码，"上线"有用
    private String sim; //手机号

    public byte getLogFlag() {
        return logFlag;
    }

    public void setLogFlag(byte logFlag) {
        this.logFlag = logFlag;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    @Override
    public String toString() {
        return "Req_0102{" + super.toString() +
                ", logFlag=" + logFlag +
                ", authCode='" + authCode + '\'' +
                ", sim=" + sim +
                '}';
    }
}
