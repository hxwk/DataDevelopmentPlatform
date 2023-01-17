package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 行驶记录数据上传 （2012版）
 * 采集指定的速度状态日志
 */
public class Res_0700_2012  extends JtsResMsg {

    @Override
    public String id() {
        return "jts.0700.2012.r";
    }

    private short sn; //应答流水号
    private Byte cmd; //命令字
    private byte[] data;  //数据块

    public short getSn() {
        return sn;
    }

    public void setSn(short sn) {
        this.sn = sn;
    }

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
        return "Res_0700_2012{" +
                "sn=" + sn +
                ", cmd=" + cmd +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
