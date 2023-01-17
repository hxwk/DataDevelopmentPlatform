package com.dfssi.dataplatform.datasync.plugin.interceptor.bean;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * CAN总线数据上传
 * @author jianKang
 * @date 2017/12/18
 */
public class Req_0705 extends JtsReqMsg {
    public String id() {
        return "0705";
    }

    private int numOfItem=0;

    /**
     * CAN总线数据接收时间
     */
    private String canBusDataReceiveTime;

    /**
     * CAN总线数据项列表
     */
    private List<CanBusParamItem> canBusParamItems;

    public String getCanBusDataReceiveTime() {
        return canBusDataReceiveTime;
    }

    public void setCanBusDataReceiveTime(String canBusDataReceiveTime) {
        this.canBusDataReceiveTime = canBusDataReceiveTime;
    }

    public List<CanBusParamItem> getCanBusParamItems() {
        return canBusParamItems;
    }

    public void setCanBusParamItems(List<CanBusParamItem> canBusParamItems) {
        this.canBusParamItems = canBusParamItems;
    }

    public int getNumOfItem() {
        return numOfItem;
    }

    public void setNumOfItem(int numOfItem) {
        this.numOfItem = numOfItem;
    }

    @Override
    public String toString() {
        /*return "Req_0705[" + super.toString() +
                "CAN numOfItem= "+ numOfItem + '\'' +
                "canBusDataReceiveTime='" + canBusDataReceiveTime + '\'' +
                ", canBusParamItems=" + canBusParamItems +
                ']';*/
        return JSON.toJSONString(this);

    }
}
