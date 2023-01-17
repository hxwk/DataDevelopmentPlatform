package com.dfssi.dataplatform.datasync.model.road.entity;

import java.util.List;

/**
 * analyze message bean to hdfs -> output
 * @author jianKang
 * @date 2017/12/23
 */
public class CanAnalyzeMsgBean {
    public String id(){
        return "output message bean";
    }
    private String can_id;
    private List<CanAnalyzeSignal> analyzeSignalList;

    public String getCan_id() {
        return can_id;
    }

    public void setCan_id(String can_id) {
        this.can_id = can_id;
    }

    public List<CanAnalyzeSignal> getAnalyzeSignalList() {
        return analyzeSignalList;
    }

    public void setAnalyzeSignalList(List<CanAnalyzeSignal> analyzeSignalList) {
        this.analyzeSignalList = analyzeSignalList;
    }

    @Override
    public String toString() {
        //return JSON.toJSONString(this);
        return "\nCANObject[" +
                "CANID='" + can_id + '\'' +
                ", CANSignals=" + analyzeSignalList +
                ']'+"\n";
    }
}
