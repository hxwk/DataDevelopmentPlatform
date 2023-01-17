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
 * 采集指定的位置信息记录
 */
public class Res_0700_09 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.09";
    }

    private List<PositionInformation2012Item> positionInformation2012Item = new ArrayList<PositionInformation2012Item>();

    private byte[] data;  //数据块（用于809协议）

    public List<PositionInformation2012Item> getPositionInformation2012Item() {
        return positionInformation2012Item;
    }

    public void setPositionInformation2012Item(List<PositionInformation2012Item> positionInformation2012Item) {
        this.positionInformation2012Item = positionInformation2012Item;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_09{" +
                "positionInformation2012Item=" + positionInformation2012Item +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
