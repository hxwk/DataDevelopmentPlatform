package com.yaxon.vn.nd.tbp.si;

import java.util.Date;

/**
 * Author: Sun Zhen
 * Time: 2014-01-20 14:44
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */
public class Req_0702Q extends JtsReqMsg{
    @Override
    public String id() {
        return "jts.0702Q";
    }

    private byte status; //上下班状态 0x01-上班  0x02-下班   0x03-无上下班，老车台协议
    private Date workDate; //插卡/拔卡时间
    private byte icResult; //IC 卡读取结果
    private String name; //驾驶员姓名
    private String IDCard; //身份证编码即存的是从业资格证编码（20位）
    private String practitionerIdCard; //从业资格证编码（20位）
    private String practitionerIdCardInstitution; //发证机构名称
    private Date validPeriod; //证件有效期  YYYYMMDD


    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public Date getWorkDate() {
        return workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public byte getIcResult() {
        return icResult;
    }

    public void setIcResult(byte icResult) {
        this.icResult = icResult;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPractitionerIdCard() {
        return practitionerIdCard;
    }

    public void setPractitionerIdCard(String practitionerIdCard) {
        this.practitionerIdCard = practitionerIdCard;
    }

    public String getPractitionerIdCardInstitution() {
        return practitionerIdCardInstitution;
    }

    public void setPractitionerIdCardInstitution(String practitionerIdCardInstitution) {
        this.practitionerIdCardInstitution = practitionerIdCardInstitution;
    }

    public Date getValidPeriod() {
        return validPeriod;
    }

    public void setValidPeriod(Date validPeriod) {
        this.validPeriod = validPeriod;
    }


    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    @Override
    public String toString() {
        return "Req_0702Q{" +
                "status=" + status +
                ", workDate=" + workDate +
                ", icResult=" + icResult +
                ", name='" + name + '\'' +
                ", IDCard='" + IDCard + '\'' +
                ", practitionerIdCard='" + practitionerIdCard + '\'' +
                ", practitionerIdCardInstitution='" + practitionerIdCardInstitution + '\'' +
                ", validPeriod=" + validPeriod +
                '}';
    }
}
