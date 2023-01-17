package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity;

import java.io.Serializable;

/**
 * 车辆里程信息
 * Created by yanghs on 2018/5/10.
 */
public class VehicleMileageDTO implements Serializable{

    private String vid;
    private String starttime;//开始时间
    private String endtime;//结束时间
    private String totaltime;//总时间
    private String totalmile;//总里程
    private String totalfuel;//总油耗

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(String totaltime) {
        this.totaltime = totaltime;
    }

    public String getTotalmile() {
        return totalmile;
    }

    public void setTotalmile(String totalmile) {
        this.totalmile = totalmile;
    }

    public String getTotalfuel() {
        return totalfuel;
    }

    public void setTotalfuel(String totalfuel) {
        this.totalfuel = totalfuel;
    }

    @Override
    public String toString() {
        return "VehicleMileageDTO{" +
                "vid='" + vid + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", totaltime='" + totaltime + '\'' +
                ", totalmile='" + totalmile + '\'' +
                ", totalfuel='" + totalfuel + '\'' +
                '}';
    }
}
