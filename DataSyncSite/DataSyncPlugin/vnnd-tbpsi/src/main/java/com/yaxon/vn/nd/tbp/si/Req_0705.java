package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 10:13
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.List;

/**
 * CAN总线数据上传
 */
public class Req_0705 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.0705";
    }

    private String canBusDataReceiveTime; //CAN总线数据接收时间
    private List<ParamItem> canBusParamItems; //CAN总线数据项列表

    public String getCanBusDataReceiveTime() {
        return canBusDataReceiveTime;
    }

    public void setCanBusDataReceiveTime(String canBusDataReceiveTime) {
        this.canBusDataReceiveTime = canBusDataReceiveTime;
    }

    public List<ParamItem> getCanBusParamItems() {
        return canBusParamItems;
    }

    public void setCanBusParamItems(List<ParamItem> canBusParamItems) {
        this.canBusParamItems = canBusParamItems;
    }

    @Override
    public String toString() {
        return "Req_0705{" + super.toString() +
                "canBusDataReceiveTime='" + canBusDataReceiveTime + '\'' +
                ", canBusParamItems=" + canBusParamItems +
                '}';
    }
}
