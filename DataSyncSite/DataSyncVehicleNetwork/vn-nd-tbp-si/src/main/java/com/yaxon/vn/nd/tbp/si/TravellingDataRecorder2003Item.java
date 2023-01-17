package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 孙震
 * Time: 2014-02-25 11:30
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 行驶记录仪03部标数据项
 */

public class TravellingDataRecorder2003Item implements Serializable {
    private Date startTime; //行驶开始时间
    private Date endTime; //行驶结束时间
    private String lic; //驾驶证号码：ASCII码字符
    private String vin; //车辆vin号：ASCII码字符
    private String lpn; //车牌号：ASCII码字符
    private String lpnType; //车牌分类
    private Integer driverCode; //驾驶员代码
    private Integer mileage; //累计行驶里程（单位：0.1公里）
    private Integer characterCoefficient; //车辆特征系数
    private Short speed; //行驶速度

    private List<AccidentSuspicionItem> accidentSuspicionItems; //事故疑点\行驶速度数据项
    private List<OvertimeItem> overtimeItems; //超时数据项

    public TravellingDataRecorder2003Item() {

    }

    public TravellingDataRecorder2003Item(Date startTime, Date endTime, String lic, String vin,
                                          String lpn, String lpnType, Integer driverCode,
                                          Integer mileage, Integer characterCoefficient, Short speed,
                                          List<AccidentSuspicionItem> accidentSuspicionItems,
                                          List<OvertimeItem> overtimeItems) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.lic = lic;
        this.vin = vin;
        this.lpn = lpn;
        this.lpnType = lpnType;
        this.driverCode = driverCode;
        this.mileage = mileage;
        this.characterCoefficient = characterCoefficient;
        this.speed = speed;
        this.accidentSuspicionItems = accidentSuspicionItems;
        this.overtimeItems = overtimeItems;
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

    public Integer getDriverCode() {
        return driverCode;
    }

    public void setDriverCode(Integer driverCode) {
        this.driverCode = driverCode;
    }

    public List<AccidentSuspicionItem> getAccidentSuspicionItems() {
        return accidentSuspicionItems;
    }

    public void setAccidentSuspicionItems(List<AccidentSuspicionItem> accidentSuspicionItems) {
        this.accidentSuspicionItems = accidentSuspicionItems;
    }

    public List<OvertimeItem> getOvertimeItems() {
        return overtimeItems;
    }

    public void setOvertimeItems(List<OvertimeItem> overtimeItems) {
        this.overtimeItems = overtimeItems;
    }

    public Short getSpeed() {
        return speed;
    }

    public void setSpeed(Short speed) {
        this.speed = speed;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Integer getCharacterCoefficient() {
        return characterCoefficient;
    }

    public void setCharacterCoefficient(Integer characterCoefficient) {
        this.characterCoefficient = characterCoefficient;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public String getLpnType() {
        return lpnType;
    }

    public void setLpnType(String lpnType) {
        this.lpnType = lpnType;
    }

    @Override
    public String toString() {
        return "TravellingDataRecorder2003Item{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", lic='" + lic + '\'' +
                ", vin='" + vin + '\'' +
                ", lpn='" + lpn + '\'' +
                ", lpnType='" + lpnType + '\'' +
                ", driverCode=" + driverCode +
                ", mileage=" + mileage +
                ", characterCoefficient=" + characterCoefficient +
                ", speed=" + speed +
                ", accidentSuspicionItems=" + accidentSuspicionItems +
                ", overtimeItems=" + overtimeItems +
                '}';
    }
}
