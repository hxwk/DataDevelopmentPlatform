package com.yaxon.vn.nd.tbp.si;

/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 移动台登录应答
 */
public class Res_qly_91 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.qly.91";
    }

    /* 应答类型 */
    private byte result;

    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Res_qly_91{" +
                "result=" + result +
                '}';
    }
}
