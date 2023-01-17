package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * Author: 赖贵明
 * Time: 2014-12-22 14:39
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 * 上行协议请求：驾驶员取消电招任务
 */
public class Req_0B08 extends JtsReqMsg {


    @Override
    public String id() {
        return  "jts.0B08";
    }

    private Integer businessId; //业务ID
    private Byte type;//取消原因 0:事故1:路堵2:其他或釆用下发取消原因


    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Req_0B08{" +
                "businessId=" + businessId +
                ", type=" + type +
                '}';
    }
}
