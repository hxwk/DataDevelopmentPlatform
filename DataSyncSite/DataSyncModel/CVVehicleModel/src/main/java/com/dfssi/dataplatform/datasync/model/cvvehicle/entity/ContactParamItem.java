package com.dfssi.dataplatform.datasync.model.cvvehicle.entity;

import java.io.Serializable;

/**
 * Author: <孙震>
 * Time: 2013-11-02 16:59
 * Copyright (C) 2013 Xiamen Yaxon Networks CO.,LTD.
 */

/**
 * 联系人参数项
 */

public class ContactParamItem implements Serializable {
    private byte flag; //标志
    private String tel; //电话号码
    private String contact; //联系人

    public ContactParamItem() {
    }

    public ContactParamItem(byte flag, String tel, String contact) {
        this.contact = contact;
        this.flag = flag;
        this.tel = tel;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "ContactParamItem{" +
                "flag=" + flag +
                ", tel='" + tel + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
