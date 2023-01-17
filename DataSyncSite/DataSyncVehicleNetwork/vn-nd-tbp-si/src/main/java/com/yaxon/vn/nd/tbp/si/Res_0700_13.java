package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 行驶记录数据上传 （2012版）
 * 采集指定的外部供电记录
 */
public class Res_0700_13 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.13";
    }

    private List<DRPowerSupply> dRPowerSupplyItem = new ArrayList<DRPowerSupply>();

    private byte[] data;  //数据块（用于809协议）

    public List<DRPowerSupply> getdRPowerSupplyItem() {
        return dRPowerSupplyItem;
    }

    public void setdRPowerSupplyItem(List<DRPowerSupply> dRPowerSupplyItem) {
        this.dRPowerSupplyItem = dRPowerSupplyItem;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_13{" +
                "dRPowerSupplyItem=" + dRPowerSupplyItem +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
