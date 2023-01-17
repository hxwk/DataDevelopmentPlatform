package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 程行荣
 * Time: 2014-02-13 14:57
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 多媒体数据上传，分包(Subpackage)
 */
public class Req_0801_P extends JtsReqMsg {
    @Override
    public String id() {return "jts.0801.p";}

    private String sim; //手机号
    private short sn = 0; //流水号
    private int packCount = 0; //分包总数，>0
    private int packIndex = 0; //分包索引，从1开始算
    private byte[] data; //分包数据

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getPackIndex() {
        return packIndex;
    }

    public void setPackIndex(int packIndex) {
        this.packIndex = packIndex;
    }

    public int getPackCount() {
        return packCount;
    }

    public void setPackCount(int packCount) {
        this.packCount = packCount;
    }

    public short getSn() {
        return sn;
    }

    public void setSn(short sn) {
        this.sn = sn;
    }


    @Override
    public String toString() {
        return "Req_0801_P{" + super.toString() +
                ", sim=" + sim +
                ", sn=" + sn +
                ", packCount=" + packCount +
                ", packIndex=" + packIndex +
                ", data=#" + (data == null ? null : data.length) +
                '}';
    }
}
