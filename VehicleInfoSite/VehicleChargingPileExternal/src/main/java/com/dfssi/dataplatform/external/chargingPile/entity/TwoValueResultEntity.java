package com.dfssi.dataplatform.external.chargingPile.entity;

/**
 * Description:
 *
 * @author JianjunWei
 * @version 2018/07/10 16:16
 */
public class TwoValueResultEntity {
    private String time;
    private long value;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
