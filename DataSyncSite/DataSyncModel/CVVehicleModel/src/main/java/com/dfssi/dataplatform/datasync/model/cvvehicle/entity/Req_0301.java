package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-02 16:18
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 事件报告
 */
public class Req_0301 extends JtsReqMsg {
    @Override
    public String id() { return "jts.0301"; }

    private Short id; //事件ID

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Req_0301{" + super.toString() +
                ", id=" + id +
                '}';
    }
}
