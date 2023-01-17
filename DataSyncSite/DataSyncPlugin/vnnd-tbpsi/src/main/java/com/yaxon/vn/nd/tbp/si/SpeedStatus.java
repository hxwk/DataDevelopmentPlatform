package com.yaxon.vn.nd.tbp.si;

/**
 * Author: Sun Zhen
 * Time: 2014-01-02 14:08
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.io.Serializable;

/**
 * 记录、参考速度
 */

public class SpeedStatus implements Serializable {

    private Float recordSpeed;//记录速度
    private Float referSpeed ;//参考速度

    public Float getRecordSpeed() {
        return recordSpeed;
    }

    public void setRecordSpeed(Float recordSpeed) {
        this.recordSpeed = recordSpeed;
    }

    public Float getReferSpeed() {
        return referSpeed;
    }

    public void setReferSpeed(Float referSpeed) {
        this.referSpeed = referSpeed;
    }

    @Override
    public String toString() {
        return "SpeedStatus{" +
                "recordSpeed=" + recordSpeed +
                ", referSpeed=" + referSpeed +
                '}';
    }
}






