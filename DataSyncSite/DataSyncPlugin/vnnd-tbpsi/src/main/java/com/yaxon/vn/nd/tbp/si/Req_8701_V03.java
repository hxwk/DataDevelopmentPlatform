package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-04 14:53
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 2003版行驶记录数据采集命令
 */
public class Req_8701_V03 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.8701.v03";
    }

    private Byte cmd; //命令字
    private byte[] data;

    public Byte getCmd() {
        return cmd;
    }

    public void setCmd(Byte cmd) {
        this.cmd = cmd;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Req_8701_03{" + super.toString() +
                ",cmd=" + cmd +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
