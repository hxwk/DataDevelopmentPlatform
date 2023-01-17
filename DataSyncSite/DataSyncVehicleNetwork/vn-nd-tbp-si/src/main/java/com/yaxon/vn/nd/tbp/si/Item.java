package com.yaxon.vn.nd.tbp.si;

import java.io.Serializable;

/**
 * Author: 杨俊辉
 * Time: 2014-09-03 19:47
 * Copyright (C) 2014 Xiamen Yaxon Networks CO.,LTD.
 */
public class Item implements Serializable {
    //参数id
    private int id;
    //参数长度
    private int length;
   //参数value
    private int value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
    @Override
    public String toString() {
        return "ParmBean{" + super.toString() +
                ",id=" + id +
                ",length=" + length +
                ",value="+value+
                '}';
    }
}
