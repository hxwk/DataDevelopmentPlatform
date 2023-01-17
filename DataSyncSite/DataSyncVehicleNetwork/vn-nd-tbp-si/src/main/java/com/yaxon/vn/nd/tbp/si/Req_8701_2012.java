package com.yaxon.vn.nd.tbp.si;

import java.util.Arrays;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class Req_8701_2012 extends JtsReqMsg{

    @Override
    public String id() {
        return "jts.8701.2012";
    }

    private Byte cmd; //命令字
    private byte[] data;  //数据块


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
        return "Req_0700_2012{" +
                "cmd=" + cmd +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
