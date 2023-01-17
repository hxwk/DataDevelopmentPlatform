package com.dfssi.dataplatform.entity.count;

import java.math.BigDecimal;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/3 19:18
 */
public class VehicleOnlineCount {

    private String enterprise;

    private String hatchback;

    private int totalVehicle;
    private long totalDuration;

    public Double getTotalDuration() {
        BigDecimal bg = new BigDecimal(totalDuration * 1.0/ (60 * 60 * 1000));
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }


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

    public int getTotalVehicle() {
        return totalVehicle;
    }

    public void setTotalVehicle(int totalVehicle) {
        this.totalVehicle = totalVehicle;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VehicleOnlineCount{");
        sb.append("enterprise='").append(enterprise).append('\'');
        sb.append(", hatchback='").append(hatchback).append('\'');
        sb.append(", totalVehicle=").append(totalVehicle);
        sb.append(", totalDuration=").append(totalDuration);
        sb.append('}');
        return sb.toString();
    }
}
