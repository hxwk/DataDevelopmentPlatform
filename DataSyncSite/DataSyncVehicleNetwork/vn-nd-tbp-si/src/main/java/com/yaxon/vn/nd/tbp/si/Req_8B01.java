package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 赖贵明
 * Time: 2014-12-22 14:39
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 * 下行协议请求：下发乘客信息
 */
public class Req_8B01 extends JtsReqMsg {


    @Override
    public String id() {
        return  "jts.8B01";
    }

    private Integer businessId; //业务ID
    private Byte businessType;   //0:即时召车；1:预约召车；2:车辆指派
    private String tel; //乘客电话号码
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

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Req_8B01{" +
                "businessId=" + businessId +
                ", businessType=" + businessType +
                ", tel='" + tel + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
