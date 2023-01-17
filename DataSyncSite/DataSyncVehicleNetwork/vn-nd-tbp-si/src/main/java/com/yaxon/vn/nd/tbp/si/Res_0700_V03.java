package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-04 14:53
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 2003版行驶记录数据上传
 */
public class Res_0700_V03 extends JtsResMsg {
    @Override
    public String id() {
        return "jts.0700.v03";
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
        return "Res_0700_03{" + super.toString() +
                ", cmd=" + cmd +
                ", data=L" + (data == null ? 0 : data.length) +
                '}';
    }
}
