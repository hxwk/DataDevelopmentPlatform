package com.dfssi.dataplatform.chargingPile.entity;

/**
 * Description 充电设备接口信息
 *
 * @author bin.Y
 * @version 2018/5/29 20:59
 */
public class ChargeConnectorInfoEntity {
    private String ConnectorID;
    private String ConnectorName;
    private int ConnectorType;
    private int VoltageUpperLimits;
    private int VoltageLowerLimits;
    private int Current;
    private double Power;
    private String ParkNo;
    private String NationalStandard;

    public String getConnectorID() {
        return ConnectorID;
    }

    public void setConnectorID(String connectorID) {
        ConnectorID = connectorID;
    }

    public String getConnectorName() {
        return ConnectorName;
    }

    public void setConnectorName(String connectorName) {
        ConnectorName = connectorName;
    }

    public int getConnectorType() {
        return ConnectorType;
    }

    public void setConnectorType(int connectorType) {
        ConnectorType = connectorType;
    }

    public int getVoltageUpperLimits() {
        return VoltageUpperLimits;
    }

    public void setVoltageUpperLimits(int voltageUpperLimits) {
        VoltageUpperLimits = voltageUpperLimits;
    }

    public int getVoltageLowerLimits() {
        return VoltageLowerLimits;
    }

    public void setVoltageLowerLimits(int voltageLowerLimits) {
        VoltageLowerLimits = voltageLowerLimits;
    }

    public int getCurrent() {
        return Current;
    }

    public void setCurrent(int current) {
        Current = current;
    }

    public double getPower() {
        return Power;
    }

    public void setPower(double power) {
        Power = power;
    }

    public String getParkNo() {
        return ParkNo;
    }

    public void setParkNo(String parkNo) {
        ParkNo = parkNo;
    }

    public String getNationalStandard() {
        return NationalStandard;
    }

    public void setNationalStandard(String nationalStandard) {
        NationalStandard = nationalStandard;
    }
}
