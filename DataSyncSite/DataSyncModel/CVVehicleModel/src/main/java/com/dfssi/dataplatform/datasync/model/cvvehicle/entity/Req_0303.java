package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-02 16:42
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 信息点播/取消
 */
public class Req_0303 extends JtsReqMsg {
    @Override
    public String id() { return "jts.0303"; }

    private Byte infoType; //信息类型
    private Byte flag; //点播取消标志

    public Byte getInfoType() {
        return infoType;
    }

    public void setInfoType(Byte infoType) {
        this.infoType = infoType;
    }

    public Byte getFlag() {
        return flag;
    }

    public void setFlag(Byte flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "Req_0303{" + super.toString() +
                ", infoType=" + infoType +
                ", flag=" + flag +
                '}';
    }
}
