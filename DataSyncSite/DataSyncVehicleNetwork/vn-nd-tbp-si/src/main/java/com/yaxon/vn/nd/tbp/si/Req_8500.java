package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 17:10
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */


/**
 * 车辆控制
 */
public class Req_8500 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8500"; }

    private byte flag; //控制标志

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "Req_8500{" + super.toString() +
                ", flag=" + flag +
                '}';
    }
}

