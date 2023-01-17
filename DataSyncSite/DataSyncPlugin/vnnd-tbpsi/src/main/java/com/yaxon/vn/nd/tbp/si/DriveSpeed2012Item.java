package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: 杨俊辉
 * Time: 2014-09-03 18:34
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class DriveSpeed2012Item implements Serializable {

    private Date startTime; //行驶开始时间
    private List<DriveSpeed> list; //单位分钟的行驶速度纪录

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public List<DriveSpeed> getList() {
        return list;
    }

    public void setList(List<DriveSpeed> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "DriveSpeed2012Item{" +
                "startTime=" + startTime +
                ", list=" + list +
                '}';
    }
}
