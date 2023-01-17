package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-02 16:09
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

import java.util.List;

/**
 * 事件设置
 */
public class Req_8301 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8301"; }

    private Byte settingType; //设置类型

    private List<ParamItem> paramItems;

    public List<ParamItem> getParamItems() {
        return paramItems;
    }

    public void setParamItems(List<ParamItem> paramItems) {
        this.paramItems = paramItems;
    }

    public Byte getSettingType() {
        return settingType;
    }

    public void setSettingType(Byte settingType) {
        this.settingType = settingType;
    }

    @Override
    public String toString() {
        return "Req_8301{" + super.toString() +
                ", settingType=" + settingType +
                ", paramItems=" + paramItems +
                '}';
    }
}
