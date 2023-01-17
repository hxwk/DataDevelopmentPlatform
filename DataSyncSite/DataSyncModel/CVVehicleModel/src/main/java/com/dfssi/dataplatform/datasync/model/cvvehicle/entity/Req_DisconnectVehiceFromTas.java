package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * Author: 李松
 * Time: 2015-5-23 15:29
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 * 断开车辆与前置机的连接(无与终端的相关协议)
 */
public class Req_DisconnectVehiceFromTas extends JtsReqMsg {
    @Override
    public String id() { return "disconnectVehiceFromTas"; }

    private Long sim;

    private Integer type;//0：只清除缓存执行不断开 1：清除缓存并执行断开

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getSim() {
        return sim;
    }

    public void setSim(Long sim) {
        this.sim = sim;
    }
}
