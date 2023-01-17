package com.yaxon.vn.nd.tbp.si.ne;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author JianKang
 * @date 2018/2/12
 * @description 燃料电池对象
 */
public class NEFuelCellBean implements Serializable {
    private short fuelCellInformationType;
    /**
     * 燃料电池电压
     */
    private int fuelCellVoltage;
    /**
     * 燃料电池电流
     */
    private int fuelCellCurrent;
    /**
     * 燃料消耗率
     */
    private int rateOfFuelConsumption;
    /**
     * 燃料电池温度探针总数
     */
    private int fuelCellProbeNumber;
    /**
     * 探针温度值
     */
    private short[] probeTemperatures;
    /**
     * 氢系统中最高温度
     */
    private int maxTemperatureInHydrogenSystem;
    /**
     * 氢系统中最高温度探针代号
     */
    private short maxTemperatureProbeSerial;
    /**
     * 氢气最高浓度
     */
    private int maxHydrogenConcentration;
    /**
     * 氢气最高浓度传感器代号
     */
    private short maxHydrogenConcentrationProbeSerial;
    /**
     * 氢气最高压力
     */
    private int maxPressureHydrogen;
    /**
     * 氢气最高压力传感器代号
     */
    private short maxPressureHydrogenProbeSerial;
    /**
     * 高压DC/DC状态Code
     */
    private short highPressDCStateCode;
    /**
     * 高压DC/DC状态
     */
    private String highPressDCState;

    public short getFuelCellInformationType() {
        return fuelCellInformationType;
    }

    public void setFuelCellInformationType(short fuelCellInformationType) {
        this.fuelCellInformationType = fuelCellInformationType;
    }

    public int getFuelCellVoltage() {
        return fuelCellVoltage;
    }

    public void setFuelCellVoltage(int fuelCellVoltage) {
        this.fuelCellVoltage = fuelCellVoltage;
    }

    public int getFuelCellCurrent() {
        return fuelCellCurrent;
    }

    public void setFuelCellCurrent(int fuelCellCurrent) {
        this.fuelCellCurrent = fuelCellCurrent;
    }

    public int getRateOfFuelConsumption() {
        return rateOfFuelConsumption;
    }

    public void setRateOfFuelConsumption(int rateOfFuelConsumption) {
        this.rateOfFuelConsumption = rateOfFuelConsumption;
    }

    public int getFuelCellProbeNumber() {
        return fuelCellProbeNumber;
    }

    public void setFuelCellProbeNumber(int fuelCellProbeNumber) {
        this.fuelCellProbeNumber = fuelCellProbeNumber;
    }

    public short[] getProbeTemperatures() {
        return probeTemperatures;
    }

    public void setProbeTemperatures(short[] probeTemperatures) {
        this.probeTemperatures = probeTemperatures;
    }

    public int getMaxTemperatureInHydrogenSystem() {
        return maxTemperatureInHydrogenSystem;
    }

    public void setMaxTemperatureInHydrogenSystem(int maxTemperatureInHydrogenSystem) {
        this.maxTemperatureInHydrogenSystem = maxTemperatureInHydrogenSystem;
    }

    public short getMaxTemperatureProbeSerial() {
        return maxTemperatureProbeSerial;
    }

    public void setMaxTemperatureProbeSerial(short maxTemperatureProbeSerial) {
        this.maxTemperatureProbeSerial = maxTemperatureProbeSerial;
    }

    public int getMaxHydrogenConcentration() {
        return maxHydrogenConcentration;
    }

    public void setMaxHydrogenConcentration(int maxHydrogenConcentration) {
        this.maxHydrogenConcentration = maxHydrogenConcentration;
    }

    public short getMaxHydrogenConcentrationProbeSerial() {
        return maxHydrogenConcentrationProbeSerial;
    }

    public void setMaxHydrogenConcentrationProbeSerial(short maxHydrogenConcentrationProbeSerial) {
        this.maxHydrogenConcentrationProbeSerial = maxHydrogenConcentrationProbeSerial;
    }

    public int getMaxPressureHydrogen() {
        return maxPressureHydrogen;
    }

    public void setMaxPressureHydrogen(int maxPressureHydrogen) {
        this.maxPressureHydrogen = maxPressureHydrogen;
    }

    public short getMaxPressureHydrogenProbeSerial() {
        return maxPressureHydrogenProbeSerial;
    }

    public void setMaxPressureHydrogenProbeSerial(short maxPressureHydrogenProbeSerial) {
        this.maxPressureHydrogenProbeSerial = maxPressureHydrogenProbeSerial;
    }

    public short getHighPressDCStateCode() {
        return highPressDCStateCode;
    }

    public void setHighPressDCStateCode(short highPressDCStateCode) {
        this.highPressDCStateCode = highPressDCStateCode;
    }

    public String getHighPressDCState() {
        return highPressDCState;
    }

    public void setHighPressDCState(String highPressDCState) {
        this.highPressDCState = highPressDCState;
    }

    @Override
    public String toString() {
        return "NEFuelCellBean{" +
                "fuelCellInformationType=" + fuelCellInformationType +
                ", fuelCellVoltage=" + fuelCellVoltage +
                ", fuelCellCurrent=" + fuelCellCurrent +
                ", rateOfFuelConsumption=" + rateOfFuelConsumption +
                ", fuelCellProbeNumber=" + fuelCellProbeNumber +
                ", probeTemperatures=" + Arrays.toString(probeTemperatures) +
                ", maxTemperatureInHydrogenSystem=" + maxTemperatureInHydrogenSystem +
                ", maxTemperatureProbeSerial=" + maxTemperatureProbeSerial +
                ", maxHydrogenConcentration=" + maxHydrogenConcentration +
                ", maxHydrogenConcentrationProbeSerial=" + maxHydrogenConcentrationProbeSerial +
                ", maxPressureHydrogen=" + maxPressureHydrogen +
                ", maxPressureHydrogenProbeSerial=" + maxPressureHydrogenProbeSerial +
                ", highPressDCStateCode=" + highPressDCStateCode +
                ", highPressDCState='" + highPressDCState +
                '}';
    }
}
