package com.dfssi.dataplatform.datasync.model.newen.entity;

import com.dfssi.dataplatform.datasync.model.common.BaseMessage;

/**
 * 车辆登入实体
 * Created by Hannibal on 2018-04-08.
 */

public class Req_01 extends BaseMessage {

    private String vin;// vin号码

    private Long loginTime;//平台登入时间

    private Short sn;//平台登入流水号

    private String iccid;//车辆iccid

    private byte chargeableChildSystemNum;//可充电储能子系统熟

    private byte chargeableSystemCodeLength; //可充电储能系统编码长度

    private String chargeableSystemCode;//可充电储能系统编码

    private String vehicleType;//车型信息

    private Integer status;//登入状态，0：失败，1：成功

    private String msgId;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public Short getSn() {
        return sn;
    }

    public void setSn(Short sn) {
        this.sn = sn;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getMsgId() {
        msgId = "32960_0" + this.getCommandSign();
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public byte getChargeableChildSystemNum() {
        return chargeableChildSystemNum;
    }

    public void setChargeableChildSystemNum(byte chargeableChildSystemNum) {
        this.chargeableChildSystemNum = chargeableChildSystemNum;
    }

    public byte getChargeableSystemCodeLength() {
        return chargeableSystemCodeLength;
    }

    public void setChargeableSystemCodeLength(byte chargeableSystemCodeLength) {
        this.chargeableSystemCodeLength = chargeableSystemCodeLength;
    }

    public String getChargeableSystemCode() {
        return chargeableSystemCode;
    }

    public void setChargeableSystemCode(String chargeableSystemCode) {
        this.chargeableSystemCode = chargeableSystemCode;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Req_01{" +
                "vin='" + vin + '\'' +
                ", loginTime=" + loginTime +
                ", sn=" + sn +
                ", msgId=" + getMsgId() +
                ", iccid='" + iccid + '\'' +
                ", chargeableChildSystemNum=" + chargeableChildSystemNum +
                ", chargeableSystemCodeLength=" + chargeableSystemCodeLength +
                ", chargeableSystemCode='" + chargeableSystemCode + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", vehicleCompany='" + super.getVehicleCompany() + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public String id() {
        return "jtsne.01";
    }
}
