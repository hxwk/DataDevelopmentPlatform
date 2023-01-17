package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;


import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.Arrays;

/**
 * 下发透传LED显示屏
 */
public class Req_8B10 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8B10";
    }

    private byte typeId; //设备类型
    private Short  dataType; //数据类型
    private byte data[]; //数据包

    public Byte getTypeId() {
        return typeId;
    }

    public void setTypeId(Byte typeId) {
        this.typeId = typeId;
    }

    public Short getDataType() {
        return dataType;
    }

    public void setDataType(Short dataType) {
        this.dataType = dataType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Req_8B10{" +
                "typeId=" + typeId +
                ", dataType=" + dataType +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
