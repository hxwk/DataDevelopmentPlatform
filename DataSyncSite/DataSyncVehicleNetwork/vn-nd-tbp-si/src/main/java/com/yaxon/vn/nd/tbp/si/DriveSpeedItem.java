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

public class DriveSpeedItem implements Serializable {

    protected long vid; //车辆id
    private String lpn;//车牌号
    private Short speed; //平均速度
    private Short status; //状态信号

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

    public Short getSpeed() {
        return speed;
    }

    public void setSpeed(Short speed) {
        this.speed = speed;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DriveSpeedItem{" +
                "vid=" + vid +
                ", lpn='" + lpn + '\'' +
                ", speed=" + speed +
                ", status=" + status +
                '}';
    }
}

