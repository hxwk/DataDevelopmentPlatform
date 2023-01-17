package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: 孙震
 * Time: 2014-02-27 10:23
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */
public class OvertimeItem  implements Serializable {
    private Date startTime; //超时开始时间
    private Date endTime; //超时结束时间

    public OvertimeItem() {

    }

    public OvertimeItem(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "OvertimeItem{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
