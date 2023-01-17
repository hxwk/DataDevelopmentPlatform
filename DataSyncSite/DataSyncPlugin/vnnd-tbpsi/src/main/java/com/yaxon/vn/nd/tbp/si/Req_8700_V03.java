package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-04 14:53
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 2003版行驶记录数据采集命令
 */
public class Req_8700_V03 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8700.v03";
    }

    private Byte cmd; //命令字

    public Byte getCmd() {
        return cmd;
    }

    public void setCmd(Byte cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "Req_8700_03{" + super.toString() +
                ",cmd=" + cmd +
                '}';
    }
}
