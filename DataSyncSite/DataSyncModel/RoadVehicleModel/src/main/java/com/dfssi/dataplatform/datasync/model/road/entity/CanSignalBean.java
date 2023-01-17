package com.dfssi.dataplatform.datasync.model.road.entity;

import java.io.Serializable;

/**
 * signal bean -> DBC
 * @author jianKang
 * @date 2017/12/22
 */
public class CanSignalBean implements Serializable {
    public String id(){
        return "SG_";
    }
    private static final String BACKSLASH = "\"";
    private String signal_name;
    private String start_bit;
    private String signal_size;
    private String byte_order;
    private String value_type;
    private String factor;
    private String offset;
    private String numScope;
    private String unit;
    private String receiver;

    public String getSignal_name() {
        return signal_name;
    }

    public void setSignal_name(String signal_name) {
        this.signal_name = signal_name;
    }

    public String getStart_bit() {
        return start_bit;
    }

    public void setStart_bit(String start_bit) {
        this.start_bit = start_bit;
    }

    public String getSignal_size() {
        return signal_size;
    }

    public void setSignal_size(String signal_size) {
        this.signal_size = signal_size;
    }

    public String getByte_order() {
        return byte_order;
    }

    public void setByte_order(String byte_order) {
        this.byte_order = byte_order;
    }

    public String getValue_type() {
        return value_type;
    }

    public void setValue_type(String value_type) {
        this.value_type = value_type;
    }

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getNumScope() {
        return numScope;
    }

    public void setNumScope(String numScope) {
        this.numScope = numScope;
    }

    public String getUnit() {
        if(null!=unit&&!"".equals(unit)&&unit.contains(BACKSLASH)){
            return unit.replace(BACKSLASH,"");
        }else {
            return unit;
        }
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    //fixme todo 输出需要打印的值 signal_name + resultValue + unit
    @Override
    public String toString() {
        return "DBCChannel" +
                "signal_name='" + signal_name + '\'' +
                ", start_bit='" + start_bit + '\'' +
                ", signal_size='" + signal_size + '\'' +
                ", byte_order='" + byte_order + '\'' +
                ", value_type='" + value_type + '\'' +
                ", factor='" + factor + '\'' +
                ", offset='" + offset + '\'' +
                ", numScope='" + numScope + '\'' +
                ", unit='" + unit + '\'' +
                ", nodeName='" + receiver + '\'' +
                '}';
        //return JSON.toJSONString(this);
    }
}
