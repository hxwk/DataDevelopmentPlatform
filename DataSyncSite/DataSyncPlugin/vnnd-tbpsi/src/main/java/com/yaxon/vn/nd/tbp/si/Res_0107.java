package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 08:58
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 查询终端属性应答
 */
public class Res_0107 extends JtsResMsg {
    @Override
    public String id() { return "jts.0107"; }

    private short terminalType; //终端类型
    private byte[] manufacturerId; //制造商ID
    private byte[] terminalModel; //终端型号
    private byte[] terminalId; //终端ID
    private String iccId; //终端sim卡ICCID
    private String terminalHardWareVersion; //终端硬件版本号
    private String terminalFirmWareVersion; //终端固件版本号
    private byte gNssAttr; //GNSS模块属性
    private byte communicateAttr; //通信模块属性

    public short getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(short terminalType) {
        this.terminalType = terminalType;
    }

    public byte[] getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(byte[] manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public byte[] getTerminalModel() {
        return terminalModel;
    }

    public void setTerminalModel(byte[] terminalModel) {
        this.terminalModel = terminalModel;
    }

    public byte[] getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(byte[] terminalId) {
        this.terminalId = terminalId;
    }

    public String getIccId() {
        return iccId;
    }

    public void setIccId(String iccId) {
        this.iccId = iccId;
    }

    public String getTerminalHardWareVersion() {
        return terminalHardWareVersion;
    }

    public void setTerminalHardWareVersion(String terminalHardWareVersion) {
        this.terminalHardWareVersion = terminalHardWareVersion;
    }

    public String getTerminalFirmWareVersion() {
        return terminalFirmWareVersion;
    }

    public void setTerminalFirmWareVersion(String terminalFirmWareVersion) {
        this.terminalFirmWareVersion = terminalFirmWareVersion;
    }

    public byte getgNssAttr() {
        return gNssAttr;
    }

    public void setgNssAttr(byte gNssAttr) {
        this.gNssAttr = gNssAttr;
    }

    public byte getCommunicateAttr() {
        return communicateAttr;
    }

    public void setCommunicateAttr(byte communicateAttr) {
        this.communicateAttr = communicateAttr;
    }

    @Override
    public String toString() {
        return "Res_0107{" + super.toString() +
                ", terminalType=" + terminalType +
                ", manufacturerId=" + Arrays.toString(manufacturerId) +
                ", terminalModel=" + Arrays.toString(terminalModel) +
                ", terminalId=" + Arrays.toString(terminalId) +
                ", iccId='" + iccId + '\'' +
                ", terminalHardWareVersion='" + terminalHardWareVersion + '\'' +
                ", terminalFirmWareVersion='" + terminalFirmWareVersion + '\'' +
                ", gNssAttr=" + gNssAttr +
                ", communicateAttr=" + communicateAttr +
                '}';
    }
}
