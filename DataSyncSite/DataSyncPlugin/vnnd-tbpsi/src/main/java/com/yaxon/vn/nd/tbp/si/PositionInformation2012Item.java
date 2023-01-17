package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Author: 杨俊辉
 * Time: 2014-09-03 18:44
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class PositionInformation2012Item implements Serializable {

    protected long vid; //车辆id
    private String lpn;//车牌号
    private Date startTime; //行驶开始时间
    private List<PositionInformationItem> list; //单位分钟的行驶速度纪录

    public String getLpn() {
        return lpn;
    }

    public void setLpn(String lpn) {
        this.lpn = lpn;
    }

    public long getVid() {
        return vid;
    }

    public void setVid(long vid) {
        this.vid = vid;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public List<PositionInformationItem> getList() {
        return list;
    }

    public void setList(List<PositionInformationItem> list) {
        this.list = list;
    }


    @Override
    public String toString() {
        return "PositionInformation2012Item{" +
                "vid=" + vid +
                ", lpn='" + lpn + '\'' +
                ", startTime=" + startTime +
                ", list=" + list +
                '}';
    }
}
