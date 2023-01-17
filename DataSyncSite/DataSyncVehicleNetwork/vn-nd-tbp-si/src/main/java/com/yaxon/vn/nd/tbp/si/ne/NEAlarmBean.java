package com.yaxon.vn.nd.tbp.si.ne;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author JianKang
 * @date 2018/2/12
 * @description NE alarm bean
 */
public class NEAlarmBean implements Serializable {
    /**
     * 告警消息ID
     */
    private short alarmInformationType;
    /**
     * 最高报警等级
     */
    private short maxAlarmRating;
    /**
     * 最高报警等级名称
     */
    private String maxAlarmRatingName;
    /**
     * 通用报警标志
     */
    private long alarmIdentification;
    /**
     * 通用报警标志列表
     */
    private List<String> alarmIdentificationList;
    /**
     * 可充电储能装置故障总数N1
     */
    private short rechargeableStorageDeviceN1;
    /**
     * 可充电储能装置故障代码列表
     */
    private List<Long> rechargeableStorageCodeList;
    /**
     * 驱动电机故障总数N2
     */
    private short driverMotorFailureN2;
    /**
     * 驱动电机故障代码列表
     */
    private List<Long> driverMotorFailureCodeList;
    /**
     * 发动机故障总数N3
     */
    private short engineFailureN3;
    /**
     * 发动机故障列表
     */
    private List<Long> engineFailureCodeList;
    /**
     * 其他故障总数N4
     */
    private short otherFailureN4;
    /**
     * 其他故障代码列表
     */
    private List<Long> otherFailureCodeList;

    public short getAlarmInformationType() {
        return alarmInformationType;
    }

    public void setAlarmInformationType(short alarmInformationType) {
        this.alarmInformationType = alarmInformationType;
    }

    public short getMaxAlarmRating() {
        return maxAlarmRating;
    }

    public void setMaxAlarmRating(short maxAlarmRating) {
        this.maxAlarmRating = maxAlarmRating;
    }

    public String getMaxAlarmRatingName() {
        return maxAlarmRatingName;
    }

    public void setMaxAlarmRatingName(String maxAlarmRatingName) {
        this.maxAlarmRatingName = maxAlarmRatingName;
    }

    public long getAlarmIdentification() {
        return alarmIdentification;
    }

    public void setAlarmIdentification(long alarmIdentification) {
        this.alarmIdentification = alarmIdentification;
    }

    public List<String> getAlarmIdentificationList() {
        return alarmIdentificationList;
    }

    public void setAlarmIdentificationList(List<String> alarmIdentificationList) {
        this.alarmIdentificationList = alarmIdentificationList;
    }

    public short getRechargeableStorageDeviceN1() {
        return rechargeableStorageDeviceN1;
    }

    public void setRechargeableStorageDeviceN1(short rechargeableStorageDeviceN1) {
        this.rechargeableStorageDeviceN1 = rechargeableStorageDeviceN1;
    }

    public List<Long> getRechargeableStorageCodeList() {
        return rechargeableStorageCodeList;
    }

    public void setRechargeableStorageCodeList(List<Long> rechargeableStorageCodeList) {
        this.rechargeableStorageCodeList = rechargeableStorageCodeList;
    }

    public short getDriverMotorFailureN2() {
        return driverMotorFailureN2;
    }

    public void setDriverMotorFailureN2(short driverMotorFailureN2) {
        this.driverMotorFailureN2 = driverMotorFailureN2;
    }

    public List<Long> getDriverMotorFailureCodeList() {
        return driverMotorFailureCodeList;
    }

    public void setDriverMotorFailureCodeList(List<Long> driverMotorFailureCodeList) {
        this.driverMotorFailureCodeList = driverMotorFailureCodeList;
    }

    public short getEngineFailureN3() {
        return engineFailureN3;
    }

    public void setEngineFailureN3(short engineFailureN3) {
        this.engineFailureN3 = engineFailureN3;
    }

    public List<Long> getEngineFailureCodeList() {
        return engineFailureCodeList;
    }

    public void setEngineFailureCodeList(List<Long> engineFailureCodeList) {
        this.engineFailureCodeList = engineFailureCodeList;
    }

    public short getOtherFailureN4() {
        return otherFailureN4;
    }

    public void setOtherFailureN4(short otherFailureN4) {
        this.otherFailureN4 = otherFailureN4;
    }

    public List<Long> getOtherFailureCodeList() {
        return otherFailureCodeList;
    }

    public void setOtherFailureCodeList(List<Long> otherFailureCodeList) {
        this.otherFailureCodeList = otherFailureCodeList;
    }

    @Override
    public String toString() {
        alarmIdentificationList = ObjectUtils.defaultIfNull(alarmIdentificationList, Lists.<String>newArrayList());
        rechargeableStorageCodeList = ObjectUtils.defaultIfNull(rechargeableStorageCodeList, Lists.<Long>newArrayList());
        driverMotorFailureCodeList = ObjectUtils.defaultIfNull(driverMotorFailureCodeList, Lists.<Long>newArrayList());
        engineFailureCodeList = ObjectUtils.defaultIfNull(engineFailureCodeList, Lists.<Long>newArrayList());
        otherFailureCodeList = ObjectUtils.defaultIfNull(otherFailureCodeList, Lists.<Long>newArrayList());
        String body = "NEAlarmBean{" +
                "alarmInformationType=" + alarmInformationType +
                ", maxAlarmRating=" + maxAlarmRating +
                ", alarmIdentification=" + alarmIdentification +
                ", alarmIdentificationList=" + alarmIdentificationList +
                ", rechargeableStorageDeviceN1=" + rechargeableStorageDeviceN1 +
                ", rechargeableStorageCodeList=" + rechargeableStorageCodeList +
                ", driverMotorFailureN2=" + driverMotorFailureN2 +
                ", driverMotorFailureCodeList=" + driverMotorFailureCodeList +
                ", engineFailureN3=" + engineFailureN3 +
                ", engineFailureCodeList=" + engineFailureCodeList +
                ", otherFailureN4=" + otherFailureN4 +
                ", otherFailureCodeList=" + otherFailureCodeList +
                '}';
        return body;
    }
}
