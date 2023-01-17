package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;
import java.util.Date;

/**
 * 行驶记录数据上传 （2012版）
 * 采集累计行驶里程
 */
public class Res_0700_03 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.03";
    }

    private Date realTime; //实时时间
    private Date firstTime; //记录仪初次安装时间
    private Integer initialMileage; //初始里程
    private Integer sumMileage; //累计行驶里程

    private byte[] data;  //数据块（用于809协议）

    public Date getRealTime() {
        return realTime;
    }

    public void setRealTime(Date realTime) {
        this.realTime = realTime;
    }

    public Date getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(Date firstTime) {
        this.firstTime = firstTime;
    }

    public Integer getInitialMileage() {
        return initialMileage;
    }

    public void setInitialMileage(Integer initialMileage) {
        this.initialMileage = initialMileage;
    }

    public Integer getSumMileage() {
        return sumMileage;
    }

    public void setSumMileage(Integer sumMileage) {
        this.sumMileage = sumMileage;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Res_0700_03{" +
                "realTime=" + realTime +
                ", firstTime=" + firstTime +
                ", initialMileage=" + initialMileage +
                ", sumMileage=" + sumMileage +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
