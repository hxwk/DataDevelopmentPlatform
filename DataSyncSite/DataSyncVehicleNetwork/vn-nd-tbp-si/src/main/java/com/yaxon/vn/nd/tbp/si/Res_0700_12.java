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
 * 采集指定的驾驶人身份记录
 */
public class Res_0700_12 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.12";
    }

    private List<DriverIdentity> driverIdentity2012Item = new ArrayList<DriverIdentity>();

    private byte[] data;  //数据块（用于809协议）

    public List<DriverIdentity> getDriverIdentity2012Item() {
        return driverIdentity2012Item;
    }

    public void setDriverIdentity2012Item(List<DriverIdentity> driverIdentity2012Item) {
        this.driverIdentity2012Item = driverIdentity2012Item;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_12{" +
                "driverIdentity2012Item=" + driverIdentity2012Item +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
