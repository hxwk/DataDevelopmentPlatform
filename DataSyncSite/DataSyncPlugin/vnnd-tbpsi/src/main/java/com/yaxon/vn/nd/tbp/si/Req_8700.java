package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-04 14:53
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 行驶记录数据采集命令
 */
public class Req_8700 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8700";
    }

    private Byte orderWord; //命令字
    private String beginTime; //开始时间
    private String endTime; //结束时间
    private Short maxN;//最大单位数据块个数 (>=1)

    public Byte getOrderWord() {
        return orderWord;
    }

    public void setOrderWord(Byte orderWord) {
        this.orderWord = orderWord;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Short getMaxN() {
        return maxN;
    }

    public void setMaxN(Short maxN) {
        this.maxN = maxN;
    }

    @Override
    public String toString() {
        return "Req_8700{" + super.toString() +
                ", orderWord=" + orderWord +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", maxN=" + maxN +
                '}';
    }
}
