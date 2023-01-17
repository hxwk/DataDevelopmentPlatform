package com.dfssi.dataplatform.entity.count;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/24 11:34
 */
public class TotalMileAndTime implements Serializable {

    private double totalMileage;

    private long totalDuration;

    private double totalPowerConsumption;

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

    public double getTotalPowerConsumption() {
        return totalPowerConsumption / 1000.0;
    }

    public void setTotalPowerConsumption(double totalPowerConsumption) {
        this.totalPowerConsumption = totalPowerConsumption;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TotalMileAndTime{");
        sb.append("totalMileage=").append(totalMileage);
        sb.append(", totalDuration=").append(totalDuration);
        sb.append('}');
        return sb.toString();
    }
}
