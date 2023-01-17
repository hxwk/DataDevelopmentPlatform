package com.dfssi.dataplatform.datasync.plugin.interceptor.canbean;

import com.alibaba.fastjson.JSON;
import com.dfssi.dataplatform.datasync.common.utils.UUIDUtil;
import org.apache.commons.lang.StringUtils;

/**
 * analyzze signal bean -> DBC
 * @author jianKang
 * @date 2017/12/23
 */
public class AnalyzeSignal {
    public String id;
    private String signal_name;
    private double value;
    private String unit;

    public String getId() {
        return id==null?UUIDUtil.getRandomUuidByTrim():id;
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
        return unit = unit.equals("\"null\"")?StringUtils.EMPTY:unit.replace("\"","");
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        /*String msgString ;
        if("\"null\"".equals(unit)){

            msgString = "\nCAN[" +
                    "signal_name='" + signal_name + '\'' +
                    ", value=" + value +
                    "" + '\'' +
                    ']'+"\n";
        }else{
            msgString = "\nCAN[" +
                    "signal_name='" + signal_name + '\'' +
                    ", value=" + value +
                    "" + unit.replaceAll("\"","") + '\'' +
                    ']'+"\n";
        }
        return msgString;*/
        return JSON.toJSONString(this);
    }
}
