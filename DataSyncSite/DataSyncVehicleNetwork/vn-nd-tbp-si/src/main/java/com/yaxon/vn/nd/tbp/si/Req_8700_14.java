package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Date;

/**
 * 行驶记录数据采集命令 （2012版）
 * 采集指定的参数修改记录
 */
public class Req_8700_14 extends JtsReqMsg {

    @Override
    public String id() {
        return "jts.8700.14";
    }

    private Byte cmd; //命令字
    private Date beginTime; //开始时间
    private Date endTime; //结束时间
    private Short maxN;//最大单位数据块个数 (>=1)

    public Byte getCmd() {
        return cmd;
    }

    public void setCmd(Byte cmd) {
        this.cmd = cmd;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Short getMaxN() {
        return maxN;
    }

    public void setMaxN(Short maxN) {
        this.maxN = maxN;
    }

    @Override
    public String toString() {
        return "Req_8700_14{" +
                "cmd=" + cmd +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                ", maxN=" + maxN +
                '}';
    }
}
