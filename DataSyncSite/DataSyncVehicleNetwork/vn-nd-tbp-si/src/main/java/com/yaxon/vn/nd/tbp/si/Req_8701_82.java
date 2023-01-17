package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */


import java.util.Date;

/**
 * 行驶记录下传参数命令 （2012版）
 * 设置车辆信息
 */
public class Req_8701_82 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8701.82";
    }

    private Byte cmd; //命令字
    private String vin; //车辆vin号：ASCII码字符
    private String lpn; //车牌号：ASCII码字符
    private String lpnType; //车牌分类

    public Byte getCmd() {
        return cmd;
    }

    public void setCmd(Byte cmd) {
        this.cmd = cmd;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public String getLpnType() {
        return lpnType;
    }

    public void setLpnType(String lpnType) {
        this.lpnType = lpnType;
    }


}
