package com.dfssi.dataplatform.vehicleinfo.vehicleroad.entity;

import java.io.Serializable;

/**
 * Author: 程行荣
 * Time: 2013-10-29 17:35
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class Item implements Serializable {
    private String paramId;//参数ID
    private String paramVal;//参数值

    public Item() {
    }

    public Item(String paramId, String paramVal) {
        this.paramId = paramId;
        this.paramVal = paramVal;
    }


    public String getParamId() {
        return paramId;
    }

    public void setParamId(String paramId) {
        this.paramId = paramId;
    }

    public String getParamVal() {
        return paramVal;
    }

    public void setParamVal(String paramVal) {
        this.paramVal = paramVal;
    }
    @Override
    public String toString() {
        return "[" + paramId + "," + paramVal + "]";
    }
}
