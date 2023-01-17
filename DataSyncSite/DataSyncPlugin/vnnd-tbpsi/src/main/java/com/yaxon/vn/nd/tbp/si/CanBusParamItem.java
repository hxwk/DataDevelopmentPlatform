package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 10:17
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.io.Serializable;
import java.util.Arrays;

/**
 * CAN总线数据项
 */

public class CanBusParamItem implements Serializable {
    private byte[] canId; //CAN ID
    private byte[] canData; //CAN DATA

    public CanBusParamItem() {
    }

    public CanBusParamItem(byte[] canId, byte[] canData) {
        this.canId = canId;
        this.canData = canData;
    }

    public byte[] getCanId() {
        return canId;
    }

    public void setCanId(byte[] canId) {
        this.canId = canId;
    }

    public byte[] getCanData() {
        return canData;
    }

    public void setCanData(byte[] canData) {
        this.canData = canData;
    }

    @Override
    public String toString() {
        return "CanBusParamItem{" +
                "canId=" + Arrays.toString(canId) +
                ", canData=" + Arrays.toString(canData) +
                '}';
    }
}
