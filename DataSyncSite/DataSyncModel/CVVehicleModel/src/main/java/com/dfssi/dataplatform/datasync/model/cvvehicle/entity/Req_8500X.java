package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: Sun Zhen
 * Time: 2014-01-03 09:38
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */
/**
 * 车辆控制——扩展请求消息(锁车)
 */
public class Req_8500X extends Req_8500 {
    @Override
    public String id() { return "jts.8500X"; }

    @Override
    public String toString() {
        return "Req_8500X{" + super.toString() +
                '}';
    }
}
