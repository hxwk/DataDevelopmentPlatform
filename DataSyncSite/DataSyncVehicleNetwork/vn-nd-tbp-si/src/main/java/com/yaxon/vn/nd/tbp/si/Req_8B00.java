package com.yaxon.vn.nd.tbp.si;

import java.util.Date;

/**
 * Author: 赖贵明
 * Time: 2014-12-22 14:39
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 * 下行协议请求：下发召车指令
 */
public class Req_8B00 extends JtsReqMsg {


    @Override
    public String id() {
        return  "jts.8B00";
    }

    private Integer businessId; //业务ID
    private Byte businessType;   //0:即时召车；1:预约召车；2:车辆指派
    private Date time; //要车时间
    private String desc;//对乘客要车大概地点的描述

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Byte getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Byte businessType) {
        this.businessType = businessType;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Req_8B00{" +
                "businessId=" + businessId +
                ", businessType=" + businessType +
                ", time=" + time +
                ", desc='" + desc + '\'' +
                '}';
    }
}
