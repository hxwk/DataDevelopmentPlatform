package com.dfssi.dataplatform.datasync.model.newen.entity;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/2/11
 * @description 整车数据
 */
public class NEVehicleBean implements Serializable {
    private short vehicleInformationType;
    /**
     * 车辆状态Code
     */
    private short vehicleStatusCode;
    /**
     * 车辆状态
     */
    private String vehicleStatus;
    /**
     * 充电状态Code
     */
    private short chargingStatusCode;
    /**
     * 充电状态
     */
    private String chargingStatus;
    /**
     * 运行模式Code
     */
    private short runModeCode;
    /**
     * 运行模式
     */
    private String runMode;
    /**
     * 车速
     */
    private int speed;
    /**
     * 累计里程
     */
    private long accumulativeMile;
    /**
     * 总电压
     */
    private int totalVoltage;
    /**
     * 总电流
     */
    private int totalElectricity;
    /**
     * SOC
     */
    private short soc;
    /**
     * DC-DC状态Code
     */
    private short dcStatusCode;
    /**
     * DC-DC状态
     */
    private String dcStatus;
    /**
     * 档位Code
     */
    private short gearCode;
    /**
     * 档位状态位信息
     */
    private List<String> gears;
    /**
     * 绝缘电阻
     */
    private int insulationResistance;
    /**
     * 加速踏板行程值
     */
    private short acceleratorPedal;
    /**
     * 制动踏板状态
     */
    private short brakePedalStatus;
    /**
     * 预留位
     */
    //private int reserved;
    public short getVehicleInformationType() {
        return vehicleInformationType;
    }

    public void setVehicleInformationType(short vehicleInformationType) {
        this.vehicleInformationType = vehicleInformationType;
    }

    public short getVehicleStatusCode() {
        return vehicleStatusCode;
    }

    public void setVehicleStatusCode(short vehicleStatusCode) {
        this.vehicleStatusCode = vehicleStatusCode;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public short getChargingStatusCode() {
        return chargingStatusCode;
    }

    public void setChargingStatusCode(short chargingStatusCode) {
        this.chargingStatusCode = chargingStatusCode;
    }

    public String getChargingStatus() {
        return chargingStatus;
    }

    public void setChargingStatus(String chargingStatus) {
        this.chargingStatus = chargingStatus;
    }

    public short getRunModeCode() {
        return runModeCode;
    }

    public void setRunModeCode(short runModeCode) {
        this.runModeCode = runModeCode;
    }

    public String getRunMode() {
        return runMode;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public long getAccumulativeMile() {
        return accumulativeMile;
    }

    public void setAccumulativeMile(long accumulativeMile) {
        this.accumulativeMile = accumulativeMile;
    }

    public int getTotalVoltage() {
        return totalVoltage;
    }

    public void setTotalVoltage(int totalVoltage) {
        this.totalVoltage = totalVoltage;
    }

    public int getTotalElectricity() {
        return totalElectricity;
    }

    public void setTotalElectricity(int totalElectricity) {
        this.totalElectricity = totalElectricity;
    }

    public short getSoc() {
        return soc;
    }

    public void setSoc(short soc) {
        this.soc = soc;
    }

    public short getDcStatusCode() {
        return dcStatusCode;
    }

    public void setDcStatusCode(short dcStatusCode) {
        this.dcStatusCode = dcStatusCode;
    }

    public String getDcStatus() {
        return dcStatus;
    }

    public void setDcStatus(String dcStatus) {
        this.dcStatus = dcStatus;
    }

    public short getGearCode() {
        return gearCode;
    }

    public void setGearCode(short gearCode) {
        this.gearCode = gearCode;
    }

    public List<String> getGears() {
        return gears;
    }

    public void setGears(List<String> gears) {
        this.gears = gears;
    }

    public int getInsulationResistance() {
        return insulationResistance;
    }

    public void setInsulationResistance(int insulationResistance) {
        this.insulationResistance = insulationResistance;
    }

    public short getAcceleratorPedal() {
        return acceleratorPedal;
    }

    public void setAcceleratorPedal(short acceleratorPedal) {
        this.acceleratorPedal = acceleratorPedal;
    }

    public short getBrakePedalStatus() {
        return brakePedalStatus;
    }

    public void setBrakePedalStatus(short brakePedalStatus) {
        this.brakePedalStatus = brakePedalStatus;
    }
    @Override
    public String toString() {
        gears = ObjectUtils.defaultIfNull(gears,Lists.<String>newArrayList());
        String bean = "NEVehicleBean{" +
                "vehicleInformationType="+vehicleInformationType+
                ", vehicleStatusCode=" + vehicleStatusCode +
                ", vehicleStatus=" + vehicleStatus +
                ", chargingStatusCode=" + chargingStatusCode +
                ", chargingStatus=" + chargingStatus +
                ", runModeCode=" + runModeCode +
                ", runMode=" + runMode +
                ", speed=" + speed +
                ", accumulativeMile=" + accumulativeMile +
                ", totalVoltage=" + totalVoltage +
                ", totalElectricity=" + totalElectricity +
                ", soc=" + soc +
                ", dcStatusCode=" + dcStatusCode +
                ", dcStatus=" + dcStatus +
                ", gearCode=" + gearCode +
                ", gears=" + gears +
                ", insulationResistance=" + insulationResistance +
                ", acceleratorPedal=" + acceleratorPedal +
                ", brakePedalStatus=" + brakePedalStatus +
                '}';
        return bean;
    }
}
