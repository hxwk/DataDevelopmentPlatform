package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-06 14:28
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 数据压缩上报
 */
public class Req_0901 extends JtsReqMsg {
    @Override
    public String id() { return "jts.0901"; }

    //压缩消息体

    @Override
    public String toString() {
        return "Req_0901{" + super.toString() +
                '}';
    }
}
