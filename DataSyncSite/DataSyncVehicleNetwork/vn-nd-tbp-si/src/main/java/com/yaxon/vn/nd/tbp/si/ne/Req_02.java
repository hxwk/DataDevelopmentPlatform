package com.yaxon.vn.nd.tbp.si.ne;


import com.yaxon.vn.nd.tbp.si.JtsReqMsg;

/**
 * @author JianKang
 * @date 2018/2/27
 * @description
 *
 */
public class Req_02 extends JtsReqMsg {
    @Override
    public String id() {
        return "jtsne.02";
    }

    private String vin;

    private String commandSign;

    private long collectTime;

    private NEVehicleBean neVehicleBean;

    private NEDriverMotor neDriverMotor;

    private NEFuelCellBean neFuelCellBean;

    private NEEngineBean neEngineBean;

    private NEGpsBean neGpsBean;

    private NEExtremumBean neExtremumBean;

    private NEAlarmBean neAlarmBean;

    private NEChargeVoltage neChargeVoltage;

    private NEChargeTemp neChargeTemp;

    private String vehicleCompany;//车企

    private String vehicleType;//车型

    private long receiveTime;//接收时间

    private int hasValidNum;//当前报文有效报文数

    private int intact;//报文是否完整 1=完整,0=未知,-1=不完整

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

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public int getHasValidNum() {
        return hasValidNum;
    }

    public void setHasValidNum(int hasValidNum) {
        this.hasValidNum = hasValidNum;
    }

    public int getIntact() {
        return intact;
    }

    public void setIntact(int intact) {
        this.intact = intact;
    }

    @Override
    public String toString() {
        return "Req_02{" +
                "vin='" + vin + '\'' +
                ", commandSign='" + commandSign + '\'' +
                ", collectTime=" + collectTime +
                ", neVehicleBean=" + neVehicleBean +
                ", neDriverMotor=" + neDriverMotor +
                ", neFuelCellBean=" + neFuelCellBean +
                ", neEngineBean=" + neEngineBean +
                ", neGpsBean=" + neGpsBean +
                ", neExtremumBean=" + neExtremumBean +
                ", neAlarmBean=" + neAlarmBean +
                ", neChargeVoltage=" + neChargeVoltage +
                ", neChargeTemp=" + neChargeTemp +
                ", vehicleCompany='" + vehicleCompany + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", receiveTime=" + receiveTime +
                ", hasValidNum=" + hasValidNum +
                ", intact=" + intact +
                '}';
    }
}
