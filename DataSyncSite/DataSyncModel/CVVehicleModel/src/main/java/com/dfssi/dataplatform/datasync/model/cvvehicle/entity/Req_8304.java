package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

/**
 * Author: <孙震>
 * Time: 2013-11-02 16:48
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */


import com.dfssi.dataplatform.datasync.model.common.JtsReqMsg;

/**
 * 信息服务
 */
public class Req_8304 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8304"; }

    private Byte type; //信息类型

    private String content; //信息内容

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Req_8304{" + super.toString() +
                ", type=" + type +
                ", content='" + content + '\'' +
                '}';
    }
}

