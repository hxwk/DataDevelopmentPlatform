package com.dfssi.dataplatform.datasync.model.newen.entity;

import java.io.Serializable;

/**
 * @author JianKang
 * @date 2018/2/11
 * @description driver motor bean
 */
public class NEDriverMotorBean implements Serializable {
    /**
     * 驱动电机序号
     */
    private short driverMotorSerial;
    /**
     * 驱动电机状态码
     */
    private short driverMotorStateCode;
    /**
     * 驱动电机状态
     */
    private String driverMotorState;
    /**
     * 驱动电机控制器温度
     */
    private short driverMotorControllerTemperature;
    /**
     * 驱动电机转速
     */
    private int driverMotorRPM;
    /**
     * 驱动电机转矩
     */
    private int driverMotorTorque;
    /**
     * 驱动电机温度
     */
    private short driverMotorTemperature;
    /**
     * 电机控制器输入电压
     */
    private int motorControllerInputVoltage;
    /**
     * 电机控制器直流母线电流
     */
    private int motorControllerNegativeDCCurrent;


    public short getDriverMotorSerial() {
        return driverMotorSerial;
    }

    public void setDriverMotorSerial(short driverMotorSerial) {
        this.driverMotorSerial = driverMotorSerial;
    }

    public short getDriverMotorStateCode() {
        return driverMotorStateCode;
    }

    public void setDriverMotorStateCode(short driverMotorStateCode) {
        this.driverMotorStateCode = driverMotorStateCode;
    }

    public String getDriverMotorState() {
        return driverMotorState;
    }

    public void setDriverMotorState(String driverMotorState) {
        this.driverMotorState = driverMotorState;
    }

    public short getDriverMotorControllerTemperature() {
        return driverMotorControllerTemperature;
    }

    public void setDriverMotorControllerTemperature(short driverMotorControllerTemperature) {
        this.driverMotorControllerTemperature = driverMotorControllerTemperature;
    }

    public int getDriverMotorRPM() {
        return driverMotorRPM;
    }

    public void setDriverMotorRPM(int driverMotorRPM) {
        this.driverMotorRPM = driverMotorRPM;
    }

    public int getDriverMotorTorque() {
        return driverMotorTorque;
    }

    public void setDriverMotorTorque(int driverMotorTorque) {
        this.driverMotorTorque = driverMotorTorque;
    }

    public short getDriverMotorTemperature() {
        return driverMotorTemperature;
    }

    public void setDriverMotorTemperature(short driverMotorTemperature) {
        this.driverMotorTemperature = driverMotorTemperature;
    }

    public int getMotorControllerInputVoltage() {
        return motorControllerInputVoltage;
    }

    public void setMotorControllerInputVoltage(int motorControllerInputVoltage) {
        this.motorControllerInputVoltage = motorControllerInputVoltage;
    }

    public int getMotorControllerNegativeDCCurrent() {
        return motorControllerNegativeDCCurrent;
    }

    public void setMotorControllerNegativeDCCurrent(int motorControllerNegativeDCCurrent) {
        this.motorControllerNegativeDCCurrent = motorControllerNegativeDCCurrent;
    }

    @Override
    public String toString() {
        return "NEDriverMotorBean{" +
                "driverMotorSerial=" + driverMotorSerial +
                ", driverMotorStateCode=" + driverMotorStateCode +
                ", driverMotorState='" + driverMotorState +
                ", driverMotorControllerTemperature=" + driverMotorControllerTemperature +
                ", driverMotorRPM=" + driverMotorRPM +
                ", driverMotorTorque=" + driverMotorTorque +
                ", driverMotorTemperature=" + driverMotorTemperature +
                ", motorControllerInputVoltage=" + motorControllerInputVoltage +
                ", motorControllerNegativeDCCurrent=" + motorControllerNegativeDCCurrent +
                '}';
    }
}
