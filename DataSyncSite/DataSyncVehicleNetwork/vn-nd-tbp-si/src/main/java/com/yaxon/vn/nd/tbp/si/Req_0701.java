package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-04 16:41
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 电子运单上报
 */
public class Req_0701 extends JtsReqMsg {
    @Override
    public String id() {
        return "jts.0701";
    }

    private String content; //电子运单内容

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Req_0701{" + super.toString() +
                ", content='" + content + '\'' +
                '}';
    }
}
