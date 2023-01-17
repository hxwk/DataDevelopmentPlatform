package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */


import java.util.Date;

/**
 * 行驶记录下传参数命令 （2012版）
 * 设置记录仪时间
 */
public class Req_8701_C2 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8701.C2";
    }

    private Byte cmd; //命令字
    private Date realTime; //设置记录仪实时时间

    public Byte getCmd() {
        return cmd;
    }

    public void setCmd(Byte cmd) {
        this.cmd = cmd;
    }

    public Date getRealTime() {
        return realTime;
    }

    public void setRealTime(Date realTime) {
        this.realTime = realTime;
    }

    @Override
    public String toString() {
        return "Req_8701_C2{" +
                "cmd=" + cmd +
                ", realTime=" + realTime +
                '}';
    }
}
