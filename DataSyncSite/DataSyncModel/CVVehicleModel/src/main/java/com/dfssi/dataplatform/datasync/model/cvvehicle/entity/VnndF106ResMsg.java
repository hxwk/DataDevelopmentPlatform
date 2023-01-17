package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;


import com.dfssi.dataplatform.datasync.model.common.VnndResMsg;

import java.util.List;

/**
 * 基类
 */
public class VnndF106ResMsg extends VnndResMsg {

    public String id() {
        return "jts.F106.nd.r";
    }
    private String serialNumber; //流水号
    private int paramNum;  //应答参数个数
    private List<Item> plist;  //参数列表

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getParamNum() {
        return paramNum;
    }

    public void setParamNum(int paramNum) {
        this.paramNum = paramNum;
    }

    public List<Item> getPlist() {
        return plist;
    }

    public void setPlist(List<Item> plist) {
        this.plist = plist;
    }
    @Override
    public String toString() {
        return "Res_F006_nd{" + super.toString() +
                ",serialNumber=" + serialNumber +
                ",paramNum=" + paramNum +
                ",plist=" + plist +
                '}';
    }
}
