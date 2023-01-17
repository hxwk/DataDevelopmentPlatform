package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-06 14:20
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

import java.util.Arrays;

/**
 * 数据下行透传
 */
public class Req_8900 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8900"; }

    private byte transparentTransmitMsgType; //透传消息类型

    //透传消息内容
    private byte[] content;


    public byte getTransparentTransmitMsgType() {
        return transparentTransmitMsgType;
    }

    public void setTransparentTransmitMsgType(byte transparentTransmitMsgType) {
        this.transparentTransmitMsgType = transparentTransmitMsgType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Req_8900{" + super.toString() +
                "transparentTransmitMsgType=" + transparentTransmitMsgType +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
