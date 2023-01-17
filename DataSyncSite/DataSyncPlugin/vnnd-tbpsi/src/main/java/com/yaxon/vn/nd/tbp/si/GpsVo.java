package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Author: 程行荣
 * Time: 2013-12-06 15:00
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class GpsVo implements Serializable {

    private String id;

    private String sim;

    private int alarm;//报警标志位

    private int state;//状态位

    private Date gpsTime;

    private int lon;

    private int lat;

    private short speed;

    private short dir;//方向

    private short alt;//高程

    private Integer mile;

    private Float fuel;

    private short speed1; //行驶记录仪速度

    private Integer signalState;

    private Short ioState;

    private byte fromQly; //千里眼协议上传标识 1：是

    private String content;  //电子运单内容

    private double cumulativeOilConsumption = 0L; //累计油耗

    private long totalFuelConsumption; //总计油耗

    private int batteryVoltage; //电瓶电压

    private int hydraulicTank; //油箱液压

    private float vehicleWeight; //车载重量

    private long canAndHydraulicTankStatus; //can and 油箱液压状态

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte getFromQly() {
        return fromQly;
    }

    public void setFromQly(byte fromQly) {
        this.fromQly = fromQly;
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

    public Integer getSignalState() {
        return signalState;
    }

    public void setSignalState(Integer signalState) {
        this.signalState = signalState;
    }

    public Short getIoState() {
        return ioState;
    }

    public void setIoState(Short ioState) {
        this.ioState = ioState;
    }

    private List<ExtraInfoItem> extraInfoItems;

    public String getId() {
        return id==null? UUID.randomUUID().toString():id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    public Date getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(Date gpsTime) {
        this.gpsTime = gpsTime;
    }

    public List<ExtraInfoItem> getExtraInfoItems() {
        return extraInfoItems;
    }

    public void setExtraInfoItems(List<ExtraInfoItem> extraInfoItems) {
        this.extraInfoItems = extraInfoItems;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
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

    public int getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(int batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public int getHydraulicTank() {
        return hydraulicTank;
    }

    public void setHydraulicTank(int hydraulicTank) {
        this.hydraulicTank = hydraulicTank;
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

    public static class ExtraInfoItem implements Serializable {
        private byte id;
        private byte[] data;

        public byte getId() {
            return id;
        }

        public void setId(byte id) {
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

    @Override
    public String toString() {
        return "GpsVo{" +
                "id=" + id +
                ", sim='" + sim + '\'' +
                ", alarm=" + alarm +
                ", state=" + state +
                ", gpsTime=" + gpsTime +
                ", lon=" + lon +
                ", lat=" + lat +
                ", speed=" + speed +
                ", dir=" + dir +
                ", alt=" + alt +
                ", mile=" + mile +
                ", fuel=" + fuel +
                ", speed1=" + speed1 +
                ", signalState=" + signalState +
                ", ioState=" + ioState +
                ", fromQly=" + fromQly +
                ", content='" + content + '\'' +
                ", cumulativeOilConsumption=" + cumulativeOilConsumption +
                ", totalFuelConsumption=" + totalFuelConsumption +
                ", batteryVoltage=" + batteryVoltage +
                ", hydraulicTank=" + hydraulicTank +
                ", vehicleWeight=" + vehicleWeight +
                ", canAndHydraulicTankStatus=" + canAndHydraulicTankStatus +
                ", extraInfoItems=" + extraInfoItems +
                '}';
    }
}
