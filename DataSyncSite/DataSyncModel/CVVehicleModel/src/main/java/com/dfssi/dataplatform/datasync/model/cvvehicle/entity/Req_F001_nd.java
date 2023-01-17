package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * Author: zhengchaoyuan
 * Time: 2017-08-24 16:50
 * Copyright (C) 2017 Xiamen Yaxon Networks CO.,LTD.
 * 南斗 下发锁车相关指令
 */
public class Req_F001_nd extends JtsReqMsg {

    public static final String _id = "jts.F001.nd";

    @Override
    public String id() {
        return "jts.F001.nd";
    }

    private Integer instruction;//命令字
    private Integer business;//业务状态
    private Integer limpStatus;//跛行要求（状态）
    private Integer tsc1;//tsc1报文ID源地址
    private Integer speedOfRevolution;//转速
    private Integer torque;//扭矩
    private Long cuid; //创建人
    private Long tid; //企业id
    private String fromIp;//前台用户ip
    private String loginName; //用户登录名
    private String lpn; //车牌号

    public Integer getInstruction() {
        return instruction;
    }

    public void setInstruction(Integer instruction) {
        this.instruction = instruction;
    }

    public Integer getBusiness() {
        return business;
    }

    public void setBusiness(Integer business) {
        this.business = business;
    }

    public Integer getLimpStatus() {
        return limpStatus;
    }

    public void setLimpStatus(Integer limpStatus) {
        this.limpStatus = limpStatus;
    }

    public Integer getTsc1() {
        return tsc1;
    }

    public void setTsc1(Integer tsc1) {
        this.tsc1 = tsc1;
    }

    public Integer getSpeedOfRevolution() {
        return speedOfRevolution;
    }

    public void setSpeedOfRevolution(Integer speedOfRevolution) {
        this.speedOfRevolution = speedOfRevolution;
    }

    public Integer getTorque() {
        return torque;
    }

    public void setTorque(Integer torque) {
        this.torque = torque;
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public String getFromIp() {
        return fromIp;
    }

    public void setFromIp(String fromIp) {
        this.fromIp = fromIp;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public Long getCuid() {
        return cuid;
    }

    public void setCuid(Long cuid) {
        this.cuid = cuid;
    }

    @Override
    public String toString() {
        return "Req_F001_nd{" +
                "instruction=" + instruction +
                ", business=" + business +
                ", lpn='" + lpn + '\'' +
                ", vin='" + vin + '\'' +
                ", limpStatus=" + limpStatus +
                ", tsc1=" + tsc1 +
                ", speedOfRevolution=" + speedOfRevolution +
                ", torque=" + torque +
                ", cuid=" + cuid +
                ", tid=" + tid +
                ", fromIp='" + fromIp + '\'' +
                ", loginName='" + loginName + '\'' +
                '}';
    }
}
