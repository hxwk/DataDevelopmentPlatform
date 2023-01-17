package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Author: JIANKANG
 * Time: 2018-09-26 19:22
 */
public class FailureCodeBean implements Serializable {
    private String id;

    private String msgId;

    private String vid;

    private String sim;

    private long alarm;

    private List<String> alarms;

    private long status;

    private long latitude;//纬度

    private long longitude;//经度

    private int height;//高程

    private int speed;//速度

    private int dir;//方向

    private long time;//时间

    private short failureNum;//故障个数

    private int lat;

    private int lon;

    private List<FailureItem> failureItemList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
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

    public long getAlarm() {
        return alarm;
    }

    public void setAlarm(long alarm) {
        this.alarm = alarm;
    }

    public List<String> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<String> alarms) {
        this.alarms = alarms;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public short getFailureNum() {
        return failureNum;
    }

    public void setFailureNum(short failureNum) {
        this.failureNum = failureNum;
    }

    public List<FailureItem> getFailureItemList() {
        return failureItemList;
    }

    public void setFailureItemList(List<FailureItem> failureItemList) {
        this.failureItemList = failureItemList;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public int getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "FailureCodeBean{" +
                "id='" + id + '\'' +
                ", msgId='" + msgId + '\'' +
                ", vid='" + vid + '\'' +
                ", sim='" + sim + '\'' +
                ", alarm=" + alarm +
                ", alarms=" + alarms +
                ", status=" + status +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", height=" + height +
                ", speed=" + speed +
                ", dir=" + dir +
                ", time=" + time +
                ", failureNum=" + failureNum +
                ", failureItemList=" + failureItemList +
                '}';
    }
}
