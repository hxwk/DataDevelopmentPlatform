package com.yaxon.vn.nd.tbp.si;

/**
 * Author: <孙震>
 * Time: 2013-11-02 16:57
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */


import java.util.List;

/**
 * 设置电话本
 */
public class Req_8401 extends JtsReqMsg {
    @Override
    public String id() { return "jts.8401"; }

    private byte type; //设置类型

    private List<ContactParamItem> contactParamItems; //联系人项

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public List<ContactParamItem> getContactParamItems() {
        return contactParamItems;
    }

    public void setContactParamItems(List<ContactParamItem> contactParamItems) {
        this.contactParamItems = contactParamItems;
    }

    @Override
    public String toString() {
        return "Req_8401{" + super.toString() +
                ", type=" + type +
                ", contactParamItems=" + contactParamItems +
                '}';
    }
}

