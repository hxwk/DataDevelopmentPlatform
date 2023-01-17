package com.dfssi.dataplatform.analysis.fuel.stats;

import java.io.Serializable;
import java.util.Objects;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/2/13 9:00
 */
public class SimpleFuel implements Serializable{

    private double speed;
    private double mile;
    private double fuel;

    private long gpsTime;

    public SimpleFuel(double speed, double mile, double fuel, long gpsTime) {
        this.speed = speed;
        this.mile = mile;
        this.fuel = fuel;
        this.gpsTime = gpsTime;
    }

    public double getSpeed() {
        return speed;
    }

    public double getMile() {
        return mile;
    }

    public double getFuel() {
        return fuel;
    }

    public long getGpsTime() {
        return gpsTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleFuel that = (SimpleFuel) o;
        return Double.compare(that.mile, mile) == 0 &&
                gpsTime == that.gpsTime;
    }

    @Override
    public int hashCode() {

        return Objects.hash(mile, gpsTime);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SimpleFuel{");
        sb.append("speed=").append(speed);
        sb.append(", mile=").append(mile);
        sb.append(", fuel=").append(fuel);
        sb.append(", gpsTime=").append(gpsTime);
        sb.append('}');
        return sb.toString();
    }
}
