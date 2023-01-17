package com.dfssi.dataplatform.datasync.plugin.interceptor.bean;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * CAN总线数据项
 * @author jianKang
 * @date 2017/12/15
 */
public class ParamItem implements Serializable {
    /**
     * CAN 总线数据项 CAN ID
     */
    private Integer paramId;
    /**
     * CAN 数据
     */
    private String paramVal;
    /**
     * 参数类型，提问下发答案id，事件id
     */
    private Byte paramType;

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
        /*return "CANBUS items[" +
                "paramId=" + paramId +
                ", paramVal='" + paramVal + '\'' +
                ", paramType=" + paramType +
                '[';*/
        return JSON.toJSONString(this);
    }
}
