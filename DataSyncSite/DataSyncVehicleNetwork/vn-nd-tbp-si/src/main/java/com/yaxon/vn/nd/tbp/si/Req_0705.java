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

    private String dbcType;

    private String sim;

    private String canBusDataReceiveTime; //CAN总线数据接收时间
    private List<CanAnalyzeSignal> canBusParamItems; //CAN总线数据项列表

    public String getCanBusDataReceiveTime() {
        return canBusDataReceiveTime;
    }

    public void setCanBusDataReceiveTime(String canBusDataReceiveTime) {
        this.canBusDataReceiveTime = canBusDataReceiveTime;
    }

    public List<CanAnalyzeSignal> getCanBusParamItems() {
        return canBusParamItems;
    }

    public void setCanBusParamItems(List<CanAnalyzeSignal> canBusParamItems) {
        this.canBusParamItems = canBusParamItems;
    }

    public String getDbcType() {
        return dbcType;
    }

    public void setDbcType(String dbcType) {
        this.dbcType = dbcType;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    @Override
    public String toString() {
        return "Req_0705{" +
                "dbcType='" + dbcType + '\'' +
                ", sim='" + sim + '\'' +
                ", canBusDataReceiveTime='" + canBusDataReceiveTime + '\'' +
                ", canBusParamItems=" + canBusParamItems +
                '}';
    }
}
