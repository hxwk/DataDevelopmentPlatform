package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-01 11:59
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 终端注册
 */
public class Req_0100 extends JtsReqMsg {

    public static final String _id = "jts.0100";

    @Override
    public String id() { return "jts.0100"; }

    private String sim;
    private Short provId; //省域ID
    private Short cityId; //市县域ID
    private String manufacturerId; //制造商ID，长度为5
    private String model; //终端型号，长度为8
    private String deviceNo; //设备编号
    private Byte col; //车牌颜色
    private String lpn; //车辆标识  col为0表示vin，不为零表示车票号

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public Short getProvId() {
        return provId;
    }

    public void setProvId(Short provId) {
        this.provId = provId;
    }

    public Short getCityId() {
        return cityId;
    }

    public void setCityId(Short cityId) {
        this.cityId = cityId;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public Byte getCol() {
        return col;
    }

    public void setCol(Byte col) {
        this.col = col;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Req_0100");
        sb.append("{sim=").append(sim);
        sb.append(", provId=").append(provId);
        sb.append(", cityId=").append(cityId);
        sb.append(", manufacturerId='").append(manufacturerId).append('\'');
        sb.append(", model='").append(model).append('\'');
        sb.append(", deviceNo='").append(deviceNo).append('\'');
        sb.append(", col=").append(col);
        sb.append(", lpn='").append(lpn).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
