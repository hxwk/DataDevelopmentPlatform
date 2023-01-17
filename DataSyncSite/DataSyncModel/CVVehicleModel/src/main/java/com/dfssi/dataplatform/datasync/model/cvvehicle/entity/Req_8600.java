package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-02 17:18
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */


import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.List;

/**
 * 设置圆形区域
 */
public class Req_8600 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8600"; }

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
        return "Req_8600{" + super.toString() +
                ", attr=" + attr +
                ", regionParamItems=" + regionParamItems +
                '}';
    }
}

