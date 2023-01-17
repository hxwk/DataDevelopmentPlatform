package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * 行驶记录数据上传 （2012版）
 * 采集指定的超时驾驶记录
 */
public class Res_0700_11 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.11";
    }
    private byte[] data;  //数据块（用于809协议）

    private List<OvertimeDriving2012Item> overtimeDriving2012Item = new ArrayList<OvertimeDriving2012Item>();

    public List<OvertimeDriving2012Item> getOvertimeDriving2012Item() {
        return overtimeDriving2012Item;
    }

    public void setOvertimeDriving2012Item(List<OvertimeDriving2012Item> overtimeDriving2012Item) {
        this.overtimeDriving2012Item = overtimeDriving2012Item;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_11{" +
                "overtimeDriving2012Item=" + overtimeDriving2012Item +
                '}';
    }


}
