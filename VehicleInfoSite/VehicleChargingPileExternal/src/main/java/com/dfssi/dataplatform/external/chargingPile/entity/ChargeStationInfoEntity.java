package com.dfssi.dataplatform.external.chargingPile.entity;

import org.apache.ibatis.type.Alias;

import java.util.ArrayList;
import java.util.List;

/**
 * Description 充电站信息实体类
 *
 * @author bin.Y
 * @version 2018/5/29 20:47
 */
@Alias("ChargeStationInfoEntity")
public class ChargeStationInfoEntity {
    private String StationID;
    private String OperatorID;
    private String EquipmentOwnerID;
    private String StationName;
    private String CountryCode;
    private String AreaCode;
    private String Address;
    private String StationTel;
    private String ServiceTel;
    private int StationType;

    private int StationStatus;
    private int ParkNums;
    private double StationLng;
    private double StationLat;
    private String SiteGuide;
    private int Construction;
    private String[] Pictures;//17
    private String MatchCars;
    private String ParkInfo;
    private String ParkOwner;

    private String ParkManager;
    private int OpenAllDay;
    private String BusineHours;
    private double MinElectricityPrice;
    private String ElectricityFee;
    private String ServiceFee;
    private int ParkFree;
    private String ParkFee;
    private String Payment;
    private int SupportOrder;

    private String Remark;
    private String PicturesTmp;

    public String getPicturesTmp() {
        return PicturesTmp;
    }

    public void setPicturesTmp(String picturesTmp) {
        PicturesTmp = picturesTmp;
    }

    private List<ChargeEquipmentInfoEntity> EquipmentInfos = new ArrayList<ChargeEquipmentInfoEntity>();

    public String getStationID() {
        return StationID;
    }

    public void setStationID(String stationID) {
        StationID = stationID;
    }

    public String getOperatorID() {
        return OperatorID;
    }

    public void setOperatorID(String operatorID) {
        OperatorID = operatorID;
    }

    public String getEquipmentOwnerID() {
        return EquipmentOwnerID;
    }

    public void setEquipmentOwnerID(String equipmentOwnerID) {
        EquipmentOwnerID = equipmentOwnerID;
    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getAreaCode() {
        return AreaCode;
    }

    public void setAreaCode(String areaCode) {
        AreaCode = areaCode;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getStationTel() {
        return StationTel;
    }

    public void setStationTel(String stationTel) {
        StationTel = stationTel;
    }

    public String getServiceTel() {
        return ServiceTel;
    }

    public void setServiceTel(String serviceTel) {
        ServiceTel = serviceTel;
    }

    public int getStationType() {
        return StationType;
    }

    public void setStationType(int stationType) {
        StationType = stationType;
    }

    public int getStationStatus() {
        return StationStatus;
    }

    public void setStationStatus(int stationStatus) {
        StationStatus = stationStatus;
    }

    public int getParkNums() {
        return ParkNums;
    }

    public void setParkNums(int parkNums) {
        ParkNums = parkNums;
    }

    public double getStationLng() {
        return StationLng;
    }

    public void setStationLng(double stationLng) {
        StationLng = stationLng;
    }

    public double getStationLat() {
        return StationLat;
    }

    public void setStationLat(double stationLat) {
        StationLat = stationLat;
    }

    public String getSiteGuide() {
        return SiteGuide;
    }

    public void setSiteGuide(String siteGuide) {
        SiteGuide = siteGuide;
    }

    public int getConstruction() {
        return Construction;
    }

    public void setConstruction(int construction) {
        Construction = construction;
    }

    public String[] getPictures() {
        return Pictures;
    }

    public void setPictures(String[] pictures) {
        Pictures = pictures;
    }

    public String getMatchCars() {
        return MatchCars;
    }

    public void setMatchCars(String matchCars) {
        MatchCars = matchCars;
    }

    public String getParkInfo() {
        return ParkInfo;
    }

    public void setParkInfo(String parkInfo) {
        ParkInfo = parkInfo;
    }

    public String getParkOwner() {
        return ParkOwner;
    }

    public void setParkOwner(String parkOwner) {
        ParkOwner = parkOwner;
    }

    public String getParkManager() {
        return ParkManager;
    }

    public void setParkManager(String parkManager) {
        ParkManager = parkManager;
    }

    public int getOpenAllDay() {
        return OpenAllDay;
    }

    public void setOpenAllDay(int openAllDay) {
        OpenAllDay = openAllDay;
    }

    public String getBusineHours() {
        return BusineHours;
    }

    public void setBusineHours(String busineHours) {
        BusineHours = busineHours;
    }

    public double getMinElectricityPrice() {
        return MinElectricityPrice;
    }

    public void setMinElectricityPrice(double minElectricityPrice) {
        MinElectricityPrice = minElectricityPrice;
    }

    public String getElectricityFee() {
        return ElectricityFee;
    }

    public void setElectricityFee(String electricityFee) {
        ElectricityFee = electricityFee;
    }

    public String getServiceFee() {
        return ServiceFee;
    }

    public void setServiceFee(String serviceFee) {
        ServiceFee = serviceFee;
    }

    public int getParkFree() {
        return ParkFree;
    }

    public void setParkFree(int parkFree) {
        ParkFree = parkFree;
    }

    public String getParkFee() {
        return ParkFee;
    }

    public void setParkFee(String parkFee) {
        ParkFee = parkFee;
    }

    public String getPayment() {
        return Payment;
    }

    public void setPayment(String payment) {
        Payment = payment;
    }

    public int getSupportOrder() {
        return SupportOrder;
    }

    public void setSupportOrder(int supportOrder) {
        SupportOrder = supportOrder;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public List<ChargeEquipmentInfoEntity> getEquipmentInfos() {
        return EquipmentInfos;
    }

    public void setEquipmentInfos(List<ChargeEquipmentInfoEntity> equipmentInfos) {
        EquipmentInfos = equipmentInfos;
    }
}
