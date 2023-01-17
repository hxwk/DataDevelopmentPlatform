package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity;

import java.math.BigDecimal;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/23 17:31
 */
public class VehicleFuelCountByDaysEntity {

    private String vid;
    private double totalmile;
    private double totalfuel;
    private long totaltime;


    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
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
}
