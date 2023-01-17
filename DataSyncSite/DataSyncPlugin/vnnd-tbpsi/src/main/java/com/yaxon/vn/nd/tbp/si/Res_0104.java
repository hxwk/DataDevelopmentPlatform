package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 程行荣
 * Time: 2013-10-30 10:17
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.List;

/**
 * 查询终端参数应答
 */
public class Res_0104 extends JtsResMsg {
    @Override
    public String id() { return "jts.0104"; }

    private List<ParamItem> paramItems;

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
