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

public class AccidentSuspicion implements Serializable {

    protected long vid; //车辆id
    private String lpn;//车牌号
    private Float speed; //行驶结束的速度
    private Byte status; //状态结束的信号

    public long getVid() {
        return vid;
    }

    public void setVid(long vid) {
        this.vid = vid;
    }

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
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
        return "AccidentSuspicion{" +
                "vid=" + vid +
                ", lpn='" + lpn + '\'' +
                ", speed=" + speed +
                ", status=" + status +
                '}';
    }
}

