package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 行驶记录数据上传 （2012版）
 * 采集记录仪状态信号配置信息
 */
public class Res_0700_06 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.06";
    }

    private Date realTime; //实时时间
    private List<StatusSignal> statusSignalItem = new ArrayList<StatusSignal>();

    private byte[] data;  //数据块（用于809协议）

    public Date getRealTime() {
        return realTime;
    }

    public void setRealTime(Date realTime) {
        this.realTime = realTime;
    }

    public List<StatusSignal> getStatusSignalItem() {
        return statusSignalItem;
    }

    public void setStatusSignalItem(List<StatusSignal> statusSignalItem) {
        this.statusSignalItem = statusSignalItem;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_06{" +
                "realTime=" + realTime +
                ", statusSignalItem=" + statusSignalItem +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
