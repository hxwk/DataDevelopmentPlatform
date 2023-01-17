package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */


/**
 * 行驶记录下传参数命令 （2012版）
 * 设置状态量配置信息
 */
public class Req_8701_84 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8701.84";
    }

    private Byte cmd; //命令字
    private String d0; //D0的状态信号信息
    private String d1; //D1的状态信号信息
    private String d2; //D2的状态信号信息
    private String d3; //D3的状态信号信息
    private String d4; //D4的状态信号信息
    private String d5; //D5的状态信号信息
    private String d6; //D6的状态信号信息
    private String d7; //D7的状态信号信息

    public Byte getCmd() {
        return cmd;
    }

    public void setCmd(Byte cmd) {
        this.cmd = cmd;
    }

    public String getD0() {
        return d0;
    }

    public void setD0(String d0) {
        this.d0 = d0;
    }

    public String getD1() {
        return d1;
    }

    public void setD1(String d1) {
        this.d1 = d1;
    }

    public String getD2() {
        return d2;
    }

    public void setD2(String d2) {
        this.d2 = d2;
    }

    public String getD3() {
        return d3;
    }

    public void setD3(String d3) {
        this.d3 = d3;
    }

    public String getD4() {
        return d4;
    }

    public void setD4(String d4) {
        this.d4 = d4;
    }

    public String getD5() {
        return d5;
    }

    public void setD5(String d5) {
        this.d5 = d5;
    }

    public String getD6() {
        return d6;
    }

    public void setD6(String d6) {
        this.d6 = d6;
    }

    public String getD7() {
        return d7;
    }

    public void setD7(String d7) {
        this.d7 = d7;
    }

    @Override
    public String toString() {
        return "Req_8701_84{" +
                "cmd=" + cmd +
                ", d0='" + d0 + '\'' +
                ", d1='" + d1 + '\'' +
                ", d2='" + d2 + '\'' +
                ", d3='" + d3 + '\'' +
                ", d4='" + d4 + '\'' +
                ", d5='" + d5 + '\'' +
                ", d6='" + d6 + '\'' +
                ", d7='" + d7 + '\'' +
                '}';
    }
}
