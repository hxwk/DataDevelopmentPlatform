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
 * 采集指定的速度状态日志
 */
public class Res_0700_15 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.15";
    }

    private List<SpeedStatus2012Item> speedStatus2012Item = new ArrayList<SpeedStatus2012Item>();

    private byte[] data;  //数据块（用于809协议）

    public List<SpeedStatus2012Item> getSpeedStatus2012Item() {
        return speedStatus2012Item;
    }

    public void setSpeedStatus2012Item(List<SpeedStatus2012Item> speedStatus2012Item) {
        this.speedStatus2012Item = speedStatus2012Item;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_15{" +
                "speedStatus2012Item=" + speedStatus2012Item +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
