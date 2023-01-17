package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-05 17:24
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 补传分包请求
 */
public class Req_8003 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8003";
    }

    private short orInfoSerialNo; //原始消息流水号
    private byte[] packageIds; //重传包ID列表

    public short getOrInfoSerialNo() {
        return orInfoSerialNo;
    }

    public void setOrInfoSerialNo(short orInfoSerialNo) {
        this.orInfoSerialNo = orInfoSerialNo;
    }

    public byte[] getPackageIds() {
        return packageIds;
    }

    public void setPackageIds(byte[] packageIds) {
        this.packageIds = packageIds;
    }

    @Override
    public String toString() {
        return "Req_8003{" + super.toString() +
                ", orInfoSerialNo=" + orInfoSerialNo +
                ", packageIds=" + Arrays.toString(packageIds) +
                '}';
    }
}
