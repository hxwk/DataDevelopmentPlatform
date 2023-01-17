package com.dfssi.dataplatform.datasync.plugin.interceptor.canbean;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * analyze message bean to hdfs -> output
 * @author jianKang
 * @date 2017/12/23
 */
public class AnalyzeMsgBean {
    public String id(){
        return "output message bean";
    }
    private String can_id;
    private List<AnalyzeSignal> analyzeSignalList;

    public String getCan_id() {
        return can_id;
    }

    public void setCan_id(String can_id) {
        this.can_id = can_id;
    }

    public List<AnalyzeSignal> getAnalyzeSignalList() {
        return analyzeSignalList;
    }

    public void setAnalyzeSignalList(List<AnalyzeSignal> analyzeSignalList) {
        this.analyzeSignalList = analyzeSignalList;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
        /*return "\nCANObject[" +
                "CANID='" + can_id + '\'' +
                ", CANSignals=" + analyzeSignalList +
                ']'+"\n";*/
    }
}
