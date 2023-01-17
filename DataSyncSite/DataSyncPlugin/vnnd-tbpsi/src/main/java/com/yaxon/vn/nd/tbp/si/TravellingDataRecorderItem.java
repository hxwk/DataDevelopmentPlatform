package com.yaxon.vn.nd.tbp.si;

/**
 * Author: Sun Zhen
 * Time: 2013-12-30 11:42
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 行驶记录仪数据项
 */

public class TravellingDataRecorderItem implements Serializable {
    private Date startTime; //行驶开始时间
    private Date endTime; //行驶结束时间
    private String lic; //驾驶证号码：ASCII码字符

    private String standardYearNo;//记录仪执行标准年号后２位
    private String modifyBillsNo;//修改单号


    private List<AccidentSuspicionItem> accidentSuspicionItems; //事故疑点数据项
    private List<DriveSpeedItem> driveSpeedItems; //行驶速度数据项
    private List<TravellingDataRecorderGpsItem> travellingDataRecorderGpsItems; //位置信息数据项

    public TravellingDataRecorderItem() {

    }

    public TravellingDataRecorderItem(Date startTime, Date endTime, String lic,
                                      List<AccidentSuspicionItem> accidentSuspicionItems,
                                      List<DriveSpeedItem> driveSpeedItems,
                                      List<TravellingDataRecorderGpsItem> travellingDataRecorderGpsItems) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.lic = lic;
        this.accidentSuspicionItems = accidentSuspicionItems;
        this.driveSpeedItems = driveSpeedItems;
        this.travellingDataRecorderGpsItems = travellingDataRecorderGpsItems;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLic() {
        return lic;
    }

    public void setLic(String lic) {
        this.lic = lic;
    }

    public String getStandardYearNo() {
        return standardYearNo;
    }

    public void setStandardYearNo(String standardYearNo) {
        this.standardYearNo = standardYearNo;
    }

    public String getModifyBillsNo() {
        return modifyBillsNo;
    }

    public void setModifyBillsNo(String modifyBillsNo) {
        this.modifyBillsNo = modifyBillsNo;
    }

    public List<AccidentSuspicionItem> getAccidentSuspicionItems() {
        return accidentSuspicionItems;
    }

    public void setAccidentSuspicionItems(List<AccidentSuspicionItem> accidentSuspicionItems) {
        this.accidentSuspicionItems = accidentSuspicionItems;
    }

    public List<TravellingDataRecorderGpsItem> getTravellingDataRecorderGpsItems() {
        return travellingDataRecorderGpsItems;
    }

    public void setTravellingDataRecorderGpsItems(List<TravellingDataRecorderGpsItem> travellingDataRecorderGpsItems) {
        this.travellingDataRecorderGpsItems = travellingDataRecorderGpsItems;
    }

    public List<DriveSpeedItem> getDriveSpeedItems() {
        return driveSpeedItems;
    }

    public void setDriveSpeedItems(List<DriveSpeedItem> driveSpeedItems) {
        this.driveSpeedItems = driveSpeedItems;
    }

    @Override
    public String toString() {
        return "TravellingDataRecorderItem{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", lic='" + lic + '\'' +
                ", accidentSuspicionItems=" + accidentSuspicionItems +
                ", driveSpeedItems=" + driveSpeedItems +
                ", travellingDataRecorderGpsItems=" + travellingDataRecorderGpsItems +
                '}';
    }
}

