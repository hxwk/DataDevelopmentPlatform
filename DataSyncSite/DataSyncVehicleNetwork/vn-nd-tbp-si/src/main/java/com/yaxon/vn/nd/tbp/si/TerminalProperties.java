package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 0107，终端属性应答消息体
 * @author hongShuai
 * @date 2018/07/16
 */
public class TerminalProperties implements Serializable {

    private List<String> terminalType;  //终端类型
    private String ManufacturerID;  //终端ID
    private String  TerminalModel;  //终端型号
    private String  TerminalID;  //终端ID
    private String  ICCID;//终端 SIM 卡 ICCID
    private int   THVNL;//终端硬件版本号长度(终端硬件版本号长度)
    private String TerminalHardwareVersionNumber; //终端硬件版本号
    private int  TFVNL;   //终端固件版本号长度 Terminal firmware version number length
    private String  TerminalFirmwareVersionNumber; //终端固件版本号 Terminal firmware version number
    private List<String> GNSS;//GNSS 模块属性
    private List<String>  CommunicationModuleAttribute;  //通信模块属性;

    public List<String> getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(List<String> terminalType) {
        this.terminalType = terminalType;
    }


    public String getManufacturerID() {
        return ManufacturerID;
    }

    public void setManufacturerID(String manufacturerID) {
        ManufacturerID = manufacturerID;
    }

    public String getTerminalModel() {
        return TerminalModel;
    }

    public void setTerminalModel(String terminalModel) {
        TerminalModel = terminalModel;
    }

    public String getTerminalID() {
        return TerminalID;
    }

    public void setTerminalID(String terminalID) {
        TerminalID = terminalID;
    }

    public String getICCID() {
        return ICCID;
    }

    public void setICCID(String ICCID) {
        this.ICCID = ICCID;
    }

    public int getTHVNL() {
        return THVNL;
    }

    public void setTHVNL(int THVNL) {
        this.THVNL = THVNL;
    }

    public String getTerminalHardwareVersionNumber() {
        return TerminalHardwareVersionNumber;
    }

    public void setTerminalHardwareVersionNumber(String terminalHardwareVersionNumber) {
        TerminalHardwareVersionNumber = terminalHardwareVersionNumber;
    }

    public int getTFVNL() {
        return TFVNL;
    }

    public void setTFVNL(int TFVNL) {
        this.TFVNL = TFVNL;
    }

    public String getTerminalFirmwareVersionNumber() {
        return TerminalFirmwareVersionNumber;
    }

    public void setTerminalFirmwareVersionNumber(String terminalFirmwareVersionNumber) {
        TerminalFirmwareVersionNumber = terminalFirmwareVersionNumber;
    }

    public List<String> getGNSS() {
        return GNSS;
    }

    public void setGNSS(List<String> GNSS) {
        this.GNSS = GNSS;
    }

    public List<String> getCommunicationModuleAttribute() {
        return CommunicationModuleAttribute;
    }

    public void setCommunicationModuleAttribute(List<String> communicationModuleAttribute) {
        CommunicationModuleAttribute = communicationModuleAttribute;
    }

    @Override
    public String toString() {
        return "terminalproperties{" +
                "terminalType='" + terminalType + '\'' +
                "ManufacturerID='" + ManufacturerID + '\''+
                "TerminalModel='" + TerminalModel + '\''+
                "TerminalID='" + TerminalID + '\''+
                "ICCID='" + ICCID + '\'' +
                "THVNL='" + THVNL + '\''+
                "TerminalHardwareVersionNumber='" + TerminalHardwareVersionNumber + '\''+
                "TFVNL='" + TFVNL + '\''+
                "TerminalFirmwareVersionNumber='" + TerminalFirmwareVersionNumber + '\'' +
                "GNSS='" + GNSS + '\''+
                "CommunicationModuleAttribute='" + CommunicationModuleAttribute + '\''+
                '}';
    }
}
