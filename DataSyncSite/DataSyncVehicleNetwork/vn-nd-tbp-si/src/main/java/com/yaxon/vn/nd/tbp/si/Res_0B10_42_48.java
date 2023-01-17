package com.yaxon.vn.nd.tbp.si;


/**
 * Author: 杨俊辉
 * Time: 2014-09-01 10:52
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */


/**
 * 下发透传LED显示屏　（LED显示屏滚动信息）应答
 */
public class Res_0B10_42_48 extends JtsResMsg {

    @Override
    public String id() {
        return "jts.8B104248";
    }

    private byte type; //应答类型 0x02 滚动消息
    private byte result; //应答结果 0x01：成功 0x02：失败 0x03：数据有误 0x04：车台和屏之间通信异常（车台和屏之间10分钟没有连路维护，则认为异常）0x05：车台和屏连接断开或者没有连接。

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

    @Override
    public String toString() {
        return "Res_0B10_42_48{" +
                "type=" + type +
                ", result=" + result +
                '}';
    }
}
