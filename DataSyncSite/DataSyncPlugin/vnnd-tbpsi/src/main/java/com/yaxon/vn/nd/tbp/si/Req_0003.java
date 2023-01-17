package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-01 09:33
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 终端注销
 */
public class Req_0003 extends JtsReqMsg {
    private String sim;

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    @Override
    public String id() {return "jts.0003";}

    @Override
    public String toString() {
        return "Req_0003{" + super.toString() + "}";
    }
}
