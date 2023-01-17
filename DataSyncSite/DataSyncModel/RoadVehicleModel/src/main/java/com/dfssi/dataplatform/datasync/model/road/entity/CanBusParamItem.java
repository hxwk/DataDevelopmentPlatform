package com.dfssi.dataplatform.datasync.model.road.entity;

/**
 * Author: <JianKang>
 * Time: 2018-01-19 10:17
 */

import java.io.Serializable;

/**
 * CAN总线数据项
 */

public class CanBusParamItem implements Serializable {
    private String canId; //CAN ID
    private String canData; //CAN DATA

    public CanBusParamItem() {
    }

    public CanBusParamItem(String canId, String canData) {
        this.canId = canId;
        this.canData = canData;
    }

    public String getCanId() {
        return canId;
    }

    public void setCanId(String canId) {
        this.canId = canId;
    }

    @Override
    public String toString() {
        return "CanBusParamItem{" +
                "canId='" + canId + '\'' +
                ", canData='" + canData + '\'' +
                '}';
    }

    public String getCanData() {
        return canData;
    }

    public void setCanData(String canData) {
        this.canData = canData;
    }
}
