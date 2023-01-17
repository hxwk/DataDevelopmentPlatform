package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 17:43
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.List;

/**
 * 设置矩形区域
 */
public class Req_8602 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8602"; }

    private byte attr; //设置属性

    private List<RegionParamItem> regionParamItems; //区域项

    public byte getAttr() {
        return attr;
    }

    public void setAttr(byte attr) {
        this.attr = attr;
    }

    public List<RegionParamItem> getRegionParamItems() {
        return regionParamItems;
    }

    public void setRegionParamItems(List<RegionParamItem> regionParamItems) {
        this.regionParamItems = regionParamItems;
    }

    @Override
    public String toString() {
        return "Req_8602{" + super.toString() +
                ", attr=" + attr +
                ", regionParamItems=" + regionParamItems +
                '}';
    }
}
