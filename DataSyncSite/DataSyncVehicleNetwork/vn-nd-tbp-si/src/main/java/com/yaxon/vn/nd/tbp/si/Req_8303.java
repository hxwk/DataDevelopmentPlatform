package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 16:35
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.List;

/**
 * 信息点播菜单设置
 */
public class Req_8303 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8303"; }

    private Byte type; //设置类型

    private List<ParamItem> paramItems;

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public List<ParamItem> getParamItems() {
        return paramItems;
    }

    public void setParamItems(List<ParamItem> paramItems) {
        this.paramItems = paramItems;
    }

    @Override
    public String toString() {
        return "Req_8303{" + super.toString() +
                ", type=" + type +
                ", paramItems=" + paramItems +
                '}';
    }
}
