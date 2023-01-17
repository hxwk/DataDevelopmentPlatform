package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 */
public class Req_F002_nd extends JtsReqMsg {

    public static final String _id = "jts.F002.nd";

    @Override
    public String id() {return "jts.F002.nd";}
    private Integer onBitchSign;//on档标志
    private Integer accBitchSign;//acc档标志
    private Integer offLineLockVehicleSign;//不在线锁车状态
    private Integer writeVDR_IDSign;//写VDR_ID标志
    private Integer openAuthSign;//启动认证标志
    private Integer closeAuthSign;//关闭功能标志
    private Integer ICHeartBeatSign;//与IC心跳状态标志
    private Integer limpStatusSign;//跛行状态标志
    private Integer processMonitorSign;//过程监控总开关标志
    private Integer offlineMonitorSign;//过程监控总开关标志
    private Integer realLimpStatusSign;//ECU实际跛行状态
    private Integer vehicleBusinessStatus;//车辆业务状态
    private Integer ICBreakSign;//IC是否有故障标识
    private Integer eecuSign;//vdr未收到eecu报文标志
    private Integer limpCommand;//跛行命令
    private Integer meterBreakSign;//仪表收到业务状态是否异常

    public Integer getOnBitchSign() {
        return onBitchSign;
    }

    public void setOnBitchSign(Integer onBitchSign) {
        this.onBitchSign = onBitchSign;
    }

    public Integer getAccBitchSign() {
        return accBitchSign;
    }

    public void setAccBitchSign(Integer accBitchSign) {
        this.accBitchSign = accBitchSign;
    }

    public Integer getOffLineLockVehicleSign() {
        return offLineLockVehicleSign;
    }

    public void setOffLineLockVehicleSign(Integer offLineLockVehicleSign) {
        this.offLineLockVehicleSign = offLineLockVehicleSign;
    }

    public Integer getWriteVDR_IDSign() {
        return writeVDR_IDSign;
    }

    public void setWriteVDR_IDSign(Integer writeVDR_IDSign) {
        this.writeVDR_IDSign = writeVDR_IDSign;
    }

    public Integer getOpenAuthSign() {
        return openAuthSign;
    }

    public void setOpenAuthSign(Integer openAuthSign) {
        this.openAuthSign = openAuthSign;
    }

    public Integer getCloseAuthSign() {
        return closeAuthSign;
    }

    public void setCloseAuthSign(Integer closeAuthSign) {
        this.closeAuthSign = closeAuthSign;
    }

    public Integer getICHeartBeatSign() {
        return ICHeartBeatSign;
    }

    public void setICHeartBeatSign(Integer ICHeartBeatSign) {
        this.ICHeartBeatSign = ICHeartBeatSign;
    }

    public Integer getLimpStatusSign() {
        return limpStatusSign;
    }

    public void setLimpStatusSign(Integer limpStatusSign) {
        this.limpStatusSign = limpStatusSign;
    }

    public Integer getProcessMonitorSign() {
        return processMonitorSign;
    }

    public void setProcessMonitorSign(Integer processMonitorSign) {
        this.processMonitorSign = processMonitorSign;
    }

    public Integer getOfflineMonitorSign() {
        return offlineMonitorSign;
    }

    public void setOfflineMonitorSign(Integer offlineMonitorSign) {
        this.offlineMonitorSign = offlineMonitorSign;
    }

    public Integer getRealLimpStatusSign() {
        return realLimpStatusSign;
    }

    public void setRealLimpStatusSign(Integer realLimpStatusSign) {
        this.realLimpStatusSign = realLimpStatusSign;
    }

    public Integer getVehicleBusinessStatus() {
        return vehicleBusinessStatus;
    }

    public void setVehicleBusinessStatus(Integer vehicleBusinessStatus) {
        this.vehicleBusinessStatus = vehicleBusinessStatus;
    }

    public Integer getICBreakSign() {
        return ICBreakSign;
    }

    public void setICBreakSign(Integer ICBreakSign) {
        this.ICBreakSign = ICBreakSign;
    }

    public Integer getEecuSign() {
        return eecuSign;
    }

    public void setEecuSign(Integer eecuSign) {
        this.eecuSign = eecuSign;
    }

    public Integer getLimpCommand() {
        return limpCommand;
    }

    public void setLimpCommand(Integer limpCommand) {
        this.limpCommand = limpCommand;
    }

    public Integer getMeterBreakSign() {
        return meterBreakSign;
    }

    public void setMeterBreakSign(Integer meterBreakSign) {
        this.meterBreakSign = meterBreakSign;
    }

    @Override
    public String toString() {
        return "Req_F002_nd{" +
                "onBitchSign=" + onBitchSign +
                ", accBitchSign=" + accBitchSign +
                ", offLineLockVehicleSign=" + offLineLockVehicleSign +
                ", writeVDR_IDSign=" + writeVDR_IDSign +
                ", openAuthSign=" + openAuthSign +
                ", closeAuthSign=" + closeAuthSign +
                ", ICHeartBeatSign=" + ICHeartBeatSign +
                ", limpStatusSign=" + limpStatusSign +
                ", processMonitorSign=" + processMonitorSign +
                ", offlineMonitorSign=" + offlineMonitorSign +
                ", realLimpStatusSign=" + realLimpStatusSign +
                ", vehicleBusinessStatus=" + vehicleBusinessStatus +
                ", ICBreakSign=" + ICBreakSign +
                ", eecuSign=" + eecuSign +
                ", limpCommand=" + limpCommand +
                ", meterBreakSign=" + meterBreakSign +
                '}';
    }
}

