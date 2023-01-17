package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 外接外围设备LED屏应答
 */
public class Req_0B10 extends JtsReqMsg{

    @Override
    public String id() {
        return "jts.0B10";
    }

    private byte typeId; // 设备类型
    private byte manufacturerMark; //厂商标识
    private short cmdType; //命令类型
    private byte[] data;  //数据块

    public byte getTypeId() {
        return typeId;
    }

    public void setTypeId(byte typeId) {
        this.typeId = typeId;
    }

    public byte getManufacturerMark() {
        return manufacturerMark;
    }

    public void setManufacturerMark(byte manufacturerMark) {
        this.manufacturerMark = manufacturerMark;
    }

    public short getCmdType() {
        return cmdType;
    }

    public void setCmdType(short cmdType) {
        this.cmdType = cmdType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Req_0B10{" +
                "typeId=" + typeId +
                ", manufacturerMark=" + manufacturerMark +
                ", cmdType=" + cmdType +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
