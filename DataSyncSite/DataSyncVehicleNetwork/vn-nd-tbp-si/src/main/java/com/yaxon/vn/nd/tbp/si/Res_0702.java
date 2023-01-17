package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-04 16:45
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 驾驶员身份信息采集上报
 */
public class Res_0702 extends JtsResMsg {
    @Override
    public String id() {
        return "jts.0702";
    }

    private byte status; //驾驶员上班状态。0x01：上班； 0x02：下班
    private String time; //上下班时间，与status匹配使用

    //以下字段必须在status为0x01时才会有效并做填充
    private byte icResult; //ic卡读取结果
    private String name; //驾驶员姓名
    private String practitionerIdCard; //从业资格证编码
    private String practitionerIdCardInstitution; //从业资格证发证机构名称
    private String cardUsefulLife; //证件有效期


    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getCardUsefulLife() {
        return cardUsefulLife;
    }

    public void setCardUsefulLife(String cardUsefulLife) {
        this.cardUsefulLife = cardUsefulLife;
    }

    @Override
    public String toString() {
        return "Req_0702{" + super.toString() +
                ", status=" + status +
                ", time='" + time + '\'' +
                ", icResult=" + icResult +
                ", name='" + name + '\'' +
                ", practitionerIdCard='" + practitionerIdCard + '\'' +
                ", practitionerIdCardInstitution='" + practitionerIdCardInstitution + '\'' +
                ", cardUsefulLife='" + cardUsefulLife + '\'' +
                '}';
    }
}
