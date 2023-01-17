package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */


import java.util.Date;

/**
 * 行驶记录下传参数命令 （2012版）
 * 设置初始里程
 */
public class Req_8701_C4 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8701.C4";
    }

    private Byte cmd; //命令字
    private Date realTime; //记录仪实时时间
    private Date firstTime; //记录仪初次安装时间
    private Integer  initialMileage; //初始里程
    private Integer sumMileage; //累计行驶里程

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

    public Date getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(Date firstTime) {
        this.firstTime = firstTime;
    }

    public Integer getInitialMileage() {
        return initialMileage;
    }

    public void setInitialMileage(Integer initialMileage) {
        this.initialMileage = initialMileage;
    }

    public Integer getSumMileage() {
        return sumMileage;
    }

    public void setSumMileage(Integer sumMileage) {
        this.sumMileage = sumMileage;
    }

    @Override
    public String toString() {
        return "Req_8701_C4{" +
                "cmd=" + cmd +
                ", realTime=" + realTime +
                ", firstTime=" + firstTime +
                ", initialMileage=" + initialMileage +
                ", sumMileage=" + sumMileage +
                '}';
    }
}
