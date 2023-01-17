package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * Author: 赖贵明
 * Time: 2014-12-22 14:39
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 * 上行协议请求：驾驶员抢答消息
 */
public class Req_0B01 extends JtsReqMsg {


    @Override
    public String id() {
        return  "jts.0B01";
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
        return "Req_0B01{" +
                "businessId=" + businessId +
                '}';
    }
}
