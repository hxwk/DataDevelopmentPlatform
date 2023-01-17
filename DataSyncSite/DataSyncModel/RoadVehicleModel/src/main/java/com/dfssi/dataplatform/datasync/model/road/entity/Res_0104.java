package com.dfssi.dataplatform.datasync.model.road.entity;

/**
 * Author: 程行荣
 * Time: 2013-10-30 10:17
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsResMsg;

import java.util.List;

/**
 * 查询终端参数应答
 */
public class Res_0104 extends JtsResMsg {
    @Override
    public String id() { return "jts.0104"; }

    private short flowNo; //流水号

    private List<ParamItem> paramItems;

    public short getFlowNo() {
        return flowNo;
    }

    public void setFlowNo(short flowNo) {
        this.flowNo = flowNo;
    }

    public List<ParamItem> getParamItems() {
        return paramItems;
    }

    public void setParamItems(List<ParamItem> paramItems) {
        this.paramItems = paramItems;
    }

    @Override
    public String toString() {
        return "Res_0104{" + super.toString() +
                ", paramItems=" + paramItems +
                '}';
    }
}
