package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity;

import java.io.Serializable;

/**
 * 报警信息
 * Created by yanghs on 2018/5/9.
 */
public class VehicleAlarmDTO implements Serializable {



    private String vid;

    /**
     * gps时间
     */
    private String gpsTime= null;

    /**
     * 故障内容
     */
    private String alarms;
    /**
     * 故障代码
     */
    private String alarm;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getGpsTime() {
        return gpsTime;
    }

    public void setGpsTime(String gpsTime) {
        this.gpsTime = gpsTime;
    }

    public String getAlarms() {
        return alarms;
    }

    public void setAlarms(String alarms) {
        this.alarms = alarms;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    @Override
    public String toString() {
        return "VehicleAlarmDTO{" +
                "vid='" + vid + '\'' +
                ", gpsTime='" + gpsTime + '\'' +
                ", alarms='" + alarms + '\'' +
                ", alarm='" + alarm + '\'' +
                '}';
    }
}
