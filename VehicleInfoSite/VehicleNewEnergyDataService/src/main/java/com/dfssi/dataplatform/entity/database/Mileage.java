package com.dfssi.dataplatform.entity.database;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/28 20:47
 */
public class Mileage {
    private String  vin;
    private long day;
    private double totaltime;
    private double startMile;
    private double endMile;
    private double onlineMile;
    private double validMile;
    private double verifyMile;
    private double gpsMile;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public double getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(double totaltime) {
        this.totaltime = totaltime;
    }

    public double getStartMile() {
        return startMile;
    }

    public void setStartMile(double startMile) {
        this.startMile = startMile;
    }

    public double getEndMile() {
        return endMile;
    }

    public void setEndMile(double endMile) {
        this.endMile = endMile;
    }

    public double getOnlineMile() {
        return onlineMile;
    }

    public void setOnlineMile(double onlineMile) {
        this.onlineMile = onlineMile;
    }

    public double getValidMile() {
        return validMile;
    }

    public void setValidMile(double validMile) {
        this.validMile = validMile;
    }

    public double getVerifyMile() {
        return verifyMile;
    }

    public void setVerifyMile(double verifyMile) {
        this.verifyMile = verifyMile;
    }

    public double getGpsMile() {
        return gpsMile;
    }

    public void setGpsMile(double gpsMile) {
        this.gpsMile = gpsMile;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Mileage{");
        sb.append("vin='").append(vin).append('\'');
        sb.append(", day=").append(day);
        sb.append(", totaltime=").append(totaltime);
        sb.append(", startMile=").append(startMile);
        sb.append(", endMile=").append(endMile);
        sb.append(", onlineMile=").append(onlineMile);
        sb.append(", validMile=").append(validMile);
        sb.append(", verifyMile=").append(verifyMile);
        sb.append(", gpsMile=").append(gpsMile);
        sb.append('}');
        return sb.toString();
    }
}
