package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/5/30
 * @description
 */
public class SecondDataPkg implements Serializable {
    private String id;
    //发动机转速
    private Integer rpm;
    //仪表车速
    private Integer instrumentSpeed;
    //车轮车速
    private Integer wheelSpeed;
    //GPS车速
    private Integer gpsSpeed;
    //油门开度
    private Short throttleOpening;
    //发动机实际输出扭矩百分比
    private Short percentagetorque;
    //驻车制动开关,制动开关,离合器开关,巡航控制设置开关CODE
    private Short switchsCode;
    //驻车制动开关,制动开关,离合器开关,巡航控制设置开关
    private List<String> switchStatus;
    //当前档位
    private Short currentBlock;
    //目标档位
    private Short targetGear;
    //发动机燃油消耗率
    private Integer engineFuelRate;
    //坡度
    private Integer grade;
    //载重
    private Integer load;
    //燃油液位
    private Short fuelLevel;
    //水温
    private Short waterTemp;
    //大气压力
    private Short barometricPressure;
    //进气温度
    private Integer intakeAirTemp;
    //大气温度
    private Short airTemp;
    //排气温度
    private Integer exhaustTemp;
    //进气歧管增压压力
    private Integer intakeQGpressure;
    //相对压力
    private Integer relativePress;
    //发动机扭矩模式
    private Short engineTorqueMode;
    //机油压力
    private Integer oilPressure;
    //尿素液位
    private Short UreaLevel;
    //状态标志位
    private Long stateFlag;
    //制动踏板开度
    private Short brakePedalOpen;
    //GSP方向
    private Integer gpsDir;
    //空调压缩机状态
    private Short airCompressorStatus;
    //变速箱输出轴转速
    private Integer transmissionOutputSpeed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRpm() {
        return rpm;
    }

    public void setRpm(Integer rpm) {
        this.rpm = rpm;
    }

    public Integer getInstrumentSpeed() {
        return instrumentSpeed;
    }

    public void setInstrumentSpeed(Integer instrumentSpeed) {
        this.instrumentSpeed = instrumentSpeed;
    }

    public Integer getWheelSpeed() {
        return wheelSpeed;
    }

    public void setWheelSpeed(Integer wheelSpeed) {
        this.wheelSpeed = wheelSpeed;
    }

    public Integer getGpsSpeed() {
        return gpsSpeed;
    }

    public void setGpsSpeed(Integer gpsSpeed) {
        this.gpsSpeed = gpsSpeed;
    }

    public Short getThrottleOpening() {
        return throttleOpening;
    }

    public void setThrottleOpening(Short throttleOpening) {
        this.throttleOpening = throttleOpening;
    }

    public Short getPercentagetorque() {
        return percentagetorque;
    }

    public void setPercentagetorque(Short percentagetorque) {
        this.percentagetorque = percentagetorque;
    }

    public Short getSwitchsCode() {
        return switchsCode;
    }

    public void setSwitchsCode(Short switchsCode) {
        this.switchsCode = switchsCode;
    }

    public List<String> getSwitchStatus() {
        return switchStatus;
    }

    public void setSwitchStatus(List<String> switchStatus) {
        this.switchStatus = switchStatus;
    }

    public Short getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(Short currentBlock) {
        this.currentBlock = currentBlock;
    }

    public Short getTargetGear() {
        return targetGear;
    }

    public void setTargetGear(Short targetGear) {
        this.targetGear = targetGear;
    }

    public Integer getEngineFuelRate() {
        return engineFuelRate;
    }

    public void setEngineFuelRate(Integer engineFuelRate) {
        this.engineFuelRate = engineFuelRate;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getLoad() {
        return load;
    }

    public void setLoad(Integer load) {
        this.load = load;
    }

    public Short getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(Short fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public Short getWaterTemp() {
        return waterTemp;
    }

    public void setWaterTemp(Short waterTemp) {
        this.waterTemp = waterTemp;
    }

    public Short getBarometricPressure() {
        return barometricPressure;
    }

    public void setBarometricPressure(Short barometricPressure) {
        this.barometricPressure = barometricPressure;
    }

    public Integer getIntakeAirTemp() {
        return intakeAirTemp;
    }

    public void setIntakeAirTemp(Integer intakeAirTemp) {
        this.intakeAirTemp = intakeAirTemp;
    }

    public Short getAirTemp() {
        return airTemp;
    }

    public void setAirTemp(Short airTemp) {
        this.airTemp = airTemp;
    }

    public Integer getExhaustTemp() {
        return exhaustTemp;
    }

    public void setExhaustTemp(Integer exhaustTemp) {
        this.exhaustTemp = exhaustTemp;
    }

    public Integer getIntakeQGpressure() {
        return intakeQGpressure;
    }

    public void setIntakeQGpressure(Integer intakeQGpressure) {
        this.intakeQGpressure = intakeQGpressure;
    }

    public Integer getRelativePress() {
        return relativePress;
    }

    public void setRelativePress(Integer relativePress) {
        this.relativePress = relativePress;
    }

    public Short getEngineTorqueMode() {
        return engineTorqueMode;
    }

    public void setEngineTorqueMode(Short engineTorqueMode) {
        this.engineTorqueMode = engineTorqueMode;
    }

    public Integer getOilPressure() {
        return oilPressure;
    }

    public void setOilPressure(Integer oilPressure) {
        this.oilPressure = oilPressure;
    }

    public Short getUreaLevel() {
        return UreaLevel;
    }

    public void setUreaLevel(Short ureaLevel) {
        UreaLevel = ureaLevel;
    }

    public Long getStateFlag() {
        return stateFlag;
    }

    public void setStateFlag(Long stateFlag) {
        this.stateFlag = stateFlag;
    }

    public Short getBrakePedalOpen() {
        return brakePedalOpen;
    }

    public void setBrakePedalOpen(Short brakePedalOpen) {
        this.brakePedalOpen = brakePedalOpen;
    }

    public Integer getGpsDir() {
        return gpsDir;
    }

    public void setGpsDir(Integer gpsDir) {
        this.gpsDir = gpsDir;
    }

    public Short getAirCompressorStatus() {
        return airCompressorStatus;
    }

    public void setAirCompressorStatus(Short airCompressorStatus) {
        this.airCompressorStatus = airCompressorStatus;
    }

    public Integer getTransmissionOutputSpeed() {
        return transmissionOutputSpeed;
    }

    public void setTransmissionOutputSpeed(Integer transmissionOutputSpeed) {
        this.transmissionOutputSpeed = transmissionOutputSpeed;
    }

    @Override
    public String toString() {
        return "SecondDataPkg{" +
                "id='" + id + '\'' +
                ", rpm=" + rpm +
                ", instrumentSpeed=" + instrumentSpeed +
                ", wheelSpeed=" + wheelSpeed +
                ", gpsSpeed=" + gpsSpeed +
                ", throttleOpening=" + throttleOpening +
                ", percentagetorque=" + percentagetorque +
                ", switchsCode=" + switchsCode +
                ", switchStatus=" + switchStatus +
                ", currentBlock=" + currentBlock +
                ", targetGear=" + targetGear +
                ", engineFuelRate=" + engineFuelRate +
                ", grade=" + grade +
                ", load=" + load +
                ", fuelLevel=" + fuelLevel +
                ", waterTemp=" + waterTemp +
                ", barometricPressure=" + barometricPressure +
                ", intakeAirTemp=" + intakeAirTemp +
                ", airTemp=" + airTemp +
                ", exhaustTemp=" + exhaustTemp +
                ", intakeQGpressure=" + intakeQGpressure +
                ", relativePress=" + relativePress +
                ", engineTorqueMode=" + engineTorqueMode +
                ", oilPressure=" + oilPressure +
                ", UreaLevel=" + UreaLevel +
                ", stateFlag=" + stateFlag +
                ", brakePedalOpen=" + brakePedalOpen +
                ", gpsDir=" + gpsDir +
                ", airCompressorStatus=" + airCompressorStatus +
                ", transmissionOutputSpeed=" + transmissionOutputSpeed +
                '}';
    }
}
