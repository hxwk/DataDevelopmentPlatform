package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/23 17:31
 */
public class VehicleFuelAnalysisInDaysEntity {

    private String vid;
    private long day;

    private double totalmile;
    private double totalfuel;
    private long totaltime;

    @JsonIgnore
    private String speedpair;
    @JsonIgnore
    private String rpmpair;
    @JsonIgnore
    private String accpair;
    @JsonIgnore
    private String  gearpair;
    @JsonIgnore
    private String gearspeedpair;
    @JsonIgnore
    private double totalbrakemile;
    @JsonIgnore
    private double totalidlefuel;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public String getSpeedpair() {
        return speedpair;
    }

    public void setSpeedpair(String speedpair) {
        this.speedpair = speedpair;
    }

    public String getRpmpair() {
        return rpmpair;
    }

    public void setRpmpair(String rpmpair) {
        this.rpmpair = rpmpair;
    }

    public String getAccpair() {
        return accpair;
    }

    public void setAccpair(String accpair) {
        this.accpair = accpair;
    }

    public String getGearpair() {
        return gearpair;
    }

    public void setGearpair(String gearpair) {
        this.gearpair = gearpair;
    }

    public String getGearspeedpair() {
        return gearspeedpair;
    }

    public void setGearspeedpair(String gearspeedpair) {
        this.gearspeedpair = gearspeedpair;
    }

    public double getTotalmile() {
        return totalmile;
    }

    public void setTotalmile(double totalmile) {
        this.totalmile = totalmile;
    }

    public double getTotalfuel() {
        return totalfuel;
    }

    public void setTotalfuel(double totalfuel) {
        this.totalfuel = totalfuel;
    }

    public double getTotaltime() {
        BigDecimal bg = new BigDecimal(totaltime * 1.0/ (60 * 60 * 1000));
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public void setTotaltime(long totaltime) {
        this.totaltime = totaltime;
    }

    public double getTotalbrakemile() {
        return totalbrakemile;
    }

    public void setTotalbrakemile(double totalbrakemile) {
        this.totalbrakemile = totalbrakemile;
    }

    public double getTotalidlefuel() {
        return totalidlefuel;
    }

    public void setTotalidlefuel(double totalidlefuel) {
        this.totalidlefuel = totalidlefuel;
    }
}
