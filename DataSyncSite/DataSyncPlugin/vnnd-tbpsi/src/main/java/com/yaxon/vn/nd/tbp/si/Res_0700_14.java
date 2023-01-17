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
 * 采集指定的参数修改记录
 */
public class Res_0700_14 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.14";
    }

    private List<Param> paramItem = new ArrayList<Param>();

    private byte[] data;  //数据块（用于809协议）

    public List<Param> getParamItem() {
        return paramItem;
    }

    public void setParamItem(List<Param> paramItem) {
        this.paramItem = paramItem;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_14{" +
                "paramItem=" + paramItem +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
