package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * Author: 赖贵明
 * Time: 2014-12-22 14:39
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 * 下行协议请求：乘客取消召车
 */
public class Req_8B09 extends JtsReqMsg {


    @Override
    public String id() {
        return  "jts.8B09";
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
        return "Req_8B09{" +
                "businessId=" + businessId +
                '}';
    }
}
