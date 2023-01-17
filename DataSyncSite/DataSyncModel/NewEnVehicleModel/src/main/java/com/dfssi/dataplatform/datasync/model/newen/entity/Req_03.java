package com.dfssi.dataplatform.datasync.model.newen.entity;


import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * @author JianKang
 * @date 2018/2/27
 * @description
 * 新能源补传数据
 */
public class Req_03 extends JtsReqMsg {
    @Override
    public String id() {
        return "jtsne.03";
    }

    private String vin;

    private long collectTime;

    private String commandSign;

    private String vehicleCompany;//车企

    private String vehicleType;//车型

    private NEVehicleBean neVehicleBean;

    private NEDriverMotor neDriverMotor;

    private NEFuelCellBean neFuelCellBean;

    private NEEngineBean neEngineBean;

    private NEGpsBean neGpsBean;

    private NEExtremumBean neExtremumBean;

    private NEAlarmBean neAlarmBean;

    private NEChargeVoltage neChargeVoltage;

    private NEChargeTemp neChargeTemp;

    @Override
    public String getVin() {
        return vin;
    }

    @Override
    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getCommandSign() {
        return commandSign;
    }

    public void setCommandSign(String commandSign) {
        this.commandSign = commandSign;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    public NEVehicleBean getNeVehicleBean() {
        return neVehicleBean;
    }

    public void setNeVehicleBean(NEVehicleBean neVehicleBean) {
        this.neVehicleBean = neVehicleBean;
    }

    public NEDriverMotor getNeDriverMotor() {
        return neDriverMotor;
    }

    public void setNeDriverMotor(NEDriverMotor neDriverMotor) {
        this.neDriverMotor = neDriverMotor;
    }

    public NEFuelCellBean getNeFuelCellBean() {
        return neFuelCellBean;
    }

    public void setNeFuelCellBean(NEFuelCellBean neFuelCellBean) {
        this.neFuelCellBean = neFuelCellBean;
    }

    public NEEngineBean getNeEngineBean() {
        return neEngineBean;
    }

    public void setNeEngineBean(NEEngineBean neEngineBean) {
        this.neEngineBean = neEngineBean;
    }

    public NEGpsBean getNeGpsBean() {
        return neGpsBean;
    }

    public void setNeGpsBean(NEGpsBean neGpsBean) {
        this.neGpsBean = neGpsBean;
    }

    public NEExtremumBean getNeExtremumBean() {
        return neExtremumBean;
    }

    public void setNeExtremumBean(NEExtremumBean neExtremumBean) {
        this.neExtremumBean = neExtremumBean;
    }

    public NEAlarmBean getNeAlarmBean() {
        return neAlarmBean;
    }

    public void setNeAlarmBean(NEAlarmBean neAlarmBean) {
        this.neAlarmBean = neAlarmBean;
    }

    public String getVehicleCompany() {
        return vehicleCompany;
    }

    public void setVehicleCompany(String vehicleCompany) {
        this.vehicleCompany = vehicleCompany;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public NEChargeVoltage getNeChargeVoltage() {
        return neChargeVoltage;
    }

    public void setNeChargeVoltage(NEChargeVoltage neChargeVoltage) {
        this.neChargeVoltage = neChargeVoltage;
    }

    public NEChargeTemp getNeChargeTemp() {
        return neChargeTemp;
    }

    public void setNeChargeTemp(NEChargeTemp neChargeTemp) {
        this.neChargeTemp = neChargeTemp;
    }

    @Override
    public String toString() {
        return "Req_03{" +
                "vin='" + vin + '\'' +
                ", collectTime=" + collectTime +
                ", commandSign='" + commandSign + '\'' +
                ", vehicleCompany='" + vehicleCompany + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", neVehicleBean=" + neVehicleBean +
                ", neDriverMotor=" + neDriverMotor +
                ", neFuelCellBean=" + neFuelCellBean +
                ", neEngineBean=" + neEngineBean +
                ", neGpsBean=" + neGpsBean +
                ", neExtremumBean=" + neExtremumBean +
                ", neAlarmBean=" + neAlarmBean +
                ", neChargeVoltage=" + neChargeVoltage +
                ", neChargeTemp=" + neChargeTemp +
                '}';
    }
}
