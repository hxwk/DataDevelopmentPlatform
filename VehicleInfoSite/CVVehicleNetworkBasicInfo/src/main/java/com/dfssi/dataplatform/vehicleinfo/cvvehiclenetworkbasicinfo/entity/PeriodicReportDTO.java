package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity;

import java.io.Serializable;

/**
 * 车辆实时信息周期上报信息
 * Created by yanghs on 2018/5/9.
 */
public class PeriodicReportDTO implements Serializable {

    private String vid;
    //发动机转速
    private String rpm;
    //仪表车速
    private String instrumentSpeed;
    //车轮车速
    private String wheelSpeed;
    //GPS车速
    private String gpsSpeed;
    //油门开度
    private String throttleOpening;
    //发动机实际输出扭矩百分比
    private String percentagetorque;
    //驻车制动开关,制动开关,离合器开关,巡航控制设置开关
    private String switchs;
    //当前档位
//    private Short currentBlock;
    //目标档位
//    private Short targetGear;
    //发动机燃油消耗率
    private String engineFuelRate;
    //坡度
//    private Integer grade;
    //载重
//    private Integer load;
    //燃油液位
    private String fuelLevel;
    //水温
    private String waterTemp;
    //大气压力
//    private Short barometricPressure;
    //进气温度
//    private Integer intakeAirTemp;
    //大气温度
//    private Short airTemp;
    //排气温度
//    private Integer exhaustTemp;
    //进气歧管增压压力
//    private Integer IntakeQGpressure;
    //相对压力
//    private Integer relativePress;
    //发动机扭矩模式
    private String engineTorqueMode;
    //机油压力
    private String oilPressure;
    //尿素液位
//    private Short UreaLevel;
    //状态标志位
//    private Long stateFlag;
    //制动踏板开度
//    private Short brakePedalOpen;
    //GSP方向
    private String gpsDir;
    //空调压缩机状态
    private String airCompressorStatus;
    //变速箱输出轴转速
    private String transmissionOutputSpeed;
    //告警信息
    private String alarmInfo;

    //GPS时间
    private String dataTime;

    @Override
    public String toString() {
        return "PeriodicReportDTO{" +
                "vid='" + vid + '\'' +
                ", rpm='" + rpm + '\'' +
                ", instrumentSpeed='" + instrumentSpeed + '\'' +
                ", wheelSpeed='" + wheelSpeed + '\'' +
                ", gpsSpeed='" + gpsSpeed + '\'' +
                ", throttleOpening='" + throttleOpening + '\'' +
                ", percentagetorque='" + percentagetorque + '\'' +
                ", switchs='" + switchs + '\'' +
                ", engineFuelRate='" + engineFuelRate + '\'' +
                ", fuelLevel='" + fuelLevel + '\'' +
                ", waterTemp='" + waterTemp + '\'' +
                ", engineTorqueMode='" + engineTorqueMode + '\'' +
                ", oilPressure='" + oilPressure + '\'' +
                ", gpsDir='" + gpsDir + '\'' +
                ", airCompressorStatus='" + airCompressorStatus + '\'' +
                ", transmissionOutputSpeed='" + transmissionOutputSpeed + '\'' +
                ", alarmInfo='" + alarmInfo + '\'' +
                ", dataTime='" + dataTime + '\'' +
                '}';
    }

    public String getAlarmInfo() {
        return alarmInfo;
    }

    public void setAlarmInfo(String alarmInfo) {
        this.alarmInfo = alarmInfo;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getRpm() {
        return rpm;
    }

    public void setRpm(String rpm) {
        this.rpm = rpm;
    }

    public String getInstrumentSpeed() {
        return instrumentSpeed;
    }

    public void setInstrumentSpeed(String instrumentSpeed) {
        this.instrumentSpeed = instrumentSpeed;
    }

    public String getWheelSpeed() {
        return wheelSpeed;
    }

    public void setWheelSpeed(String wheelSpeed) {
        this.wheelSpeed = wheelSpeed;
    }

    public String getGpsSpeed() {
        return gpsSpeed;
    }

    public void setGpsSpeed(String gpsSpeed) {
        this.gpsSpeed = gpsSpeed;
    }

    public String getThrottleOpening() {
        return throttleOpening;
    }

    public void setThrottleOpening(String throttleOpening) {
        this.throttleOpening = throttleOpening;
    }

    public String getPercentagetorque() {
        return percentagetorque;
    }

    public void setPercentagetorque(String percentagetorque) {
        this.percentagetorque = percentagetorque;
    }

    public String getSwitchs() {
        return switchs;
    }

    public void setSwitchs(String switchs) {
        this.switchs = switchs;
    }

    public String getEngineFuelRate() {
        return engineFuelRate;
    }

    public void setEngineFuelRate(String engineFuelRate) {
        this.engineFuelRate = engineFuelRate;
    }

    public String getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(String fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public String getWaterTemp() {
        return waterTemp;
    }

    public void setWaterTemp(String waterTemp) {
        this.waterTemp = waterTemp;
    }

    public String getEngineTorqueMode() {
        return engineTorqueMode;
    }

    public void setEngineTorqueMode(String engineTorqueMode) {
        this.engineTorqueMode = engineTorqueMode;
    }

    public String getOilPressure() {
        return oilPressure;
    }

    public void setOilPressure(String oilPressure) {
        this.oilPressure = oilPressure;
    }

    public String getGpsDir() {
        return gpsDir;
    }

    public void setGpsDir(String gpsDir) {
        this.gpsDir = gpsDir;
    }

    public String getAirCompressorStatus() {
        return airCompressorStatus;
    }

    public void setAirCompressorStatus(String airCompressorStatus) {
        this.airCompressorStatus = airCompressorStatus;
    }

    public String getTransmissionOutputSpeed() {
        return transmissionOutputSpeed;
    }

    public void setTransmissionOutputSpeed(String transmissionOutputSpeed) {
        this.transmissionOutputSpeed = transmissionOutputSpeed;
    }
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

}
