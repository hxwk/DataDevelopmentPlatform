package com.yaxon.vn.nd.tbp.si;

/**
 * Author: Sun Zhen
 * Time: 2014-01-02 14:08
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.io.Serializable;

/**
 * 行驶速度数据项
 */

public class DriveSpeed implements Serializable {

    protected long vid; //车辆id
    private String lpn;//车牌号
    private Float speed; //平均速度
    private Byte status; //状态信号

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
        return "DriveSpeed{" +
                "vid=" + vid +
                ", lpn='" + lpn + '\'' +
                ", speed=" + speed +
                ", status=" + status +
                '}';
    }
}

