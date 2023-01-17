package com.yaxon.vn.nd.tbp.si;

import com.yaxon.vndp.dms.Message;

/**
 * Author: 程行荣
 * Time: 2013-10-29 20:42
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * （上行/下行）请求消息基类
 */
public abstract class JtsReqMsg implements Message {
    /* 车辆ID */
    protected String vid;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    @Override
    public String toString() {
        return "vid=" + vid;
    }
}
