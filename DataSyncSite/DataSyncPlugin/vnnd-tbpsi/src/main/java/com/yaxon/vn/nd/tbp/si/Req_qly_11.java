package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 移动台登录请求
 */
public class Req_qly_11 extends JtsReqMsg {

    @Override
    public String id() {
        return "jts.qly.11";
    }

    /* 手机号 */
    private long sim;

    public long getSim() {
        return sim;
    }

    public void setSim(long sim) {
        this.sim = sim;
    }

    @Override
    public String toString() {
        return "Req_qly_11{" +
                "sim=" + sim +
                '}';
    }
}
