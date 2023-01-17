package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;


import com.dfssi.dataplatform.datasync.model.common.VnndResMsg;

import java.util.Date;

/**
 * 基类
 */
public class VnndF003ResMsg extends VnndResMsg {

    public String id() {
        return "jts.F003.nd.r";
    }
    private Integer instruction;//命令字
    private Date respTime;//应答时间
    private byte rc;//查询结果 0成功；  1失败；  2查询超时
    private byte on;//ON档标志 0非ON档；  1 ON档
    private byte acc;// ACC档标志 0非ACC档  1 ACC档
    private byte notOnlineLock;//不在线锁车状态 0默认 1不在线而锁车
    private byte writeVdrId;//写VDR_ID标志 0未写入  1成功写入
    private byte startupAuthentication;//启动认证标志 0未认证  1认证成功
    private byte functionShutdown;//功能关闭标志  0未解除  1解除成功
    private byte icHeartbeat;//与IC心跳状态标志 0正常 1超时 2握手不通过
    private byte limpStatus;//跛行状态 0正常 1命令准备跛行 2命令执行跛行 3不在线准备跛行 4不在线执行跛行 5校验失败准备跛行 6校验失败执行跛行 7超时准备跛行 8超时执行跛行 14跛行失败
    private byte processMonitorSwitch;//过程监控总开关 0关闭 1打开
    private byte notOnlineMonitorSwitch;//不在线监控开关 0关闭 1打开
    private byte eecuLimpStatus;//EECU实际跛行状态 0未跛行 1跛行开启 其他值无效
    private byte vehicleBusiness;//车辆业务状态 0无效 1总部出库 2经销商入库（消贷车管理） 3经销商出库 4退车入库（经销商） 5退回总部（总部） 6消贷车管理 其他值无效
    private byte icFault;//IC是否故障 南斗还没定义值
    private byte vdrEecuMessage;//VDR是否收到EECU报文 0收到  1未收到
    private byte limp;//跛行命令 0 VDR未执行跛行 1 VDR执行跛行
    private byte meterBusiness;//仪表收到的业务状态是否异常 0正常 1异常
    private byte canOff;//can总线是都断开  0正常 1异常
    private byte canContinuity;//can是否连续 0正常 1异常
    private String vdrId;//终端ID
    private String vin;//底盘号

    public Integer getInstruction() {
        return instruction;
    }

    public void setInstruction(Integer instruction) {
        this.instruction = instruction;
    }

    public Date getRespTime() {
        return respTime;
    }

    public void setRespTime(Date respTime) {
        this.respTime = respTime;
    }

    public byte getRc() {
        return rc;
    }

    public void setRc(byte rc) {
        this.rc = rc;
    }

    public byte getOn() {
        return on;
    }

    public void setOn(byte on) {
        this.on = on;
    }

    public byte getAcc() {
        return acc;
    }

    public void setAcc(byte acc) {
        this.acc = acc;
    }

    public byte getNotOnlineLock() {
        return notOnlineLock;
    }

    public void setNotOnlineLock(byte notOnlineLock) {
        this.notOnlineLock = notOnlineLock;
    }

    public byte getWriteVdrId() {
        return writeVdrId;
    }

    public void setWriteVdrId(byte writeVdrId) {
        this.writeVdrId = writeVdrId;
    }

    public byte getStartupAuthentication() {
        return startupAuthentication;
    }

    public void setStartupAuthentication(byte startupAuthentication) {
        this.startupAuthentication = startupAuthentication;
    }

    public byte getFunctionShutdown() {
        return functionShutdown;
    }

    public void setFunctionShutdown(byte functionShutdown) {
        this.functionShutdown = functionShutdown;
    }

    public byte getIcHeartbeat() {
        return icHeartbeat;
    }

    public void setIcHeartbeat(byte icHeartbeat) {
        this.icHeartbeat = icHeartbeat;
    }

    public byte getLimpStatus() {
        return limpStatus;
    }

    public void setLimpStatus(byte limpStatus) {
        this.limpStatus = limpStatus;
    }

    public byte getProcessMonitorSwitch() {
        return processMonitorSwitch;
    }

    public void setProcessMonitorSwitch(byte processMonitorSwitch) {
        this.processMonitorSwitch = processMonitorSwitch;
    }

    public byte getNotOnlineMonitorSwitch() {
        return notOnlineMonitorSwitch;
    }

    public void setNotOnlineMonitorSwitch(byte notOnlineMonitorSwitch) {
        this.notOnlineMonitorSwitch = notOnlineMonitorSwitch;
    }

    public byte getEecuLimpStatus() {
        return eecuLimpStatus;
    }

    public void setEecuLimpStatus(byte eecuLimpStatus) {
        this.eecuLimpStatus = eecuLimpStatus;
    }

    public byte getVehicleBusiness() {
        return vehicleBusiness;
    }

    public void setVehicleBusiness(byte vehicleBusiness) {
        this.vehicleBusiness = vehicleBusiness;
    }

    public byte getIcFault() {
        return icFault;
    }

    public void setIcFault(byte icFault) {
        this.icFault = icFault;
    }

    public byte getVdrEecuMessage() {
        return vdrEecuMessage;
    }

    public void setVdrEecuMessage(byte vdrEecuMessage) {
        this.vdrEecuMessage = vdrEecuMessage;
    }

    public byte getLimp() {
        return limp;
    }

    public void setLimp(byte limp) {
        this.limp = limp;
    }

    public byte getMeterBusiness() {
        return meterBusiness;
    }

    public void setMeterBusiness(byte meterBusiness) {
        this.meterBusiness = meterBusiness;
    }

    public byte getCanOff() {
        return canOff;
    }

    public void setCanOff(byte canOff) {
        this.canOff = canOff;
    }

    public byte getCanContinuity() {
        return canContinuity;
    }

    public void setCanContinuity(byte canContinuity) {
        this.canContinuity = canContinuity;
    }

    public String getVdrId() {
        return vdrId;
    }

    public void setVdrId(String vdrId) {
        this.vdrId = vdrId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Override
    public String toString() {
        return "Res_F003_nd{" + super.toString() +
                "instruction=" + instruction +
                ", respTime=" + respTime +
                ", rc=" + rc +
                ", on=" + on +
                ", acc=" + acc +
                ", notOnlineLock=" + notOnlineLock +
                ", writeVdrId=" + writeVdrId +
                ", startupAuthentication=" + startupAuthentication +
                ", functionShutdown=" + functionShutdown +
                ", icHeartbeat=" + icHeartbeat +
                ", limpStatus=" + limpStatus +
                ", processMonitorSwitch=" + processMonitorSwitch +
                ", notOnlineMonitorSwitch=" + notOnlineMonitorSwitch +
                ", eecuLimpStatus=" + eecuLimpStatus +
                ", vehicleBusiness=" + vehicleBusiness +
                ", icFault=" + icFault +
                ", vdrEecuMessage=" + vdrEecuMessage +
                ", limp=" + limp +
                ", meterBusiness=" + meterBusiness +
                ", canOff=" + canOff +
                ", canContinuity=" + canContinuity +
                ", vdrId='" + vdrId + '\'' +
                ", vin='" + vin + '\'' +
                '}';
    }
}
