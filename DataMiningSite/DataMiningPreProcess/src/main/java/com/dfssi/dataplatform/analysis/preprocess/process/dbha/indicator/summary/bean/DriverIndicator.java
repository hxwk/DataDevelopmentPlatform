package com.dfssi.dataplatform.analysis.preprocess.process.dbha.indicator.summary.bean;

/**
 * Created by Hannibal on 2018-03-01.
 */
public class DriverIndicator {

    private String day;

    private String driverId;

    private int indicatorId;

    private int dailyCount;

    private double dailySum;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public int getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(int indicatorId) {
        this.indicatorId = indicatorId;
    }

    public int getDailyCount() {
        return dailyCount;
    }

    public void setDailyCount(int dailyCount) {
        this.dailyCount = dailyCount;
    }

    public double getDailySum() {
        return dailySum;
    }

    public void setDailySum(double dailySum) {
        this.dailySum = dailySum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DriverIndicator that = (DriverIndicator) o;

        if (indicatorId != that.indicatorId) return false;
        if (!day.equals(that.day)) return false;
        return driverId.equals(that.driverId);

    }

    @Override
    public int hashCode() {
        int result = day.hashCode();
        result = 31 * result + driverId.hashCode();
        result = 31 * result + indicatorId;
        return result;
    }
}
