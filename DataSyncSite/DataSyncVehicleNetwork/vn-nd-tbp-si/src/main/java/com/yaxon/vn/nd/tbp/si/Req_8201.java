package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 14:59
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 位置信息查询
 */
public class Req_8201 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8201"; }

    private long sim;

    public long getSim() {
        return sim;
    }

    public void setSim(long sim) {
        this.sim = sim;
    }

    @Override
    public String toString() {
        return "Req_8201{" +
                "sim=" + sim +
                '}';
    }
}
