package com.dfssi.dataplatform.entity.database;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/29 11:51
 */
public class EvsVehicleTypeDetect {

    private long day;
    private String vin;
    private int errorCount;
    private int totalCount;

    private boolean missing = false;
    private boolean overRate = false;
    private double errorRate;
    private double standardRate;

    public String getDay() {
        return String.valueOf(day);
    }

    public void setDay(long day) {
        this.day = day;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isMissing() {
        return missing;
    }

    public void setMissing(boolean missing) {
        this.missing = missing;
    }

    public boolean isOverRate() {
        return overRate;
    }

    public void setOverRate(boolean overRate) {
        this.overRate = overRate;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }

    public double getStandardRate() {
        return standardRate;
    }

    public void setStandardRate(double standardRate) {
        this.standardRate = standardRate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EvsVehicleTypeDetect{");
        sb.append("day=").append(day);
        sb.append(", vin='").append(vin).append('\'');
        sb.append(", errorCount=").append(errorCount);
        sb.append(", totalCount=").append(totalCount);
        sb.append(", missing=").append(missing);
        sb.append(", overRate=").append(overRate);
        sb.append(", errorRate=").append(errorRate);
        sb.append(", standardRate=").append(standardRate);
        sb.append('}');
        return sb.toString();
    }
}
