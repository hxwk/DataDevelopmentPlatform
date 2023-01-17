package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.Date;

/**
 * @author JianKang
 * @date 2018/3/2
 * @description
 * 驾驶员身份信息项
 */
public class DriverCardInforItem implements Serializable {
    /**
     * 唯一标识
     */
    private String id;
    /**
     * 消息Id
     */
    private String msgId;
    /**
     * 车辆唯一标识
     */
    private String vid;
    /**
     * 上下班状态 0x01-上班  0x02-下班   0x03-无上下班，老车台协议
     */
    private byte status;
    /**
     * 插卡/拔卡时间
     */
    private Date workDate;
    /**
     * IC 卡读取结果
     */
    private byte icResult;
    /**
     * IC 卡读取结果原因
     */
    private String icResultReason;
    /**
     * 驾驶员姓名
     */
    private String name;
    /**
     * 身份证编码即存的是从业资格证编码（20位）
     */
    private String IDCard;
    /**
     * 从业资格证编码（20位）
     */
    private String practitionerIdCard;
    /**
     * 发证机构名称
     */
    private String practitionerIdCardInstitution;
    /**
     * 证件有效期  YYYYMMDD
     */
    private Date validPeriod;
    /**
     * 平台接收时间
     */
    private long ptfReceiveTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

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

    public String getIcResultReason() {
        return icResultReason;
    }

    public void setIcResultReason(String icResultReason) {
        this.icResultReason = icResultReason;
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

    public long getPtfReceiveTime() {
        return ptfReceiveTime;
    }

    public void setPtfReceiveTime(long ptfReceiveTime) {
        this.ptfReceiveTime = ptfReceiveTime;
    }

    @Override
    public String toString() {
        return "DriverCardInforItem{" +
                "id=" + id +
                ", msgId=" + msgId +
                ", vid=" + vid +
                ", status=" + status +
                ", workDate=" + workDate +
                ", icResult=" + icResult +
                ", icResultReason=" + icResultReason +
                ", name=" + name +
                ", IDCard=" + IDCard +
                ", practitionerIdCard=" + practitionerIdCard +
                ", practitionerIdCardInstitution=" + practitionerIdCardInstitution +
                ", validPeriod=" + validPeriod +
                ", ptfReceiveTime=" + ptfReceiveTime +
                '}';
    }
}
