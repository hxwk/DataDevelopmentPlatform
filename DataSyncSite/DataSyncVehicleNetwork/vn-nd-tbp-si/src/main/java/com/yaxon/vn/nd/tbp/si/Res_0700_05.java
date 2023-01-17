package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 行驶记录数据上传 （2012版）
 * 采集车辆信息
 */
public class Res_0700_05 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.05";
    }

    private String vin; //车辆vin号：ASCII码字符
    private String lpn; //车牌号：ASCII码字符
    private String lpnType; //车牌分类

    private byte[] data;  //数据块（用于809协议）

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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_05{" +
                "vin='" + vin + '\'' +
                ", lpn='" + lpn + '\'' +
                ", lpnType='" + lpnType + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
