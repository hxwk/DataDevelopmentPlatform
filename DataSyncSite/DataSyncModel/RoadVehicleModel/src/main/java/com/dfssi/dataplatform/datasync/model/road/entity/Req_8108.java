package com.dfssi.dataplatform.datasync.model.road.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-06 09:13
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 下发终端升级包
 */
public class Req_8108 extends JtsReqMsg {
    @Override
    public String id() {return "jts.8108";}


    private String sim; //sim卡下发的时候根据这个来路由到车辆

    private String  updateType; //升级类型    A0:DBC文件   A1: MCU APP升级文件  A2: 通讯模块APP升级文件 A3: 视频模块App升级文件
    private String manufacturerId; //制造商ID

    private byte versionLength;  //版本号长度
    private String version; //版本号

    private String filePath; //升级包文件路径

   /* private int updateDataLength;//升级数据包长度

    private byte[]  updateData; //升级数据包*/

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

   /* public int getUpdateDataLength() {
        return updateDataLength;
    }

    public void setUpdateDataLength(int updateDataLength) {
        this.updateDataLength = updateDataLength;
    }

    public byte[] getUpdateData() {
        return updateData;
    }

    public void setUpdateData(byte[] updateData) {
        this.updateData = updateData;
    }*/


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
                ", manufacturerId=" + manufacturerId +
                ", version='" + version + '\'' +
                ", filePath='" + filePath + '\'' +
                ", sim='" + sim + '\'' +
                '}';
    }
}
