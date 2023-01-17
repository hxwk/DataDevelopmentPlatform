package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author jianKang
 * @date 2017/12/23
 */
public class CanAnalyzeSignal implements Serializable {
    public String id;
    private String signal_name;
    private double value;
    private String unit;

    public String getId() {
        return id==null? UUID.randomUUID().toString():id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSignal_name() {
        return signal_name;
    }

    public void setSignal_name(String signal_name) {
        this.signal_name = signal_name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit = unit.equals("\"null\"")? StringUtils.EMPTY:unit.replace("\"","");
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        String msgString ;
        if("null".equals(unit)){
            msgString = "CAN[id=" + id+
                    ",signal_name='" + signal_name + '\'' +
                    ", value=" + value +
                    "" +']';
        }else{
            msgString = "CAN[id=" + id+
                    ",signal_name='" + signal_name + '\'' +
                    ", value=" + value +
                    "" + unit +']';
        }
        return msgString;
    }
}
