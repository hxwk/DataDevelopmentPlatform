package com.yaxon.vn.nd.tbp.si;

import java.util.Arrays;

/**
 * Author: Sun Zhen
 * Time: 2014-01-11 16:24
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */
public class Req_0805Q extends JtsReqMsg{

    public static final byte PHOTO_OK = 0x00;  //成功
    public static final byte PHOTO_FAIL = 0x01;  //失败
    public static final byte PHOTO_NOT_SUPPORT = 0x02; //通道不支持

    @Override
    public String id() { return "jts.0805Q"; }

    private Byte result; //应答结果
    private Byte num; //多媒体ID个数
    private Byte[] mediaIds; //多媒体ID列表

    public Byte getResult() {
        return result;
    }

    public void setResult(Byte result) {
        this.result = result;
    }

    public Byte getNum() {
        return num;
    }

    public void setNum(Byte num) {
        this.num = num;
    }

    public Byte[] getMediaIds() {
        return mediaIds;
    }

    public void setMediaIds(Byte[] mediaIds) {
        this.mediaIds = mediaIds;
    }

    @Override
    public String toString() {
        return "Req_0805Q{" + super.toString() +
                ", result=" + result +
                ", num=" + num +
                ", mediaIds=" + Arrays.toString(mediaIds) +
                '}';
    }
}
