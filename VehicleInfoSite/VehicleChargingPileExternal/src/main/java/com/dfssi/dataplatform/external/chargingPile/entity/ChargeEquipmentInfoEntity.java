package com.dfssi.dataplatform.external.chargingPile.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Description充电设备信息
 *
 * @author bin.Y
 * @version 2018/5/29 20:54
 */
public class ChargeEquipmentInfoEntity {
    private String EquipmentID;
    private String ManufacturerID;
    private String EquipmentModel;
    private String EquipmentName;
    private String ProductionDate;
    private String ConstructionTime;
    private int EquipmentType;
    private int EquipmentStatus;
    private Double EquipmentPower;
    private int NewNationalStandard;
    private double EquipmentLng;
    private double EquipmentLat;
    private List<ChargeConnectorInfoEntity> ConnectorInfos = new ArrayList<ChargeConnectorInfoEntity>();

    public String getEquipmentID() {
        return EquipmentID;
    }

    public void setEquipmentID(String equipmentID) {
        EquipmentID = equipmentID;
    }

    public String getManufacturerID() {
        return ManufacturerID;
    }

    public void setManufacturerID(String manufacturerID) {
        ManufacturerID = manufacturerID;
    }

    public String getEquipmentModel() {
        return EquipmentModel;
    }

    public void setEquipmentModel(String equipmentModel) {
        EquipmentModel = equipmentModel;
    }

    public String getEquipmentName() {
        return EquipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        EquipmentName = equipmentName;
    }

    public String getProductionDate() {
        return ProductionDate;
    }

    public void setProductionDate(String productionDate) {
        ProductionDate = productionDate;
    }

    public String getConstructionTime() {
        return ConstructionTime;
    }

    public void setConstructionTime(String constructionTime) {
        ConstructionTime = constructionTime;
    }

    public int getEquipmentType() {
        return EquipmentType;
    }

    public void setEquipmentType(int equipmentType) {
        EquipmentType = equipmentType;
    }

    public int getEquipmentStatus() {
        return EquipmentStatus;
    }

    public void setEquipmentStatus(int equipmentStatus) {
        EquipmentStatus = equipmentStatus;
    }

    public Double getEquipmentPower() {
        return EquipmentPower;
    }

    public void setEquipmentPower(Double equipmentPower) {
        EquipmentPower = equipmentPower;
    }

    public int getNewNationalStandard() {
        return NewNationalStandard;
    }

    public void setNewNationalStandard(int newNationalStandard) {
        NewNationalStandard = newNationalStandard;
    }

    public List<ChargeConnectorInfoEntity> getConnectorInfos() {
        return ConnectorInfos;
    }

    public void setConnectorInfos(List<ChargeConnectorInfoEntity> connectorInfos) {
        ConnectorInfos = connectorInfos;
    }

    public double getEquipmentLng() {
        return EquipmentLng;
    }

    public void setEquipmentLng(double equipmentLng) {
        EquipmentLng = equipmentLng;
    }

    public double getEquipmentLat() {
        return EquipmentLat;
    }

    public void setEquipmentLat(double equipmentLat) {
        EquipmentLat = equipmentLat;
    }
}
