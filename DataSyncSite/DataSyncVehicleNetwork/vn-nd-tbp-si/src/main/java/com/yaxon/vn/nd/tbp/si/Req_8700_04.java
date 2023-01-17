package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 行驶记录数据采集命令 （2012版）
 * 采集记录仪脉冲系数
 */
public class Req_8700_04 extends JtsReqMsg {

    @Override
    public String id() {
        return "jts.8700.04";
    }

    private Byte cmd; //命令字


    public Byte getCmd() {
        return cmd;
    }

    public void setCmd(Byte cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "Req_8700_04{" +
                "cmd=" + cmd +
                '}';
    }
}
