package com.dfssi.dataplatform.datasync.model.newen.entity;

import java.io.Serializable;

/**
 * @author JianKang
 * @date 2018/2/11
 * @description 新能源实时信息上报实体
 */
public class NERealTimeDataReportBean implements Serializable {
    /**
     *车辆唯一标识
     */
    private String vin="";
    /**
     *采集时间
     */
    private long collectTime;
    /**
     *信息类型标识
     */
    private short informationType;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public long getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(long collectTime) {
        this.collectTime = collectTime;
    }

    public short getInformationType() {
        return informationType;
    }

    public void setInformationType(short informationType) {
        this.informationType = informationType;
    }

    @Override
    public String toString() {
        return "NERealTimeDataReportBean{" +
                "vin='" + vin +
                ", collectTime=" + collectTime +
                ", informationType=" + informationType +
                '}';
    }
}
