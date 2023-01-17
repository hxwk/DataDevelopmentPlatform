package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 赖贵明
 * Time: 2014-12-22 14:39
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 * 上行协议请求：驾驶员任务完成确认消息
 */
public class Req_0B07 extends JtsReqMsg {


    @Override
    public String id() {
        return  "jts.0B07";
    }

    private Integer businessId; //业务ID

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    @Override
    public String toString() {
        return "Req_0B07{" +
                "businessId=" + businessId +
                '}';
    }
}
