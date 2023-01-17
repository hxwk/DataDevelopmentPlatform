package com.dfssi.dataplatform.entity.count;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/4/24 11:34
 */
public class TotalRunningMsg implements Serializable {

    private double totalMileage;

    private long totalDuration;

    private double totalPowerConsumption;

    @JsonIgnore
    private int totalcharge;

    @JsonIgnore
    private int totalrun;

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

    public int getTotalcharge() {
        return totalcharge;
    }

    public void setTotalcharge(int totalcharge) {
        this.totalcharge = totalcharge;
    }

    public int getTotalrun() {
        return totalrun;
    }

    public void setTotalrun(int totalrun) {
        this.totalrun = totalrun;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TotalRunningMsg{");
        sb.append("totalMileage=").append(totalMileage);
        sb.append(", totalDuration=").append(totalDuration);
        sb.append(", totalPowerConsumption=").append(totalPowerConsumption);
        sb.append(", totalcharge=").append(totalcharge);
        sb.append(", totalrun=").append(totalrun);
        sb.append('}');
        return sb.toString();
    }
}
