package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/5/23 19:13
 */
public class VehicleFuelDistributedEntity {

    @JsonIgnore
    private String distributedType;
    //@JsonIgnore
    //private NumberFormat format;

    private int level;
    private int levelLength;
    private long count;
    private long time;
    private double mile;
    private double fuel;

    public VehicleFuelDistributedEntity(String distributedType,
                                        int level,
                                        int levelLength,
                                        long count,
                                        long time,
                                        double mile,
                                        double fuel) {
        this.distributedType = distributedType;
        this.level = level;
        this.levelLength = levelLength;
        this.count = count;
        this.time = time;
        this.mile = mile;
        this.fuel = fuel;

        //format = NumberFormat.getInstance();
        //format.setGroupingUsed(false);
        //format.setMaximumFractionDigits(16);
    }

    public int getLevel() {
        return level;
    }

    public int getLevelLength() {
        return levelLength;
    }

    public long getCount() {
        return count;
    }

    public double getTime() {
        BigDecimal bg = new BigDecimal(time * 1.0/ (60 * 60 * 1000));
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public double getMile() {
        BigDecimal bg = new BigDecimal(mile);
        return bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public double getFuel() {
        BigDecimal bg = new BigDecimal(fuel);
        return bg.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleFuelDistributedEntity that = (VehicleFuelDistributedEntity) o;

        boolean status = (this.level == that.level &&
                Objects.equals(this.distributedType, that.distributedType));
        if(status){
            that.count += this.count;
            that.time  += this.time;
            that.mile  += this.mile;
            that.fuel  += this.fuel;
        }

        return status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distributedType, level);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VehicleFuelDistributedEntity{");
        sb.append("level=").append(level);
        sb.append(", levelLength=").append(levelLength);
        sb.append(", count=").append(count);
        sb.append(", time=").append(time);
        sb.append(", mile=").append(mile);
        sb.append(", fuel=").append(fuel);
        sb.append('}');
        return sb.toString();
    }
}
