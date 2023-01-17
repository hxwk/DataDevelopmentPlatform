package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-06 09:13
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.Arrays;

/**
 * 下发终端升级包
 */
public class Req_8108 extends JtsReqMsg {
    @Override
    public String id() {return "jts.8108";}


    private String sim; //sim卡下发的时候根据这个来路由到车辆

    private byte updateType; //升级类型
    private byte[] manufacturerId; //制造商ID

    private byte versionLength;  //版本号长度
    private String version; //版本号


    private Long updateDataLength;//升级数据包长度

    private byte[]  updateData; //升级数据包

    public byte getUpdateType() {
        return updateType;
    }

    public void setUpdateType(byte updateType) {
        this.updateType = updateType;
    }

    public byte[] getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(byte[] manufacturerId) {
        this.manufacturerId = manufacturerId;
    }


    public byte getVersionLength() {
        return versionLength;
    }

    public void setVersionLength(byte versionLength) {
        this.versionLength = versionLength;
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getUpdateDataLength() {
        return updateDataLength;
    }

    public void setUpdateDataLength(Long updateDataLength) {
        this.updateDataLength = updateDataLength;
    }

    public byte[] getUpdateData() {
        return updateData;
    }

    public void setUpdateData(byte[] updateData) {
        this.updateData = updateData;
    }


    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    @Override
    public String toString() {
        return "Req_8108{" + super.toString() +
                ", updateType=" + updateType +
                ", manufacturerId=" + Arrays.toString(manufacturerId) +
                ", version='" + version + '\'' +
                ", sim='" + sim + '\'' +
                '}';
    }
}
