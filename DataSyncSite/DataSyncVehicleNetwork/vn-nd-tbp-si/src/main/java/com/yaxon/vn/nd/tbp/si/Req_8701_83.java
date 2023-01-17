package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */


import java.util.Date;

/**
 * 行驶记录下传参数命令 （2012版）
 * 设置记录仪初次安装日期
 */
public class Req_8701_83 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8701.83";
    }

    private Byte cmd; //命令字
    private Date firstTime; //设置记录仪初次安装时间

    public Byte getCmd() {
        return cmd;
    }

    public void setCmd(Byte cmd) {
        this.cmd = cmd;
    }

    public Date getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(Date firstTime) {
        this.firstTime = firstTime;
    }

    @Override
    public String toString() {
        return "Req_8701_83{" +
                "cmd=" + cmd +
                ", firstTime=" + firstTime +
                '}';
    }
}
