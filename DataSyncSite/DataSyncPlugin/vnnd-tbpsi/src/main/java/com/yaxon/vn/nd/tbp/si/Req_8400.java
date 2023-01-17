package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 16:52
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */


/**
 * 电话回拨
 */
public class Req_8400 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8400"; }

    private byte flag; //标志 0：普通通话 1：监听
    private String tel; //电话号码

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override
    public String toString() {
        return "Req_8400{" + super.toString() +
                ", flag=" + flag +
                ", tel='" + tel + '\'' +
                '}';
    }
}

