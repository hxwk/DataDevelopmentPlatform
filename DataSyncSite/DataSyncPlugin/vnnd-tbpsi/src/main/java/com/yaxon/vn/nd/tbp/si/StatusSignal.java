package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: 杨俊辉
 * Time: 2014-09-04 10:06
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class StatusSignal implements Serializable {

    protected long vid; //车辆id
    private String lpn;//车牌号
    private Byte status; //状态信号
    private String d;//状态信号名称


    private Date realTime; //实时时间    add by zsq

    public Date getRealTime() {
        return realTime;
    }

    public void setRealTime(Date realTime) {
        this.realTime = realTime;
    }

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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    @Override
    public String toString() {
        return "StatusSignal{" +
                "vid=" + vid +
                ", lpn='" + lpn + '\'' +
                ", status=" + status +
                ", d='" + d + '\'' +
                '}';
    }
}
