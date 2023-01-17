package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 行驶记录数据上传 （2012版）
 * 采集记录仪执行标准版本
 */
public class Res_0700_00  extends JtsResMsg  {

    @Override
    public String id() {
        return "jts.0700.00";
    }

    private String version;//版本号
    private String serialNo;//修改单号

    private byte[] data;  //数据块（用于809协议）

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_00{" +
                "version='" + version + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
