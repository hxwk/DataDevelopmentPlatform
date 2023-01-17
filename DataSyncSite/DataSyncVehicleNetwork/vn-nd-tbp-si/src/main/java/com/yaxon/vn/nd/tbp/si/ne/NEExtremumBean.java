package com.yaxon.vn.nd.tbp.si.ne;

import java.io.Serializable;

/**
 * @author JianKang
 * @date 2018/2/12
 * @description 极值数据
 */
public class NEExtremumBean implements Serializable {
    private short extremumInformationType;
    /**
     * 最高电压电池子系统号
     */
    private short highVBatterySubNum;
    /**
     * 最高电压电池单体代号
     */
    private short highVBatteryCellCode;
    /**
     * 电池单体电压最高值
     */
    private int maximumBatteryVoltage;
    /**
     * 最低电压电池子系统号
     */
    private short lowVBatterySubNum;
    /**
     * 最低电压电池单体代号
     */
    private short lowVBatteryCellCode;
    /**
     * 电池单体电压最低值
     */
    private int minimumBatteryVoltage;
    /**
     * 最高温度子系统号
     */
    private short highTemperatureSubNum;
    /**
     * 最高温度探针序号
     */
    private short highTemperatureProbeSerial;
    /**
     * 最高温度值
     */
    private short maxTemperatureValue;
    /**
     * 最低温度子系统号
     */
    private short lowTemperatureSubNum;
    /**
     * 最低温度探针序号
     */
    private short lowTemperatureProbeSerial;
    /**
     * 最低温度值
     */
    private short minTemperatureValue;

    public short getExtremumInformationType() {
        return extremumInformationType;
    }

    public void setExtremumInformationType(short extremumInformationType) {
        this.extremumInformationType = extremumInformationType;
    }

    public short getHighVBatterySubNum() {
        return highVBatterySubNum;
    }

    public void setHighVBatterySubNum(short highVBatterySubNum) {
        this.highVBatterySubNum = highVBatterySubNum;
    }

    public short getHighVBatteryCellCode() {
        return highVBatteryCellCode;
    }

    public void setHighVBatteryCellCode(short highVBatteryCellCode) {
        this.highVBatteryCellCode = highVBatteryCellCode;
    }

    public int getMaximumBatteryVoltage() {
        return maximumBatteryVoltage;
    }

    public void setMaximumBatteryVoltage(int maximumBatteryVoltage) {
        this.maximumBatteryVoltage = maximumBatteryVoltage;
    }

    public short getLowVBatterySubNum() {
        return lowVBatterySubNum;
    }

    public void setLowVBatterySubNum(short lowVBatterySubNum) {
        this.lowVBatterySubNum = lowVBatterySubNum;
    }

    public short getLowVBatteryCellCode() {
        return lowVBatteryCellCode;
    }

    public void setLowVBatteryCellCode(short lowVBatteryCellCode) {
        this.lowVBatteryCellCode = lowVBatteryCellCode;
    }

    public int getMinimumBatteryVoltage() {
        return minimumBatteryVoltage;
    }

    public void setMinimumBatteryVoltage(int minimumBatteryVoltage) {
        this.minimumBatteryVoltage = minimumBatteryVoltage;
    }

    public short getHighTemperatureSubNum() {
        return highTemperatureSubNum;
    }

    public void setHighTemperatureSubNum(short highTemperatureSubNum) {
        this.highTemperatureSubNum = highTemperatureSubNum;
    }

    public short getHighTemperatureProbeSerial() {
        return highTemperatureProbeSerial;
    }

    public void setHighTemperatureProbeSerial(short highTemperatureProbeSerial) {
        this.highTemperatureProbeSerial = highTemperatureProbeSerial;
    }

    public short getMaxTemperatureValue() {
        return maxTemperatureValue;
    }

    public void setMaxTemperatureValue(short maxTemperatureValue) {
        this.maxTemperatureValue = maxTemperatureValue;
    }

    public short getLowTemperatureSubNum() {
        return lowTemperatureSubNum;
    }

    public void setLowTemperatureSubNum(short lowTemperatureSubNum) {
        this.lowTemperatureSubNum = lowTemperatureSubNum;
    }

    public short getLowTemperatureProbeSerial() {
        return lowTemperatureProbeSerial;
    }

    public void setLowTemperatureProbeSerial(short lowTemperatureProbeSerial) {
        this.lowTemperatureProbeSerial = lowTemperatureProbeSerial;
    }

    public short getMinTemperatureValue() {
        return minTemperatureValue;
    }

    public void setMinTemperatureValue(short minTemperatureValue) {
        this.minTemperatureValue = minTemperatureValue;
    }

    @Override
    public String toString() {
        return "NEExtremumBean{" +
                "extremumInformationType=" + extremumInformationType +
                ", highVBatterySubNum=" + highVBatterySubNum +
                ", highVBatteryCellCode=" + highVBatteryCellCode +
                ", maximumBatteryVoltage=" + maximumBatteryVoltage +
                ", lowVBatterySubNum=" + lowVBatterySubNum +
                ", lowVBatteryCellCode=" + lowVBatteryCellCode +
                ", minimumBatteryVoltage=" + minimumBatteryVoltage +
                ", highTemperatureSubNum=" + highTemperatureSubNum +
                ", highTemperatureProbeSerial=" + highTemperatureProbeSerial +
                ", maxTemperatureValue=" + maxTemperatureValue +
                ", lowTemperatureSubNum=" + lowTemperatureSubNum +
                ", lowTemperatureProbeSerial=" + lowTemperatureProbeSerial +
                ", minTemperatureValue=" + minTemperatureValue +
                '}';
    }
}
