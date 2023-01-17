package com.dfssi.dataplatform.datasync.model.road.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-06 10:13
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.List;

/**
 * CAN总线数据上传
 */
public class Req_0705 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.0705";
    }

    private String dbcType;

    private String sim;

    /**
     * 新增位置信息
     */
    //报警标志位
    private int alarm = 0;
    //报警信息列表
    private List<String> alarms;
    //状态位
    private int state = 0;
    //车辆状态列表
    private List<String> vehicleStatus;
    //经度
    private int lon;
    //纬度
    private int lat;
    //高程
    private short alt;
    //Gps speed
    private short speed;
    //方向
    private short dir;

    private String canBusDataReceiveTime; //CAN总线数据接收时间
    private List<CanAnalyzeSignal> canBusParamItems; //CAN总线数据项列表

    public String getCanBusDataReceiveTime() {
        return canBusDataReceiveTime;
    }

    public void setCanBusDataReceiveTime(String canBusDataReceiveTime) {
        this.canBusDataReceiveTime = canBusDataReceiveTime;
    }

    public List<CanAnalyzeSignal> getCanBusParamItems() {
        return canBusParamItems;
    }

    public void setCanBusParamItems(List<CanAnalyzeSignal> canBusParamItems) {
        this.canBusParamItems = canBusParamItems;
    }

    public String getDbcType() {
        return dbcType;
    }

    public void setDbcType(String dbcType) {
        this.dbcType = dbcType;
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

    public short getAlt() {
        return alt;
    }

    public void setAlt(short alt) {
        this.alt = alt;
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

    @Override
    public String toString() {
        return "Req_0705{" +
                "dbcType='" + dbcType + '\'' +
                ", sim='" + sim + '\'' +
                ", alarm=" + alarm +
                ", alarms=" + alarms +
                ", state=" + state +
                ", vehicleStatus=" + vehicleStatus +
                ", lon=" + lon +
                ", lat=" + lat +
                ", alt=" + alt +
                ", speed=" + speed +
                ", dir=" + dir +
                ", canBusDataReceiveTime='" + canBusDataReceiveTime + '\'' +
                ", canBusParamItems=" + canBusParamItems +
                '}';
    }
}
