package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;

/**
 * Author: zhengchaoyuan
 * Time: 2017-08-30 17:43
 * Copyright (C) 2017 Xiamen Yaxon Networks CO.,LTD.
 */
public class Res_8106_nd extends JtsResMsg {
    @Override
    public String id() {
        return "jts.8106.nd.r";
    }
    private int ip1;
    private int ip2;

    public int getIp1() {
        return ip1;
    }

    public void setIp1(int ip1) {
        this.ip1 = ip1;
    }

    public int getIp2() {
        return ip2;
    }

    public void setIp2(int ip2) {
        this.ip2 = ip2;
    }

    @Override
    public String toString() {
        return "Res_8106_nd{" + super.toString() +
                "ip1=" + ip1 +
                ", ip2=" + ip2 +
                '}';
    }
}
