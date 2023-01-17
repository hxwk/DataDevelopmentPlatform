package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: 杨俊辉
 * Time: 2014-09-03 19:18
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class OvertimeDriving2012Item implements Serializable {

    protected long vid; //车辆id
    private String lpn;//车牌号
    private String lic; //驾驶证号码：ASCII码字符
    private Date startTime; //行驶开始时间
    private Date endTime; //行驶结束时间

    private Position startPosition; // 连续驾驶开始时间的最后一次有效位置信息
    private Position lastPosition;  // 连续驾驶结束时间的最后一次有效位置信息

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

    public String getLic() {
        return lic;
    }

    public void setLic(String lic) {
        this.lic = lic;
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

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position startPosition) {
        this.startPosition = startPosition;
    }

    public Position getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Position lastPosition) {
        this.lastPosition = lastPosition;
    }

    @Override
    public String toString() {
        return "OvertimeDriving2012Item{" +
                "vid=" + vid +
                ", lpn='" + lpn + '\'' +
                ", lic='" + lic + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", startPosition=" + startPosition +
                ", lastPosition=" + lastPosition +
                '}';
    }
}
