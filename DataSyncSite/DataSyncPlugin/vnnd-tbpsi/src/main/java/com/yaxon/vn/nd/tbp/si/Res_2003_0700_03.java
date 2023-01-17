package com.yaxon.vn.nd.tbp.si;

import java.util.Arrays;

/**
 * Author: 杨俊辉
 * Time: 2014-10-17 15:04
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class Res_2003_0700_03 extends JtsResMsg {

    @Override
    public String id() {
        { return "jts.2003070003"; }
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
        return "Res_2003_0700_03{" +
                "cmd=" + cmd +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
