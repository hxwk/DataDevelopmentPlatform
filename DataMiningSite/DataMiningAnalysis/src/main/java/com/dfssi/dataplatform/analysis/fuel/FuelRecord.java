package com.dfssi.dataplatform.analysis.fuel;

import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/20 13:20
 */
public class FuelRecord implements Comparable<FuelRecord>, Serializable {

    //数据去重id
    private String fuelId;

    //数据中的id
    private String id;

    //汽车sim卡号
    private String sim;

    //汽车唯一识别码
    private String vid;

    //累计里程    单位：km
    private double totalmile;

    //是否为高原
    private int isplateau;

    //是否为eco省油模式
    private int iseco;

    //累计油耗    单位：L
    private double totalfuel;

    //经度
    private double lon;

    //纬度
    private double lat;

    //高程    单位：m
    private double alt;

    //数据上传时间 精确到毫秒
    private long uploadtime;

    private boolean isOn;

    private String tripid;

    //线路id
    private String routeid;

    //驾驶异常告警信息
    private Set<String> alarms;

    //驾驶异常告警程度
    private int alarmDegree = -1;

    private double speed;

    private long interval = 0L;
    private double mileGap = 0.0;
    private double fuelGap = 0.0;

    public FuelRecord() { }

    public FuelRecord(String fuelId,
                      String id,
                      String sim,
                      String vid,
                      double totalmile,
                      int isplateau,
                      int iseco,
                      double totalfuel,
                      double lon,
                      double lat,
                      double alt,
                      long uploadtime,
                      boolean isOn,
                      String tripid,
                      String routeid) {

        this.fuelId = fuelId;
        this.id = id;
        this.sim = sim;
        this.vid = vid;
        this.totalmile = totalmile;
        this.isplateau = isplateau;
        this.iseco = iseco;
        this.totalfuel = totalfuel;
        this.lon = lon;
        this.lat = lat;
        this.alt = alt;
        this.uploadtime = uploadtime;
        this.isOn = isOn;
        this.tripid = tripid;
        this.routeid = routeid;
    }

    @Override
    public int compareTo(FuelRecord o) {
        return Long.compare(uploadtime, o.uploadtime);
    }

    /**
     * @param o
     * @return
     *
     * scala的hashSet和java的hashSet在比对数据是刚好相反的
     * scala是拿未入集合的数据 与集合的数据进行比较 即 o  为未入集合的数据
     * java是拿集合中的数据  与 未入集合的数据比较  即 o 为集合总的数据
     *
     *
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FuelRecord that = (FuelRecord) o;
        boolean equals = Objects.equals(fuelId, that.fuelId);

        //当两条数据的经纬度 和 总计里程相同的情况下 更新集合中的数据到最新状态
        if(equals){
            if(that.uploadtime > this.uploadtime){
                this.uploadtime = that.uploadtime;
                this.id = that.id;
                this.isOn = that.isOn;
                this.speed = that.speed;
            }
            if(that.totalfuel > this.totalfuel)this.totalfuel = that.totalfuel;
            if(that.alarmDegree > this.alarmDegree)this.alarmDegree = that.alarmDegree;

            if(that.alarms != null){
                if(this.alarms == null){
                    this.alarms = that.alarms;
                }else {
                    this.alarms.addAll(that.alarms);
                }
            }

        }

        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fuelId);
    }

    public String getFuelId() {
        return fuelId;
    }

    public void setFuelId(String fuelId) {
        this.fuelId = fuelId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public double getTotalmile() {
        return totalmile;
    }

    public void setTotalmile(double totalmile) {
        this.totalmile = totalmile;
    }

    public int getIsplateau() {
        return isplateau;
    }

    public void setIsplateau(int isplateau) {
        this.isplateau = isplateau;
    }

    public int getIseco() {
        return iseco;
    }

    public void setIseco(int iseco) {
        this.iseco = iseco;
    }

    public double getTotalfuel() {
        return totalfuel;
    }

    public void setTotalfuel(double totalfuel) {
        this.totalfuel = totalfuel;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public long getUploadtime() {
        return uploadtime;
    }

    public void setUploadtime(long uploadtime) {
        this.uploadtime = uploadtime;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public String getTripid() {
        return tripid;
    }

    public void setTripid(String tripid) {
        this.tripid = tripid;
    }

    public String getRouteid() {
        return routeid;
    }

    public void setRouteid(String routeid) {
        this.routeid = routeid;
    }

    public Set<String> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<String> alarms) {
        if(alarms != null)this.alarms = Sets.newHashSet(alarms);
    }

    public int getAlarmDegree() {
        return alarmDegree;
    }

    public void setAlarmDegree(int alarmDegree) {
        this.alarmDegree = alarmDegree;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setFuelMsg(long interval, double mileGap, double fuelGap){
        this.interval = interval;
        this.mileGap = mileGap;
        this.fuelGap = fuelGap;
    }

    public long getInterval() {
        return interval;
    }

    public double getMileGap() {
        return mileGap;
    }

    public double getFuelGap() {
        return fuelGap;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FuelRecord{");
        sb.append("fuelId='").append(fuelId).append('\'');
        sb.append(", id='").append(id).append('\'');
        sb.append(", sim='").append(sim).append('\'');
        sb.append(", vid='").append(vid).append('\'');
        sb.append(", totalmile=").append(totalmile);
        sb.append(", isplateau=").append(isplateau);
        sb.append(", iseco=").append(iseco);
        sb.append(", totalfuel=").append(totalfuel);
        sb.append(", lon=").append(lon);
        sb.append(", lat=").append(lat);
        sb.append(", alt=").append(alt);
        sb.append(", uploadtime=").append(uploadtime);
        sb.append(", isOn=").append(isOn);
        sb.append(", tripid='").append(tripid).append('\'');
        sb.append(", routeid='").append(routeid).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
