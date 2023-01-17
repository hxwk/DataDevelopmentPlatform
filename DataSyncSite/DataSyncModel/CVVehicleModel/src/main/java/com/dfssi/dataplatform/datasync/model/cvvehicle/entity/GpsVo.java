package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: 程行荣
 * Time: 2013-12-06 15:00
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 * @modifier Jiankang
 * @modify Date 2018-02-21
 */

public class GpsVo implements Serializable {
    private String id;

    private String vid;

    private String sim;
    //报警标志位
    private int alarm=0;
    //报警信息列表
    private List<String> alarms ;
    //状态位
    private int state=0;
    //车辆状态列表
    private List<String> vehicleStatus;
    //自定义消息接收时间
    private long receiveMsgTime;

    private Date gpsTime;

    private String gpsTimestamp;

    private int lon;

    private int lat;
    //Gps speed
    private short speed;

    private short dir;//方向

    private short alt;//高程

    private Integer mile;

    private Float fuel;
    //行驶记录仪速度
    private short speed1;

    private int signalState;
    //车辆扩展状态
    private List<String> signalStates;

    private Short ioState;
    //千里眼协议上传标识 1：是
    private byte fromQly;
    //电子运单内容
    private String content;
    //累计油耗
    private double cumulativeOilConsumption = 0L;
    //总计油耗
    private long totalFuelConsumption;
    //电瓶电压
    private short batteryVoltage;
    //油箱液压
    private int hydraulicTank;
    //F2报警信息code
    private long alarmInforCode;
    //F2报警信息列表
    private List<String> alarmLists;
    //车载重量
    private float vehicleWeight;
    //can and 油箱液压状态
    private long canAndHydraulicTankStatus;
    //消息id
    private String msgId;
    //驾驶行为异常警报类型
    private List<String> abnormalDrivingBehaviorAlarmType;
    //驾驶行为异常警报程度
    private Short abnormalDrivingBehaviorAlarmDegree;
    //附加信息列表
    private List<ExtraInfoItem> extraInfoItems;
    //发送机转速
    private float engineSpeed;
    //发动机实际输出扭矩百分比
    private int  torquePercentage;
    //油门开度
    private int throttleOpen;


    private Short sensorSpeed;//传感器车速
    private Short wheelSpeed;//车轮车速
    private int braking;//制动信号
    private int gear;//档位信号
    private int brakeStatus;//刹车开合状态
    private int absStatus;//abs状态
    private String collTime; //采集时间精确到ms

    private byte highPrecisionFlag;//是否高精度
    private String highPrecisionLon;//高精度经度
    private String highPrecisionLat;//高精度纬度
    private int direction;//方向
    private int absSpeed;//abs速度
    private String absTime;//abs计算所需时间

    public GpsVo() {
        alarms = Lists.newArrayList();
        vehicleStatus = Lists.newArrayList();
        signalStates = Lists.newArrayList();
        abnormalDrivingBehaviorAlarmType = Lists.newArrayList();
        extraInfoItems = Lists.newArrayList();
    }

    public Short getSensorSpeed() {
        return sensorSpeed;
    }

    public void setSensorSpeed(Short sensorSpeed) {
        this.sensorSpeed = sensorSpeed;
    }

    public Short getWheelSpeed() {
        return wheelSpeed;
    }

    public void setWheelSpeed(Short wheelSpeed) {
        this.wheelSpeed = wheelSpeed;
    }

    public int getBraking() {
        return braking;
    }

    public void setBraking(int braking) {
        this.braking = braking;
    }

    public int getGear() {
        return gear;
    }

    public void setGear(int gear) {
        this.gear = gear;
    }

    public int getBrakeStatus() {
        return brakeStatus;
    }

    public void setBrakeStatus(int brakeStatus) {
        this.brakeStatus = brakeStatus;
    }

    public int getAbsStatus() {
        return absStatus;
    }

    public void setAbsStatus(int absStatus) {
        this.absStatus = absStatus;
    }

    public String getCollTime() {
        return collTime;
    }

    public void setCollTime(String collTime) {
        this.collTime = collTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public List<String> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<String> alarms) {
        this.alarms = alarms;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<String> getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(List<String> vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public long getReceiveMsgTime() {
        return receiveMsgTime;
    }

    public void setReceiveMsgTime(long receiveMsgTime) {
        this.receiveMsgTime = receiveMsgTime;
    }

    public Date getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(Date gpsTime) {
        this.gpsTime = gpsTime;
    }

    public String getGpsTimestamp() {
        return gpsTimestamp;
    }

    public void setGpsTimestamp(String gpsTimestamp) {
        this.gpsTimestamp = gpsTimestamp;
    }

    public int getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public short getSpeed() {
        return speed;
    }

    public void setSpeed(short speed) {
        this.speed = speed;
    }

    public short getDir() {
        return dir;
    }

    public void setDir(short dir) {
        this.dir = dir;
    }

    public short getAlt() {
        return alt;
    }

    public void setAlt(short alt) {
        this.alt = alt;
    }

    public Integer getMile() {
        return mile;
    }

    public void setMile(Integer mile) {
        this.mile = mile;
    }

    public Float getFuel() {
        return fuel;
    }

    public void setFuel(Float fuel) {
        this.fuel = fuel;
    }

    public short getSpeed1() {
        return speed1;
    }

    public void setSpeed1(short speed1) {
        this.speed1 = speed1;
    }

    public int getSignalState() {
        return signalState;
    }

    public void setSignalState(int signalState) {
        this.signalState = signalState;
    }

    public List<String> getSignalStates() {
        return signalStates;
    }

    public void setSignalStates(List<String> signalStates) {
        this.signalStates = signalStates;
    }

    public Short getIoState() {
        return ioState;
    }

    public void setIoState(Short ioState) {
        this.ioState = ioState;
    }

    public byte getFromQly() {
        return fromQly;
    }

    public void setFromQly(byte fromQly) {
        this.fromQly = fromQly;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getAbnormalDrivingBehaviorAlarmType() {
        return abnormalDrivingBehaviorAlarmType;
    }

    public void setAbnormalDrivingBehaviorAlarmType(List<String> abnormalDrivingBehaviorAlarmType) {
        this.abnormalDrivingBehaviorAlarmType = abnormalDrivingBehaviorAlarmType;
    }

    public Short getAbnormalDrivingBehaviorAlarmDegree() {
        return abnormalDrivingBehaviorAlarmDegree;
    }

    public void setAbnormalDrivingBehaviorAlarmDegree(Short abnormalDrivingBehaviorAlarmDegree) {
        this.abnormalDrivingBehaviorAlarmDegree = abnormalDrivingBehaviorAlarmDegree;
    }

    public List<ExtraInfoItem> getExtraInfoItems() {
        return extraInfoItems;
    }

    public void setExtraInfoItems(List<ExtraInfoItem> extraInfoItems) {
        this.extraInfoItems = extraInfoItems;
    }

    public static class ExtraInfoItem implements Serializable {
        private int id;
        private byte[] data;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "ExtraInfoItem{" +
                    "id=" + id +
                    ", data=L" + (data == null ? 0 : data.length) +
                    '}';
        }
    }

    public double getCumulativeOilConsumption() {
        return cumulativeOilConsumption;
    }

    public void setCumulativeOilConsumption(double cumulativeOilConsumption) {
        this.cumulativeOilConsumption = cumulativeOilConsumption;
    }

    public long getTotalFuelConsumption() {
        return totalFuelConsumption;
    }

    public void setTotalFuelConsumption(long totalFuelConsumption) {
        this.totalFuelConsumption = totalFuelConsumption;
    }

    public short getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(short batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public int getHydraulicTank() {
        return hydraulicTank;
    }

    public void setHydraulicTank(int hydraulicTank) {
        this.hydraulicTank = hydraulicTank;
    }

    public long getAlarmInforCode() {
        return alarmInforCode;
    }

    public void setAlarmInforCode(long alarmInforCode) {
        this.alarmInforCode = alarmInforCode;
    }

    public List<String> getAlarmLists() {
        return alarmLists;
    }

    public void setAlarmLists(List<String> alarmLists) {
        this.alarmLists = alarmLists;
    }

    public float getVehicleWeight() {
        return vehicleWeight;
    }

    public void setVehicleWeight(float vehicleWeight) {
        this.vehicleWeight = vehicleWeight;
    }

    public long getCanAndHydraulicTankStatus() {
        return canAndHydraulicTankStatus;
    }

    public void setCanAndHydraulicTankStatus(long canAndHydraulicTankStatus) {
        this.canAndHydraulicTankStatus = canAndHydraulicTankStatus;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    public float getEngineSpeed() {
        return engineSpeed;
    }

    public void setEngineSpeed(float engineSpeed) {
        this.engineSpeed = engineSpeed;
    }

    public int getTorquePercentage() {
        return torquePercentage;
    }

    public void setTorquePercentage(int torquePercentage) {
        this.torquePercentage = torquePercentage;
    }

    public int getThrottleOpen() {
        return throttleOpen;
    }

    public void setThrottleOpen(int throttleOpen) {
        this.throttleOpen = throttleOpen;
    }

    public byte getHighPrecisionFlag() {
        return highPrecisionFlag;
    }

    public void setHighPrecisionFlag(byte highPrecisionFlag) {
        this.highPrecisionFlag = highPrecisionFlag;
    }

    public String getHighPrecisionLon() {
        return highPrecisionLon;
    }

    public void setHighPrecisionLon(String highPrecisionLon) {
        this.highPrecisionLon = highPrecisionLon;
    }

    public String getHighPrecisionLat() {
        return highPrecisionLat;
    }

    public void setHighPrecisionLat(String highPrecisionLat) {
        this.highPrecisionLat = highPrecisionLat;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getAbsSpeed() {
        return absSpeed;
    }

    public void setAbsSpeed(int absSpeed) {
        this.absSpeed = absSpeed;
    }

    public String getAbsTime() {
        return absTime;
    }

    public void setAbsTime(String absTime) {
        this.absTime = absTime;
    }

    @Override
    public String toString() {
        return "GpsVo{" +
                "id='" + id + '\'' +
                ", vid='" + vid + '\'' +
                ", sim='" + sim + '\'' +
                ", alarm=" + alarm +
                ", alarms=" + alarms +
                ", state=" + state +
                ", vehicleStatus=" + vehicleStatus +
                ", receiveMsgTime=" + receiveMsgTime +
                ", gpsTime=" + gpsTime +
                ", gpsTimestamp='" + gpsTimestamp + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                ", speed=" + speed +
                ", dir=" + dir +
                ", alt=" + alt +
                ", mile=" + mile +
                ", fuel=" + fuel +
                ", speed1=" + speed1 +
                ", signalState=" + signalState +
                ", signalStates=" + signalStates +
                ", ioState=" + ioState +
                ", fromQly=" + fromQly +
                ", content='" + content + '\'' +
                ", cumulativeOilConsumption=" + cumulativeOilConsumption +
                ", totalFuelConsumption=" + totalFuelConsumption +
                ", batteryVoltage=" + batteryVoltage +
                ", hydraulicTank=" + hydraulicTank +
                ", alarmInforCode=" + alarmInforCode +
                ", alarmLists=" + alarmLists +
                ", vehicleWeight=" + vehicleWeight +
                ", canAndHydraulicTankStatus=" + canAndHydraulicTankStatus +
                ", msgId='" + msgId + '\'' +
                ", abnormalDrivingBehaviorAlarmType=" + abnormalDrivingBehaviorAlarmType +
                ", abnormalDrivingBehaviorAlarmDegree=" + abnormalDrivingBehaviorAlarmDegree +
                ", engineSpeed='" + engineSpeed + '\'' +
                ", torquePercentage='" + torquePercentage + '\'' +
                ", throttleOpen='" + throttleOpen + '\'' +
                ", highPrecisionFlag='" + highPrecisionFlag + '\'' +
                ", highPrecisionLon='" + highPrecisionLon + '\'' +
                ", highPrecisionLat='" + highPrecisionLat + '\'' +
                ", direction='" + direction + '\'' +
                ", absSpeed='" + absSpeed + '\'' +
                ", absTime='" + absTime + '\'' +
                //", extraInfoItems=" + extraInfoItems +
                '}';
    }
}