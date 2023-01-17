package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */


import java.util.Date;

/**
 * 行驶记录下传参数命令 （2012版）
 * 设置记录仪脉冲系数
 */
public class Req_8701_C3 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8701.C3";
    }

    private Byte cmd; //命令字
    private Date realTime; //实时时间
    private Integer impulseRatio; //脉冲系数

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

    public Integer getImpulseRatio() {
        return impulseRatio;
    }

    public void setImpulseRatio(Integer impulseRatio) {
        this.impulseRatio = impulseRatio;
    }
}
