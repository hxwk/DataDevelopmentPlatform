package com.dfssi.dataplatform.entity;

import com.dfssi.dataplatform.utils.NumberUtil;

/**
 * Description:
 *      特征分析 基本信息
 * @author LiXiaoCong
 * @version 2018/7/24 20:44
 */
public class FeatureAnalysisBasicInformation {

    private double dayMile;
    private double tripMile;
    private double dayDrivingTime;
    private double tripDrivingTime;
    private double dayTrips;
    private double dayEnergyConsumption;

    public double getDayMile() {
        return NumberUtil.rounding(dayMile, 1);
    }

    public void setDayMile(double dayMile) {
        this.dayMile = dayMile;
    }

    public double getTripMile() {
        return NumberUtil.rounding(tripMile, 1);
    }

    public void setTripMile(double tripMile) {
        this.tripMile = tripMile;
    }

    public double getDayDrivingTime() {
        return NumberUtil.rounding(dayDrivingTime /(1000 * 60.0), 1);
    }

    public void setDayDrivingTime(double dayDrivingTime) {
        this.dayDrivingTime = dayDrivingTime;
    }

    public double getTripDrivingTime() {
        return NumberUtil.rounding(tripDrivingTime / (1000 * 60.0), 1);
    }

    public void setTripDrivingTime(double tripDrivingTime) {
        this.tripDrivingTime = tripDrivingTime;
    }

    public double getDayTrips() {
        return NumberUtil.rounding(dayTrips, 1);
    }

    public void setDayTrips(double dayTrips) {
        this.dayTrips = dayTrips;
    }

    public double getDayEnergyConsumption() {
        return NumberUtil.rounding(dayEnergyConsumption, 1);
    }

    public void setDayEnergyConsumption(double dayEnergyConsumption) {
        this.dayEnergyConsumption = dayEnergyConsumption;
    }
}
