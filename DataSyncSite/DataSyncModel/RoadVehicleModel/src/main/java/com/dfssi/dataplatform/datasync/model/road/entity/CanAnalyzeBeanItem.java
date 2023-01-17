package com.dfssi.dataplatform.datasync.model.road.entity;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * @author jianKang
 * @date 2018/01/19
 */
public class CanAnalyzeBeanItem implements Serializable {
    public String id;
    private String sim;
    private String vid;
    private String dbcType;
    private String msgId;

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
    //CAN 数据项个数
    private int itemNum;
    //CAN 总线数据接收时间
    private long receiveTime;
    //CAN 总线数据项
    private List<CanAnalyzeSignal> messageBeanList;

    public String getId() {
        return id == null ? UUID.randomUUID().toString() : id;
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
        return sim == null ? "unknown" : sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
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

    public String getDbcType() {
        return dbcType == null ? "spf" : dbcType;
    }

    public void setDbcType(String dbcType) {
        this.dbcType = dbcType;
    }

    public long getReceiveTime() {
        return receiveTime == 0 ? System.currentTimeMillis() : receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public List<CanAnalyzeSignal> getMessageBeanList() {
        return messageBeanList;
    }

    public void setMessageBeanList(List<CanAnalyzeSignal> messageBeanList) {
        this.messageBeanList = messageBeanList;
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    @Override
    public String toString() {
        return "CanAnalyzeBeanItem{" +
                "id='" + id + '\'' +
                ", sim='" + sim + '\'' +
                ", vid='" + vid + '\'' +
                ", dbcType='" + dbcType + '\'' +
                ", msgId='" + msgId + '\'' +
                ", alarm=" + alarm +
                ", alarms=" + alarms +
                ", state=" + state +
                ", vehicleStatus=" + vehicleStatus +
                ", lon=" + lon +
                ", lat=" + lat +
                ", alt=" + alt +
                ", speed=" + speed +
                ", dir=" + dir +
                ", itemNum=" + itemNum +
                ", receiveTime=" + receiveTime +
                ", messageBeanList=" + messageBeanList +
                '}';
    }
}
