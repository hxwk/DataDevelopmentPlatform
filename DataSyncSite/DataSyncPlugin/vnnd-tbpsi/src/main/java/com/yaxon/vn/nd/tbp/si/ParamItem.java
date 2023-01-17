package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;

/**
 * Author: 程行荣
 * Time: 2013-10-29 17:35
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

public class ParamItem implements Serializable {
    private Integer paramId;//参数ID
    private String paramVal;//参数值
    private Byte paramType;//参数类型，提问下发答案id，事件id

    public static final byte PT_STR = 1;
    public static final byte PT_UINT8 = 2;
    public static final byte PT_UINT16 = 3;
    public static final byte PT_UINT32 = 4;
    public static final byte PT_BYTES = 5; //字节数组，用16进制文本表示

    public ParamItem() {
    }

    public ParamItem(Integer paramId, String paramVal) {
        this.paramId = paramId;
        this.paramVal = paramVal;
    }

    public ParamItem(Integer paramId, String paramVal, Byte paramType) {
        this.paramId = paramId;
        this.paramVal = paramVal;
        this.paramType = paramType;
    }

    public Integer getParamId() {
        return paramId;
    }

    public void setParamId(Integer paramId) {
        this.paramId = paramId;
    }

    public String getParamVal() {
        return paramVal;
    }

    public void setParamVal(String paramVal) {
        this.paramVal = paramVal;
    }

    public Byte getParamType() {
        return paramType;
    }

    public void setParamType(Byte paramType) {
        this.paramType = paramType;
    }

    @Override
    public String toString() {
        return "[" + paramId + ",\"" + paramVal + "\"," + paramType + ']';
    }
}
