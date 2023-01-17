package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 09:13
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 下发终端升级包
 */
public class Req_8108 extends JtsReqMsg {
    @Override
    public String id() {return "jts.8108";}

    private byte updateType; //升级类型
    private byte[] manufacturerId; //制造商ID
    private String version; //版本号

    //升级数据包


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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Req_8108{" + super.toString() +
                ", updateType=" + updateType +
                ", manufacturerId=" + Arrays.toString(manufacturerId) +
                ", version='" + version + '\'' +
                '}';
    }
}
