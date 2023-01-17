package com.dfssi.dataplatform.analysis.fuel;

import com.dfssi.common.UUIDs;

import java.io.Serializable;

/**
 * Description:
 *
 * @author LiXiaoCong
 * @version 2018/1/19 10:02
 */
public class TripRecord implements Serializable{

    private String id;
    private String sim;
    private String vid;
    private long starttime;
    private long endtime;
    private long interval;
    private double starttotalmile;
    private double endtotalmile;
    private double starttotalfuel;
    private double endtotalfuel;
    private double startlat;
    private double endlat;
    private double startlon;
    private double endlon;
    private int iseco;
    private int isover;
    private int isvalid;

    private boolean empty;


    public static TripRecord newTrip(FuelRecord record){
        TripRecord tripRecord = new TripRecord();
        tripRecord.setId(UUIDs.uuidFromBytesWithNoSeparator(record.toString().getBytes()));
        boolean on = record.isOn();
        tripRecord.setIsvalid(on ? 1 : 0);
        tripRecord.setIsover(on ? 0 : 1);
        tripRecord.setSim(record.getSim());
        tripRecord.setVid(record.getVid());

        tripRecord.setStarttotalfuel(record.getTotalfuel());
        tripRecord.setEndtotalfuel(record.getTotalfuel());

        tripRecord.setStarttotalmile(record.getTotalmile());
        tripRecord.setEndtotalmile(record.getTotalmile());

        tripRecord.setStartlat(record.getLat());
        tripRecord.setEndlat(record.getLat());

        tripRecord.setStartlon(record.getLon());
        tripRecord.setEndlon(record.getLon());

        tripRecord.setStarttime(record.getUploadtime());
        tripRecord.setEndtime(record.getUploadtime());
        tripRecord.setInterval(0L);
        tripRecord.setIseco(record.getIseco());

        tripRecord.setEmpty(false);
        return tripRecord;
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

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public long getInterval() {
        return endtime - starttime;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public double getEndlat() {
        return endlat;
    }

    public void setEndlat(double endlat) {
        this.endlat = endlat;
    }

    public double getEndlon() {
        return endlon;
    }

    public void setEndlon(double endlon) {
        this.endlon = endlon;
    }

    public int getIseco() {
        return iseco;
    }

    public void setIseco(int iseco) {
        this.iseco = iseco;
    }

    public int getIsover() {
        return isover;
    }

    public void setIsover(int isover) {
        this.isover = isover;
    }

    public int getIsvalid() {
        return isvalid;
    }

    public void setIsvalid(int isvalid) {
        this.isvalid = isvalid;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isNotEmpty() {
        return !empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public double getStarttotalmile() {
        return starttotalmile;
    }

    public void setStarttotalmile(double starttotalmile) {
        this.starttotalmile = starttotalmile;
    }

    public double getStarttotalfuel() {
        return starttotalfuel;
    }

    public void setStarttotalfuel(double starttotalfuel) {
        this.starttotalfuel = starttotalfuel;
    }

    public double getEndtotalmile() {
        return endtotalmile;
    }

    public void setEndtotalmile(double endtotalmile) {
        this.endtotalmile = endtotalmile;
    }

    public double getEndtotalfuel() {
        return endtotalfuel;
    }

    public void setEndtotalfuel(double endtotalfuel) {
        this.endtotalfuel = endtotalfuel;
    }

    public double getStartlat() {
        return startlat;
    }

    public void setStartlat(double startlat) {
        this.startlat = startlat;
    }

    public double getStartlon() {
        return startlon;
    }

    public void setStartlon(double startlon) {
        this.startlon = startlon;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TripRecord{");
        sb.append("id='").append(id).append('\'');
        sb.append(", sim='").append(sim).append('\'');
        sb.append(", starttime=").append(starttime);
        sb.append(", endtime=").append(endtime);
        sb.append(", interval=").append(interval);
        sb.append(", starttotalmile=").append(starttotalmile);
        sb.append(", starttotalfuel=").append(starttotalfuel);
        sb.append(", endtotalmile=").append(endtotalmile);
        sb.append(", endtotalfuel=").append(endtotalfuel);
        sb.append(", startlat=").append(startlat);
        sb.append(", startlon=").append(startlon);
        sb.append(", endlat=").append(endlat);
        sb.append(", endlon=").append(endlon);
        sb.append(", isover=").append(isover);
        sb.append(", isvalid=").append(isvalid);
        sb.append(", empty=").append(empty);
        sb.append('}');
        return sb.toString();
    }
}
