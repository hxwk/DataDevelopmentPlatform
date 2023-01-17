package com.dfssi.dataplatform.datasync.model.newen.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author JianKang
 * @date 2018/3/17
 * @description
 */
public class NEMessage2KafkaBean implements Serializable {
    private String id = UUID.randomUUID().toString();
    private String msgId ;
    private long collectTime;
    private String vin ="";
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
    private long receiveTime;//平台接收到报文的时间
    private int hasValidNum;//当前报文有效报文数
    private int intact;//报文是否完整 1=完整,0=未知,-1=不完整

    public String getId() {
        return id;
    }

    public String getMsgId() {
        return msgId;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
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
        return "NEMessage2KafkaBean{" +
                "id='" + id + '\'' +
                ", msgId='" + msgId + '\'' +
                ", collectTime=" + collectTime +
                ", vin='" + vin + '\'' +
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
                ", receiveTime=" + receiveTime +
                ", hasValidNum=" + hasValidNum +
                ", intact=" + intact +
                '}';
    }
}
