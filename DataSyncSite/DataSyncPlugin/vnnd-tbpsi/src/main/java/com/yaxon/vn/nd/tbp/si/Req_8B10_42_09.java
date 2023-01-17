package com.yaxon.vn.nd.tbp.si;


/**
 * 下发透传LED显示屏 （控制LED显示屏信息）
 */
public class Req_8B10_42_09 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8B104209";
    }

    private byte typeId; //设备类型
    private Short  dataType; //数据类型

    private byte controlType; //控制类型 0x04：清空LED顶灯显示内容
    private byte controlContent; //控制内容（只需要1字节）

    public byte getTypeId() {
        return typeId;
    }

    public void setTypeId(byte typeId) {
        this.typeId = typeId;
    }

    public Short getDataType() {
        return dataType;
    }

    public void setDataType(Short dataType) {
        this.dataType = dataType;
    }

    public byte getControlType() {
        return controlType;
    }

    public void setControlType(byte controlType) {
        this.controlType = controlType;
    }

    public byte getControlContent() {
        return controlContent;
    }

    public void setControlContent(byte controlContent) {
        this.controlContent = controlContent;
    }

    @Override
    public String toString() {
        return "Req_8B10_42_09{" +
                "typeId=" + typeId +
                ", dataType=" + dataType +
                ", controlType=" + controlType +
                ", controlContent=" + controlContent +
                '}';
    }
}
