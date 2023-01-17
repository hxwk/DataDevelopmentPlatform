package com.dfssi.dataplatform.chargingPile.entity;

/**
 * Description 充电设备接口状态
 *
 * @author bin.Y
 * @version 2018/5/30 9:10
 */
public class ChargeConnectorStatusInfoEntity {
    private String ConnectorID;
    private int Status;
    private int CurrentA;
    private int CurrentB;
    private int CurrentC;
    private int VoltageA;
    private int VoltageB;
    private int VoltageC;
    private int ParkStatus;
    private int LockStatus;
    private double SOC;

    public String getConnectorID() {
        return ConnectorID;
    }

    public void setConnectorID(String connectorID) {
        ConnectorID = connectorID;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getCurrentA() {
        return CurrentA;
    }

    public void setCurrentA(int currentA) {
        CurrentA = currentA;
    }

    public int getCurrentB() {
        return CurrentB;
    }

    public void setCurrentB(int currentB) {
        CurrentB = currentB;
    }

    public int getCurrentC() {
        return CurrentC;
    }

    public void setCurrentC(int currentC) {
        CurrentC = currentC;
    }

    public int getVoltageA() {
        return VoltageA;
    }

    public void setVoltageA(int voltageA) {
        VoltageA = voltageA;
    }

    public int getVoltageB() {
        return VoltageB;
    }

    public void setVoltageB(int voltageB) {
        VoltageB = voltageB;
    }

    public int getVoltageC() {
        return VoltageC;
    }

    public void setVoltageC(int voltageC) {
        VoltageC = voltageC;
    }

    public int getParkStatus() {
        return ParkStatus;
    }

    public void setParkStatus(int parkStatus) {
        ParkStatus = parkStatus;
    }

    public int getLockStatus() {
        return LockStatus;
    }

    public void setLockStatus(int lockStatus) {
        LockStatus = lockStatus;
    }

    public double getSOC() {
        return SOC;
    }

    public void setSOC(double SOC) {
        this.SOC = SOC;
    }
}
