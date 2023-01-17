package com.dfssi.dataplatform.datasync.model.newen.entity;

import java.io.Serializable;

/**
 * @author JianKang
 * @date 2018/2/12
 * @description ne engine bean
 */
public class NEEngineBean implements Serializable {
    private short engineInformationType;
    /**
     * 发动机状态Code
     */
    private short engineStateCode;
    /**
     * 发动机状态
     */
    private String engineState;
    /**
     * 曲轴转速
     */
    private int speedOfCrankshaft;
    /**
     * 燃料消耗率
     */
    private int specificFuelConsumption;

    public short getEngineInformationType() {
        return engineInformationType;
    }

    public void setEngineInformationType(short engineInformationType) {
        this.engineInformationType = engineInformationType;
    }

    public short getEngineStateCode() {
        return engineStateCode;
    }

    public void setEngineStateCode(short engineStateCode) {
        this.engineStateCode = engineStateCode;
    }

    public String getEngineState() {
        return engineState;
    }

    public void setEngineState(String engineState) {
        this.engineState = engineState;
    }

    public int getSpeedOfCrankshaft() {
        return speedOfCrankshaft;
    }

    public void setSpeedOfCrankshaft(int speedOfCrankshaft) {
        this.speedOfCrankshaft = speedOfCrankshaft;
    }

    public int getSpecificFuelConsumption() {
        return specificFuelConsumption;
    }

    public void setSpecificFuelConsumption(int specificFuelConsumption) {
        this.specificFuelConsumption = specificFuelConsumption;
    }

    @Override
    public String toString() {
        return "NEEngineBean{" +
                "engineInformationType=" + engineInformationType +
                ", engineStateCode=" + engineStateCode +
                ", engineState='" + engineState +
                ", speedOfCrankshaft=" + speedOfCrankshaft +
                ", specificFuelConsumption=" + specificFuelConsumption +
                '}';
    }
}
