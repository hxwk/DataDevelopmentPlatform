package com.dfssi.dataplatform.entity.count;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/24 11:34
 */
public class VehicleMileAndTime implements Serializable {

    private String enterprise;
    private String hatchback;
    private String vin;
    private double totalMileage;
    private long totalDuration;

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String enterprise) {
        this.enterprise = enterprise;
    }

    public String getHatchback() {
        return hatchback;
    }

    public void setHatchback(String hatchback) {
        this.hatchback = hatchback;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public double getTotalMileage() {
        BigDecimal bg = new BigDecimal(totalMileage);
        return  bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public void setTotalMileage(double totalMileage) {
        this.totalMileage = totalMileage;
    }

    public Double getTotalDuration() {
        BigDecimal bg = new BigDecimal(totalDuration * 1.0/ (60 * 60 * 1000));
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VehicleMileAndTime{");
        sb.append("enterprise='").append(enterprise).append('\'');
        sb.append(", hatchback='").append(hatchback).append('\'');
        sb.append(", vin='").append(vin).append('\'');
        sb.append(", totalMileage=").append(totalMileage);
        sb.append(", totalDuration=").append(totalDuration);
        sb.append('}');
        return sb.toString();
    }
}
