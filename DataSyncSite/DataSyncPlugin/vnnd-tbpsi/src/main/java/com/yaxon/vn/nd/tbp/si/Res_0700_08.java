package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 行驶记录数据上传 （2012版）
 * 采集指定的行驶速度记录
 */
public class Res_0700_08 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.08";
    }

    private List<DriveSpeed2012Item> driveSpeed2012Item = new ArrayList<DriveSpeed2012Item>();

    private byte[] data;  //数据块（用于809协议）

    public List<DriveSpeed2012Item> getDriveSpeed2012Item() {
        return driveSpeed2012Item;
    }

    public void setDriveSpeed2012Item(List<DriveSpeed2012Item> driveSpeed2012Item) {
        this.driveSpeed2012Item = driveSpeed2012Item;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_08{" +
                "driveSpeed2012Item=" + driveSpeed2012Item +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
