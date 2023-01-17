package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * 行驶记录数据上传 （2012版）
 * 采集指定的事故疑点记录
 */
public class Res_0700_10 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.10";
    }
    private byte[] data;  //数据块（用于809协议）

    private List<AccidentSuspicion2012Item> accidentSuspicion2012Item = new ArrayList<AccidentSuspicion2012Item>();

    public List<AccidentSuspicion2012Item> getAccidentSuspicion2012Item() {
        return accidentSuspicion2012Item;
    }

    public void setAccidentSuspicion2012Item(List<AccidentSuspicion2012Item> accidentSuspicion2012Item) {
        this.accidentSuspicion2012Item = accidentSuspicion2012Item;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_10{" +
                "accidentSuspicion2012Item=" + accidentSuspicion2012Item +
                '}';
    }
}
