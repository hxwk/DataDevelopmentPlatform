package com.dfssi.dataplatform.datasync.model.newen.entity;

import com.dfssi.dataplatform.datasync.model.common.BaseMessage;

/**
 * 车辆登出实体
 * Created by Hannibal on 2018-04-08.
 */

public class Req_04 extends BaseMessage {

    private String vin;// vin号码

    private Long logoutTime;//平台登出时间

    private Short sn;//车辆登入流水号

    private String commandSign;//命令标识

    private String vehicleType;//车型信息

    private String msgId;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Long getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Long logoutTime) {
        this.logoutTime = logoutTime;
    }

    public Short getSn() {
        return sn;
    }

    public void setSn(Short sn) {
        this.sn = sn;
    }

    public String getCommandSign() {
        return commandSign;
    }

    public void setCommandSign(String commandSign) {
        this.commandSign = commandSign;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
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
        return "Req_04{" +
                "vin='" + vin + '\'' +
                ", logoutTime=" + logoutTime +
                ", sn=" + sn +
                ", msgId=" + getMsgId() +
                ", commandSign='" + commandSign + '\'' +
                ", vehicleCompany='" + super.getVehicleCompany() + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                '}';
    }

    @Override
    public String id() {
        return "jtsne.04";
    }
}
