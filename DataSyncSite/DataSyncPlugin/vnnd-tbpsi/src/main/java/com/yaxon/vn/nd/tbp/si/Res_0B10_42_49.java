package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 下发透传LED显示屏 （控制LED显示屏信息）应答
 */
public class Res_0B10_42_49 extends JtsResMsg{
    @Override
    public String id() {
        return "jts.8B104249";
    }

    private byte type; //应答类型 0x04 清空LED顶灯显示内容
    private byte result; //应答结果 0x01：成功 0x02：失败
    private byte extendDate[];//拓展数据（N）

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
    }

    public byte[] getExtendDate() {
        return extendDate;
    }

    public void setExtendDate(byte[] extendDate) {
        this.extendDate = extendDate;
    }

    @Override
    public String toString() {
        return "Res_0B10_42_49{" +
                "type=" + type +
                ", result=" + result +
                ", extendDate=" + Arrays.toString(extendDate) +
                '}';
    }
}
