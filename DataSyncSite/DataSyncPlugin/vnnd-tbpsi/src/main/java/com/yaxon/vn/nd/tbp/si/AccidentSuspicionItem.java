package com.yaxon.vn.nd.tbp.si;

/**
 * Author: Sun Zhen
 * Time: 2013-12-30 11:47
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.io.Serializable;

/**
 * 行驶记录仪事故疑点数据项
 */

public class AccidentSuspicionItem implements Serializable {
    private Short speed; //行驶速度
    private Byte status; //状态信号

    public AccidentSuspicionItem() {

    }

    public AccidentSuspicionItem(Short speed, Byte status) {
        this.speed = speed;
        this.status = status;
    }

    public Short getSpeed() {
        return speed;
    }

    public void setSpeed(Short speed) {
        this.speed = speed;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AccidentSuspicionItem{" +
                "speed=" + speed +
                ", status=" + status +
                '}';
    }
}

