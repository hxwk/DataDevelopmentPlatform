package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 程行荣
 * Time: 2013-10-29 17:29
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 查询终端参数
 */
public class Req_8104 extends JtsReqMsg {
    @Override
    public String id() {return "jts.8104";}

    private long sim;

    public long getSim() {
        return sim;
    }

    public void setSim(long sim) {
        this.sim = sim;
    }

    /// 消息体为空
    @Override
    public String toString() {
        return "Req_8104{" + super.toString() + "}";
    }
}
