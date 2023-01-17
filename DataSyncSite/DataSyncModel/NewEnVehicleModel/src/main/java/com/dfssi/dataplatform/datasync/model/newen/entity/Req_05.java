package com.dfssi.dataplatform.datasync.model.newen.entity;

import com.dfssi.dataplatform.datasync.model.common.BaseMessage;

/**
 * 平台登入实体
 * Created by Hannibal on 2018-04-08.
 */

public class Req_05 extends BaseMessage {

    private String vin;// vin号码

    private Long loginTime;//平台登入时间

    private Short sn;//平台登入流水号

    private String username;//平台用户名

    private String password;//平台密码

    private byte encryptRule; //加密规则

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte getEncryptRule() {
        return encryptRule;
    }

    public void setEncryptRule(byte encryptRule) {
        this.encryptRule = encryptRule;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsgId() {
        msgId = "32960_0" + this.getCommandSign();
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "Req_05{" +
                "vin='" + vin + '\'' +
                ", commandSign='" + super.getCommandSign() + '\'' +
                ", vehicleCompany='" + super.getVehicleCompany() + '\'' +
                ", loginTime=" + loginTime +
                ", sn=" + sn +
                ", msgId=" + getMsgId() +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", encryptRule=" + encryptRule +
                ", status=" + status +
                '}';
    }

    @Override
    public String id() {
        return "jtsne.05";
    }
}
