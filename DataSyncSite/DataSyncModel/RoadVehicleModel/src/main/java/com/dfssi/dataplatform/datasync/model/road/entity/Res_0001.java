package com.dfssi.dataplatform.datasync.model.road.entity;

/**
 * Author: 程行荣
 * Time: 2013-10-30 10:17
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;

/**
 * 终端通用应答
 */
public class Res_0001 extends JtsResMsg {
    @Override
    public String id() { return "jts.0001"; }

    @Override
    public String toString() {
        return "Res_0001{" + super.toString() + "}";
    }
}
