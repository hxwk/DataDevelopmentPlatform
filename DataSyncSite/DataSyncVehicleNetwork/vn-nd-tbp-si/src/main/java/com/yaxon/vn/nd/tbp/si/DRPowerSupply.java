package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: 杨俊辉
 * Time: 2014-09-03 19:47
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class DRPowerSupply implements Serializable {

    private long vid; //车辆id
    private String lpn;//车牌号
    private Date createTime; //事件发生时间
    private Byte type; //事件类型

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DRPowerSupply{" +
                "vid=" + vid +
                ", lpn='" + lpn + '\'' +
                ", createTime=" + createTime +
                ", type=" + type +
                '}';
    }
}
